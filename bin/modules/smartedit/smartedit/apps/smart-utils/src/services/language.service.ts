/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { Inject, Injectable, Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    EVENT_SERVICE,
    LANGUAGE_SERVICE_CONSTANTS,
    SELECTED_LANGUAGE,
    SWITCH_LANGUAGE_EVENT
} from '../constants';
import { Payload } from '../dtos';
import { IEventService, ILanguageServiceConstants, IStorageService } from '../interfaces';
import { PromiseUtils } from '../utils';
import { BrowserService } from './browser';
import { rarelyChangingContent, Cached } from './cache';
import { LogService } from './log.service';
import { IRestService, IRestServiceFactory, RestServiceFactory } from './rest';

/**
 * @ngdoc interface
 * @name @smartutils.interfaces:ILanguage
 * @description
 * Interface for language information
 */
export interface ILanguage extends Payload {
    active: boolean;
    isocode: string;
    name: string;
    nativeName: string;
    required: boolean;
}

export interface IToolingLanguage extends Payload {
    isoCode: string;
    name: string;
}

/**
 * @ngdoc object
 * @name resourceLocationsModule.object:LANGUAGE_RESOURCE_URI
 *
 * @description
 * Resource URI of the languages REST service.
 */

/**
 * @ngdoc service
 * @name @smartutils.services:LanguageService
 */

@Injectable()
export class LanguageService {
    constructor(
        protected logService: LogService,
        protected translateService: TranslateService,
        protected promiseUtils: PromiseUtils,
        @Inject(EVENT_SERVICE) protected eventService: IEventService,
        protected browserService: BrowserService,
        protected storageService: IStorageService,
        protected injector: Injector,
        @Inject(LANGUAGE_SERVICE_CONSTANTS)
        protected languageServiceConstants: ILanguageServiceConstants
    ) {}

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getToolingLanguages
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Retrieves a list of language descriptors using REST calls to the i18n API.
     *
     * @returns {Promise<IToolingLanguage[]>} A promise that resolves to an array of IToolingLanguage.
     */
    @Cached({ actions: [rarelyChangingContent] })
    getToolingLanguages(): Promise<IToolingLanguage[]> {
        return this.i18nLanguageRestService
            .get({})
            .then((response) => response.languages)
            .catch((error: any) => {
                this.logService.error(
                    'LanguageService.getToolingLanguages() - Error loading tooling languages'
                );
                return Promise.reject(error);
            });
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getBrowserLanguageIsoCode
     * @methodOf @smartutils.services:LanguageService
     *
     * @deprecated since 1808
     *
     * @description
     * Uses the browser's current locale to determine the selected language ISO code.
     *
     * @returns {String} The language ISO code of the browser's currently selected locale.
     */
    getBrowserLanguageIsoCode(): string {
        return window.navigator.language.split('-')[0];
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getBrowserLocale
     * @methodOf @smartutils.services:LanguageService
     *
     * @deprecated since 1808 - use browserService instead.
     *
     * @description
     * determines the browser locale in the format en_US
     *
     * @returns {string} the browser locale
     */
    getBrowserLocale(): string {
        return this.browserService.getBrowserLocale();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getResolveLocale
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale. It determines in the
     * following order:
     *
     * 1. Check if the user has previously selected the language
     * 2. Check if the user browser locale is supported in the system
     *
     * @returns {Promise<string>} the locale
     */
    getResolveLocale(): Promise<string> {
        return this._getDefaultLanguage();
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#getResolveLocaleIsoCode
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Resolve the user preference tooling locale ISO code. i.e.: If the selected tooling language is 'en_US',
     * the resolved value will be 'en'.
     *
     * @returns {Promise<string>} A promise that resolves to the isocode of the tooling language.
     */
    getResolveLocaleIsoCode(): Promise<string> {
        return this.getResolveLocale().then(
            (resolveLocale: string) => this.convertBCP47TagToJavaTag(resolveLocale).split('_')[0]
        );
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#setSelectedToolingLanguage
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Set the user preference language in the storage service
     *
     * @param {IToolingLanguage} language the language object to be saved.
     */
    setSelectedToolingLanguage(language: IToolingLanguage): void {
        this.storageService.setValueInLocalStorage(SELECTED_LANGUAGE, language, false);
        this.translateService.use(language.isoCode);
        this.setApplicationTitle();
        this.eventService.publish(SWITCH_LANGUAGE_EVENT, {
            isoCode: language.isoCode
        });
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#registerSwitchLanguage
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Register a callback function to the gateway in order to switch the tooling language
     */
    registerSwitchLanguage(): void {
        this.eventService.subscribe<IToolingLanguage>(
            SWITCH_LANGUAGE_EVENT,
            (eventId: string, language?: IToolingLanguage) => {
                if (this.translateService.currentLang !== language.isoCode) {
                    this.translateService.use(language.isoCode);
                }
            }
        );
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#convertBCP47TagToJavaTag
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Method converts the BCP47 language tag representing the locale to the default java representation.
     * For example, method converts "en-US" to "en_US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in java representation
     */
    convertBCP47TagToJavaTag(languageTag: string): string {
        return !!languageTag ? languageTag.replace(/-/g, '_') : languageTag;
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#convertJavaTagToBCP47Tag
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Method converts the default java language tag representing the locale to the BCP47 representation.
     * For example, method converts "en_US" to "en-US".
     *
     * @param {string} languageTag the language tag to be converted.
     *
     * @returns {string} the languageTag in BCP47 representation
     */
    convertJavaTagToBCP47Tag(languageTag: string): string {
        return !!languageTag ? languageTag.replace(/_/g, '-') : languageTag;
    }

    protected _getDefaultLanguage(): Promise<string> {
        return this.storageService.getValueFromLocalStorage(SELECTED_LANGUAGE, false).then(
            (lang: { name: string; isoCode: string }) =>
                lang ? lang.isoCode : this.browserService.getBrowserLocale(),
            () => this.browserService.getBrowserLocale()
        );
    }

    protected setApplicationTitle(): void {
        this.translateService.get('se.application.name').subscribe((pageTitle: string) => {
            document.title = pageTitle;
        });
    }

    protected get i18nLanguageRestService(): IRestService<{ languages: IToolingLanguage[] }> {
        return this.injector
            .get<IRestServiceFactory>(RestServiceFactory)
            .get<{ languages: IToolingLanguage[] }>(
                this.languageServiceConstants.I18N_LANGUAGES_RESOURCE_URI
            );
    }
}
