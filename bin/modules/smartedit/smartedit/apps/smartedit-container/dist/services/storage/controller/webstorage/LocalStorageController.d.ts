import { IStoragePropertiesService } from 'smarteditcommons';
import { AbstractWebStorageController } from './AbstractWebStorageController';
/** @internal */
export declare class LocalStorageController extends AbstractWebStorageController {
    private storagePropertiesService;
    readonly storageType: string;
    constructor(storagePropertiesService: IStoragePropertiesService);
    getStorageApi(): Storage;
    getStorageRootKey(): string;
}
