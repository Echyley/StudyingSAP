/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageDisplayConditionsService } from 'cmssmarteditcontainer/services/pageDisplayConditions';
import { ICatalogService } from 'smarteditcommons';

const mockCatalog = {
    name: {
        de: 'Deutscher Produktkatalog Kleidung'
    },
    pageDisplayConditions: [
        {
            options: [
                {
                    label: 'page.displaycondition.primary',
                    id: 'PRIMARY'
                }
            ],
            typecode: 'ProductPage'
        },
        {
            options: [
                {
                    label: 'page.displaycondition.variation',
                    id: 'VARIATION'
                }
            ],
            typecode: 'CategoryPage'
        },
        {
            options: [
                {
                    label: 'page.displaycondition.primary',
                    id: 'PRIMARY'
                },
                {
                    label: 'page.displaycondition.variation',
                    id: 'VARIATION'
                }
            ],
            typecode: 'ContentPage'
        }
    ],
    uid: 'apparel-ukContentCatalog',
    version: 'Online'
};

describe('PageDisplayConditionsService - ', () => {
    let pageDisplayConditionsService: PageDisplayConditionsService;
    let catalogService: jasmine.SpyObj<ICatalogService>;

    const uriContextMocks = {
        siteUID: 'mockSiteUID',
        catalogId: 'mockCatalogId',
        catalogVersion: 'mockCatalogVersion'
    };
    const pageTypeMocks = {
        byTypeCode: {
            CONTENT_PAGE: {
                typeCode: 'ContentPage'
            },
            CATEGORY_PAGE: {
                typeCode: 'CategoryPage'
            },
            PRODUCT_PAGE: {
                typeCode: 'ProductPage'
            }
        }
    };
    const pageDisplayConditionMockClass = class PageDisplayCondition {
        PRIMARY = {
            label: 'page.displaycondition.primary',
            description: 'page.displaycondition.primary.description',
            isPrimary: true
        };
        VARIANT = {
            label: 'page.displaycondition.variation',
            description: 'page.displaycondition.variation.description',
            isPrimary: false
        };
        ALL = [
            {
                label: 'page.displaycondition.primary',
                description: 'page.displaycondition.primary.description',
                isPrimary: true
            },
            {
                label: 'page.displaycondition.variation',
                description: 'page.displaycondition.variation.description',
                isPrimary: false
            }
        ];
    };

    beforeEach(() => {
        catalogService = jasmine.createSpyObj('catalogService', ['getContentCatalogVersion']);
        catalogService.getContentCatalogVersion.and.callFake((): any => {
            return Promise.resolve(mockCatalog);
        });

        pageDisplayConditionsService = new PageDisplayConditionsService(catalogService);
    });

    describe('getNewPageConditions() - ', () => {
        it('Page Type with only primary option returns primary display condition', () => {
            pageDisplayConditionsService
                .getNewPageConditions(
                    pageTypeMocks.byTypeCode.PRODUCT_PAGE.typeCode,
                    uriContextMocks
                )
                .then((data) => {
                    expect(data).toEqual([new pageDisplayConditionMockClass().PRIMARY]);
                });
        });

        it('Page Type with only variation option returns variation display condition', () => {
            pageDisplayConditionsService
                .getNewPageConditions(
                    pageTypeMocks.byTypeCode.CATEGORY_PAGE.typeCode,
                    uriContextMocks
                )
                .then((data) => {
                    expect(data).toEqual([new pageDisplayConditionMockClass().VARIANT]);
                });
        });
        it('Page Type with both primary and variation options return both display conditions', () => {
            pageDisplayConditionsService
                .getNewPageConditions(
                    pageTypeMocks.byTypeCode.CONTENT_PAGE.typeCode,
                    uriContextMocks
                )
                .then((data) => {
                    expect(data).toEqual(new pageDisplayConditionMockClass().ALL);
                });
        });
    });
});
