import { ISharedDataService, LogService, PromiseUtils, RestServiceFactory } from 'smarteditcommons';
import { ConfigurationItem, ConfigurationObject } from './Configuration';
/**
 * LoadConfigManagerService is used to retrieve configurations stored in configuration API.
 */
export declare class LoadConfigManagerService {
    private sharedDataService;
    private logService;
    private promiseUtils;
    private SMARTEDIT_RESOURCE_URI_REGEXP;
    private SMARTEDIT_ROOT;
    private restService;
    constructor(restServicefactory: RestServiceFactory, sharedDataService: ISharedDataService, logService: LogService, promiseUtils: PromiseUtils, SMARTEDIT_RESOURCE_URI_REGEXP: RegExp, SMARTEDIT_ROOT: string);
    /**
     * Retrieves configuration from an API and returns as an array of mapped key/value pairs.
     *
     * ### Example:
     *
     *      loadConfigManagerService.loadAsArray().then(
     *          (response: ConfigurationItem[]) => {
     *              this._prettify(response);
     *          }));
     *
     *
     *
     * @returns  a promise of configuration values as an array of mapped configuration key/value pairs
     */
    loadAsArray(): Promise<ConfigurationItem[]>;
    /**
     * Retrieves a configuration from the API and converts it to an object.
     *
     * ### Example:
     *
     *
     *      loadConfigManagerService.loadAsObject().then((conf: ConfigurationObject) => {
     *          sharedDataService.set('defaultToolingLanguage', conf.defaultToolingLanguage);
     *      });
     *
     * @returns a promise of configuration values as an object of mapped configuration key/value pairs
     */
    loadAsObject(): Promise<ConfigurationObject>;
    private _convertToObject;
    private _getLocation;
    private _parse;
}
