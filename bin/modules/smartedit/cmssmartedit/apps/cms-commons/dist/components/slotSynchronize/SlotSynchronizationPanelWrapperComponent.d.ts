import { EventEmitter } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { IPageInfoService, TypedMap, Nullable, IPageService, PageContentSlotsService } from 'smarteditcommons';
import { ISyncStatus, ISyncJob } from '../../dtos';
import { ISynchronizationPanelApi } from '../../modules';
import { SlotSynchronizationService } from '../../services/SlotSynchronizationService';
export declare class SlotSynchronizationPanelWrapperComponent {
    private pageService;
    private pageInfoService;
    private slotSynchronizationService;
    private pageContentSlotsService;
    private translateService;
    slotId: string;
    slotSyncItemsUpdated: EventEmitter<void>;
    private synchronizationPanelApi;
    constructor(pageService: IPageService, pageInfoService: IPageInfoService, slotSynchronizationService: SlotSynchronizationService, pageContentSlotsService: PageContentSlotsService, translateService: TranslateService);
    getApi(api: ISynchronizationPanelApi): void;
    getSyncStatus: () => Promise<Nullable<ISyncStatus>>;
    performSync: (itemsToSync: TypedMap<string>[]) => Promise<ISyncJob>;
    syncItemsUpdated(): void;
    private isSyncDisallowed;
    private isPageSlot;
    private isPageApproved;
    private disableSync;
}
