/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Payload } from '../dtos';
import { IEventService, ILanguageServiceConstants, IStorageService } from '../interfaces';
import { PromiseUtils } from '../utils';
import { BrowserService } from './browser';
import { LogService } from './log.service';
import { IRestService } from './rest';
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
export declare class LanguageService {
    protected logService: LogService;
    protected translateService: TranslateService;
    protected promiseUtils: PromiseUtils;
    protected eventService: IEventService;
    protected browserService: BrowserService;
    protected storageService: IStorageService;
    protected injector: Injector;
    protected languageServiceConstants: ILanguageServiceConstants;
    constructor(logService: LogService, translateService: TranslateService, promiseUtils: PromiseUtils, eventService: IEventService, browserService: BrowserService, storageService: IStorageService, injector: Injector, languageServiceConstants: ILanguageServiceConstants);
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
    getToolingLanguages(): Promise<IToolingLanguage[]>;
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
    getBrowserLanguageIsoCode(): string;
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
    getBrowserLocale(): string;
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
    getResolveLocale(): Promise<string>;
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
    getResolveLocaleIsoCode(): Promise<string>;
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
    setSelectedToolingLanguage(language: IToolingLanguage): void;
    /**
     * @ngdoc method
     * @name @smartutils.services:LanguageService#registerSwitchLanguage
     * @methodOf @smartutils.services:LanguageService
     *
     * @description
     * Register a callback function to the gateway in order to switch the tooling language
     */
    registerSwitchLanguage(): void;
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
    convertBCP47TagToJavaTag(languageTag: string): string;
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
    convertJavaTagToBCP47Tag(languageTag: string): string;
    protected _getDefaultLanguage(): Promise<string>;
    protected setApplicationTitle(): void;
    protected get i18nLanguageRestService(): IRestService<{
        languages: IToolingLanguage[];
    }>;
}
