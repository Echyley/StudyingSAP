/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IStorageOptions } from 'smarteditcommons';
import { AbstractWebStorageController } from './AbstractWebStorageController';

/** @internal */
export class WebStorageBridge {
    constructor(
        private controller: AbstractWebStorageController,
        private configuration: IStorageOptions
    ) {}

    saveStorageData(data: any): Promise<boolean> {
        return this.controller.saveStorageData(this.configuration.storageId, data);
    }

    getStorageData(): Promise<any> {
        return this.controller.getStorageData(this.configuration.storageId);
    }
}
