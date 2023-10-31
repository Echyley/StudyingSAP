/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import * as lodash from 'lodash';

import {
    IStorageProperties,
    IStoragePropertiesService,
    STORAGE_PROPERTIES_TOKEN
} from 'smarteditcommons';
import { defaultStorageProperties } from './defaultStorageProperties';

/**
 * The storagePropertiesService is a provider that implements the IStoragePropertiesService
 * interface and exposes the default storage properties. These properties are used to bootstrap various
 * pieces of the storage system.
 * By Means of StorageModule.configure() you would might change the default localStorage key names, or storage types.
 */
/** @internal */
@Injectable()
export class StoragePropertiesService implements IStoragePropertiesService {
    private readonly properties: IStorageProperties;

    constructor(@Inject(STORAGE_PROPERTIES_TOKEN) storageProperties: IStorageProperties[]) {
        this.properties = lodash.cloneDeep(defaultStorageProperties);

        storageProperties.forEach((properties) => {
            lodash.merge(this.properties, properties);
        });
    }

    getProperty(propertyName: keyof IStorageProperties): any {
        return this.properties[propertyName];
    }
}
