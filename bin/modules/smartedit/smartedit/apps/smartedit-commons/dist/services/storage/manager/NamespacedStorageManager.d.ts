import { IStorage } from '../IStorage';
import { IStorageController } from '../IStorageController';
import { IStorageManager } from '../IStorageManager';
import { IStorageOptions } from '../IStorageOptions';
/**
 * @internal
 * @ignore
 */
export declare class NamespacedStorageManager implements IStorageManager {
    private readonly storageManager;
    private readonly namespace;
    constructor(storageManager: IStorageManager, namespace: string);
    getStorage(storageConfiguration: IStorageOptions): Promise<IStorage<any, any>>;
    deleteStorage(storageId: string, force?: boolean): Promise<boolean>;
    deleteExpiredStorages(force?: boolean): Promise<boolean>;
    hasStorage(storageId: string): boolean;
    registerStorageController(controller: IStorageController): void;
    getNamespaceStorageId(storageId: string): string;
    getStorageManager(): IStorageManager;
}
