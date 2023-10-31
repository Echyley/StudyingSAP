/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    CrossFrameEventService,
    EVENT_CONTENT_CATALOG_UPDATE,
    IAlertService,
    ICatalogService,
    IExperience,
    ISharedDataService,
    IWaitDialogService,
    LogService,
    SystemEventService,
    Timer,
    TimerService,
    TypedMap,
    EXPERIENCE_STORAGE_KEY,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

import { DEFAULT_SYNCHRONIZATION_POLLING } from '../../constants';
import { synchronizationUtils } from '../../SynchronizationUtils';
import { SyncQueue } from '../../SyncQueue';
import { ISynchronizationPanelApi, ISyncStatusItem } from '../../types';

@Component({
    selector: 'se-synchronization-panel',
    templateUrl: './SynchronizationPanelComponent.html',
    styleUrls: ['./SynchronizationPanelComponent.scss']
})
export class SynchronizationPanelComponent implements OnInit, OnDestroy {
    @Input() itemId: string;
    @Input() selectAllLabel: string;
    @Input() getSyncStatus: (id: string) => Promise<ISyncStatusItem>;
    @Input() performSync: (payload: TypedMap<string>[]) => Promise<any>;
    @Input() showFooter: boolean;

    @Output() getApi = new EventEmitter<ISynchronizationPanelApi>();
    @Output() selectedItemsUpdate = new EventEmitter<ISyncStatusItem[]>();
    @Output() syncStatusReady = new EventEmitter<ISyncStatusItem>();
    @Output() syncItemsUpdated: EventEmitter<void> = new EventEmitter();

    public showItemList: boolean;
    public message: { type: string; description: string };
    public isLoading: boolean;
    public disableList: boolean;
    public api: ISynchronizationPanelApi;

    private readonly SYNC_POLLING_SPEED_PREFIX: string;
    private syncQueue: SyncQueue;
    private selectedItemsStorage: ISyncStatusItem[];
    private rootItem: ISyncStatusItem;
    private reSyncTimer: Timer;
    private unsubscribeFastFetch: () => void;

    constructor(
        private waitDialogService: IWaitDialogService,
        private logService: LogService,
        private crossFrameEventService: CrossFrameEventService,
        private systemEventService: SystemEventService,
        private timerService: TimerService,
        private alertService: IAlertService,
        private translateService: TranslateService,
        private sharedDataService: ISharedDataService,
        private catalogService: ICatalogService,
        private userTrackingService: IUserTrackingService
    ) {
        this.showFooter = true;
        this.showItemList = true;
        this.message = null;
        this.disableList = false;
        this.isLoading = false;
        this.SYNC_POLLING_SPEED_PREFIX = 'syncPanel-';
        this.syncQueue = new SyncQueue();
        this.api = {
            selectAll: (): void => {
                if (!this.isRootItemExist()) {
                    throw new Error(
                        "Synchronization status is not available. The 'selectAll' function should be used with 'onSyncStatusReady' event."
                    );
                }
                this.toggleRootItem(true);
            },
            displayItemList: (visible: boolean): void => {
                this.showItemList = visible;
            },
            disableItemList: (disableList: boolean): void => {
                this.disableList = disableList;
            },
            setMessage: (msgConfig: { type: string; description: string }): void => {
                this.message = msgConfig;
            },
            disableItem: null
        };
    }

    ngOnInit(): void {
        this.getApi.emit(this.api);

        this.unsubscribeFastFetch = this.crossFrameEventService.subscribe(
            DEFAULT_SYNCHRONIZATION_POLLING.FAST_FETCH,
            (id, data) => this.fetchSyncStatus(data as unknown as ISyncStatusItem)
        );
        this.fetchSyncStatus();

        // start timer polling
        this.reSyncTimer = this.timerService.createTimer(
            () => this.fetchSyncStatus(),
            DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME
        );

        this.reSyncTimer.start();
    }

