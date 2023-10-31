/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
    TrashListDropdownItemsWrapperComponent,
    ModifiedTimeWrapperComponent,
    NumberOfRestrictionsWrapperComponent,
    PageStatusWrapperComponent
} from 'cmssmarteditcontainer/components/pages/pageListComponentWrappers';
import { TrashedPageListComponent } from 'cmssmarteditcontainer/components/pages/trashedPageList/TrashedPageListComponent';
import {
    DynamicPagedListApi,
    ICatalogService,
    IUrlService,
    Pagination,
    SystemEventService,
    cmsitemsUri
} from 'smarteditcommons';

describe('TrashedPageListComponent', () => {
    let component: TrashedPageListComponent;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let route: ActivatedRoute;
    let urlService: jasmine.SpyObj<IUrlService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockSiteId = 'siteID';
    const mockCatalogId = 'catalogID';
    const mockCatalogVersion = 'catalogVersion';
    const mockCatalogName = { en: 'mockCatalogName' };

    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getContentCatalogsForSite'
        ]);
        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        catalogService.getContentCatalogsForSite.and.returnValue(
            Promise.resolve<any>([{ catalogId: mockCatalogId, name: mockCatalogName }])
        );

        urlService.buildUriContext.and.returnValue({ key: 'value' });

        route = {
            snapshot: {
                params: {
                    siteId: mockSiteId,
                    catalogId: mockCatalogId,
                    catalogVersion: mockCatalogVersion
                }
            }
        } as unknown as ActivatedRoute;

        component = new TrashedPageListComponent(
            catalogService,
            route,
            urlService,
            systemEventService,
            cdr
        );
    });

    describe('WHEN component is initialized THEN', () => {
        it('should set site params', () => {
            component.ngOnInit();

            expect(component.siteUID).toEqual(mockSiteId);
            expect(component.catalogId).toEqual(mockCatalogId);
            expect(component.catalogVersion).toEqual(mockCatalogVersion);
        });

        it('should set uri context', () => {
            component.ngOnInit();

            expect(urlService.buildUriContext).toHaveBeenCalledWith(
                mockSiteId,
                mockCatalogId,
                mockCatalogVersion
            );
            expect(component.uriContext).toEqual({ key: 'value' });
        });

        it('should setTrashedListConfigBasis', () => {
            component.ngOnInit();

            expect(component.trashedPageListConfig).toEqual(
                jasmine.objectContaining({
                    sortBy: 'name',
                    reversed: false,
                    itemsPerPage: 10,
                    displayCount: true,
                    queryParams: {
                        catalogId: mockCatalogId,
                        catalogVersion: mockCatalogVersion,
                        typeCode: 'AbstractPage',
                        itemSearchParams: 'pageStatus:deleted'
                    },
                    uri: cmsitemsUri
                })
            );
        });

        it('should setTrashedListColumns', () => {
            component.ngOnInit();

            expect(component.trashedPageListConfig).toEqual(
                jasmine.objectContaining({
                    keys: [
                        {
                            property: 'name',
                            i18n: 'se.cms.pagelist.headerpagename',
                            sortable: true
                        },
                        {
                            property: 'uid',
                            i18n: 'se.cms.pagelist.headerpageid',
                            sortable: true
                        },
                        {
                            property: 'itemtype',
                            i18n: 'se.cms.pagelist.headerpagetype',
                            sortable: true
                        },
                        {
                            property: 'label',
                            i18n: 'se.cms.pagelist.headerpagelable',
                            sortable: false
                        },
                        {
                            property: 'numberOfRestrictions',
                            i18n: 'se.cms.pagelist.headerrestrictions',
                            sortable: false,
                            component: NumberOfRestrictionsWrapperComponent
                        },
                        {
                            property: 'modifiedtime',
                            i18n: 'se.cms.trashedpagelist.trashed.date',
                            sortable: true,
                            component: ModifiedTimeWrapperComponent
                        },
                        {
                            property: 'pageStatus',
                            i18n: 'se.cms.pagelist.headerpagestatus',
                            sortable: false,
                            component: PageStatusWrapperComponent
                        },
                        {
                            property: 'dropdownitems',
                            i18n: 'se.cms.pagelist.headerpageaction',
                            sortable: false,
                            component: TrashListDropdownItemsWrapperComponent
                        }
                    ]
                })
            );
        });

        it('should setCatalogName', async () => {
            await component.ngOnInit();

            expect(component.catalogName).toEqual(mockCatalogName);
        });
    });

    describe('WHEN onMaskChange is called THEN', () => {
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();
            await component.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('should update mask value', () => {
            component.onMaskChange('new val');

            clock.tick(501);

            expect(component.mask).toEqual('new val');
        });

        it('should update only recent value', () => {
            component.onMaskChange('val1');
            clock.tick(499);

            expect(component.mask).toEqual('');

            component.onMaskChange('val2');
            clock.tick(2); // so we are at 501 (499 + 2);
            expect(component.mask).toEqual('');

            clock.tick(500);
            expect(component.mask).toEqual('val2');
        });
    });

    describe('WHEN onPageItemsUpdate is called THEN', () => {
        it('should update count value', () => {
            component.onPageItemsUpdate({ totalCount: 10 } as Pagination);

            expect(component.count).toEqual(10);
        });
    });

    describe('WHEN reset is called THEN', () => {
        it('should change mask value to empty string', () => {
            component.mask = 'value';

            component.reset();

            expect(component.mask).toEqual('');
        });
    });

    describe('WHEN getApi is called THEN', () => {
        const mockMethod = jasmine.createSpy();
        const mockedApi: DynamicPagedListApi = { reloadItems: mockMethod };
        it('should assign dynamicPagedListApi', () => {
            component.getApi(mockedApi);

            expect(component.dynamicPagedListApi).toEqual(mockedApi);
        });
    });
});
