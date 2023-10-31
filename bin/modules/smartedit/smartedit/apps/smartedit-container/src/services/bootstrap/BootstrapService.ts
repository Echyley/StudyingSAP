/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpClient } from '@angular/common/http';
import { Inject } from '@angular/core';
import * as lo from 'lodash';
import {
    scriptUtils,
    BootstrapPayload,
    Cloneable,
    ISharedDataService,
    LogService,
    ModuleUtils,
    PromiseUtils,
    SmarteditBootstrapGateway,
    TypedMap
} from 'smarteditcommons';
import { ConfigurationObject } from 'smarteditcontainer/services/bootstrap/Configuration';
import {
    ConfigurationModules,
    Module
} from 'smarteditcontainer/services/bootstrap/ConfigurationModules';
import { SmarteditBundle } from 'smarteditcontainer/services/bootstrap/SmarteditBundle';
import { ThemeSwitchService } from '../theme/ThemeSwitchService';
import { ConfigurationExtractorService } from './ConfigurationExtractorService';

declare let E2E_ENVIRONMENT: boolean;

export class BootstrapService {
    constructor(
        private configurationExtractorService: ConfigurationExtractorService,
        private sharedDataService: ISharedDataService,
        private logService: LogService,
        private httpClient: HttpClient,
        private promiseUtils: PromiseUtils,
        private smarteditBootstrapGateway: SmarteditBootstrapGateway,
        private moduleUtils: ModuleUtils,
        @Inject('SMARTEDIT_INNER_FILES') private SMARTEDIT_INNER_FILES: string[],
        @Inject('SMARTEDIT_INNER_FILES_POST') private SMARTEDIT_INNER_FILES_POST: string[]
    ) {}

    bootstrapContainerModules(configurations: ConfigurationObject): Promise<BootstrapPayload> {
        const deferred = this.promiseUtils.defer<BootstrapPayload>();
        const seContainerModules: ConfigurationModules =
            this.configurationExtractorService.extractSEContainerModules(configurations);
        const orderedApplications = this.configurationExtractorService.orderApplications(
            seContainerModules.applications
        );

        this.logService.debug('outerAppLocations are:', orderedApplications);

        this.sharedDataService.set('authenticationMap', seContainerModules.authenticationMap);
        this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);

        const constants = this._getConstants(configurations);
        this._getValidApplications(orderedApplications).then((validApplications: Module[]) => {
            scriptUtils.injectJS().execute({
                srcs: validApplications.map((app) => app.location),
                callback: () => {
                    const modules = [...window.__smartedit__.pushedModules];

                    // The original validApplications contains the list of modules that must be loaded dynamically as determined by
                    // the SmartEdit Configuration service; this is the legacy way of loading extensions.
                    // However, now extensions can also be loaded at compilation time. The code of those extensions is not required to be
                    // loaded dynamically, but it's still necessary to load their Angular top-level module. Those modules are defined in
                    // the smartEditContainerAngularApps global variable.
                    window.__smartedit__.smartEditContainerAngularApps.forEach(
                        (appName: string) => {
                            validApplications.push({
                                name: appName,
                                location: ''
                            });
                        }
                    );

                    validApplications.forEach((app) => {
                        const esModule = this.moduleUtils.getNgModule(app.name);
                        if (esModule) {
                            modules.push(esModule);
                        }
                    });
                    deferred.resolve({
                        modules,
                        constants
                    });
                }
            });
        });

        return deferred.promise;
    }
    /**
     * Retrieve SmartEdit inner application configuration and dispatch 'bundle' event with list of resources.
     * @param configurations
     */
    bootstrapSEApp(configurations: ConfigurationObject): Promise<void> {
        const seModules: ConfigurationModules =
            this.configurationExtractorService.extractSEModules(configurations);
        const orderedApplications = this.configurationExtractorService.orderApplications(
            seModules.applications
        );

        this.sharedDataService.set('authenticationMap', seModules.authenticationMap);
        this.sharedDataService.set('credentialsMap', configurations['authentication.credentials']);
        const resources = {
            properties: this._getConstants(configurations),
            js: [
                {
                    src:
                        configurations.smarteditroot +
                        '/static-resources/thirdparties/blockumd/blockumd.js'
                },
                {
                    src:
                        configurations.smarteditroot +
                        '/static-resources/dist/smartedit-new/vendors.js'
                },
                {
                    src:
                        configurations.smarteditroot +
                        '/static-resources/thirdparties/ckeditor/ckeditor.js'
                },
                {
                    src:
                        configurations.smarteditroot +
                        '/static-resources/dist/smartedit-new/smartedit.js'
                }
            ],
            css: [
                configurations.smarteditroot + ThemeSwitchService.cssStringUrl,
                configurations.smarteditroot + ThemeSwitchService.cssCustomStringUrl,
                configurations.smarteditroot + '/static-resources/dist/smartedit-new/main.css'
            ]
        } as SmarteditBundle;

        return this._getValidApplications(orderedApplications).then(
            (validApplications: Module[]) => {
                // The original validApplications contains the list of modules that must be loaded dynamically as determined by
                // the SmartEdit Configuration service; this is the legacy way of loading extensions.
                // However, now extensions can also be loaded at compilation time. The code of those extensions is not required to be
                // loaded dynamically, but it's still necessary to load their Angular top-level module. Those modules are defined in
                // the smartEditInnerAngularApps global variable.
                window.__smartedit__.smartEditInnerAngularApps.forEach((appName: string) => {
                    validApplications.push({
                        name: appName,
                        location: ''
                    });
                });

                if (E2E_ENVIRONMENT && this.SMARTEDIT_INNER_FILES) {
                    // Note: This is only enabled on e2e tests. In production, this is removed by webpack.
                    resources.js = this.SMARTEDIT_INNER_FILES.map((filePath: string) => ({
                        src: configurations.domain + filePath
                    }));
                }

                resources.js = resources.js.concat(
                    validApplications.map((app) => {
                        const source = { src: app.location };
                        return source;
                    })
                );

                if (E2E_ENVIRONMENT && this.SMARTEDIT_INNER_FILES_POST) {
                    // Note: This is only enabled on e2e tests. In production, this is removed by webpack.
                    resources.js = resources.js.concat(
                        this.SMARTEDIT_INNER_FILES_POST.map((filePath: string) => ({
                            src: configurations.domain + filePath
                        }))
                    );
                }

                resources.properties.applications = validApplications.map((app) => app.name);
                this.smarteditBootstrapGateway
                    .getInstance()
                    .publish('bundle', { resources } as unknown as Cloneable);
            }
        );
    }

    private _getConstants(configurations: ConfigurationObject): TypedMap<string> {
        return {
            domain: configurations.domain as string,
            smarteditroot: configurations.smarteditroot as string
        };
    }
    /**
     * Applications are considered valid if they can be retrieved over the wire
     */
    private _getValidApplications(applications: Module[]): Promise<Module[]> {
        return Promise.all(
            applications.map((application) => {
                const deferred = this.promiseUtils.defer<Module>();
                this.httpClient.get(application.location, { responseType: 'text' }).subscribe(
                    () => {
                        deferred.resolve(application);
                    },
                    (e) => {
                        this.logService.error(
                            `Failed to load application '${application.name}' from location ${application.location}; SmartEdit functionality may be compromised.`
                        );
                        deferred.resolve();
                    }
                );
                return deferred.promise;
            })
        ).then(
            (validApplications: Module[]) => lo.merge(lo.compact(validApplications)) as Module[]
        );
    }
}
