import { Cloneable, IStorage, IStorageOptions } from 'smarteditcommons';
import { WebStorageBridge } from './WebStorageBridge';
/** @internal */
export declare class WebStorage<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {
    private readonly controller;
    private readonly storageConfiguration;
    static ERR_INVALID_QUERY_OBJECT(queryObjec: any, storageId: string): Error;
    constructor(controller: WebStorageBridge, storageConfiguration: IStorageOptions);
    clear(): Promise<boolean>;
    find(queryObject?: Q): Promise<D[]>;
    get(queryObject?: Q): Promise<D>;
    put(obj: D, queryObject?: Q): Promise<boolean>;
    remove(queryObject?: Q): Promise<D>;
    getLength(): Promise<number>;
    dispose(): Promise<boolean>;
    entries(): Promise<any[]>;
    private getKeyFromQueryObj;
}
