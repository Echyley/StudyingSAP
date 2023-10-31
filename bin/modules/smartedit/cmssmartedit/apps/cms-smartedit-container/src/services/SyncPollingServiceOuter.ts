/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    CMSITEMS_UPDATE_EVENT,
    DEFAULT_SYNCHRONIZATION_EVENT,
    DEFAULT_SYNCHRONIZATION_POLLING,
    ISyncJob,
    ISyncStatus,
    SynchronizationResourceService
} from 'cmscommons';
import { throttle } from 'lodash';
import { from, Observable } from 'rxjs';
import { share } from 'rxjs/internal/operators';
import {
    CrossFrameEventService,
    GatewayProxied,
    InvalidateCache,
    ICatalogService,
    IExperienceService,
    IPageInfoService,
    IUriContext,
    SystemEventService,
    TimerService,
    TypedMap,
    LogService,
    OVERLAY_RERENDERED_EVENT,
    EVENTS,
    Timer,
    stringUtils,
    cmsitemsEvictionTag,
    ISyncPollingService,
    SmarteditBootstrapGateway
} from 'smarteditcommons';

@GatewayProxied(
    'getSyncStatus',
    'fetchSyncStatus',
    'changePollingSpeed',
    'registerSyncPollingEvents',
    'performSync'
)
@Injectable()
export class SyncPollingService extends ISyncPollingService {
    public SYNC_POLLING_THROTTLE = 500;

    private syncStatus: TypedMap<ISyncStatus> = {};
    private triggers: Set<string> = new Set();
    private syncPollingTimer: Timer = null;
    private refreshInterval: number = null;
    private syncPageObservableMap = new Map<string, Observable<ISyncStatus>>();

    constructor(
        private logService: LogService,
        private pageInfoService: IPageInfoService,
        private experienceService: IExperienceService,
        private catalogService: ICatalogService,
        private synchronizationResourceService: SynchronizationResourceService,
        private crossFrameEventService: CrossFrameEventService,
        private systemEventService: SystemEventService,
        private timerService: TimerService,
        private readonly smarteditBootstrapGateway: SmarteditBootstrapGateway
    ) {
        super();

        this.initSyncPolling();
    }

    @InvalidateCache(cmsitemsEvictionTag)
    public async performSync(
        array: TypedMap<string>[],
        uriContext: IUriContext
    ): Promise<ISyncJob> {
        const currentPageFromActiveCatalog = await this.isCurrentPageFromActiveCatalog();

        if (currentPageFromActiveCatalog) {
            return Promise.reject();
        }

        const activeVersion = await this.catalogService.getContentCatalogActiveVersion(uriContext);

        return this.synchronizationResourceService
            .getPageSynchronizationPostRestService(uriContext)
            .save({
                target: activeVersion,
                items: array
            });
    }

    public async getSyncStatus(
        pageUUID?: string,
        uriContext?: IUriContext,
        forceGetSynchronization?: boolean
    ): Promise<ISyncStatus> {
        if (forceGetSynchronization) {
            this.clearSyncPageObservableMap();
        }

        if (
            this.syncStatus[pageUUID] &&
            pageUUID === this.syncStatus[pageUUID].itemId &&
            !forceGetSynchronization
        ) {
            return this.syncStatus[pageUUID];
        }

        let _pageUUID: string;

        try {
            _pageUUID = await this.getPageUUID(pageUUID);
        } catch {
            this.logService.error('syncPollingService::getSyncStatus - failed call to getPageUUID');
            this.syncPollingTimer.stop();
            return this.fetchSyncStatus(pageUUID, uriContext);
        }

        try {
            const syncStatus = await this.fetchSyncStatus(_pageUUID, uriContext);
            const syncPollingType = this.getSyncPollingTypeFromInterval(this.refreshInterval);
            this.startSync(syncPollingType);

            return syncStatus;
        } catch {
            this.logService.error(
                'syncPollingService::getSyncStatus - failed call to fetchSyncStatus'
            );
            return Promise.reject();
        }
    }

    public async fetchSyncStatus(
        _pageUUID?: string,
        uriContext?: IUriContext
    ): Promise<ISyncStatus> {
        try {
            const pageUUID = await this.getPageUUID(_pageUUID);
            if (!pageUUID) {
                return {} as ISyncStatus;
            }

            const currentPageFromActiveCatalog = await this.isCurrentPageFromActiveCatalog();
            if (currentPageFromActiveCatalog) {
                this.logService.debug(
                    "When the content catalog is online version, don't need to get the synchronization status."
                );
                return Promise.reject();
            }

            const activeVersion = await this.catalogService.getContentCatalogActiveVersion(
                uriContext
            );
            const uniqueKeyForPage = stringUtils.encode(pageUUID);
            const syncPageObservable = this.syncPageObservableMap.get(uniqueKeyForPage);

            // Re-use pre-existing Observable to avoid concurrent HTTP call to the same synchronization url.
            return syncPageObservable
                ? syncPageObservable.toPromise()
                : this.fetchPageSynchronization(activeVersion, pageUUID, uriContext).toPromise();
        } catch (err) {
            this.stopSync();
            this.logService.warn(err);
            return Promise.reject();
        }
    }

