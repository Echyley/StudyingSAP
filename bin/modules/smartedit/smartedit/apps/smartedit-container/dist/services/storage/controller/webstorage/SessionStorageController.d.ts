import { IStoragePropertiesService } from 'smarteditcommons';
import { AbstractWebStorageController } from './AbstractWebStorageController';
/** @internal */
export declare class SessionStorageController extends AbstractWebStorageController {
    private storagePropertiesService;
    readonly storageType: string;
    constructor(storagePropertiesService: IStoragePropertiesService);
    getStorageApi(): Storage;
    getStorageRootKey(): string;
}
