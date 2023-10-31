/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStorageManager } from '../IStorageManager';
import { IStorageManagerFactory } from '../IStorageManagerFactory';
import { NamespacedStorageManager } from './NamespacedStorageManager';

/**
 * @internal
 * @ignore
 */
export class StorageManagerFactory implements IStorageManagerFactory {
    static ERR_INVALID_NAMESPACE(namespace: string): Error {
        return new Error(
            `StorageManagerFactory Error: invalid namespace [${namespace}]. Namespace must be a non-empty string`
        );
    }

    constructor(private readonly theOneAndOnlyStorageManager: IStorageManager) {}

    getStorageManager(namespace: string): IStorageManager {
        this.validateNamespace(namespace);
        return new NamespacedStorageManager(
            this.theOneAndOnlyStorageManager,
            namespace
        ) as unknown as IStorageManager;
    }

    private validateNamespace(namespace: string): void {
        if (typeof namespace !== 'string' || namespace.length <= 0) {
            throw StorageManagerFactory.ERR_INVALID_NAMESPACE(namespace);
        }
    }
}
