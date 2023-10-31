/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { ProductDropdownPopulator } from 'cmssmarteditcontainer/components/genericEditor/dropdownPopulators';
import { ContextAwareCatalogService } from 'cmssmarteditcontainer/services';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorItemPayload,
    DropdownPopulatorPagePayload,
    LanguageService,
    UriDropdownPopulator
} from 'smarteditcommons';

describe('ProductDropdownPopulator', () => {
    const SEARCH_URI_MOCK = 'search/uri';
    const ITEM_URI_MOCK = 'product/resource/api/uri';

    const PRODUCTS_MOCK = [
        {
            id: '1',
            name: 'test'
        },
        {
            id: '2',
            name: 'any product'
        }
    ];
    const currentCatalog = {
        catalogVersion: 'Online',
        catalogId: 1
    };
    const PAGE_PAYLOAD_MOCK = {
        search: '',
        pageSize: 10,
        currentPage: 1,
        model: {
            productCatalog: currentCatalog.catalogId
        },
        field: {
            uri: undefined
        }
    } as unknown as DropdownPopulatorPagePayload;
    const PAGE_RESPONSE_MOCK: DropdownPopulatorFetchPageResponse<any> = {
        field: null,
        pagination: null,
        results: PRODUCTS_MOCK
    };
    const ITEM_PAYLOAD_MOCK = {
        search: '',
        pageSize: 10,
        currentPage: 1,
        field: {
            idAttribute: 'id',
            labelAttributes: ['name'],
            uri: undefined
        }
    } as unknown as DropdownPopulatorItemPayload;

    let contextAwareCatalogService: jasmine.SpyObj<ContextAwareCatalogService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let uriDropdownPopulator: jasmine.SpyObj<UriDropdownPopulator>;

    let service: ProductDropdownPopulator;
    beforeEach(() => {
        contextAwareCatalogService = jasmine.createSpyObj<ContextAwareCatalogService>(
            'contextAwareCatalogService',
            ['getProductSearchUri', 'getProductItemUri']
        );

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        uriDropdownPopulator = jasmine.createSpyObj<UriDropdownPopulator>('uriDropdownPopulator', [
            'fetchPage',
            'getItem'
        ]);

        service = new ProductDropdownPopulator(
            contextAwareCatalogService,
            languageService,
            translateService,
            uriDropdownPopulator
        );
    });

    beforeEach(() => {
        contextAwareCatalogService.getProductSearchUri.and.returnValue(
            Promise.resolve(SEARCH_URI_MOCK)
        );
        contextAwareCatalogService.getProductItemUri.and.returnValue(
            Promise.resolve(ITEM_URI_MOCK)
        );

        uriDropdownPopulator.fetchPage.and.returnValue(Promise.resolve(PAGE_RESPONSE_MOCK));
        uriDropdownPopulator.getItem.and.returnValue(Promise.resolve(PRODUCTS_MOCK[0]));
    });

    it('should be paged', () => {
        expect(service.isPaged()).toBe(true);
    });

    it('should be able to fetch items per page with expected parameters', async () => {
        const page = await service.fetchPage(PAGE_PAYLOAD_MOCK);

        expect(contextAwareCatalogService.getProductSearchUri).toHaveBeenCalledWith(
            PAGE_PAYLOAD_MOCK.model.productCatalog as any
        );

        const expectedPayload = PAGE_PAYLOAD_MOCK;
        expectedPayload.field.uri = SEARCH_URI_MOCK;
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(expectedPayload);

        expect(page).toEqual(PAGE_RESPONSE_MOCK);
    });

    it('should be able to fetch item by id', async () => {
        const item = await service.getItem(ITEM_PAYLOAD_MOCK);

        const expectedPayload = ITEM_PAYLOAD_MOCK;
        expectedPayload.field.uri = SEARCH_URI_MOCK;
        expect(uriDropdownPopulator.getItem).toHaveBeenCalledWith(expectedPayload);

        expect(item).toBe(PRODUCTS_MOCK[0]);
    });
});
