import { IStorage, IStorageController, IStorageOptions, IStoragePropertiesService } from 'smarteditcommons';
/** @internal */
export declare class MemoryStorageController implements IStorageController {
    readonly storageType: string;
    private readonly storages;
    constructor(storagePropertiesService: IStoragePropertiesService);
    getStorage(options: IStorageOptions): Promise<IStorage<any, any>>;
    deleteStorage(storageId: string): Promise<boolean>;
    getStorageIds(): Promise<string[]>;
}