    ngOnDestroy(): void {
        // The synchronization panel can be closed from outside by, for example, toolbar api.
        // If this happens in the middle of the synchronization process the wait modal will never disappear.
        // So here we wait until the sync queue is empty and only after that everything closes.
        const finalizeTimer = this.timerService.createTimer(() => {
            if (this.syncQueue.isEmpty()) {
                this.reSyncTimer.teardown();
                this.unsubscribeFastFetch();
                this.systemEventService.publishAsync(
                    DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN,
                    this.SYNC_POLLING_SPEED_PREFIX + this.itemId
                );
                this.toggleRootItem(false);
                this.toggleWaitModal(false);
                finalizeTimer.teardown();
            } else {
                // Sometimes the wait modal can be disabled if the ESC button is clicked and the synchronization panel is
                // closed by another api (for example, toolbar api).
                // In that case we need to reEnable the wait modal.
                this.toggleWaitModal(true);
            }
        }, 200);
        finalizeTimer.start();
    }

    /**
     * Synchronizes all selected items on the panel.
     */
    public syncItems(): Promise<void> {
        const selectedItemPayloads = this.getSelectedItemPayloads();
        const selectedItemPayloadIds = selectedItemPayloads.map(
            (syncItemPayload) => syncItemPayload.itemId
        );
        this.userTrackingService.trackingUserAction(USER_TRACKING_FUNCTIONALITY.TOOL_BAR, 'Sync');
        this.syncQueue.addItems(selectedItemPayloadIds);

        if (this.atLeastOneSelectedItemExists()) {
            this.toggleWaitModal(true);
            return this.performSync(selectedItemPayloads).then(
                () => {
                    this.speedUpPolling();
                },
                () => {
                    this.logService.warn(
                        '[synchronizationPanel] - Could not perform synchronization.'
                    );
                    this.toggleWaitModal(false);
                    // If perform sync fails, empty the sync queue to stop show wait modal in ngOnDestroy function
                    this.syncQueue.emptyItems();
                }
            );
        } else {
            return Promise.resolve();
        }
    }

    /**
     * Reacts on the sync panel item click.
     * - It selects all dependent items if the main item is selected. It never deselects dependent items if root item is deselected.
     * - It saves all currently selected items to the storage so selected items can be preserved later after fetching new status.
     * - It emits the currently selected items.
     *
     *  Note: selected flag is set by SynchronizationPanelItemComponent.
     */
    public selectionChange(index?: number): void {
        if (index === 0) {
            this.toggleAllDependentItems();
        }

        this.saveCurrentlySelectedItemsInStorage();

        this.selectedItemsUpdate.emit(this.getSelectedItems());
    }

    /**
     * Verifies whether the synchronization button on the panel must be disabled.
     */
    public isSyncButtonDisabled(): boolean {
        return this.disableList || this.noSelectedItems() || this.syncQueue.hasAtLeastOneItem();
    }

    /**
     * Retrieves the actual sync statuses for root item as well as all dependent items.
     */
    public async fetchSyncStatus(eventData?: ISyncStatusItem): Promise<void> {
        if (eventData && eventData.itemId !== this.itemId) {
            return;
        }

        this.isLoading = true;
        const rootItem = await this.getSyncStatus(this.itemId);
        this.setRootItem(rootItem);
        this.restoreSelectionAfterFetchingUpdatedItems();
        this.markExternalItems();
        this.showSyncErrors();
        this.updateSyncQueue();
        this.setExternalItemsCatalogVersionName();

        if (this.syncQueue.isEmpty()) {
            this.slowDownPolling();
            this.toggleWaitModal(false);
        }

        this.syncStatusReady.emit(this.getRootItem());
        this.syncItemsUpdated.emit();

        this.isLoading = false;
    }

    /**
     * Marks sync status item as external if its catalog version differs from the current item catalog version.
     */
    public markExternalItems(): void {
        const rootItem = this.getRootItem();
        const rootItemCatalogVersion = rootItem.catalogVersionUuid;
        const dependentItems = this.getDependentItems();
        dependentItems.forEach((item) => {
            item.isExternal = item.catalogVersionUuid !== rootItemCatalogVersion;
        });
    }

