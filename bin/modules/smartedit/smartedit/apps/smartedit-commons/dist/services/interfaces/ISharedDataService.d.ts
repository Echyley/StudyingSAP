import { Cloneable } from '@smart/utils';
/**
 * Provides an abstract extensible shared data service. Used to store any data to be used either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export declare abstract class ISharedDataService {
    /**
     * Get the data for the given key.
     */
    get(key: string): Promise<Cloneable>;
    /**
     * Set data for the given key.
     */
    set(key: string, value: Cloneable): Promise<void>;
    /**
     * Convenience method to retrieve and modify on the fly the content stored under a given key
     *
     * @param modifyingCallback callback fed with the value stored under the given key. The callback must return the new value of the object to update.
     */
    update(key: string, modifyingCallback: (oldValue: any) => any): Promise<void>;
    /**
     * Remove the entry for the given key.
     *
     * @returns A promise which resolves to the removed data for the given key.
     */
    remove(key: string): Promise<Cloneable>;
    /**
     * Checks the given key exists or not.
     *
     * @param key The key of the data to check.
     * @returns A promise which resolves to true if the given key is found. Otherwise false.
     */
    containsKey(key: string): Promise<boolean>;
}
