import { HttpClient } from '@angular/common/http';
import { BootstrapPayload, ISharedDataService, LogService, ModuleUtils, PromiseUtils, SmarteditBootstrapGateway } from 'smarteditcommons';
import { ConfigurationObject } from 'smarteditcontainer/services/bootstrap/Configuration';
import { ConfigurationExtractorService } from './ConfigurationExtractorService';
export declare class BootstrapService {
    private configurationExtractorService;
    private sharedDataService;
    private logService;
    private httpClient;
    private promiseUtils;
    private smarteditBootstrapGateway;
    private moduleUtils;
    private SMARTEDIT_INNER_FILES;
    private SMARTEDIT_INNER_FILES_POST;
    constructor(configurationExtractorService: ConfigurationExtractorService, sharedDataService: ISharedDataService, logService: LogService, httpClient: HttpClient, promiseUtils: PromiseUtils, smarteditBootstrapGateway: SmarteditBootstrapGateway, moduleUtils: ModuleUtils, SMARTEDIT_INNER_FILES: string[], SMARTEDIT_INNER_FILES_POST: string[]);
    bootstrapContainerModules(configurations: ConfigurationObject): Promise<BootstrapPayload>;
    /**
     * Retrieve SmartEdit inner application configuration and dispatch 'bundle' event with list of resources.
     * @param configurations
     */
    bootstrapSEApp(configurations: ConfigurationObject): Promise<void>;
    private _getConstants;
    /**
     * Applications are considered valid if they can be retrieved over the wire
     */
    private _getValidApplications;
}