    /**
     * Returns the list of sync statuses for all elements on synchronization panel (main and all dependent).
     * It returns the status of the item itself and all its dependencies. If the synchronization status
     * of the item is not available the empty list is returned.
     */
    public getAllItems(): ISyncStatusItem[] {
        return this.isRootItemExist() ? [this.getRootItem(), ...this.getDependentItems()] : [];
    }

    /**
     * Returns the root item.
     */
    public getRootItem(): ISyncStatusItem {
        return this.rootItem;
    }

    private async setExternalItemsCatalogVersionName(): Promise<void> {
        const experience = await this.getCurrentExperience();
        const allItems = this.getAllItems();
        const externalItems = allItems.filter((item) => synchronizationUtils.isExternalItem(item));

        const catalogVersionPromises = externalItems.map(
            (externalItem) =>
                new Promise(async (resolve) => {
                    const { catalogName } = await this.catalogService.getCatalogVersionByUuid(
                        externalItem.catalogVersionUuid,
                        experience.siteDescriptor.uid
                    );
                    externalItem.catalogVersionName = catalogName;
                    resolve(externalItem);
                })
        );
        await Promise.all(catalogVersionPromises);
    }

    /**
     * Slows down synchronization status polling.
     * - Slows down timer on current panel.
     * - Sends event to slow down polling for a root item id.
     */
    private slowDownPolling(): void {
        this.reSyncTimer.restart(DEFAULT_SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME);
        this.systemEventService.publishAsync(
            DEFAULT_SYNCHRONIZATION_POLLING.SLOW_DOWN,
            this.SYNC_POLLING_SPEED_PREFIX + this.itemId
        );
    }

    /**
     * Speeds up synchronization status polling.
     * - Speeds up timer or current panel.
     * - Sends event to speed up polling for a root item id.
     */
    private speedUpPolling(): void {
        this.reSyncTimer.restart(DEFAULT_SYNCHRONIZATION_POLLING.FAST_POLLING_TIME);
        this.systemEventService.publishAsync(
            DEFAULT_SYNCHRONIZATION_POLLING.SPEED_UP,
            this.SYNC_POLLING_SPEED_PREFIX + this.itemId
        );
    }

    /**
     * Saves root item.
     */
    private setRootItem(rootItem: ISyncStatusItem): void {
        this.rootItem = rootItem;
    }

    /**
     * Verifies whether the root item exists or not.
     */
    private isRootItemExist(): boolean {
        return this.getRootItem() != null;
    }

    /**
     * Returns the list of sync statuses for all dependent elements on synchronization panel. Excluding the main one.
     */
    private getDependentItems(): ISyncStatusItem[] {
        return this.isRootItemExist() ? this.getRootItem().selectedDependencies : [];
    }

    /**
     * Returns currently selected experience.
     */
    private getCurrentExperience(): Promise<IExperience> {
        return this.sharedDataService.get(EXPERIENCE_STORAGE_KEY) as Promise<IExperience>;
    }

    /**
     * Returns the list of item sync payloads for selected items.
     * This list is used to perform synchronization.
     */
    private getSelectedItemPayloads(): TypedMap<string>[] {
        return this.getSelectedItems().map(({ itemId, itemType }) => ({
            itemId,
            itemType
        }));
    }

    /**
     * Returns the list of selected items on the panel.
     */
    private getSelectedItems(): ISyncStatusItem[] {
        return this.getAllItems().filter((item) => item.selected);
    }

    /**
     * Toggle root item (selects or deselects).
     */
    private toggleRootItem(selected: boolean): void {
        if (this.isRootItemExist()) {
            const rootItem = this.getRootItem();
            rootItem.selected = selected;
            this.selectionChange(0);
        }
    }

    /**
     * Selects/deselects all dependent items on synchronization panel.
     * - It ignores items in sync
     * - It ignores external items.
     */
    private toggleAllDependentItems(): void {
        this.getDependentItems()
            .filter((item) => !synchronizationUtils.isInSync(item))
            .filter((item) => !synchronizationUtils.isExternalItem(item))
            .forEach((item) => {
                item.selected = this.getRootItem().selected;
            });
    }

