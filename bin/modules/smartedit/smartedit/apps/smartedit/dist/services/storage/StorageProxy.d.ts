import { Cloneable, IStorage, IStorageGateway, IStorageOptions } from 'smarteditcommons';
/** @internal */
export declare class StorageProxy<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {
    private configuration;
    private storageGateway;
    constructor(configuration: IStorageOptions, storageGateway: IStorageGateway);
    clear(): Promise<boolean>;
    dispose(): Promise<boolean>;
    entries(): Promise<any[]>;
    find(queryObject?: Q): Promise<D[]>;
    get(queryObject?: Q): Promise<D>;
    getLength(): Promise<number>;
    put(obj: D, queryObject?: Q): Promise<boolean>;
    remove(queryObject?: Q): Promise<D>;
}
