/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    IBaseCatalog,
    ICatalogService,
    IExperienceService,
    IStorageService,
    ComponentMenuService,
    CatalogVersion
} from 'smarteditcommons';

let catalogService: jasmine.SpyObj<ICatalogService>;
let experienceService: jasmine.SpyObj<IExperienceService>;
let storageService: jasmine.SpyObj<IStorageService>;

let service: ComponentMenuService;

const SITE_ID = 'some site ID';

const CATALOG_1 = {
    name: {
        en: 'catalog1'
    },
    catalogId: 'catalog1_ID',
    parents: [],
    versions: [
        {
            version: 'catalog1_inactive_1',
            uuid: 'catalog1_inactive_1_uuid',
            active: false
        },
        {
            version: 'catalog1_active',
            uuid: 'catalog1_active_uuid',
            active: true
        },
        {
            version: 'catalog1_inactive_2',
            uuid: 'catalog1_inactive_2_uuid',
            active: false
        }
    ]
};

const CATALOG_2 = {
    name: {
        en: 'catalog2'
    },
    catalogId: 'catalog2_ID',
    parents: [
        {
            catalogId: 'electronicsContentCatalog',
            catalogName: {
                en: 'parentsCatalog'
            },
            versions: [
                {
                    active: true,
                    uuid: 'electronicsContentCatalog/Online',
                    version: 'Online'
                },
                {
                    active: false,
                    uuid: 'electronicsContentCatalog/Staged',
                    version: 'Staged'
                }
            ]
        }
    ],
    versions: [
        {
            version: 'catalog2_active',
            uuid: 'catalog2_active_uuid',
            active: true
        },
        {
            version: 'catalog2_inactive_1',
            uuid: 'catalog2_inactive_1_uuid',
            active: false
        },
        {
            version: 'catalog2_inactive_2',
            uuid: 'catalog2_inactive_2_uuid',
            active: false
        }
    ]
};

const CATALOG_VERSIONS = [
    {
        id: 'versionABC'
    },
    {
        id: 'versionCDE'
    },
    {
        id: 'versionXYZ'
    }
] as CatalogVersion[];