    /**
     * Shows or hides the wait modal panel.
     * @param show if true shows the wait modal, hides otherwise.
     */
    private toggleWaitModal(show: boolean): void {
        if (show) {
            this.waitDialogService.showWaitModal('se.sync.synchronizing');
        } else {
            this.waitDialogService.hideWaitModal();
        }
    }

    /**
     * Saves currently selected items in storage.
     * It's necessary cause items are constantly updating in fetchSyncStatus.
     * This storage will be used to preserve selected items after fetchSyncStatus is executed.
     */
    private saveCurrentlySelectedItemsInStorage(): void {
        this.selectedItemsStorage = this.getSelectedItems();
    }

    /**
     * Retrieves the selected items from storage.
     * This storage will be used to preserve selected item after fetchSyncStatus is executed.
     */
    private getSelectedItemsFromStorage(): ISyncStatusItem[] {
        return this.selectedItemsStorage || [];
    }

    /**
     * After each and every time we retrieve the sync status for current item and all dependencies
     * it's necessary to select back items that were previously selected. Method iterates over previously selected items
     * and reinitialize "selected" attribute in newly retrieved statuses.
     */
    private restoreSelectionAfterFetchingUpdatedItems(): void {
        const selectedItemsFromStorage = this.getSelectedItemsFromStorage();
        const selectedItemsFromStorageIds = selectedItemsFromStorage.map((item) => item.itemId);
        this.getAllItems()
            .filter((item) => !synchronizationUtils.isInSync(item))
            .filter((item) => selectedItemsFromStorageIds.indexOf(item.itemId) > -1)
            .forEach((item) => (item.selected = true));
    }

    /**
     * Updates the synchronization queue.
     * - If the sync queue was not empty, and now it's empty it means that the synchronization has been finished THEN
     *   the EVENT_CONTENT_CATALOG_UPDATE must be sent AND the wait modal must be disabled.
     * - If the sync queue was empty and now is also empty THEN the wait modal must be disabled.
     * - IF the sync queue was empty/was not empty and now is not empty THEN nothing is happening (no events, no disabling for wait modal)
     */
    private updateSyncQueue(): void {
        const syncQueueWasNotEmpty = this.syncQueue.hasAtLeastOneItem();
        const allItems = this.getAllItems();

        // remove items from queue
        allItems
            .filter((item) => this.syncQueue.itemExists(item))
            .filter(
                (item) =>
                    !(
                        synchronizationUtils.isSyncInProgress(item) ||
                        synchronizationUtils.isInNotSync(item)
                    )
            )
            .forEach((item) => this.syncQueue.removeItem(item));

        // add items to queue
        allItems
            .filter((item) => !this.syncQueue.itemExists(item))
            .filter((item) => synchronizationUtils.isSyncInProgress(item))
            .forEach((item) => this.syncQueue.addItems([item.itemId]));

        const syncQueueIsNowEmpty = this.syncQueue.isEmpty();

        if (syncQueueWasNotEmpty && syncQueueIsNowEmpty) {
            this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE);
        }
    }

    /**
     * Displays error message for all item that are in sync queue and at the same time in SyncFailed state.
     */
    private showSyncErrors(): void {
        const itemsInErrors = this.getAllItems()
            .filter((item) => this.syncQueue.itemExists(item))
            .filter((item) => synchronizationUtils.isSyncInFailed(item))
            .map((item) => item.itemId);

        if (itemsInErrors.length > 0) {
            this.alertService.showDanger({
                message: this.translateService.instant(
                    'se.cms.synchronization.panel.failure.message',
                    {
                        items: itemsInErrors.join(' ')
                    }
                ),
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        }
    }

    /**
     * Verifies whether at least one item is selected on panel.
     */
    private atLeastOneSelectedItemExists(): boolean {
        return this.getSelectedItems().length > 0;
    }

    /**
     * Verifies whether there is no selected items.
     */
    private noSelectedItems(): boolean {
        return this.getSelectedItems().length === 0;
    }
}
