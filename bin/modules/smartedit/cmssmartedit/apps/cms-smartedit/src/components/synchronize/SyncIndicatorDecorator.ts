/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ChangeDetectionStrategy, ChangeDetectorRef, ViewRef } from '@angular/core';
import {
    DEFAULT_SYNCHRONIZATION_STATUSES,
    DEFAULT_SYNCHRONIZATION_POLLING,
    ISyncStatus,
    SlotSynchronizationService
} from 'cmscommons';
import {
    AbstractDecorator,
    CrossFrameEventService,
    ICatalogService,
    IPageInfoService
} from 'smarteditcommons';

@Component({
    selector: 'sync-indicator',
    templateUrl: './SyncIndicatorDecorator.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SyncIndicatorDecorator extends AbstractDecorator {
    public pageUUID: string;
    public syncStatus: ISyncStatus;
    public isVersionNonActive: boolean;

    private unRegisterSyncPolling: () => void;

    constructor(
        private catalogService: ICatalogService,
        private slotSynchronizationService: SlotSynchronizationService,
        private crossFrameEventService: CrossFrameEventService,
        private pageInfoService: IPageInfoService,
        private cdr: ChangeDetectorRef
    ) {
        super();
        // initial sync status is set to unavailable until the first fetch
        this.isVersionNonActive = false;
        this.syncStatus = {
            status: DEFAULT_SYNCHRONIZATION_STATUSES.UNAVAILABLE
        } as ISyncStatus;
    }

    async ngOnInit(): Promise<void> {
        await Promise.all([this.getPageUuid(), this.getIsVersionNonActive()]);

        if (this.isVersionNonActive) {
            await this.fetchSyncStatus();
        }

        this.unRegisterSyncPolling = this.crossFrameEventService.subscribe(
            DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
            async () => {
                await this.fetchSyncStatus();
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            }
        );

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        if (this.unRegisterSyncPolling) {
            this.unRegisterSyncPolling();
        }
    }

    private async getPageUuid(): Promise<void> {
        this.pageUUID = await this.pageInfoService.getPageUUID();
    }

    private async getIsVersionNonActive(): Promise<void> {
        this.isVersionNonActive = await this.catalogService.isContentCatalogVersionNonActive();
    }

    private async fetchSyncStatus(): Promise<void> {
        if (!this.isVersionNonActive) {
            return;
        }
        try {
            const syncStatus = await this.slotSynchronizationService.getSyncStatus(
                this.pageUUID,
                this.componentAttributes.smarteditComponentId
            );
            if (this.slotSynchronizationService.syncStatusExists(syncStatus)) {
                this.syncStatus = syncStatus;
            }
        } catch {
            this.syncStatus.status = DEFAULT_SYNCHRONIZATION_STATUSES.UNAVAILABLE;
        }
    }
}
