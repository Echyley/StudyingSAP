/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Interface provider that allows you to mutate the default
 * storage properties before the storage system is initialized.
 */
export abstract class IStoragePropertiesService {
    getProperty(propertyName: string): any {
        'proxyFunction';
        return null;
    }
}
