import { IStorageManager } from './IStorageManager';
import { IStorageOptions } from './IStorageOptions';
export declare abstract class IStorageManagerGateway extends IStorageManager {
    getStorageSanitityCheck(storageConfiguration: IStorageOptions): Promise<boolean>;
}
