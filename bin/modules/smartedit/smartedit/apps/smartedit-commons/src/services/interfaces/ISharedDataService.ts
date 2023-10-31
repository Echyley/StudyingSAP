/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Cloneable } from '@smart/utils';

/**
 * Provides an abstract extensible shared data service. Used to store any data to be used either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export abstract class ISharedDataService {
    /**
     * Get the data for the given key.
     */
    get(key: string): Promise<Cloneable> {
        'proxyFunction';
        return null;
    }

    /**
     * Set data for the given key.
     */
    set(key: string, value: Cloneable): Promise<void> {
        'proxyFunction';
        return null;
    }

    /**
     * Convenience method to retrieve and modify on the fly the content stored under a given key
     *
     * @param modifyingCallback callback fed with the value stored under the given key. The callback must return the new value of the object to update.
     */
    update(key: string, modifyingCallback: (oldValue: any) => any): Promise<void> {
        'proxyFunction';
        return null;
    }

    /**
     * Remove the entry for the given key.
     *
     * @returns A promise which resolves to the removed data for the given key.
     */
    remove(key: string): Promise<Cloneable> {
        'proxyFunction';
        return null;
    }

    /**
     * Checks the given key exists or not.
     *
     * @param key The key of the data to check.
     * @returns A promise which resolves to true if the given key is found. Otherwise false.
     */
    containsKey(key: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}
