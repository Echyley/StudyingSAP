/** @internal */
export declare class MetaDataMapStorage<T> {
    private readonly storageKey;
    constructor(storageKey: string);
    getAll(): T[];
    get(storageId: string): T;
    put(storageId: string, value: T): void;
    remove(storageId: string): void;
    removeAll(): void;
    private getDataFromStore;
    private setDataInStore;
}
