/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageType, PageTypeService } from 'cmssmarteditcontainer/dao/PageTypeService';
import { IRestService, RestServiceFactory, CMSPageTypes } from 'smarteditcommons';

describe('PageTypeService', () => {
    const mockResponse = {
        pageTypes: [
            {
                code: CMSPageTypes.ContentPage,
                name: {
                    en: 'Content Page - en',
                    fr: 'Content Page - fr'
                },
                description: {
                    en: 'Description for content page - en',
                    fr: 'Description for content page - fr'
                },
                type: 'contentPageTypeData'
            },
            {
                code: CMSPageTypes.ProductPage,
                name: {
                    en: 'Product Page - en',
                    fr: 'Product Page - fr'
                },
                description: {
                    en: 'Description for product page - en',
                    fr: 'Description for product page - fr'
                },
                type: 'productPageTypeData'
            }
        ] as PageType[]
    };

    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;

    let pageTypeRestService: jasmine.SpyObj<IRestService<any>>;

    let pageTypeService: PageTypeService;
    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        pageTypeRestService = jasmine.createSpyObj<IRestService<any>>('restService', ['get']);
        restServiceFactory.get.and.returnValue(pageTypeRestService);

        pageTypeService = new PageTypeService(restServiceFactory);
    });

    beforeEach(() => {
        pageTypeRestService.get.and.returnValue(Promise.resolve(mockResponse));
    });

    it('GIVEN that the page types are request multiple times THEN all subsequent request should read from the cache', () => {
        pageTypeService.getPageTypes();
        pageTypeService.getPageTypes();
        expect(pageTypeRestService.get.calls.count()).toBe(1);
    });

    it('GIVEN that the page types are requested, a promise is returned containing an array of Page Type IDs', async () => {
        const result = await pageTypeService.getPageTypes();
        expect(result).toEqual(mockResponse.pageTypes);
    });
});
