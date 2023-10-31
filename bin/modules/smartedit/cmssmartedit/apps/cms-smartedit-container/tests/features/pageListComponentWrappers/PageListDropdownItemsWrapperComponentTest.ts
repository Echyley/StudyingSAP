/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageListDropdownItemsWrapperComponent } from 'cmssmarteditcontainer/components/pages/pageListComponentWrappers';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { DataTableComponentData, ICatalogVersionPermissionService } from 'smarteditcommons';

describe('PageListDropdownItemsWrapperComponent', () => {
    let component: PageListDropdownItemsWrapperComponent;
    let route: jasmine.SpyObj<ActivatedRoute>;
    let catalogVersionPermissionService: jasmine.SpyObj<ICatalogVersionPermissionService>;
    let managePageService: jasmine.SpyObj<ManagePageService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;

    const dropdownData: DataTableComponentData = {
        column: {
            i18n: 'se.key',
            property: 'prop',
            sortable: false
        },
        item: {
            uid: 'uid'
        }
    };

    beforeEach(() => {
        route = jasmine.createSpyObj<ActivatedRoute>('route', ['snapshot']);
        catalogVersionPermissionService = jasmine.createSpyObj<ICatalogVersionPermissionService>(
            'catalogVersionPermissionService',
            ['hasSyncPermissionToActiveCatalogVersion']
        );
        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'isPageCloneable'
        ]);
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        route.snapshot.params = {
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion'
        };

        component = new PageListDropdownItemsWrapperComponent(
            route,
            dropdownData,
            catalogVersionPermissionService,
            managePageService,
            cdr
        );
    });

    it('GIVEN component is initialized WHEN it does not have synchronize and clone permission THEN it should set 2 dropdown items', async () => {
        catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion.and.returnValue(
            Promise.resolve(false)
        );
        managePageService.isPageCloneable.and.returnValue(Promise.resolve(false));

        await component.ngOnInit();

        expect(component.dropdownItems.length).toEqual(2);
    });

    it('GIVEN component is initialized WHEN it can be synchronized and cloned THEN it should set 4 dropdown items', async () => {
        catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion.and.returnValue(
            Promise.resolve(true)
        );
        managePageService.isPageCloneable.and.returnValue(Promise.resolve(true));

        await component.ngOnInit();

        expect(component.dropdownItems.length).toEqual(4);
    });
});
