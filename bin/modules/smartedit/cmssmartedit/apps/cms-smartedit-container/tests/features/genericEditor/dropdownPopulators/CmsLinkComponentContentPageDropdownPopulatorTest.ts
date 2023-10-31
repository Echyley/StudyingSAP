/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { CmsLinkComponentContentPageDropdownPopulator } from 'cmssmarteditcontainer/components/genericEditor/dropdownPopulators';
import { ContextAwareCatalogService } from 'cmssmarteditcontainer/services';
import {
    DropdownPopulatorPagePayload,
    LanguageService,
    UriDropdownPopulator
} from 'smarteditcommons';

describe('CmsLinkComponentContentPageDropdownPopulator', () => {
    const SEARCH_URI_MOCK = 'search/uri';
    const ITEM_URI_MOCK = 'product/resource/api/uri';
    const PAGES_MOCKS = [
        {
            id: 1,
            name: 'test'
        },
        {
            id: 2,
            name: 'any page'
        }
    ];
    const PAYLOAD_MOCK = {
        field: {
            idAttribute: 'id',
            labelAttributes: ['name'],
            uri: undefined
        }
    } as DropdownPopulatorPagePayload;

    let contextAwareCatalogService: jasmine.SpyObj<ContextAwareCatalogService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let uriDropdownPopulator: jasmine.SpyObj<UriDropdownPopulator>;

    let service: CmsLinkComponentContentPageDropdownPopulator;
    beforeEach(() => {
        contextAwareCatalogService = jasmine.createSpyObj<ContextAwareCatalogService>(
            'contextAwareCatalogService',
            ['getContentPageSearchUri', 'getContentPageItemUri']
        );

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        uriDropdownPopulator = jasmine.createSpyObj<UriDropdownPopulator>('uriDropdownPopulator', [
            'fetchPage',
            'getItem'
        ]);

        service = new CmsLinkComponentContentPageDropdownPopulator(
            contextAwareCatalogService,
            languageService,
            translateService,
            uriDropdownPopulator
        );
    });

    beforeEach(() => {
        contextAwareCatalogService.getContentPageSearchUri.and.returnValue(
            Promise.resolve(SEARCH_URI_MOCK)
        );
        contextAwareCatalogService.getContentPageItemUri.and.returnValue(
            Promise.resolve(ITEM_URI_MOCK)
        );

        uriDropdownPopulator.getItem.and.returnValue(Promise.resolve<any>(PAGES_MOCKS[0]));
    });

    it('should be paged', () => {
        expect(service.isPaged()).toBe(true);
    });

    it('should be able to fetch all items with expected parameters', async () => {
        // WHEN
        await service.fetchPage(PAYLOAD_MOCK);

        // THEN
        expect(contextAwareCatalogService.getContentPageSearchUri).toHaveBeenCalled();

        const expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = SEARCH_URI_MOCK;
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);
    });

    it('should be able to fetch item by id', async () => {
        // WHEN
        await service.getItem(PAYLOAD_MOCK);

        // THEN
        expect(contextAwareCatalogService.getContentPageItemUri).toHaveBeenCalled();

        const expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = ITEM_URI_MOCK;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);
    });
});
