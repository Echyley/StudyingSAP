import { Cloneable, IStorage, IStorageController, IStorageGateway, IStorageManagerGateway, IStorageOptions, LogService } from 'smarteditcommons';
/** @internal */
export declare class StorageManagerGateway implements IStorageManagerGateway {
    private $log;
    private storageGateway;
    constructor($log: LogService, storageGateway: IStorageGateway);
    /**
     * Disabled for inner app, due not to being able to pass storage controller instances across the gateway
     * @param {IStorageController} controller
     */
    registerStorageController(controller: IStorageController): void;
    getStorage(storageConfiguration: IStorageOptions): Promise<IStorage<Cloneable, Cloneable>>;
    deleteExpiredStorages(force?: boolean): Promise<boolean>;
    deleteStorage(storageId: string, force?: boolean): Promise<boolean>;
    getStorageSanitityCheck(storageConfiguration: IStorageOptions): Promise<boolean>;
    hasStorage(storageId: string): boolean;
}
