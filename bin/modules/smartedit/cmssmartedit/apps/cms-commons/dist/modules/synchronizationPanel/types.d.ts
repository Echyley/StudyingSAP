import { ISyncStatus } from '../../dtos';
export interface ISynchronizationPanelApi {
    /**
     * Function that selects all items on synchronization panel. Should be used with onSyncStatusReady event.
     */
    selectAll: () => void;
    /**
     * Function that changes the visibility of the item list.
     */
    displayItemList: (visible: boolean) => void;
    /**
     * Function that determines if the panel, as a whole, should be disabled.
     */
    disableItemList: (disable: boolean) => void;
    /**
     * Function that adds a message (yMessage) in the panel.
     */
    setMessage: (msg: {
        type: string;
        description: string;
    }) => void;
    /**
     * Function that determines if an item should be disabled.
     */
    disableItem: (item: ISyncStatusItem) => void;
}
export interface ISyncStatusItem extends ISyncStatus {
    isExternal: boolean;
    selected: boolean;
    status: SynchronizationStatus;
    i18nKey: string;
    selectedDependencies: ISyncStatusItem[];
}
export interface ISyncPayload {
    itemId: string;
    itemType: string;
}
export declare enum SynchronizationStatus {
    Unavailable = "UNAVAILABLE",
    InSync = "IN_SYNC",
    NotSync = "NOT_SYNC",
    InProgress = "IN_PROGRESS",
    SyncFailed = "SYNC_FAILED"
}
