import { IStorageManager } from './IStorageManager';
/**
 * Represents a typical factory of {@link IStorageManager}(s).
 * There should typically only be 1 StorageManager in the system, which make this factory seem redundant, but it's used
 * to create wrapper around the single real StorageManager.
 *
 * The main use-case is for namespacing. A namespaced storagemanager will take care to prevent storageID clashes
 * between extensions or teams.
 */
export declare abstract class IStorageManagerFactory {
    /**
     * @param namespace A unique namespace for all your storage ids
     */
    getStorageManager(namespace: string): IStorageManager;
}
