/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    IAlertServiceType,
    IPageInfoService,
    TypedMap,
    Nullable,
    IPageService,
    PageContentSlotsService
} from 'smarteditcommons';
import { SlotStatus, ISyncStatus, ISyncJob } from '../../dtos';
import { ISynchronizationPanelApi } from '../../modules';
import { SlotSynchronizationService } from '../../services/SlotSynchronizationService';

/**
 * This component is used by slotSyncButton and it's role is take care of some logic to make slotSyncButton "lighter"
 */
@Component({
    selector: 'se-slot-synchronization-panel-wrapper',
    templateUrl: './SlotSynchronizationPanelWrapperComponent.html'
})
export class SlotSynchronizationPanelWrapperComponent {
    @Input() slotId: string;
    @Output() slotSyncItemsUpdated: EventEmitter<void> = new EventEmitter();
    private synchronizationPanelApi: ISynchronizationPanelApi;

    constructor(
        private pageService: IPageService,
        private pageInfoService: IPageInfoService,
        private slotSynchronizationService: SlotSynchronizationService,
        private pageContentSlotsService: PageContentSlotsService,
        private translateService: TranslateService
    ) {}

    public getApi(api: ISynchronizationPanelApi): void {
        this.synchronizationPanelApi = api;
    }

    public getSyncStatus = async (): Promise<Nullable<ISyncStatus>> => {
        const pageId = await this.pageInfoService.getPageUID();
        const syncStatus = await this.slotSynchronizationService.getSyncStatus(pageId, this.slotId);
        if (!this.slotSynchronizationService.syncStatusExists(syncStatus)) {
            throw new Error(
                'The SlotSynchronizationPanel must only be called for the slot whose sync status is available.'
            );
        }

        const isDisallowed = await this.isSyncDisallowed();
        if (isDisallowed) {
            this.disableSync();
        }

        return syncStatus;
    };

    public performSync = (itemsToSync: TypedMap<string>[]): Promise<ISyncJob> =>
        this.slotSynchronizationService.performSync(itemsToSync, null);

    public syncItemsUpdated(): void {
        this.slotSyncItemsUpdated.emit();
    }

    private async isSyncDisallowed(): Promise<boolean> {
        const [isPageSlot, isPageApproved] = await Promise.all([
            this.isPageSlot(),
            this.isPageApproved()
        ]);

        return isPageSlot && !isPageApproved;
    }

    private async isPageSlot(): Promise<boolean> {
        const slotStatus = await this.pageContentSlotsService.getSlotStatus(this.slotId);

        return slotStatus === SlotStatus.PAGE || slotStatus === SlotStatus.OVERRIDE;
    }

    private async isPageApproved(): Promise<boolean> {
        const pageUuid = await this.pageInfoService.getPageUUID();

        return this.pageService.isPageApproved(pageUuid);
    }

    private disableSync(): void {
        this.synchronizationPanelApi.setMessage({
            type: IAlertServiceType.WARNING,
            description: this.translateService.instant('se.cms.synchronization.slot.disabled.msg')
        });
        this.synchronizationPanelApi.disableItemList(true);
    }
}
