/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { CategoryDropdownPopulator } from 'cmssmarteditcontainer/components/genericEditor/dropdownPopulators';
import { ContextAwareCatalogService } from 'cmssmarteditcontainer/services';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorPagePayload,
    LanguageService,
    UriDropdownPopulator,
    functionsUtils
} from 'smarteditcommons';

describe('CategoryDropdownPopulator', () => {
    const SEARCH_URI_MOCK = 'search/uri/mock';
    const ITEM_URI_MOCK = 'item/uri/mock';

    const PRODUCT_CATEGORIES_MOCK = [
        {
            id: '1',
            name: 'test'
        },
        {
            id: '2',
            name: 'any category'
        }
    ];

    let PAYLOAD_MOCK: DropdownPopulatorPagePayload;

    const PAGE_RESPONSE_MOCK: DropdownPopulatorFetchPageResponse = {
        field: null,
        pagination: null,
        results: PRODUCT_CATEGORIES_MOCK
    };

    const ITEM_RESPONSE_MOCK = PRODUCT_CATEGORIES_MOCK[0];

    let contextAwareCatalogService: jasmine.SpyObj<ContextAwareCatalogService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let uriDropdownPopulator: jasmine.SpyObj<UriDropdownPopulator>;

    let service: CategoryDropdownPopulator;
    beforeEach(() => {
        contextAwareCatalogService = jasmine.createSpyObj<ContextAwareCatalogService>(
            'contextAwareCatalogService',
            ['getProductCategorySearchUri', 'getProductCategoryItemUri']
        );

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        uriDropdownPopulator = jasmine.createSpyObj<UriDropdownPopulator>('uriDropdownPopulator', [
            'fetchPage',
            'getItem'
        ]);

        service = new CategoryDropdownPopulator(
            contextAwareCatalogService,
            languageService,
            translateService,
            uriDropdownPopulator
        );
    });

    beforeEach(() => {
        PAYLOAD_MOCK = {
            field: {
                idAttribute: 'id',
                labelAttributes: ['name'],
                params: {
                    langIsoCode: undefined
                },
                uri: undefined
            },
            model: {
                productCatalog: '1'
            },
            selection: {
                label: 'Category #1',
                value: 'category_1'
            },
            search: '',
            pageSize: 10,
            currentPage: 1
        } as unknown as DropdownPopulatorPagePayload;

        contextAwareCatalogService.getProductCategorySearchUri.and.returnValue(
            Promise.resolve(SEARCH_URI_MOCK)
        );
        contextAwareCatalogService.getProductCategoryItemUri.and.returnValue(
            Promise.resolve(ITEM_URI_MOCK)
        );

        languageService.getResolveLocale.and.returnValue(Promise.resolve('en'));

        uriDropdownPopulator.fetchPage.and.returnValue(Promise.resolve(PAGE_RESPONSE_MOCK));
        uriDropdownPopulator.getItem.and.returnValue(Promise.resolve(ITEM_RESPONSE_MOCK));
    });

    it('should be paged', () => {
        expect(service.isPaged()).toBe(true);
    });

    it('should be able to fetch items per page with expected parameters', async () => {
        const page = await service.fetchPage(PAYLOAD_MOCK);

        expect(contextAwareCatalogService.getProductCategorySearchUri).toHaveBeenCalledWith(
            PAYLOAD_MOCK.model.productCatalog as any
        );

        const expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = SEARCH_URI_MOCK;
        expectedPayload.field.params.langIsoCode = 'en';
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);

        expect(page).toEqual(PAGE_RESPONSE_MOCK);
    });

    it('GIVEN productCatalog is not provided WHEN fetchPage is called THEN it rejects', async () => {
        delete PAYLOAD_MOCK.model.productCatalog;

        try {
            await service.fetchPage(PAYLOAD_MOCK);
            functionsUtils.assertFail();
        } catch (error) {
            expect(error).toEqual(
                new Error('"productCatalog" is required but it was not provided.')
            );
        }
    });

    it('should be able to fetch item by id', async () => {
        const item = await service.getItem(PAYLOAD_MOCK);

        expect(contextAwareCatalogService.getProductCategoryItemUri).toHaveBeenCalled();

        const expectedPayload = PAYLOAD_MOCK;
        expectedPayload.field.uri = ITEM_URI_MOCK;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);

        expect(item).toEqual(ITEM_RESPONSE_MOCK);
    });
});
