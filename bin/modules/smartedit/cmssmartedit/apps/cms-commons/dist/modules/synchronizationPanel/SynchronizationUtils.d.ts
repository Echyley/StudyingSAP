import { ISyncStatus } from '../../dtos';
import { ISyncStatusItem } from './types';
declare type SyncStatus = ISyncStatusItem | ISyncStatus;
export declare class SynchronizationUtils {
    /**
     * Verifies whether the sync status item is synchronized.
     */
    isInSync(item: SyncStatus): boolean;
    /**
     * Verifies whether the sync status item is not sync.
     */
    isInNotSync(item: SyncStatus): boolean;
    /**
     * Verifies whether the item has failed synchronization status.
     */
    isSyncInFailed(item: SyncStatus): boolean;
    isSyncInProgress(item: SyncStatus): boolean;
    /**
     * Verifies whether the item is external.
     */
    isExternalItem(syncStatusItem: ISyncStatusItem): boolean;
}
export declare const synchronizationUtils: SynchronizationUtils;
export {};
