import { Cloneable } from '@smart/utils';
/**
 *  Represents a data access point, but depending on the implementation doesn't
 *  necessarily contain the stored data directly. For example a cloud storage implementation might access the remote
 *  storage via REST on a get(), or it might fetch the whole storage and keep it in memory.
 *
 *  This interface is kept purposely un-opinionated to support a variety of storage types.
 *
 *  # Generics
 *  The interface requires two generics types be provided,
 *
 *  ### Q extends Cloneable
 *  This is the query type for the storage. In a typical key-value storage this is often a string key type, but
 *  in a more complex storage implementation, for instance indexedDb, this could be a query object.
 *  ### D extends Cloneable
 *  This represents the type of data being stored.
 *
 *  # Cloneable
 *  The IStorage interface does not allow any data or query types, they must extend the Cloneable interface.
 *  This is because the data must be able to be serialized, both for remote storages, and for storages that live in
 *  the inner smartedit application, as this data is passed over the w3c postMessage() between frames.
 */
export interface IStorage<Q extends Cloneable, D extends Cloneable> {
    /**
     * Get a data record from the storage.
     *
     * @param queryObject - The query type. In a key-value storage this would be the key
     */
    get: (queryObject?: Q) => Promise<D>;
    /**
     * Put a data record into the storage, or replace an existing record
     *
     * @param obj The data to store.
     * @param queryObject The query object. In a key-value storage this would be the key
     */
    put: (obj: D, queryObject?: Q) => Promise<boolean>;
    /**
     * Remove a data record from the storage.
     *
     * @param queryObject The query object. In a key-value storage this would be the key
     */
    remove: (queryObject?: Q) => Promise<D>;
    /**
     * Find multiple records of data in the storage.
     *
     * @param queryObject The query type. In a key-value storage this would be the key
     */
    find: (queryObject?: Q) => Promise<D[]>;
    /**
     * Remove all data records from the storage.
     */
    clear: () => Promise<boolean>;
    /**
     * Get the number of data records in the storage
     */
    getLength: () => Promise<number>;
    /**
     * Remove all storage records and remove the storage itself and all metadata from the storage manager.
     */
    dispose: () => Promise<boolean>;
    /**
     * Get all query/data entries from the storage.
     *
     * For key-value storages this will be an array of key-value 2-tuples.
     *
     * ```[key, value][]```
     */
    entries: () => Promise<any[]>;
}
