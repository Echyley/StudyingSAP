import { Cloneable, IStorage, IStorageController, IStorageManager, IStorageManagerGateway, IStorageOptions } from 'smarteditcommons';
/** @internal */
export declare class StorageManagerGateway implements IStorageManagerGateway {
    private storageManager;
    constructor(storageManager: IStorageManager);
    getStorageSanitityCheck(storageConfiguration: IStorageOptions): Promise<boolean>;
    deleteExpiredStorages(force?: boolean): Promise<boolean>;
    deleteStorage(storageId: string, force?: boolean): Promise<boolean>;
    hasStorage(storageId: string): boolean;
    getStorage(storageConfiguration: IStorageOptions): Promise<IStorage<Cloneable, Cloneable>>;
    registerStorageController(controller: IStorageController): void;
}
