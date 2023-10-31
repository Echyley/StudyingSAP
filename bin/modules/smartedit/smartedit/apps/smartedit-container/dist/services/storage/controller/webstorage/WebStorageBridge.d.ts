import { IStorageOptions } from 'smarteditcommons';
import { AbstractWebStorageController } from './AbstractWebStorageController';
/** @internal */
export declare class WebStorageBridge {
    private controller;
    private configuration;
    constructor(controller: AbstractWebStorageController, configuration: IStorageOptions);
    saveStorageData(data: any): Promise<boolean>;
    getStorageData(): Promise<any>;
}
