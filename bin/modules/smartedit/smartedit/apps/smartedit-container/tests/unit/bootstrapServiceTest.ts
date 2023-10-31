/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import {
    promiseUtils,
    scriptUtils,
    ISharedDataService,
    LogService,
    MessageGateway,
    ModuleUtils,
    SmarteditBootstrapGateway
} from 'smarteditcommons';
import { BootstrapService } from 'smarteditcontainer/services';
import { ConfigurationObject } from 'smarteditcontainer/services/bootstrap/Configuration';
import { Module } from 'smarteditcontainer/services/bootstrap/ConfigurationModules';
import { ThemeSwitchService } from 'smarteditcontainer/services/theme/ThemeSwitchService';

describe('bootstrapService', () => {
    let bootstrapService: BootstrapService;
    let configurationExtractorService: any;
    let sharedDataService: ISharedDataService;
    let execute: jasmine.Spy;
    let logService: jasmine.SpyObj<LogService>;
    let httpClient: jasmine.SpyObj<HttpClient>;
    let messageGateway: jasmine.SpyObj<MessageGateway>;
    let smarteditBootstrapGateway: jasmine.SpyObj<SmarteditBootstrapGateway>;
    let moduleUtils: jasmine.SpyObj<ModuleUtils>;

    const configurations = {
        smarteditroot: 'smarteditroot1',
        domain: 'domain1',
        'authentication.credentials': {
            key2: 'value2'
        }
    } as ConfigurationObject;

    beforeEach(() => {
        moduleUtils = jasmine.createSpyObj<ModuleUtils>('moduleUtils', ['getNgModule']);
        moduleUtils.getNgModule.and.callFake((appName: string) => {
            if (appName === 'AppA') {
                return 'AppAModule' as any;
            }
            return null;
        });

        logService = jasmine.createSpyObj('logService', ['debug', 'error']);

        const successUriList = [
            'SELocationForAppZ',
            'SELocationForAppX',
            'SEForAppA',
            'SEForAppB',
            'SEForAppC',
            'SEForAppD',
            'SEContainerLocationForAppA',
            'SEContainerLocationForAppA',
            'SEContainerLocationForAppB',
            'SEContainerLocationForAppC',
            'SEContainerLocationForAppD'
        ];
        const failureUriList = ['SELocationForAppY'];

        httpClient = jasmine.createSpyObj<HttpClient>('httpClient', ['get']);
        httpClient.get.and.callFake(((uri: string) => {
            if (successUriList.includes(uri)) {
                return of('success');
            } else if (failureUriList.includes(uri)) {
                return throwError('failure');
            } else {
                throw new Error(`unexpected call to httpClient.get: ${uri}`);
            }
        }) as any);

        sharedDataService = jasmine.createSpyObj('sharedDataService', ['set', 'get']);

        configurationExtractorService = jasmine.createSpyObj('configurationExtractorService', [
            'extractSEContainerModules',
            'extractSEModules',
            'orderApplications'
        ]);
        ThemeSwitchService.cssStringUrl = '/123';
        ThemeSwitchService.cssCustomStringUrl = '/456';
        configurationExtractorService.orderApplications.and.callFake((apps: Module[]) => {
            apps.reverse();
            return apps;
        });

        configurationExtractorService.extractSEContainerModules.and.returnValue({
            applications: [
                { name: 'AppA', location: 'SEContainerLocationForAppA' },
                { name: 'AppB', location: 'SEContainerLocationForAppB' },
                { name: 'AppC', location: 'SEContainerLocationForAppC' },
                { name: 'AppY', location: 'SELocationForAppY' }
            ],
            authenticationMap: {
                key1: 'value1'
            }
        });

        configurationExtractorService.extractSEModules.and.returnValue({
            applications: [
                { name: 'AppX', location: 'SELocationForAppX' },
                { name: 'AppY', location: 'SELocationForAppY' },
                { name: 'AppZ', location: 'SELocationForAppZ' }
            ],
            authenticationMap: {
                key1: 'value1'
            }
        });

        execute = jasmine.createSpy('execute');
        execute.and.callFake((arg: { srcs: string[]; callback: () => void }) => {
            arg.callback();
        });

        messageGateway = jasmine.createSpyObj('messageGateway', ['publish']);

        spyOn(scriptUtils, 'injectJS').and.returnValue({ execute } as any);

        smarteditBootstrapGateway = jasmine.createSpyObj('smarteditBootstrapGateway', [
            'getInstance'
        ]);
        smarteditBootstrapGateway.getInstance.and.returnValue(messageGateway);

        bootstrapService = new BootstrapService(
            configurationExtractorService,
            sharedDataService,
            logService,
            httpClient,
            promiseUtils,
            smarteditBootstrapGateway,
            moduleUtils,
            [],
            []
        );
    });

    afterEach(() => {
        expect(configurationExtractorService.orderApplications).toHaveBeenCalled();
    });

    it(
        'calling bootstrapContainerModules will invoke extractSEContainerModules and inject the javascript sources,' +
            ' push the modules to smarteditcontainer module and re-bootstrap smarteditcontainer',
        async () => {
            const bootstrapPayload = await bootstrapService.bootstrapContainerModules(
                configurations
            );

            expect(bootstrapPayload.modules).toEqual(['AppAModule']);

            expect(execute).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    srcs: [
                        'SEContainerLocationForAppC',
                        'SEContainerLocationForAppB',
                        'SEContainerLocationForAppA'
                    ]
                })
            );

            expect(Object.keys(execute.calls.argsFor(0)[0]).length).toBe(2);

            expect(sharedDataService.set).toHaveBeenCalledWith('authenticationMap', {
                key1: 'value1'
            });
            expect(sharedDataService.set).toHaveBeenCalledWith('credentialsMap', {
                key2: 'value2'
            });
        }
    );

    it('outer applications will be sorted by means of extends keyword and applications extending unknown apps will be ignored', async () => {
        configurationExtractorService.extractSEContainerModules.and.returnValue({
            applications: [
                { name: 'AppB', location: 'SEContainerLocationForAppB' },
                { name: 'AppA', location: 'SEContainerLocationForAppA' }
            ]
        });

        const bootstrapPayload = await bootstrapService.bootstrapContainerModules(configurations);

        expect(bootstrapPayload.modules).toEqual(['AppAModule']);

        expect(execute).toHaveBeenCalledWith(
            jasmine.objectContaining({
                srcs: ['SEContainerLocationForAppA', 'SEContainerLocationForAppB']
            })
        );
    });

    it(
        'calling bootstrapSEApp will invoke extractSEModules and inject the javascript sources by means of postMessage' +
            ' and if the module fails to load it will not be injected as module AppY fails because of URL not found',
        async () => {
            await bootstrapService.bootstrapSEApp(configurations);

            expect(configurationExtractorService.extractSEModules).toHaveBeenCalledWith(
                configurations
            );

            expect(sharedDataService.set).toHaveBeenCalledWith('authenticationMap', {
                key1: 'value1'
            });
            expect(sharedDataService.set).toHaveBeenCalledWith('credentialsMap', {
                key2: 'value2'
            });

            expect(messageGateway.publish).toHaveBeenCalledWith('bundle', {
                resources: {
                    properties: {
                        domain: 'domain1',
                        smarteditroot: 'smarteditroot1',
                        applications: ['AppZ', 'AppX']
                    },
                    js: [
                        {
                            src: 'smarteditroot1/static-resources/thirdparties/blockumd/blockumd.js'
                        },
                        { src: 'smarteditroot1/static-resources/dist/smartedit-new/vendors.js' },
                        {
                            src: 'smarteditroot1/static-resources/thirdparties/ckeditor/ckeditor.js'
                        },
                        { src: 'smarteditroot1/static-resources/dist/smartedit-new/smartedit.js' },
                        { src: 'SELocationForAppZ' },
                        { src: 'SELocationForAppX' }
                    ],
                    css: [
                        'smarteditroot1/123',
                        'smarteditroot1/456',
                        'smarteditroot1/static-resources/dist/smartedit-new/main.css'
                    ]
                }
            });

            expect(logService.error).toHaveBeenCalledWith(
                "Failed to load application 'AppY' from location SELocationForAppY; SmartEdit functionality may be compromised."
            );
        }
    );

    it('inner applications will be sorted by means of extends keyword and applications extending unknown apps will be ignored', async () => {
        configurationExtractorService.extractSEModules.and.returnValue({
            applications: [
                { name: 'AppB', location: 'SEForAppB' },
                { name: 'AppA', location: 'SEForAppA' }
            ]
        });

        await bootstrapService.bootstrapSEApp(configurations);

        expect((messageGateway.publish as any).calls.argsFor(0)[1].resources.js).toEqual([
            { src: 'smarteditroot1/static-resources/thirdparties/blockumd/blockumd.js' },
            { src: 'smarteditroot1/static-resources/dist/smartedit-new/vendors.js' },
            { src: 'smarteditroot1/static-resources/thirdparties/ckeditor/ckeditor.js' },
            { src: 'smarteditroot1/static-resources/dist/smartedit-new/smartedit.js' },
            { src: 'SEForAppA' },
            { src: 'SEForAppB' }
        ]);
        expect((messageGateway.publish as any).calls.argsFor(0)[1].resources.css).toEqual([
            'smarteditroot1/123',
            'smarteditroot1/456',
            'smarteditroot1/static-resources/dist/smartedit-new/main.css'
        ]);
    });
});
