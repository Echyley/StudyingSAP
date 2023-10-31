/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import * as lodash from 'lodash';
import {
    CONFIGURATION_URI,
    IRestService,
    ISharedDataService,
    LogService,
    OperationContextRegistered,
    PromiseUtils,
    RestServiceFactory
} from 'smarteditcommons';
import { Configuration, ConfigurationItem, ConfigurationObject } from './Configuration';

/**
 * LoadConfigManagerService is used to retrieve configurations stored in configuration API.
 */
@OperationContextRegistered(CONFIGURATION_URI, 'TOOLING')
export class LoadConfigManagerService {
    private restService: IRestService<ConfigurationItem>;

    constructor(
        restServicefactory: RestServiceFactory,
        private sharedDataService: ISharedDataService,
        private logService: LogService,
        private promiseUtils: PromiseUtils,
        @Inject('SMARTEDIT_RESOURCE_URI_REGEXP') private SMARTEDIT_RESOURCE_URI_REGEXP: RegExp,
        @Inject('SMARTEDIT_ROOT') private SMARTEDIT_ROOT: string
    ) {
        this.restService = restServicefactory.get<ConfigurationItem>(CONFIGURATION_URI);
    }

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
    loadAsArray(): Promise<ConfigurationItem[]> {
        const deferred = this.promiseUtils.defer<ConfigurationItem[]>();
        this.restService.query().then(
            (response: ConfigurationItem[]) => {
                deferred.resolve(this._parse(response));
            },
            (error: any) => {
                this.logService.log('Fail to load the configurations.', error);
                deferred.reject();
            }
        );
        return deferred.promise;
    }

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
    loadAsObject(): Promise<ConfigurationObject> {
        const deferred = this.promiseUtils.defer<ConfigurationObject>();
        this.loadAsArray().then((response) => {
            try {
                const conf = this._convertToObject(response);
                this.sharedDataService.set('configuration', conf);
                deferred.resolve(conf);
            } catch (e) {
                this.logService.error(
                    'LoadConfigManager.loadAsObject - _convertToObject failed to load configuration:',
                    response
                );
                this.logService.error(e);
                deferred.reject();
            }
        });
        return deferred.promise;
    }

    private _convertToObject(configuration: Configuration): ConfigurationObject {
        const configurations = configuration.reduce(
            (previous: ConfigurationObject, current: ConfigurationItem) => {
                try {
                    if (typeof previous[current.key] !== 'undefined') {
                        this.logService.error(
                            'LoadConfigManager._convertToObject() - duplicate configuration keys found: ' +
                                current.key
                        );
                    }
                    previous[current.key] = JSON.parse(current.value);
                } catch (parseError) {
                    this.logService.error(
                        'item _key_ from configuration contains unparsable JSON data _value_ and was ignored'
                            .replace('_key_', current.key)
                            .replace('_value_', current.value)
                    );
                }
                return previous;
            },
            {} as ConfigurationObject
        );

        try {
            configurations.domain = this.SMARTEDIT_RESOURCE_URI_REGEXP.exec(this._getLocation())[1];
        } catch (e) {
            throw new Error(
                `location ${this._getLocation()} doesn't match the expected pattern ${
                    this.SMARTEDIT_RESOURCE_URI_REGEXP
                }`
            );
        }
        configurations.smarteditroot = configurations.domain + '/' + this.SMARTEDIT_ROOT;
        return configurations;
    }

    private _getLocation(): string {
        return document.location.href;
    }

    // FIXME: weird on an array and useless
    private _parse(configuration: ConfigurationItem[]): ConfigurationItem[] {
        const conf = lodash.cloneDeep(configuration);
        Object.keys(conf).forEach((key) => {
            try {
                (conf as any)[key] = JSON.parse((conf as any)[key]);
            } catch (e) {
                //
            }
        });
        return conf;
    }
}
