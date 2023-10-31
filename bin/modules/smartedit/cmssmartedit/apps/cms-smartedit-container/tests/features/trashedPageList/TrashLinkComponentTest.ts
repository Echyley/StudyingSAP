/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TrashLinkComponent } from 'cmssmarteditcontainer/components/pages/trashLink/TrashLinkComponent';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    SmarteditRoutingService,
    IUrlService,
    ICatalogService,
    SystemEventService
} from 'smarteditcommons';

describe('TrashLinkComponent', () => {
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    let component: TrashLinkComponent;
    let componentAny: any;

    let route: ActivatedRoute;
    let routingsService: jasmine.SpyObj<SmarteditRoutingService>;
    let managePageService: jasmine.SpyObj<ManagePageService>;
    let urlService: jasmine.SpyObj<IUrlService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    const mockSiteId = 'mockSiteId';
    const mockCatalogId = 'mockCatalogId';
    const mockCatalogVersion = 'mockCatalogVersion';
    const mockBuiltUriContext = {
        SITE_ID: mockSiteId,
        CATALOG_ID: mockCatalogId,
        CATALOG_VERSION: mockCatalogVersion
    };

    beforeEach(() => {
        route = {
            snapshot: {
                params: {
                    siteId: mockSiteId,
                    catalogId: mockCatalogId,
                    catalogVersion: mockCatalogVersion
                }
            }
        } as unknown as ActivatedRoute;
        routingsService = jasmine.createSpyObj<SmarteditRoutingService>('routingsService', ['go']);

        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'getSoftDeletedPagesCount'
        ]);
        managePageService.getSoftDeletedPagesCount.and.returnValue(Promise.resolve(8));

        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
        urlService.buildUriContext.and.returnValue(mockBuiltUriContext);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'isContentCatalogVersionNonActive'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);

        component = new TrashLinkComponent(
            route,
            routingsService,
            managePageService,
            urlService,
            catalogService,
            systemEventService,
            cdr
        );
        componentAny = component;
    });

    it('GIVEN component is initialized WHEN catalog is not active THEN it should set value for non active catalog and update trashed pages count and create system event listener', async () => {
        catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(true));

        await component.ngOnInit();

        expect(componentAny.siteId).toEqual(mockSiteId);
        expect(componentAny.catalogId).toEqual(mockCatalogId);
        expect(componentAny.catalogVersion).toEqual(mockCatalogVersion);
        expect(componentAny.uriContext).toEqual(mockBuiltUriContext);
        expect(component.isNonActiveCatalog).toEqual(true);

        expect(managePageService.getSoftDeletedPagesCount).toHaveBeenCalledWith(
            mockBuiltUriContext
        );
        expect(systemEventService.subscribe).toHaveBeenCalledWith(
            'EVENT_CONTENT_CATALOG_UPDATE',
            jasmine.any(Function)
        );
    });

    it('GIVEN component is initialized WHEN catalog is active THEN it should not update trashed pages count', async () => {
        catalogService.isContentCatalogVersionNonActive.and.returnValue(Promise.resolve(false));

        await component.ngOnInit();

        expect(component.isNonActiveCatalog).toEqual(false);
        expect(managePageService.getSoftDeletedPagesCount).not.toHaveBeenCalled();
    });

    it('WHEN onDestroy is called THEN it should unsubscribe system event listener', async () => {
        const unsubscribe = jasmine.createSpy();
        systemEventService.subscribe.and.returnValue(unsubscribe);

        await component.ngOnInit();

        component.ngOnDestroy();

        expect(unsubscribe).toHaveBeenCalled();
    });

    it('WHEN updateTrashedPagesCount is called THEN it should get it from managePageService and update translatio data', async () => {
        await component.ngOnInit();
        await component.updateTrashedPagesCount();

        expect(component.trashedPagesTranslationData).toEqual({
            totalCount: 8
        });
        expect(managePageService.getSoftDeletedPagesCount).toHaveBeenCalledWith(
            mockBuiltUriContext
        );
    });

    it('WHEN goToTrash is called THEN it should use routingService to navigate there', async () => {
        await component.ngOnInit();

        component.goToTrash();

        expect(routingsService.go).toHaveBeenCalledWith(
            'trashedpages/mockSiteId/mockCatalogId/mockCatalogVersion'
        );
    });
});
