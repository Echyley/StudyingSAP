/**
 * Defines the interface for the configurable properties used in various part of the storage system.
 *
 * See defaultStorageProperties for default values.
 */
export interface IStorageProperties {
    /**
     * The default number of milliseconds before an idle storage becomes expired.
     */
    STORAGE_IDLE_EXPIRY: number;
    /**
     * The localStorage key used by the StorageManager to store its stores metadata
     */
    LOCAL_STORAGE_KEY_STORAGE_MANAGER_METADATA: string;
    STORAGE_TYPE_LOCAL_STORAGE: string;
    STORAGE_TYPE_SESSION_STORAGE: string;
    STORAGE_TYPE_IN_MEMORY: string;
    /**
     * The root localStorage key where all storages are nested.
     */
    LOCAL_STORAGE_ROOT_KEY: string;
    /**
     * The root sessionStorage key where all storages are nested.
     */
    SESSION_STORAGE_ROOT_KEY: string;
}
