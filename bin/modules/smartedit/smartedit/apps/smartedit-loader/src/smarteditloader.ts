/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useClass:false */
import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA, ErrorHandler, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import {
    commonNgZone,
    moduleUtils,
    nodeUtils,
    AuthenticationService,
    DEFAULT_AUTHENTICATION_CLIENT_ID,
    HttpInterceptorModule,
    IAuthenticationService,
    ISessionService,
    ISharedDataService,
    IStorageService,
    ResourceNotFoundErrorInterceptor,
    RestServiceFactory,
    RetryInterceptor,
    SeTranslationModule,
    SharedComponentsModule,
    SmarteditCommonsModule,
    SmarteditErrorHandler,
    SMARTEDITCONTAINER_COMPONENT_NAME,
    SETTINGS_URI,
    SMARTEDITLOADER_COMPONENT_NAME,
    SSOAuthenticationHelper,
    UnauthorizedErrorInterceptor,
    BootstrapPayload,
    LogService,
    ThemesService
} from 'smarteditcommons';
import {
    AlertServiceModule,
    ConfigurationExtractorService,
    DelegateRestService,
    LoadConfigManagerService,
    SessionService,
    SharedDataService,
    StorageService,
    TranslationsFetchService,
    BootstrapService,
    ConfigurationObject,
    smarteditContainerFactory,
    ThemeSwitchService
} from 'smarteditcontainer';
import { SmarteditloaderComponent } from './SmarteditloaderComponent';

export const SmarteditLoaderFactory = (modules: any[]): any => {
    @NgModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [
            BrowserModule,
            FormsModule,
            ReactiveFormsModule,
            HttpClientModule,
            SmarteditCommonsModule,
            SharedComponentsModule,
            ...modules,
            AlertServiceModule,
            HttpInterceptorModule.forRoot(
                UnauthorizedErrorInterceptor,
                RetryInterceptor,
                ResourceNotFoundErrorInterceptor
            ),
            SeTranslationModule.forRoot(TranslationsFetchService)
        ],
        providers: [
            moduleUtils.provideValues({ SSO_CLIENT_ID: DEFAULT_AUTHENTICATION_CLIENT_ID }),
            SSOAuthenticationHelper,
            ConfigurationExtractorService,
            ThemesService,
            ThemeSwitchService,
            {
                provide: ErrorHandler,
                useClass: SmarteditErrorHandler
            },
            DelegateRestService,
            RestServiceFactory,
            {
                provide: IAuthenticationService,
                useClass: AuthenticationService
            },
            {
                provide: ISessionService,
                useClass: SessionService
            },
            {
                provide: ISharedDataService,
                useClass: SharedDataService
            },
            {
                provide: IStorageService,
                useClass: StorageService
            },
            BootstrapService,
            LoadConfigManagerService,
            moduleUtils.bootstrap(
                (
                    ssoAuthenticationHelper: SSOAuthenticationHelper,
                    loadConfigManagerService: LoadConfigManagerService,
                    bootstrapService: BootstrapService,
                    logService: LogService
                ) => {
                    if (ssoAuthenticationHelper.isSSODialog()) {
                        ssoAuthenticationHelper.completeDialog();
                    } else {
                        loadConfigManagerService
                            .loadAsObject()
                            .then((configurations: ConfigurationObject) => {
                                bootstrapService
                                    .bootstrapContainerModules(configurations)
                                    .then((bootstrapPayload: BootstrapPayload) => {
                                        const smarteditloaderNode = document.querySelector(
                                            SMARTEDITLOADER_COMPONENT_NAME
                                        );
                                        smarteditloaderNode.parentNode.insertBefore(
                                            document.createElement(
                                                SMARTEDITCONTAINER_COMPONENT_NAME
                                            ),
                                            smarteditloaderNode
                                        );

                                        platformBrowserDynamic()
                                            .bootstrapModule(
                                                smarteditContainerFactory(bootstrapPayload),
                                                {
                                                    ngZone: commonNgZone
                                                }
                                            )
                                            .then((ref: any) => {
                                                //
                                            })
                                            .catch((err) => logService.error(err));
                                    });
                            });
                    }
                },
                [SSOAuthenticationHelper, LoadConfigManagerService, BootstrapService, LogService]
            )
        ],
        declarations: [SmarteditloaderComponent],
        entryComponents: [SmarteditloaderComponent],
        bootstrap: [SmarteditloaderComponent]
    })
    class Smarteditloader {}
    return Smarteditloader;
};

const setGlobalBasePathURL = async (): Promise<void> => {
    try {
        const settings = await window.fetch(SETTINGS_URI);
        const settingsJSON = await settings.json();
        if (settingsJSON['smartedit.globalBasePath']) {
            RestServiceFactory.setGlobalBasePath(String(settingsJSON['smartedit.globalBasePath']));
        }
    } catch (err) {
        console.log('Failure on loading Settings URL');
    }
    return Promise.resolve();
};

window.smarteditJQuery(document).ready(() => {
    if (!document.querySelector(SMARTEDITLOADER_COMPONENT_NAME)) {
        document.body.appendChild(document.createElement(SMARTEDITLOADER_COMPONENT_NAME));
    }

    const modules = [...window.__smartedit__.pushedModules];

    setGlobalBasePathURL().then(() => {
        platformBrowserDynamic()
            .bootstrapModule(SmarteditLoaderFactory(modules), { ngZone: commonNgZone })
            .catch(() => console.log('Failed on setting global basePath URL'));
    });
});
