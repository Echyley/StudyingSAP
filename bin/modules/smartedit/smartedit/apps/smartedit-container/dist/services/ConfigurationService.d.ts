import { NgForm } from '@angular/forms';
import { LogService, RestServiceFactory } from 'smarteditcommons';
import { ConfigurationItem } from 'smarteditcontainer/services/bootstrap/Configuration';
import { LoadConfigManagerService } from './bootstrap/LoadConfigManagerService';
/** @internal */
export declare class ConfigurationService {
    private logService;
    private loadConfigManagerService;
    private restServiceFactory;
    private readonly ABSOLUTE_URI_NOT_APPROVED;
    private readonly ABSOLUTE_URI_REGEX;
    private editorCRUDService;
    private configuration;
    private pristine;
    private loadCallback;
    constructor(logService: LogService, loadConfigManagerService: LoadConfigManagerService, restServiceFactory: RestServiceFactory);
    addEntry(): void;
    removeEntry(entry: ConfigurationItem, configurationForm: NgForm): void;
    filterConfiguration(): ConfigurationItem[];
    filterErrorConfiguration(): ConfigurationItem[];
    validateUserInput(entry: ConfigurationItem): void;
    submit(configurationForm: NgForm): Promise<any[]>;
    init(_loadCallback?: () => void): Promise<any>;
    private reconstructConfigurationEntry;
    private reset;
    private addError;
    private addKeyError;
    private addValueError;
    private prettify;
    /**
     * for editing purposes
     */
    private loadAndPresent;
    private isValid;
    private validate;
}