    /*
     * This method is used to change the speed (up/down) of the sync polling. EventId could be syncPollingSpeedUp or syncPollingSlowDown.
     * If multiple services are changing the speed of the polling and in order to differentiate between them 'key' must to be used and it should be unique among the services.
     * For example: key could be servicePrefix + pageUuid/itemId
     * If at least one service needs fast polling, refreshInterval will be set to fast polling. If none of them needs fast polling, refreshInterval will be set to slow polling.
     */
    public changePollingSpeed(eventId: string, key?: string): void {
        if (eventId === DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP) {
            this.syncStatus = {};
            if (key && !this.triggers.has(key)) {
                this.triggers.add(key);
            }

            this.refreshInterval = DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
        } else {
            if (key) {
                this.triggers.delete(key);
            }
            if (this.triggers.size === 0) {
                this.refreshInterval = DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
            }
        }

        this.syncPollingTimer.restart(this.refreshInterval);
    }

    private fetchPageSynchronization(
        activeVersion: string,
        pageUUID: string,
        uriContext: IUriContext
    ): Observable<ISyncStatus> {
        const syncObservable = from(
            this.getPageSlotSyncStatus(uriContext, pageUUID, activeVersion)
        );
        const uniqueKeyForPage = stringUtils.encode(pageUUID);
        this.syncPageObservableMap.set(uniqueKeyForPage, syncObservable);

        return syncObservable.pipe(share());
    }

    private async getPageSlotSyncStatus(
        uriContext: IUriContext,
        pageUUID: string,
        activeVersion: string
    ): Promise<ISyncStatus> {
        const syncStatus = await this.synchronizationResourceService
            .getPageSynchronizationGetRestService(uriContext)
            .get({
                pageUid: pageUUID,
                target: activeVersion
            });
        const lastSyncStatus = this.syncStatus[syncStatus.itemId];

        this.crossFrameEventService.publish(
            DEFAULT_SYNCHRONIZATION_POLLING.FETCH_STATUS,
            syncStatus
        );

        if (JSON.stringify(syncStatus) !== JSON.stringify(lastSyncStatus)) {
            this.crossFrameEventService.publish(
                DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
                syncStatus
            );

            if (
                !lastSyncStatus ||
                syncStatus.lastModifiedDate !== lastSyncStatus.lastModifiedDate ||
                syncStatus.status !== lastSyncStatus.status
            ) {
                this.crossFrameEventService.publish(CMSITEMS_UPDATE_EVENT);
                this.crossFrameEventService.publish(EVENTS.PAGE_UPDATED, {
                    uuid: pageUUID
                });
            }
        }

        this.syncStatus[syncStatus.itemId] = syncStatus;
        this.clearSyncPageObservableMap();

        return syncStatus;
    }

    private stopSync(): void {
        if (this.syncPollingTimer.isActive()) {
            this.syncPollingTimer.stop();
        }
        this.clearSyncStatus();
    }

    private startSync(syncPollingType?: string): void {
        if (!this.syncPollingTimer.isActive()) {
            this.changePollingSpeed(syncPollingType || DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN);
        }
    }

    private initSyncPolling(): void {
        this.refreshInterval = DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
        /**
         * When multiple items needs sync polling at different paces (either slow or fast),
         * triggers array makes sure to set to fast polling even if any one of them needs fast polling.
         */
        this.triggers.clear();
        this.syncStatus = {};

        const changePolling = this.changePollingSpeed.bind(this);

        this.systemEventService.subscribe(DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP, changePolling);
        this.systemEventService.subscribe(DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN, changePolling);

        this.crossFrameEventService.subscribe(
            DEFAULT_SYNCHRONIZATION_POLLING.FETCH_SYNC_STATUS_ONCE,
            (eventId: string, pageUUID: string) => {
                this.fetchSyncStatus(pageUUID).catch(() => {}); // Error has been already handled in detail in fetchSyncStatus.
            }
        );

        this.crossFrameEventService.subscribe(
            OVERLAY_RERENDERED_EVENT,
            throttle(() => {
                if (this.syncPollingTimer.isActive()) {
                    this.fetchSyncStatus().catch(() => {}); // Error has been already handled in detail in fetchSyncStatus.
                }
            }, this.SYNC_POLLING_THROTTLE)
        );

        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
            this.clearSyncStatus();
            this.clearSyncPageObservableMap();
        });

        this.crossFrameEventService.subscribe(
            DEFAULT_SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED,
            () => {
                this.syncStatus = {};
                this.fetchSyncStatus().catch(() => {}); // Error has been already handled in detail in fetchSyncStatus.
            }
        );

        // fetchSyncStatus callback uses current page uuid by default
        this.syncPollingTimer = this.timerService.createTimer(
            () => this.fetchSyncStatus().catch(() => {}), // Error has been already handled in detail in fetchSyncStatus.
            this.refreshInterval
        );

        // start sync polling if it is a storefront page
        this.smarteditBootstrapGateway.getInstance().subscribe('bootstrapSmartEdit', () => {
            const syncPollingType = this.getSyncPollingTypeFromInterval(this.refreshInterval);
            this.startSync(syncPollingType);
        });
    }

    private clearSyncPageObservableMap(): void {
        this.syncPageObservableMap.clear();
    }

    private clearSyncStatus(): void {
        this.syncStatus = {};
    }

    private getPageUUID(_pageUUID: string): Promise<string> {
        return !stringUtils.isBlank(_pageUUID)
            ? Promise.resolve(_pageUUID)
            : this.pageInfoService.getPageUUID();
    }

    private async isCurrentPageFromActiveCatalog(): Promise<boolean> {
        const currentExperience = await this.experienceService.getCurrentExperience();

        return currentExperience.pageContext
            ? currentExperience.pageContext.active
            : currentExperience.catalogDescriptor.active;
    }

    private getSyncPollingTypeFromInterval(interval: number): string {
        return interval === DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME
            ? DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP
            : DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN;
    }
}
