import { IStorageProperties, IStoragePropertiesService } from 'smarteditcommons';
/**
 * The storagePropertiesService is a provider that implements the IStoragePropertiesService
 * interface and exposes the default storage properties. These properties are used to bootstrap various
 * pieces of the storage system.
 * By Means of StorageModule.configure() you would might change the default localStorage key names, or storage types.
 */
/** @internal */
export declare class StoragePropertiesService implements IStoragePropertiesService {
    private readonly properties;
    constructor(storageProperties: IStorageProperties[]);
    getProperty(propertyName: keyof IStorageProperties): any;
}
