import { Cloneable, IStorage, IStorageGateway, IStorageOptions } from 'smarteditcommons';
/** @internal */
export declare class StorageGateway implements IStorageGateway {
    handleStorageRequest(storageConfiguration: IStorageOptions, method: keyof IStorage<Cloneable, Cloneable>, args: Cloneable[]): Promise<any>;
}
