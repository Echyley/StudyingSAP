/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStoragePropertiesService } from 'smarteditcommons';
import { AbstractWebStorageController } from './AbstractWebStorageController';

/** @internal */
export class LocalStorageController extends AbstractWebStorageController {
    readonly storageType: string;

    constructor(private storagePropertiesService: IStoragePropertiesService) {
        super();
        this.storageType = this.storagePropertiesService.getProperty('STORAGE_TYPE_LOCAL_STORAGE');
    }

    getStorageApi(): Storage {
        return window.localStorage;
    }

    getStorageRootKey(): string {
        return this.storagePropertiesService.getProperty('LOCAL_STORAGE_ROOT_KEY');
    }
}
