/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { ProductCatalogDropdownPopulator } from 'cmssmarteditcontainer/components/genericEditor/dropdownPopulators';
import {
    DropdownPopulatorPayload,
    ICatalogService,
    LanguageService,
    OptionsDropdownPopulator
} from 'smarteditcommons';

describe('ProductCatalogDropdownPopulator', () => {
    const CATALOGS_MOCKS = [
        {
            catalogId: 'apparelProductCatalog',
            name: {
                en: 'Apparel Product Catalog',
                de: 'Produktkatalog Kleidung'
            },
            versions: [
                {
                    active: true,
                    uuid: 'apparelProductCatalog/Online',
                    version: 'Online'
                },
                {
                    active: false,
                    uuid: 'apparelProductCatalog/Staged',
                    version: 'Staged'
                }
            ]
        },
        {
            catalogId: 'apparelProductCatalog2',
            name: {
                en: 'Apparel Product Catalog2',
                de: 'Produktkatalog Kleidung2'
            },
            versions: [
                {
                    active: false,
                    uuid: 'apparelProductCatalog/Online2',
                    version: 'Online'
                },
                {
                    active: false,
                    uuid: 'apparelProductCatalog/Staged2',
                    version: 'Staged'
                }
            ]
        }
    ];

    const PAYLOAD_MOCK = {
        field: {
            idAttribute: 'catalogId',
            labelAttributes: ['name'],
            editable: true,
            propertyType: 'productCatalog'
        },
        options: undefined
    } as unknown as DropdownPopulatorPayload;

    let catalogService: jasmine.SpyObj<ICatalogService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let optionsDropdownPopulator: jasmine.SpyObj<OptionsDropdownPopulator>;
    let translateService: jasmine.SpyObj<TranslateService>;

    let service: ProductCatalogDropdownPopulator;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getProductCatalogsBySiteKey'
        ]);

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        optionsDropdownPopulator = jasmine.createSpyObj<OptionsDropdownPopulator>(
            'optionsDropdownPopulator',
            ['fetchAll']
        );

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        service = new ProductCatalogDropdownPopulator(
            catalogService,
            languageService,
            optionsDropdownPopulator,
            translateService
        );
    });

    beforeEach(() => {
        catalogService.getProductCatalogsBySiteKey.and.returnValue(
            Promise.resolve<any>(CATALOGS_MOCKS)
        );

        optionsDropdownPopulator.fetchAll.and.returnValue(Promise.resolve([]));
    });

    it('should not be paged', () => {
        expect(service.isPaged()).toBe(false);
    });

    it('should be able to fetch all items with expected parameters', async () => {
        await service.fetchAll(PAYLOAD_MOCK);

        PAYLOAD_MOCK.field.options = [...CATALOGS_MOCKS];
        expect(optionsDropdownPopulator.fetchAll).toHaveBeenCalledWith(PAYLOAD_MOCK);
    });
});
