/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISyncStatus } from '../../dtos';
import { ISyncStatusItem, SynchronizationStatus } from './types';

type SyncStatus = ISyncStatusItem | ISyncStatus;
export class SynchronizationUtils {
    /**
     * Verifies whether the sync status item is synchronized.
     */
    public isInSync(item: SyncStatus): boolean {
        return item.status === SynchronizationStatus.InSync;
    }

    /**
     * Verifies whether the sync status item is not sync.
     */
    public isInNotSync(item: SyncStatus): boolean {
        return item.status === SynchronizationStatus.NotSync;
    }
    /**
     * Verifies whether the item has failed synchronization status.
     */
    public isSyncInFailed(item: SyncStatus): boolean {
        return item.status === SynchronizationStatus.SyncFailed;
    }

    public isSyncInProgress(item: SyncStatus): boolean {
        return item.status === SynchronizationStatus.InProgress;
    }

    /**
     * Verifies whether the item is external.
     */
    public isExternalItem(syncStatusItem: ISyncStatusItem): boolean {
        return !!syncStatusItem.isExternal;
    }
}

export const synchronizationUtils = new SynchronizationUtils();
