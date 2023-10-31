import { IStorageFactory } from './IStorageFactory';
/**
 *  Represents the controller for a specific type of storage.
 *
 *  When the storage manager receives a request to access, create or remove a storage, it delegates to the storage
 *  controller of the appropriate storage type to handle these operations.
 *
 *  The controller must be able to create {@link IStorage storages} even across multiple instances
 *  of the smartedit application, if it handles persisted storages.
 */
export interface IStorageController extends IStorageFactory {
    /**
     * The storage type handled by this controller. A StorageManager can only have 1 controller per type registered.
     */
    readonly storageType: string;
    getStorageIds(): Promise<string[]>;
    /**
     * Permanently remove a storage and all its data.
     *
     * @returns A promise resolving to true when the delete operation is complete
     */
    deleteStorage(storageId: string): Promise<boolean>;
}
