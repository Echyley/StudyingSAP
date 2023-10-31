/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { of } from 'rxjs';
import {
    DropdownPopulatorPayload,
    functionsUtils,
    ICatalog,
    ICatalogService,
    ISharedDataService,
    ISite,
    L10nPipe,
    LanguageService
} from 'smarteditcommons';
import { PreviewDatapreviewCatalogDropdownPopulator } from 'smarteditcontainer/services';

interface ICatalogMock extends ICatalog {
    _siteId: string;
}

describe('PreviewDatapreviewCatalogDropdownPopulator', () => {
    let previewDatapreviewCatalogDropdownPopulator: PreviewDatapreviewCatalogDropdownPopulator;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;

    const experience: any = {
        siteDescriptor: {
            uid: 'siteId1'
        } as ISite
    };

    const catalogDescriptors: ICatalogMock[] = [
        {
            catalogId: 'myCatalogId1',
            _siteId: 'siteId1',
            name: {
                en: 'myCatalog1'
            },
            versions: [
                {
                    version: 'myCatalogVersion1',
                    active: null,
                    homepage: null,
                    pageDisplayConditions: null,
                    uuid: null
                }
            ]
        },
        {
            catalogId: 'myCatalogId2',
            _siteId: 'siteId1',
            name: {
                en: 'myCatalog2'
            },
            versions: [
                {
                    version: 'myCatalogVersion2',
                    active: null,
                    homepage: null,
                    pageDisplayConditions: null,
                    uuid: null
                }
            ]
        },
        {
            catalogId: 'myCatalogId3',
            _siteId: 'siteId3',
            name: {
                en: 'myCatalog3'
            },
            versions: [
                {
                    version: 'myCatalogVersion3',
                    active: null,
                    homepage: null,
                    pageDisplayConditions: null,
                    uuid: null
                }
            ]
        },
        {
            catalogId: 'myCatalogId4',
            _siteId: 'siteId4',
            name: {
                en: 'myCatalog4'
            },
            versions: [
                {
                    version: 'myCatalogVersion4',
                    active: null,
                    homepage: null,
                    pageDisplayConditions: null,
                    uuid: null
                }
            ]
        },
        {
            catalogId: 'myCatalogId5',
            _siteId: 'siteId5',
            name: {
                en: 'myCatalog5'
            },
            versions: [
                {
                    version: 'myCatalogVersion5',
                    active: null,
                    homepage: null,
                    pageDisplayConditions: null,
                    uuid: null
                }
            ]
        }
    ];

    beforeEach(() => {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
        catalogService = jasmine.createSpyObj('catalogService', ['getContentCatalogsForSite']);
        languageService = jasmine.createSpyObj('languageService', [
            'getLanguagesForSite',
            'getResolveLocaleIsoCode'
        ]);
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);
        l10nPipe.transform.and.callFake((catalog: any) => {
            return of(catalog.en);
        });

        previewDatapreviewCatalogDropdownPopulator = new PreviewDatapreviewCatalogDropdownPopulator(
            catalogService,
            sharedDataService,
            l10nPipe,
            languageService
        );
    });

    it(
        'GIVEN sharedDataService returns a resolved promise WHEN fetchAll is called THEN it will return a list of catalog ID -' +
            ' catalog versions',
        async () => {
            // GIVEN
            sharedDataService.get.and.returnValue(Promise.resolve(experience));
            catalogService.getContentCatalogsForSite.and.callFake((siteId: string) =>
                mockGetContentCatalogsForSite(siteId)
            );

            // WHEN
            const catalogs = await previewDatapreviewCatalogDropdownPopulator.fetchAll(
                {} as DropdownPopulatorPayload
            );

            // THEN
            expect(catalogs).toEqual([
                {
                    id: 'siteId1|myCatalogId1|myCatalogVersion1',
                    label: 'myCatalog1 - myCatalogVersion1'
                },
                {
                    id: 'siteId1|myCatalogId2|myCatalogVersion2',
                    label: 'myCatalog2 - myCatalogVersion2'
                }
            ]);
            expect(sharedDataService.get).toHaveBeenCalled();
            expect(catalogService.getContentCatalogsForSite.calls.count()).toBe(1);
        }
    );

    it(
        'GIVEN sharedDataService returns a resolved promise WHEN fetchAll is called with a search string THEN it will return a' +
            ' list of catalog ID - catalog versions filtered based on the search string',
        async () => {
            // GIVEN
            sharedDataService.get.and.returnValue(Promise.resolve(experience));
            catalogService.getContentCatalogsForSite.and.callFake((siteId: string) =>
                mockGetContentCatalogsForSite(siteId)
            );

            // WHEN
            const catalogs = await previewDatapreviewCatalogDropdownPopulator.fetchAll({
                field: null,
                model: null,
                selection: null,
                search: 'myCatalogVersion1'
            });

            // THEN
            expect(catalogs).toEqual([
                {
                    id: 'siteId1|myCatalogId1|myCatalogVersion1',
                    label: 'myCatalog1 - myCatalogVersion1'
                }
            ]);
        }
    );

    it('GIVEN sharedDataService returns a rejected promise WHEN fetchAll is called THEN it will return a rejected promise', async () => {
        // GIVEN
        sharedDataService.get.and.returnValue(Promise.reject());
        catalogService.getContentCatalogsForSite.and.callFake((siteId: string) =>
            mockGetContentCatalogsForSite(siteId)
        );

        try {
            await previewDatapreviewCatalogDropdownPopulator.fetchAll(
                {} as DropdownPopulatorPayload
            );

            functionsUtils.assertFail();
        } catch (e) {
            expect(sharedDataService.get).toHaveBeenCalled();
            expect(catalogService.getContentCatalogsForSite).not.toHaveBeenCalled();
        }
    });

    function mockGetContentCatalogsForSite(siteId: string): Promise<any[]> {
        const catalogDescriptorsForSite = catalogDescriptors.filter(
            (catalogDescriptor) => catalogDescriptor._siteId === siteId
        );
        return Promise.resolve(catalogDescriptorsForSite);
    }
});