describe('ComponentMenuService', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    describe('getContentCatalogs', () => {
        it(
            'GIVEN the site has multiple content catalogs ' +
                'WHEN hasMultipleContentCatalogs is called ' +
                'THEN it returns true',
            async () => {
                // GIVEN
                const contentCatalogs: any = [
                    { catalogId: 'catalog1', parents: [{ catalogId: 'catalog2' }] }
                ];

                const mockExperience = {
                    pageContext: {
                        siteId: SITE_ID,
                        catalogId: 'catalog1'
                    }
                };
                mockGetCurrentExperienceSpy(mockExperience);

                catalogService.getContentCatalogsForSite.and.returnValue(
                    Promise.resolve(contentCatalogs)
                );

                // WHEN
                const hasMultipleContentCatalogs = await service.hasMultipleContentCatalogs();

                // THEN
                expect(hasMultipleContentCatalogs).toBe(true);
            }
        );

        it(
            'GIVEN the site has a single content catalog ' +
                'WHEN hasMultipleContentCatalogs is called ' +
                'THEN it returns false',
            async () => {
                // GIVEN
                const mockExperience = {
                    pageContext: {
                        siteId: SITE_ID
                    }
                };
                mockGetCurrentExperienceSpy(mockExperience);

                const contentCatalogs: any = ['SomeCatalog1'];
                catalogService.getContentCatalogsForSite.and.returnValue(
                    Promise.resolve(contentCatalogs)
                );

                // WHEN
                const hasMultipleContentCatalogs = await service.hasMultipleContentCatalogs();

                // THEN
                expect(hasMultipleContentCatalogs).toBe(false);
            }
        );

        it(
            'WHEN getContentCatalogs is called ' +
                'THEN it returns only the catalog version selected in the experience',
            async () => {
                // GIVEN
                const mockExperience = {
                    pageContext: {
                        siteId: SITE_ID
                    }
                };
                mockGetCurrentExperienceSpy(mockExperience);

                const expectedCatalogs = [
                    { catalogId: 'SomeCatalog1' },
                    { catalogId: 'SomeCatalog2' }
                ] as IBaseCatalog[];
                catalogService.getContentCatalogsForSite.and.returnValue(
                    Promise.resolve(expectedCatalogs)
                );

                // WHEN
                const contentCatalogs = await service.getContentCatalogs();

                // THEN
                expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith(SITE_ID);
                expect(contentCatalogs).toEqual(expectedCatalogs);
            }
        );

        it(
            'GIVEN the site has only one content catalog ' +
                'WHEN getValidContentCatalogVersions is called ' +
                'THEN it returns only the catalog version selected in the experience',
            async () => {
                // GIVEN
                const mockExperience = {
                    pageContext: {
                        siteId: SITE_ID,
                        catalogId: CATALOG_1.catalogId,
                        catalogVersion: 'catalog1_active'
                    }
                };
                mockGetCurrentExperienceSpy(mockExperience);

                const contentCatalogs: any = [CATALOG_1];
                catalogService.getContentCatalogsForSite.and.returnValue(
                    Promise.resolve(contentCatalogs)
                );

                // WHEN
                const catalogVersions = await service.getValidContentCatalogVersions();

                // THEN
                expect(catalogVersions.length).toBe(1);
                expect(catalogVersions[0]).toEqual({
                    isCurrentCatalog: true,
                    catalogName: { en: 'catalog1' },
                    catalogId: 'catalog1_ID',
                    catalogVersionId: 'catalog1_active',
                    id: 'catalog1_active_uuid'
                });
            }
        );

        it(
            'GIVEN the site has multiple content catalogs ' +
                'WHEN getValidContentCatalogVersions is called ' +
                'THEN it returns the catalog version selected in the experience and active version of the other catalogs',
            async () => {
                // GIVEN
                const mockExperience = {
                    pageContext: {
                        siteId: SITE_ID,
                        catalogId: CATALOG_2.catalogId,
                        catalogVersion: 'catalog2_active'
                    }
                };
                mockGetCurrentExperienceSpy(mockExperience);

                const contentCatalogs: any = [CATALOG_2, CATALOG_1];
                catalogService.getContentCatalogsForSite.and.returnValue(
                    Promise.resolve(contentCatalogs)
                );

                // WHEN
                const catalogVersions = await service.getValidContentCatalogVersions();

                // THEN
                expect(catalogVersions.length).toBe(2);
                expect(catalogVersions[0]).toEqual({
                    isCurrentCatalog: false,
                    catalogName: { en: 'parentsCatalog' },
                    catalogId: 'electronicsContentCatalog',
                    catalogVersionId: 'Online',
                    id: 'electronicsContentCatalog/Online'
                });
                expect(catalogVersions[1]).toEqual({
                    isCurrentCatalog: true,
                    catalogName: { en: 'catalog2' },
                    catalogId: 'catalog2_ID',
                    catalogVersionId: 'catalog2_active',
                    id: 'catalog2_active_uuid'
                });
            }
        );
    });

    describe('cookie methods', () => {
        it(
            'GIVEN no cookie is set ' +
                'WHEN getInitialCatalogVersion is called ' +
                'THEN it returns the last catalog version',
            async () => {
                // GIVEN
                storageService.getValueFromLocalStorage.and.returnValue(Promise.resolve());

                // WHEN
                const selectedCatalogVersion = await service.getInitialCatalogVersion(
                    CATALOG_VERSIONS
                );

                // THEN
                expect(storageService.getValueFromLocalStorage).toHaveBeenCalledWith(
                    jasmine.any(String),
                    false
                );
                expect(selectedCatalogVersion).toBe(CATALOG_VERSIONS[2]);
            }
        );

        it(
            'GIVEN a cookie is set ' +
                'WHEN getInitialCatalogVersion is called ' +
                'THEN it returns the catalog version stored in the cookie',
            () => {
                // GIVEN
                const expectedCatalogVersion = CATALOG_VERSIONS[1];
                storageService.getValueFromLocalStorage.and.returnValue(
                    Promise.resolve(expectedCatalogVersion.id)
                );

                // WHEN
                const returnedPromise = service.getInitialCatalogVersion(CATALOG_VERSIONS);

                // THEN
                returnedPromise.then(function (selectedCatalogVersion) {
                    expect(selectedCatalogVersion).toBe(expectedCatalogVersion);
                });
            }
        );

        it(
            'WHEN persistCatalogVersion is called ' +
                'THEN it stores the catalog version in the cookie',
            () => {
                // GIVEN
                const catalogVersionToStore = 'some catalog version';

                // WHEN
                service.persistCatalogVersion(catalogVersionToStore);

                // THEN
                expect(storageService.setValueInLocalStorage).toHaveBeenCalledWith(
                    jasmine.any(String),
                    catalogVersionToStore,
                    false
                );
            }
        );
    });
});

function mockGetCurrentExperienceSpy(experience: any) {
    experienceService.getCurrentExperience.and.returnValue(Promise.resolve(experience));
}

function buildCommonSpyObjects() {
    catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
        'getContentCatalogsForSite'
    ]);
    experienceService = jasmine.createSpyObj<IExperienceService>('experienceService', [
        'getCurrentExperience'
    ]);
    storageService = jasmine.createSpyObj<IStorageService>('storageService', [
        'getValueFromLocalStorage',
        'setValueInLocalStorage'
    ]);

    service = new ComponentMenuService(catalogService, experienceService, storageService);
}
