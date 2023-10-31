/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TrashListDropdownItemsWrapperComponent } from 'cmssmarteditcontainer/components/pages/pageListComponentWrappers';
import { DataTableComponentData, ICatalogVersionPermissionService } from 'smarteditcommons';

describe('DropItemsWrapperComponent', () => {
    let component: TrashListDropdownItemsWrapperComponent;
    let route: jasmine.SpyObj<ActivatedRoute>;
    let catalogVersionPermissionService: jasmine.SpyObj<ICatalogVersionPermissionService>;
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
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);

        route.snapshot.params = {
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion'
        };

        component = new TrashListDropdownItemsWrapperComponent(
            route,
            dropdownData,
            catalogVersionPermissionService,
            cdr
        );
    });

    it('GIVEN component cannot be synchronized WHEN it is initialized THEN it should set 2 dropdown items', async () => {
        catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion.and.returnValue(
            Promise.resolve(false)
        );

        await component.ngOnInit();

        expect(component.dropdownItems.length).toEqual(2);
    });

    it('GIVEN component can be synchronized WHEN it is initialized THEN it should set 3 dropdown items', async () => {
        catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion.and.returnValue(
            Promise.resolve(true)
        );

        await component.ngOnInit();

        expect(component.dropdownItems.length).toEqual(3);
    });
});
