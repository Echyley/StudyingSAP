/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { APP_INITIALIZER, ComponentRef, ModuleWithProviders, NgModule } from '@angular/core';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { IStorageService } from '../../interfaces';
import { BrowserService } from '../../services/browser';
import { ITranslationsFetchService } from './i-translations-fetch.service';
import { TranslateHttpLoader } from './translate-http.loader';

@NgModule({
    imports: [
        TranslateModule.forRoot({
            isolate: false,
            loader: {
                provide: TranslateLoader,
                useClass: TranslateHttpLoader
            }
        })
    ],
    exports: [TranslateModule]
})
export class TranslationModule {
    static forChild(): ModuleWithProviders<TranslateModule> {
        return TranslateModule.forChild({
            isolate: false,
            loader: {
                provide: TranslateLoader,
                useClass: TranslateHttpLoader
            }
        });
    }

    static forRoot(
        TranslationsFetchServiceClass: new (...args: any[]) => ITranslationsFetchService
    ): ModuleWithProviders<TranslationModule> {
        return {
            ngModule: TranslationModule,
            providers: [
                {
                    provide: ITranslationsFetchService,
                    useClass: TranslationsFetchServiceClass
                },
                {
                    provide: APP_INITIALIZER,
                    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                    useFactory(
                        translate: TranslateService,
                        storageService: IStorageService,
                        browserService: BrowserService
                    ) {
                        storageService
                            .getValueFromLocalStorage('SELECTED_LANGUAGE', false)
                            .then(
                                (lang: { name: string; isoCode: string }) =>
                                    lang ? lang.isoCode : browserService.getBrowserLocale(),
                                () => browserService.getBrowserLocale()
                            )
                            .then((lang: string) => {
                                translate.setDefaultLang(lang);
                                translate.use(lang);
                            });

                        // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
                        return (component: ComponentRef<any>) => {
                            // an initializer useFactory must return a function
                        };
                    },
                    deps: [TranslateService, IStorageService, BrowserService],
                    multi: true
                }
            ]
        };
    }
}
