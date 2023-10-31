/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageListComponent } from 'cmssmarteditcontainer/components/pages/pageList/PageListComponent';
import {
    PageNameWrapperComponent,
    NumberOfRestrictionsWrapperComponent,
    PageStatusWrapperComponent,
    PageListDropdownItemsWrapperComponent
} from 'cmssmarteditcontainer/components/pages/pageListComponentWrappers';
import { AddPageWizardService } from 'cmssmarteditcontainer/services/pages/AddPageWizardService';
import {
    IUrlService,
    ICatalogService,
    SystemEventService,
    DynamicPagedListApi,
    Pagination,
    cmsitemsUri,
    IUserTrackingService
} from 'smarteditcommons';

describe('PageListComponent', () => {
    let component: PageListComponent;
    let componentAny: any;
    let activatedRoute: ActivatedRoute;
    let urlService: jasmine.SpyObj<IUrlService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let addPageWizardService: jasmine.SpyObj<AddPageWizardService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;
    let unsubscribeSystemEvent: jasmine.Spy;

    const mockSiteId = 'mockSiteId';
    const mockCatalogId = 'mockCatalogId';
    const mockCatalogVersion = 'mockCatalogVersion';
    const mockCatalogs = [
        { catalogId: 'catalogId', name: { en: 'catalog1' } },
        { catalogId: mockCatalogId, name: { en: 'catalog2' } }
    ];
    const mockBuiltUriContext = {
        SITE_ID: mockSiteId,
        CATALOG_ID: mockCatalogId,
        CATALOG_VERSION: mockCatalogVersion
    };
    const mockBuiltPageUriContext = {
        PAGE_SITE_ID: mockSiteId,
        PAGE_CATALOG_ID: mockCatalogId,
        PAGE_CATALOG_VERSION: mockCatalogVersion
    };
    const mockDynamicPageApi = {
        reloadItems: jasmine.createSpy()
    } as DynamicPagedListApi;

    beforeEach(() => {
        urlService = jasmine.createSpyObj<IUrlService>('urlService', [
            'buildPageUriContext',
            'buildUriContext'
        ]);
        urlService.buildPageUriContext.and.returnValue(mockBuiltPageUriContext);
        urlService.buildUriContext.and.returnValue(mockBuiltUriContext);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'isContentCatalogVersionNonActive',
            'getContentCatalogsForSite'
        ]);
        catalogService.getContentCatalogsForSite.and.returnValue(
            Promise.resolve<any>(mockCatalogs)
        );

        addPageWizardService = jasmine.createSpyObj<AddPageWizardService>('addPageWizardService', [
            'openAddPageWizard'
        ]);
        addPageWizardService.openAddPageWizard.and.returnValue(Promise.resolve());

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        unsubscribeSystemEvent = jasmine.createSpy();
        systemEventService.subscribe.and.returnValue(unsubscribeSystemEvent);

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        activatedRoute = {
            snapshot: {
                params: {
                    siteId: mockSiteId,
                    catalogId: mockCatalogId,
                    catalogVersion: mockCatalogVersion
                }
            }
        } as unknown as ActivatedRoute;

        component = new PageListComponent(
            activatedRoute,
            urlService,
            catalogService,
            addPageWizardService,
            systemEventService,
            cdr,
            userTrackingService
        );
        componentAny = component;
    });

    describe('initialization', () => {
        it('WHEN component is constructed THEN it should have properties initialized', () => {
            expect(component.isReady).toEqual(false);
            expect(component.count).toEqual(0);
            expect(component.catalogId).toEqual(mockCatalogId);
            expect(component.catalogVersion).toEqual(mockCatalogVersion);
            expect(component.siteUid).toEqual(mockSiteId);

            expect(component.catalogName).toEqual({});

            expect(component.pageUriContext).toEqual(mockBuiltPageUriContext);
            expect(component.uriContext).toEqual(mockBuiltUriContext);

            expect(component.pageListConfig).toEqual({
                sortBy: 'name',
                reversed: false,
                itemsPerPage: 10,
                displayCount: true,
                keys: [],
                queryParams: {
                    catalogId: mockCatalogId,
                    catalogVersion: mockCatalogVersion,
                    typeCode: 'AbstractPage',
                    itemSearchParams: 'pageStatus:active',
                    fields: 'PAGE_LIST'
                },
                uri: cmsitemsUri
            });

            expect(component.query).toEqual('');

            expect(urlService.buildPageUriContext).toHaveBeenCalledWith(
                mockSiteId,
                mockCatalogId,
                mockCatalogVersion
            );

            expect(urlService.buildUriContext).toHaveBeenCalledWith(
                mockSiteId,
                mockCatalogId,
                mockCatalogVersion
            );
        });

        it('WHEN ngOnInit is called AND catalog is nonActive THEN it should set proper page list config keys, catalog name, and subscribe to system event', async () => {
            catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(true));

            await component.ngOnInit();

            expect(component.pageListConfig.keys).toEqual([
                {
                    property: 'name',
                    i18n: 'se.cms.pagelist.headerpagename',
                    sortable: true,
                    component: PageNameWrapperComponent
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
                    property: 'masterTemplateId',
                    i18n: 'se.cms.pagelist.headerpagetemplate',
                    sortable: false
                },
                {
                    property: 'numberOfRestrictions',
                    i18n: 'se.cms.pagelist.headerrestrictions',
                    sortable: false,
                    component: NumberOfRestrictionsWrapperComponent
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
                    component: PageListDropdownItemsWrapperComponent
                }
            ]);
            expect(component.catalogName).toEqual({ en: 'catalog2' });
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'EVENT_CONTENT_CATALOG_UPDATE',
                jasmine.any(Function)
            );
            expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith(mockSiteId);
        });

        it('WHEN ngOnInit is called AND catalog is active THEN it should set page proper list config keys, catalog name, and subscribe to system event', async () => {
            catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.pageListConfig.keys).toEqual([
                {
                    property: 'name',
                    i18n: 'se.cms.pagelist.headerpagename',
                    sortable: true,
                    component: PageNameWrapperComponent
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
                    property: 'masterTemplateId',
                    i18n: 'se.cms.pagelist.headerpagetemplate',
                    sortable: false
                },
                {
                    property: 'numberOfRestrictions',
                    i18n: 'se.cms.pagelist.headerrestrictions',
                    sortable: false,
                    component: NumberOfRestrictionsWrapperComponent
                },
                {
                    property: 'dropdownitems',
                    i18n: 'se.cms.pagelist.headerpageaction',
                    sortable: false,
                    component: PageListDropdownItemsWrapperComponent
                }
            ]);
            expect(component.catalogName).toEqual({ en: 'catalog2' });
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'EVENT_CONTENT_CATALOG_UPDATE',
                jasmine.any(Function)
            );
            expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith(mockSiteId);
        });
    });

    describe('On destroy', () => {
        it('should unsubscribe system event and subject listener', async () => {
            const unsubscribeSpy = jasmine.createSpy();
            spyOn(componentAny.querySubscription, 'unsubscribe');
            systemEventService.subscribe.and.returnValue(unsubscribeSpy);

            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unsubscribeSpy).toHaveBeenCalled();
            expect(componentAny.querySubscription.unsubscribe).toHaveBeenCalled();
        });
    });

    it('WHEN onPageItemsUpdate is called THEN it should set count', () => {
        component.onPageItemsUpdate({ totalCount: 10 } as Pagination);

        expect(component.count).toEqual(10);
    });

    it('WHEN dynamicPagedListApi is defined AND onContentCatalogUpdate is called THEN it should reload items', () => {
        component.dynamicPagedListApi = mockDynamicPageApi;
        component.onContentCatalogUpdate();

        expect(mockDynamicPageApi.reloadItems).toHaveBeenCalled();
    });

    describe('WHEN onQueryChange is called THEN', () => {
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();
            await component.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('should update query value', () => {
            component.onQueryChange('new val');

            clock.tick(501);

            expect(component.query).toEqual('new val');
        });

        it('should update only recent value', () => {
            component.onQueryChange('val1');
            clock.tick(499);

            expect(component.query).toEqual('');

            component.onQueryChange('val2');
            clock.tick(2); // so we are at 501 (499 + 2);
            expect(component.query).toEqual('');

            clock.tick(500);
            expect(component.query).toEqual('val2');
        });
    });

    it('WHEN getApi is called THEN it should assign dynami paged list api', () => {
        component.getApi(mockDynamicPageApi);

        expect(component.dynamicPagedListApi).toEqual(mockDynamicPageApi);
    });

    it('WHEN openAddPagWizard is called THEN it should open wizard from addPageWizardService and when it resolves, reload items AND track user action', async () => {
        component.getApi(mockDynamicPageApi);
        await component.openAddPageWizard();

        expect(addPageWizardService.openAddPageWizard).toHaveBeenCalled();
        expect(mockDynamicPageApi.reloadItems).toHaveBeenCalled();
        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });

    describe('WHEN onQueryChange is called THEN', () => {
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();
            await component.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('WHEN reset is called THEN it should clear the query', () => {
            component.query = 'asd';
            component.reset();
            clock.tick(501);
            expect(component.query).toEqual('');
        });
    });
});
