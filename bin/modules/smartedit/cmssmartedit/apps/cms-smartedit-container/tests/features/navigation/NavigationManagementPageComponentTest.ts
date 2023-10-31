/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavigationManagementPageComponent } from 'cmssmarteditcontainer/components/navigation/NavigationManagementPageComponent';
import {
    IUrlService,
    IPermissionService,
    ICatalogService,
    IUriContext,
    IBaseCatalog
} from 'smarteditcommons';

describe('NavigationManagementPageComponent', () => {
    let component: NavigationManagementPageComponent;
    let activatedRoute: ActivatedRoute;
    let urlService: jasmine.SpyObj<IUrlService>;
    let permissionService: jasmine.SpyObj<IPermissionService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const mockRouteParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };

    const mockUriContext = {
        SITE_ID: mockRouteParams.siteId,
        CATALOG_ID: mockRouteParams.catalogId,
        CATALOG_VERSION: mockRouteParams.catalogVersion
    } as IUriContext;

    const mockCatalogs = [
        { catalogId: 'catalogId', name: { en: 'catalogName' } },
        { catalogId: 'catalogId2', name: { en: 'catalogName2' } },
        { catalogId: 'catalogId3', name: { en: 'catalogName3' } }
    ] as unknown as IBaseCatalog[];

    beforeEach(() => {
        activatedRoute = {
            snapshot: {
                params: mockRouteParams
            }
        } as unknown as ActivatedRoute;

        urlService = jasmine.createSpyObj<IUrlService>('urlService', ['buildUriContext']);
        urlService.buildUriContext.and.returnValue(mockUriContext);

        permissionService = jasmine.createSpyObj<IPermissionService>('permissionService', [
            'isPermitted'
        ]);
        permissionService.isPermitted.and.returnValue(Promise.resolve(true));

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getContentCatalogsForSite'
        ]);
        catalogService.getContentCatalogsForSite.and.returnValue(Promise.resolve(mockCatalogs));

        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        component = new NavigationManagementPageComponent(
            activatedRoute,
            urlService,
            permissionService,
            catalogService,
            cdr
        );
    });

    describe('initialize', () => {
        it('WHEN called THEN it should set catalogVersion, uriContext, catalogName and readOnly', async () => {
            await component.ngOnInit();

            expect(component.catalogVersion).toEqual('catalogVersion');
            expect(component.uriContext).toEqual(mockUriContext);
            expect(component.readOnly).toEqual(false);
            expect(component.catalogName).toEqual({ en: 'catalogName' });
        });

        it('WHEN permission is not granted THEN it should set readOnly to true', async () => {
            permissionService.isPermitted.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.readOnly).toEqual(true);
        });

        it('WHEN catalog is not found THEN it should set empty map', async () => {
            catalogService.getContentCatalogsForSite.and.returnValue(Promise.resolve([]));

            await component.ngOnInit();

            expect(component.catalogName).toEqual({});
        });
    });
});
