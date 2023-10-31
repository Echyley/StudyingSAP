import { LogService } from 'smarteditcommons';
import { ConfigurationObject } from './Configuration';
import { ConfigurationModules, Module } from './ConfigurationModules';
/** @internal */
export declare class ConfigurationExtractorService {
    private logService;
    constructor(logService: LogService);
    extractSEContainerModules(configurations: ConfigurationObject): ConfigurationModules;
    extractSEModules(configurations: ConfigurationObject): ConfigurationModules;
    orderApplications(applications: Module[]): Module[];
    private _addExtendingAppsInOrder;
    private _getAppsAndLocations;
}
