/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ModuleWithProviders } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { ITranslationsFetchService } from './i-translations-fetch.service';
export declare class TranslationModule {
    static forChild(): ModuleWithProviders<TranslateModule>;
    static forRoot(TranslationsFetchServiceClass: new (...args: any[]) => ITranslationsFetchService): ModuleWithProviders<TranslationModule>;
}
