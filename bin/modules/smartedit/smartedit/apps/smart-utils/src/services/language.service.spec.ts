/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import 'jasmine';
import { Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { IEventService, ILanguageServiceConstants, IStorageService } from '../interfaces';
import { coreAnnotationsHelper } from '../unit';
import { promiseUtils } from '../utils';
import { IToolingLanguage, LanguageService } from './language.service';
import { LogService } from './log.service';
import { IRestService, RestServiceFactory } from './rest';

describe('languageService', () => {
    const logService: jasmine.SpyObj<LogService> = jasmine.createSpyObj('log', [
        'error',
        'debug',
        'warn'
    ]);

    const translateService: jasmine.SpyObj<TranslateService> =
        jasmine.createSpyObj<TranslateService>('translateService', ['use', 'get']);
    let eventService: jasmine.SpyObj<IEventService>;
    let browserService: any;
    const constants: ILanguageServiceConstants = {
        I18N_LANGUAGES_RESOURCE_URI: 'I18N_LANGUAGES_RESOURCE_URI',
        LANGUAGE_RESOURCE_URI: 'LANGUAGE_RESOURCE_URI'
    };
    const storageService: jasmine.SpyObj<IStorageService> = jasmine.createSpyObj('storageService', [
        'getValueFromLocalStorage',
        'setValueInLocalStorage'
    ]);
    const restServiceFactory: jasmine.SpyObj<RestServiceFactory> =
        jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', ['get']);

    const i18nLanguageRestService: jasmine.SpyObj<
        IRestService<{
            languages: IToolingLanguage[];
        }>
    > = jasmine.createSpyObj<IRestService<{ languages: IToolingLanguage[] }>>(
        'languageRestService',
        ['get']
    );

    const SWITCH_LANGUAGE_EVENT = 'SWITCH_LANGUAGE_EVENT';
    const SELECTED_LANGUAGE = 'SELECTED_LANGUAGE';

    const MOCK_TOOLING_LANGUAGES: any = {
        languages: [
            {
                isoCode: 'en',
                name: 'English'
            },
            {
                isoCode: 'de',
                name: 'German'
            },
            {
                isoCode: 'pt_BR',
                name: 'Portuguese'
            }
        ]
    };
    const DEFAULT_BROWSER_LOCALE = 'pt_BR';
    const I18N_APP_NAME = 'se.application.name';
    const MOCK_APP_TITLE = 'SmartEdit Page Title';

    let languageService: LanguageService;
    let injector: jasmine.SpyObj<Injector>;

    beforeEach(() => {
        eventService = jasmine.createSpyObj('eventService', ['publish', 'subscribe']);
        browserService = jasmine.createSpyObj('browserService', ['getBrowserLocale']);
        browserService.getBrowserLocale.and.returnValue(DEFAULT_BROWSER_LOCALE);

        injector = jasmine.createSpyObj('injector', ['get']);

        restServiceFactory.get.and.returnValue(i18nLanguageRestService);
        injector.get.and.returnValue(restServiceFactory);

        (translateService.use as any).calls.reset();
        translateService.get.and.returnValue(of(MOCK_APP_TITLE));
        (translateService.get as any).calls.reset();

        coreAnnotationsHelper.init();

        languageService = new LanguageService(
            logService,
            translateService,
            promiseUtils,
            eventService,
            browserService,
            storageService,
            injector,
            constants
        );
    });

    it('GIVEN i18n REST call succeed WHEN requesting tooling languages THEN it receives a promise which contains a list of languages ', (done) => {
        i18nLanguageRestService.get.and.returnValue(Promise.resolve(MOCK_TOOLING_LANGUAGES));

        const promise = languageService.getToolingLanguages();

        promise.then((value) => {
            expect(value).toEqual(MOCK_TOOLING_LANGUAGES.languages);
            done();
        });
    });

    it('GIVEN i18n REST call fails WHEN requesting tooling languages THEN it receives a rejected promise', (done) => {
        i18nLanguageRestService.get.and.returnValue(Promise.reject('Error'));

        const promise = languageService.getToolingLanguages();

        promise.catch((error) => {
            expect(error).toBe('Error');
            done();
        });
    });

    it('GIVEN I have previously selected a locale (de), THEN I expect to get that locale (de)', (done) => {
        storageService.getValueFromLocalStorage.and.returnValue(
            Promise.resolve({
                name: 'German',
                isoCode: 'de'
            })
        );

        i18nLanguageRestService.get.and.returnValue(Promise.resolve(MOCK_TOOLING_LANGUAGES));

        const promise = languageService.getResolveLocale();

        promise.then((value) => {
            expect(value).toBe('de');
            done();
        });
    });

    it('GIVEN I have previously selected a locale (de), THEN I expect to get the iso code for that locale', (done) => {
        storageService.getValueFromLocalStorage.and.returnValue(
            Promise.resolve({
                name: 'German',
                isoCode: 'de'
            })
        );
        i18nLanguageRestService.get.and.returnValue(Promise.resolve(MOCK_TOOLING_LANGUAGES));

        const promise = languageService.getResolveLocale();

        promise.then((value) => {
            expect(value).toBe('de');
            done();
        });
    });

    it('GIVEN I have not previously selected a locale, THEN I expect to get the browser locale', (done) => {
        storageService.getValueFromLocalStorage.and.returnValue(Promise.resolve());
        i18nLanguageRestService.get.and.returnValue(Promise.resolve(MOCK_TOOLING_LANGUAGES));

        const promise = languageService.getResolveLocale();

        promise.then((value) => {
            expect(value).toBe(DEFAULT_BROWSER_LOCALE);
            done();
        });
    });

    it('GIVEN I have not previously selected a locale, THEN I expect to be able to resolve the browser locale iso code', (done) => {
        storageService.getValueFromLocalStorage.and.returnValue(Promise.resolve());
        i18nLanguageRestService.get.and.returnValue(Promise.resolve(MOCK_TOOLING_LANGUAGES));

        const promise = languageService.getResolveLocaleIsoCode();

        promise.then((value) => {
            expect(value).toEqual(DEFAULT_BROWSER_LOCALE.split('_')[0]);
            done();
        });
    });

    it('GIVEN I register for switching the language THEN it should subscribe to the gateway', () => {
        languageService.registerSwitchLanguage();
        expect(eventService.subscribe).toHaveBeenCalledWith(
            SWITCH_LANGUAGE_EVENT,
            jasmine.any(Function)
        );

        expect(translateService.use).not.toHaveBeenCalled();

        const callback = (eventService as any).subscribe.calls.argsFor(0)[1];

        callback('eventId', {
            isoCode: 'kl',
            name: 'ANY_NAME'
        } as IToolingLanguage);

        expect(translateService.use).toHaveBeenCalledWith('kl');
    });

    it('GIVEN I select a language THEN it should save the language in the cookie AND switch the language AND publish an event to the gateway', () => {
        const language = {
            name: 'German',
            isoCode: 'de'
        };

        languageService.setSelectedToolingLanguage(language);

        expect(storageService.setValueInLocalStorage).toHaveBeenCalledWith(
            SELECTED_LANGUAGE,
            language,
            false
        );
        expect(translateService.use).toHaveBeenCalledWith('de');
        expect(translateService.get).toHaveBeenCalledWith(I18N_APP_NAME);
        expect(document.title).toEqual(MOCK_APP_TITLE);

        expect(eventService.publish).toHaveBeenCalledWith(SWITCH_LANGUAGE_EVENT, {
            isoCode: language.isoCode
        });
    });

    it('GIVEN tag in BCP47 format WHEN convertBCP47TagToJavaTag is used THEN it is converted to java tag', () => {
        const bcp47Tag = 'en-US';

        const javaTag = languageService.convertBCP47TagToJavaTag(bcp47Tag);

        expect(javaTag).toEqual('en_US');
    });

    it('GIVEN tag in java format WHEN convertJavaTagToBCP47Tag is used THEN it is converted to BCP47 tag', () => {
        const javaTag = 'en_US';

        const bcp47Tag = languageService.convertJavaTagToBCP47Tag(javaTag);

        expect(bcp47Tag).toEqual('en-US');
    });
});
