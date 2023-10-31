/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    ISynchronizationPanelApi,
    ISyncJob,
    ISyncStatus,
    ISyncStatusItem,
    SynchronizationPanelComponent
} from 'cmscommons';
import { HomepageService } from 'cmssmarteditcontainer/services';
import { cloneDeep } from 'lodash';
import {
    CrossFrameEventService,
    EVENTS,
    IAlertServiceType,
    IUriContext,
    TypedMap,
    IPageService,
    ICMSPage,
    ISyncPollingService
} from 'smarteditcommons';
import { PAGE_SYNC_STATUS_READY } from '../../constants';
import { PageSyncConditions } from '../../types';

@Component({
    selector: 'se-page-synchronization-panel',
    templateUrl: './PageSynchronizationPanelComponent.html'
})
export class PageSynchronizationPanelComponent implements OnInit, OnDestroy {
    @Output() selectedItemsUpdate: EventEmitter<ISyncStatusItem[]>;

    @Input() uriContext: IUriContext;
    @Input() cmsPage: ICMSPage;
    @Input() showFooter: boolean;

    @ViewChild(SynchronizationPanelComponent, { static: false })
    synchronizationPanel: SynchronizationPanelComponent;

    /**
     * Root Sync Status Item
     */
    public syncStatus: ISyncStatus;
    public pageSyncConditions: PageSyncConditions;

    private synchronizationPanelApi: ISynchronizationPanelApi;
    private unSubPageUpdatedEvent: () => void;

    constructor(
        private pageService: IPageService,
        private homepageService: HomepageService,
        private crossFrameEventService: CrossFrameEventService,
        private syncPollingService: ISyncPollingService,
        private translateService: TranslateService
    ) {
        this.selectedItemsUpdate = new EventEmitter<ISyncStatusItem[]>();
        this.showFooter = true;

        this.syncStatus = null;

        this.pageSyncConditions = {
            canSyncHomepage: false,
            pageHasUnavailableDependencies: false,
            pageHasSyncStatus: false,
            pageHasNoDepOrNoSyncStatus: false
        };
    }

    ngOnInit(): void {
        this.unSubPageUpdatedEvent = this.crossFrameEventService.subscribe(
            EVENTS.PAGE_UPDATED,
            () => this.evaluateIfSyncIsApproved()
        );
    }

    ngOnDestroy(): void {
        this.unSubPageUpdatedEvent();
    }

    public getSyncStatus = async (): Promise<ISyncStatus> => {
        const [canSyncHomepage, syncStatus] = await Promise.all([
            this.homepageService.canSyncHomepage(this.cmsPage, this.uriContext),
            this.syncPollingService.getSyncStatus(this.cmsPage.uuid, this.uriContext),
            this.evaluateIfSyncIsApproved()
        ]);

        this.pageSyncConditions.canSyncHomepage = canSyncHomepage;

        this.syncStatus = syncStatus;

        return this.syncStatus;
    };

    public performSync = (items: TypedMap<string>[]): Promise<ISyncJob> =>
        this.syncPollingService.performSync(items, this.uriContext);

    public onGetApi(api: ISynchronizationPanelApi): void {
        this.synchronizationPanelApi = api;

        this.synchronizationPanelApi.disableItem = (item): boolean =>
            !this.pageSyncConditions.canSyncHomepage && item === this.syncStatus;
    }

    public onSyncStatusReady(syncStatus: ISyncStatus): void {
        this.syncStatusReady(syncStatus);
    }

    public onSelectedItemsUpdate(items: ISyncStatusItem[]): void {
        this.selectedItemsUpdate.emit(items);
    }

    public syncItems(): Promise<void> {
        return this.synchronizationPanel.syncItems();
    }

    private syncStatusReady = (syncStatus: ISyncStatus): void => {
        this.pageSyncConditions.pageHasUnavailableDependencies =
            syncStatus.unavailableDependencies.length > 0;
        this.pageSyncConditions.pageHasSyncStatus = !!syncStatus.lastSyncStatus;
        this.pageSyncConditions.pageHasNoDepOrNoSyncStatus =
            this.pageSyncConditions.pageHasUnavailableDependencies ||
            !this.pageSyncConditions.pageHasSyncStatus;

        this.pageSyncConditions = cloneDeep(this.pageSyncConditions);

        if (this.pageSyncConditions.pageHasUnavailableDependencies) {
            this.hidePageSync();
        } else if (!this.pageSyncConditions.pageHasSyncStatus) {
            this.showPageSync();
        } else {
            this.enableSlotsSync();
        }

        this.crossFrameEventService.publish(PAGE_SYNC_STATUS_READY, this.pageSyncConditions);
    };

    private async evaluateIfSyncIsApproved(): Promise<void> {
        const isPageApproved = await this.pageService.isPageApproved(this.cmsPage.uuid);
        if (!isPageApproved) {
            this.disablePageSync();
        }
    }

    private disablePageSync(): void {
        this.synchronizationPanelApi.setMessage({
            type: IAlertServiceType.WARNING,
            description: this.translateService.instant('se.cms.synchronization.slot.disabled.msg')
        });
        this.synchronizationPanelApi.disableItemList(true);
    }

    private hidePageSync(): void {
        this.synchronizationPanelApi.displayItemList(false);
    }

    // enable page sync only
    private showPageSync(): void {
        this.synchronizationPanelApi.selectAll();
        this.synchronizationPanelApi.displayItemList(false);
    }

    // enable slot/page sync
    private enableSlotsSync(): void {
        this.synchronizationPanelApi.displayItemList(true);
    }
}
