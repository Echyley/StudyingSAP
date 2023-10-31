'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var http = require('@angular/common/http');
var core = require('@angular/core');
var forms = require('@angular/forms');
var platformBrowser = require('@angular/platform-browser');
var platformBrowserDynamic = require('@angular/platform-browser-dynamic');
var smarteditcommons = require('smarteditcommons');
var smarteditcontainer = require('smarteditcontainer');

/******************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */

function __decorate(decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
}

function __awaiter(thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
}

const legacyLoaderTagName = 'legacy-loader';
window.__smartedit__.addDecoratorPayload("Component", "SmarteditloaderComponent", {
    selector: smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME,
    template: `<${legacyLoaderTagName}></${legacyLoaderTagName}>`
});
let SmarteditloaderComponent = class SmarteditloaderComponent {
};
SmarteditloaderComponent = __decorate([
    core.Component({
        selector: smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME,
        template: `<${legacyLoaderTagName}></${legacyLoaderTagName}>`
    })
], SmarteditloaderComponent);

const SmarteditLoaderFactory = (modules) => {
    let Smarteditloader = class Smarteditloader {
    };
    Smarteditloader = __decorate([
        core.NgModule({
            schemas: [core.CUSTOM_ELEMENTS_SCHEMA],
            imports: [
                platformBrowser.BrowserModule,
                forms.FormsModule,
                forms.ReactiveFormsModule,
                http.HttpClientModule,
                smarteditcommons.SmarteditCommonsModule,
                smarteditcommons.SharedComponentsModule,
                ...modules,
                smarteditcontainer.AlertServiceModule,
                smarteditcommons.HttpInterceptorModule.forRoot(smarteditcommons.UnauthorizedErrorInterceptor, smarteditcommons.RetryInterceptor, smarteditcommons.ResourceNotFoundErrorInterceptor),
                smarteditcommons.SeTranslationModule.forRoot(smarteditcontainer.TranslationsFetchService)
            ],
            providers: [
                smarteditcommons.moduleUtils.provideValues({ SSO_CLIENT_ID: smarteditcommons.DEFAULT_AUTHENTICATION_CLIENT_ID }),
                smarteditcommons.SSOAuthenticationHelper,
                smarteditcontainer.ConfigurationExtractorService,
                smarteditcommons.ThemesService,
                smarteditcontainer.ThemeSwitchService,
                {
                    provide: core.ErrorHandler,
                    useClass: smarteditcommons.SmarteditErrorHandler
                },
                smarteditcontainer.DelegateRestService,
                smarteditcommons.RestServiceFactory,
                {
                    provide: smarteditcommons.IAuthenticationService,
                    useClass: smarteditcommons.AuthenticationService
                },
                {
                    provide: smarteditcommons.ISessionService,
                    useClass: smarteditcontainer.SessionService
                },
                {
                    provide: smarteditcommons.ISharedDataService,
                    useClass: smarteditcontainer.SharedDataService
                },
                {
                    provide: smarteditcommons.IStorageService,
                    useClass: smarteditcontainer.StorageService
                },
                smarteditcontainer.BootstrapService,
                smarteditcontainer.LoadConfigManagerService,
                smarteditcommons.moduleUtils.bootstrap((ssoAuthenticationHelper, loadConfigManagerService, bootstrapService, logService) => {
                    if (ssoAuthenticationHelper.isSSODialog()) {
                        ssoAuthenticationHelper.completeDialog();
                    }
                    else {
                        loadConfigManagerService
                            .loadAsObject()
                            .then((configurations) => {
                            bootstrapService
                                .bootstrapContainerModules(configurations)
                                .then((bootstrapPayload) => {
                                const smarteditloaderNode = document.querySelector(smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME);
                                smarteditloaderNode.parentNode.insertBefore(document.createElement(smarteditcommons.SMARTEDITCONTAINER_COMPONENT_NAME), smarteditloaderNode);
                                platformBrowserDynamic.platformBrowserDynamic()
                                    .bootstrapModule(smarteditcontainer.smarteditContainerFactory(bootstrapPayload), {
                                    ngZone: smarteditcommons.commonNgZone
                                })
                                    .then((ref) => {
                                    //
                                })
                                    .catch((err) => logService.error(err));
                            });
                        });
                    }
                }, [smarteditcommons.SSOAuthenticationHelper, smarteditcontainer.LoadConfigManagerService, smarteditcontainer.BootstrapService, smarteditcommons.LogService])
            ],
            declarations: [SmarteditloaderComponent],
            entryComponents: [SmarteditloaderComponent],
            bootstrap: [SmarteditloaderComponent]
        })
    ], Smarteditloader);
    return Smarteditloader;
};
const setGlobalBasePathURL = () => __awaiter(void 0, void 0, void 0, function* () {
    try {
        const settings = yield window.fetch(smarteditcommons.SETTINGS_URI);
        const settingsJSON = yield settings.json();
        if (settingsJSON['smartedit.globalBasePath']) {
            smarteditcommons.RestServiceFactory.setGlobalBasePath(String(settingsJSON['smartedit.globalBasePath']));
        }
    }
    catch (err) {
        console.log('Failure on loading Settings URL');
    }
    return Promise.resolve();
});
window.smarteditJQuery(document).ready(() => {
    if (!document.querySelector(smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME)) {
        document.body.appendChild(document.createElement(smarteditcommons.SMARTEDITLOADER_COMPONENT_NAME));
    }
    const modules = [...window.__smartedit__.pushedModules];
    setGlobalBasePathURL().then(() => {
        platformBrowserDynamic.platformBrowserDynamic()
            .bootstrapModule(SmarteditLoaderFactory(modules), { ngZone: smarteditcommons.commonNgZone })
            .catch(() => console.log('Failed on setting global basePath URL'));
    });
});

exports.ng = SmarteditLoaderFactory;
