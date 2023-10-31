import { IStorage, IStorageController, IStorageManager, IStorageOptions, IStoragePropertiesService, LogService } from 'smarteditcommons';
/** @internal */
export declare class StorageManager implements IStorageManager {
    private logService;
    private storagePropertiesService;
    private readonly storageMetaDataMap;
    private readonly storageControllers;
    private readonly storages;
    static ERR_NO_STORAGE_TYPE_CONTROLLER(storageType: string): Error;
    constructor(logService: LogService, storagePropertiesService: IStoragePropertiesService);
    registerStorageController(controller: IStorageController): void;
    getStorage(storageConfiguration: IStorageOptions): Promise<IStorage<any, any>>;
    hasStorage(storageId: string): boolean;
    deleteStorage(storageId: string, force?: boolean): Promise<boolean>;
    deleteExpiredStorages(force?: boolean): Promise<boolean>;
    private updateStorageMetaData;
    private isStorageExpired;
    private applyDisposeDecorator;
    private getStorageController;
    private verifyMetaData;
    private setDefaultStorageOptions;
}
