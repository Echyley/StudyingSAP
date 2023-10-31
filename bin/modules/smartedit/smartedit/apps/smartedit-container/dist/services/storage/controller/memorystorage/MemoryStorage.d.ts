import { Cloneable, IStorage } from 'smarteditcommons';
/** @internal */
export declare class MemoryStorage<Q extends Cloneable, D extends Cloneable> implements IStorage<Q, D> {
    private data;
    clear(): Promise<boolean>;
    dispose(): Promise<boolean>;
    find(queryObject?: Q): Promise<D[]>;
    get(queryObject?: Q): Promise<D>;
    getLength(): Promise<number>;
    put(obj: D, queryObject?: Q): Promise<boolean>;
    remove(queryObject?: Q): Promise<D>;
    entries(): Promise<any[]>;
    private getKey;
}
