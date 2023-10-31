import { IStorage, IStorageController, IStorageOptions } from 'smarteditcommons';
/** @internal */
export declare abstract class AbstractWebStorageController implements IStorageController {
    abstract readonly storageType: string;
    abstract getStorageApi(): Storage;
    abstract getStorageRootKey(): string;
    getStorage(configuration: IStorageOptions): Promise<IStorage<any, any>>;
    deleteStorage(storageId: string): Promise<boolean>;
    getStorageIds(): Promise<string[]>;
    saveStorageData(storageId: string, data: any): Promise<boolean>;
    getStorageData(storageId: string): Promise<any>;
    private setWebStorageContainer;
    private getWebStorageContainer;
}
