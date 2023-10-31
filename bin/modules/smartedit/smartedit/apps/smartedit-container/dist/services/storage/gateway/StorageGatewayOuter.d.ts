import { Cloneable, IStorage, IStorageGateway, IStorageManager, IStorageOptions } from 'smarteditcommons';
/** @internal */
export declare class StorageGateway implements IStorageGateway {
    private storageManager;
    constructor(storageManager: IStorageManager);
    handleStorageRequest(storageConfiguration: IStorageOptions, method: keyof IStorage<Cloneable, Cloneable>, args: Cloneable[]): Promise<any>;
}
