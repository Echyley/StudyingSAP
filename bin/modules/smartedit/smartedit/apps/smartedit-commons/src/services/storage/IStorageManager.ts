/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Cloneable } from '@smart/utils';
import { IStorage } from './IStorage';
import { IStorageController } from './IStorageController';
import { IStorageOptions } from './IStorageOptions';

/**
 * Represents a manager of multiple {@link IStorage}(s).
 *
 * Typically there is 1 StorageManager in the system, and it is responsible accessing, creating and deleting storages,
 * usually by delegating to {@link IStorageController}(s).
 *
 */
export abstract class IStorageManager {
    registerStorageController(controller: IStorageController): void {
        'proxyFunction';
    }

    /**
     * Check if a storage has been created.
     */
    hasStorage(storageId: string): boolean {
        'proxyFunction';

        return null;
    }

    /**
     * Get an existing or new storage
     */
    getStorage(storageConfiguration: IStorageOptions): Promise<IStorage<Cloneable, Cloneable>> {
        'proxyFunction';

        return null;
    }

    /**
     * Permanently delete a storage and all its data
     *
     * @param force If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
    deleteStorage(storageId: string, force?: boolean): Promise<boolean> {
        'proxyFunction';

        return Promise.resolve(true);
    }

    /**
     * Delete all storages that have exceeded their idle timeout time.
     * See {@link IStorageOptions} for more details
     * @param force If force is false and a storage is found with no storage controller to handle its
     * type then it will not be deleted. This can be useful in some cases when you haven't registered a controller yet.
     */
    deleteExpiredStorages(force?: boolean): Promise<boolean> {
        'proxyFunction';

        return Promise.resolve(true);
    }
}
