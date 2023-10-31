/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Cloneable, IStorage, IStorageGateway, IStorageOptions } from 'smarteditcommons';

/** @internal */
export class StorageProxy<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {
    constructor(private configuration: IStorageOptions, private storageGateway: IStorageGateway) {}

    clear(): Promise<boolean> {
        return this.storageGateway.handleStorageRequest(this.configuration, 'clear', []);
    }

    dispose(): Promise<boolean> {
        return this.storageGateway.handleStorageRequest(this.configuration, 'dispose', []);
    }

    entries(): Promise<any[]> {
        return this.storageGateway.handleStorageRequest(this.configuration, 'entries', []);
    }

    find(queryObject?: Q): Promise<D[]> {
        const args = queryObject ? [queryObject] : [];
        return this.storageGateway.handleStorageRequest(this.configuration, 'find', args);
    }

    get(queryObject?: Q): Promise<D> {
        const args = queryObject ? [queryObject] : [];
        return this.storageGateway.handleStorageRequest(this.configuration, 'get', args);
    }

    getLength(): Promise<number> {
        return this.storageGateway.handleStorageRequest(this.configuration, 'getLength', []);
    }

    put(obj: D, queryObject?: Q): Promise<boolean> {
        const args = queryObject ? [obj, queryObject] : [obj];
        return this.storageGateway.handleStorageRequest(this.configuration, 'put', args);
    }

    remove(queryObject?: Q): Promise<D> {
        const args = queryObject ? [queryObject] : [];
        return this.storageGateway.handleStorageRequest(this.configuration, 'remove', args);
    }
}
