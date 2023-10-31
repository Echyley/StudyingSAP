/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ProductCategorySelectItem, ProductCategoryService } from 'cmssmarteditcontainer';
import { CatalogInformationService } from 'cmssmarteditcontainer/components/genericEditor/catalog/services';
import {
    ICatalogService,
    IExperience,
    ISharedDataService,
    ProductSelectItem
} from 'smarteditcommons';
import { IProductSearch, ProductService } from 'smarteditcontainer';

describe('CatalogInformationService', () => {
    const SITE_1_UID = 'some site uid';
    const SITE_2_UID = 'some site 2 uid';

    const experience = {
        siteDescriptor: {
            uid: SITE_1_UID
        }
    } as IExperience;

    const site1CatalogInfo = [
        {
            catalogId: 'catalog1',
            name: {
                en: 'Catalog 1'
            },
            versions: [
                {
                    version: 'version1_1'
                },
                {
                    version: 'version1_2'
                }
            ]
        },
        {
            catalogId: 'catalog2',
            name: {
                en: 'Catalog 2'
            },
            versions: [
                {
                    version: 'version2_1'
                },
                {
                    version: 'version2_2'
                }
            ]
        }
    ];

    const site2CatalogInfo = [
        {
            catalogId: 'catalog3',
            name: 'Catalog 3',
            versions: [
                {
                    version: 'version3_1'
                },
                {
                    version: 'version3_2'
                }
            ]
        },
        {
            catalogId: 'catalog4',
            name: 'Catalog 4',
            versions: [
                {
                    version: 'version4_1'
                },
                {
                    version: 'version4_2'
                }
            ]
        }
    ];

    const mask = 'some mask';
    const pageSize = 10;
    const currentPage = 0;

    const products = {
        pagination: null,
        products: [
            {
                uid: 'some product'
            }
        ]
    };

    const categories: any = {
        pagination: null,
        productCategories: [
            {
                uid: 'some category'
            }
        ]
    };

    let catalogService: jasmine.SpyObj<ICatalogService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let productCategoryService: jasmine.SpyObj<ProductCategoryService>;
    let productService: jasmine.SpyObj<ProductService>;

    let service: CatalogInformationService;
    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getProductCatalogsForSite'
        ]);

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        productCategoryService = jasmine.createSpyObj<ProductCategoryService>(
            'productCategoryService',
            ['getCategoryById', 'getCategories']
        );

        productService = jasmine.createSpyObj<ProductService>('productService', [
            'getProductById',
            'findProducts'
        ]);

        service = new CatalogInformationService(
            catalogService,
            sharedDataService,
            productCategoryService,
            productService
        );
    });

    beforeEach(() => {
        sharedDataService.get.and.returnValue(Promise.resolve(experience));
        catalogService.getProductCatalogsForSite.and.callFake((siteUID): any => {
            if (siteUID === SITE_1_UID) {
                return Promise.resolve(site1CatalogInfo);
            } else if (siteUID === SITE_2_UID) {
                return Promise.resolve(site2CatalogInfo);
            }
        });

        // Products
        productService.findProducts.and.returnValue(Promise.resolve<any>(products));
        productService.getProductById.and.returnValue(Promise.resolve<any>(products.products[0]));

        // Categories
        productCategoryService.getCategories.and.returnValue(categories);
        productCategoryService.getCategoryById.and.returnValue(categories.productCategories[0]);
    });

    it(
        'GIVEN a site has catalogs ' +
            'WHEN getProductCatalogsInformation is called ' +
            'THEN the catalog information should return that information',
        async () => {
            // GIVEN
            const expectedResult = [
                {
                    id: 'catalog1',
                    name: {
                        en: 'Catalog 1'
                    },
                    versions: [
                        {
                            id: 'version1_1',
                            label: 'version1_1'
                        },
                        {
                            id: 'version1_2',
                            label: 'version1_2'
                        }
                    ]
                },
                {
                    id: 'catalog2',
                    name: {
                        en: 'Catalog 2'
                    },
                    versions: [
                        {
                            id: 'version2_1',
                            label: 'version2_1'
                        },
                        {
                            id: 'version2_2',
                            label: 'version2_2'
                        }
                    ]
                }
            ];

            // WHEN
            const productCatalog = await service.getProductCatalogsInformation();

            // THEN
            expect(catalogService.getProductCatalogsForSite).toHaveBeenCalledWith(SITE_1_UID);
            expect(productCatalog).toEqual(expectedResult);
        }
    );

    it(
        'GIVEN the service has catalogs of a site already cached AND that site has changed' +
            'WHEN getProductCatalogsInformation is called ' +
            'THEN the catalog information should be retrieved again',
        async () => {
            // GIVEN
            await service.getProductCatalogsInformation();
            catalogService.getProductCatalogsForSite.calls.reset();

            // changing the site
            sharedDataService.get.and.returnValue(
                Promise.resolve({ ...experience, siteDescriptor: { uid: SITE_2_UID } })
            );

            // WHEN
            await service.getProductCatalogsInformation();

            // THEN
            expect(catalogService.getProductCatalogsForSite).toHaveBeenCalledWith(SITE_2_UID);
        }
    );

    it(
        'GIVEN the service has catalogs of a site already cached AND that site has not changed' +
            'WHEN getProductCatalogsInformation is called ' +
            'THEN the catalog information should not be retrieved again',
        async () => {
            // GIVEN
            await service.getProductCatalogsInformation();
            catalogService.getProductCatalogsForSite.calls.reset();

            // WHEN
            await service.getProductCatalogsInformation();

            // THEN
            expect(catalogService.getProductCatalogsForSite).not.toHaveBeenCalled();
        }
    );

    it(
        'WHEN productsFetchStrategy is called ' +
            'THEN it returns an object with the right fetchPage to retrieve multiple products',
        async () => {
            const expected = [
                {
                    uid: 'some product',
                    id: 'some product'
                }
            ] as ProductSelectItem[];

            // GIVEN
            const catalogInfo = {
                siteUID: SITE_1_UID
            } as IProductSearch;

            // WHEN
            const productsFetchStrategy = service.productsFetchStrategy;
            const page = await productsFetchStrategy.fetchPage(
                catalogInfo,
                mask,
                pageSize,
                currentPage
            );

            // THEN
            expect(productService.findProducts).toHaveBeenCalledWith(catalogInfo, {
                mask,
                pageSize,
                currentPage
            });
            expect(page.pagination).toBe(products.pagination);
            expect(page.results).toEqual(expected);
        }
    );

    it(
        'WHEN productsFetchStrategy is called ' +
            'THEN it returns an object with the right fetchEntity to retrieve a single product',
        async () => {
            const expected = {
                uid: 'some product',
                id: 'some product'
            } as ProductSelectItem;

            // GIVEN
            const productUID = 'someProductId';

            // WHEN
            const productsFetchStrategy = service.productsFetchStrategy;
            const product = await productsFetchStrategy.fetchEntity(productUID);

            // THEN
            expect(productService.getProductById).toHaveBeenCalledWith(SITE_1_UID, productUID);
            expect(product).toEqual(expected);
        }
    );

    it(
        'WHEN categoriesFetchStrategy is called ' +
            'THEN it returns an object with the right fetchPage to retrieve multiple categories',
        async () => {
            const expected = [
                {
                    uid: 'some category',
                    id: 'some category'
                }
            ] as ProductCategorySelectItem[];

            // GIVEN
            const catalogInfo = {
                catalogId: '',
                catalogVersion: '',
                siteUID: SITE_1_UID
            };

            // WHEN
            const categoriesFetchStrategy = service.categoriesFetchStrategy;
            const page = await categoriesFetchStrategy.fetchPage(
                catalogInfo,
                mask,
                pageSize,
                currentPage
            );

            // THEN
            expect(productCategoryService.getCategories).toHaveBeenCalledWith({
                catalogId: catalogInfo.catalogId,
                catalogVersion: catalogInfo.catalogVersion,
                siteUID: catalogInfo.siteUID,
                mask,
                pageSize,
                currentPage
            });
            expect(page.pagination).toBe(categories.pagination);
            expect(page.results).toEqual(expected);
        }
    );

    it(
        'WHEN categoriesFetchStrategy is called ' +
            'THEN it returns an object with the right fetchEntity to retrieve a single category',
        async () => {
            const expected = {
                uid: 'some category',
                id: 'some category'
            } as ProductCategorySelectItem;
            // GIVEN
            const categoryUID = 'someCategoryId';

            // WHEN
            const categoriesFetchStrategy = service.categoriesFetchStrategy;
            const category = await categoriesFetchStrategy.fetchEntity(categoryUID);

            // THEN
            expect(productCategoryService.getCategoryById).toHaveBeenCalledWith(
                SITE_1_UID,
                categoryUID
            );
            expect(category).toEqual(expected);
        }
    );
});
