/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ContextAwareCatalogService } from 'cmssmarteditcontainer/services';
import { ICatalogService, ISharedDataService } from 'smarteditcommons';

describe('ContextAwareCatalogService', () => {
    const catalogVersion = 'Online';
    const catalogId = 'testCatalogId';
    const CATEGORY_SEARCH_RESOURCE_URI =
        '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/categories';
    const ITEM_RESOURCE_URI = '/cmssmarteditwebservices/v1/sites/:siteUID/categories';
    const PRODUCT_LIST_RESOURCE_URI =
        '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products';
    const PAGE_LIST_RESOURCE_URI =
        '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/pages';

    let categorySearchResultUri: string;
    let categoryItemResultUri: string;
    let productListResultUri: string;
    let pageListResultUri: string;

    let catalogService: jasmine.SpyObj<ICatalogService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;

    let service: ContextAwareCatalogService;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getActiveProductCatalogVersionByCatalogId',
            'getActiveContentCatalogVersionByCatalogId'
        ]);

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        service = new ContextAwareCatalogService(catalogService, sharedDataService);
    });

    beforeEach(() => {
        catalogService.getActiveProductCatalogVersionByCatalogId.and.returnValue(
            Promise.resolve(catalogVersion)
        );
        catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue(
            Promise.resolve(catalogVersion)
        );

        sharedDataService.get.and.returnValue(
            Promise.resolve({
                catalogDescriptor: {
                    catalogId
                }
            })
        );

        categorySearchResultUri = CATEGORY_SEARCH_RESOURCE_URI.replace(
            /:catalogId/gi,
            catalogId
        ).replace(/:catalogVersion/gi, catalogVersion);

        categoryItemResultUri = ITEM_RESOURCE_URI.replace(/:siteUID/gi, 'CURRENT_CONTEXT_SITE_ID');

        productListResultUri = PRODUCT_LIST_RESOURCE_URI.replace(/:catalogId/gi, catalogId).replace(
            /:catalogVersion/gi,
            catalogVersion
        );

        pageListResultUri = PAGE_LIST_RESOURCE_URI.replace(
            /CURRENT_CONTEXT_CATALOG/gi,
            catalogId
        ).replace(/CURRENT_CONTEXT_CATALOG_VERSION/gi, catalogVersion);
    });

    it('should be able to return proper uri for product category list', async () => {
        expect(await service.getProductCategorySearchUri(catalogId)).toBe(categorySearchResultUri);
    });

    it('should be able to return proper uri for product category item', async () => {
        expect(await service.getProductCategoryItemUri()).toBe(categoryItemResultUri);
    });

    it('should be able to return proper uri for content page list', async () => {
        const pageSearchUri = pageListResultUri + '?typeCode=ContentPage';
        expect(await service.getContentPageSearchUri()).toBe(pageSearchUri);
    });

    it('should be able to return proper uri for content page item', async () => {
        expect(await service.getContentPageItemUri()).toBe(pageListResultUri);
    });

    it('should be able to return proper uri for product list', async () => {
        expect(await service.getProductSearchUri(catalogId)).toBe(productListResultUri);
    });

    it('should be able to return proper uri for product item', async () => {
        expect(await service.getProductItemUri()).toBe(
            '/cmssmarteditwebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/products'
        );
    });
});
