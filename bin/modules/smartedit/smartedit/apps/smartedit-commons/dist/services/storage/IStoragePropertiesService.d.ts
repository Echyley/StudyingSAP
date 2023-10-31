/**
 * Interface provider that allows you to mutate the default
 * storage properties before the storage system is initialized.
 */
export declare abstract class IStoragePropertiesService {
    getProperty(propertyName: string): any;
}
