/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SimpleChanges } from '@angular/core';
import { ItemManagementComponent } from 'cmssmarteditcontainer/components/itemManagement/ItemManagerComponent';

describe('ItemManagementComponent', () => {
    let component: ItemManagementComponent;

    const mockItem = {
        typeCode: 'typeCode',
        name: 'asd',
        uid: 'uid',
        uuid: 'UUID',
        catalogVersion: 'staged'
    };

    beforeEach(() => {
        component = new ItemManagementComponent();
    });

    it('GIVEN unsupported mode THEN it should throw an error', () => {
        component.mode = 'unknown';

        expect(() => {
            component.ngOnInit();
        }).toThrow('ItemManagementComponent.ngOnInit() - Mode not supported: unknown');
    });

    it('GIVEN component type is not defined and item is defined THEN it should set component type to item code', () => {
        component.mode = 'add';
        component.item = mockItem;

        component.ngOnInit();

        expect(component.componentType).toEqual(mockItem.typeCode);
    });

    it('GIVEN item is not defined THEN should set itemId to null', () => {
        component.mode = 'add';
        component.itemId = '123';

        component.ngOnInit();

        expect(component.itemId).toEqual(null);
    });

    it('GIVEN uuid is defined in item THEN is should be assigned to component item id', () => {
        component.mode = 'add';
        component.item = { ...mockItem, uid: null };

        component.ngOnInit();

        expect(component.itemId).toEqual(mockItem.uuid);
    });

    it('GIVEN uuid is not available but uid is THEN uid should be assigned to component item id', () => {
        component.mode = 'add';
        component.item = { ...mockItem, uuid: null };

        component.ngOnInit();

        expect(component.itemId).toEqual(mockItem.uid);
    });

    it('GIVEN uuid and uid are not available THEN null should be updated to component item id', () => {
        component.itemId = '123';

        component.ngOnChanges({
            item: { ...mockItem, uuid: null, uid: null }
        } as unknown as SimpleChanges);

        expect(component.itemId).toEqual(null);
    });

    it('GIVEN uuid are available but uid is null THEN uuid should be updated to component item id', () => {
        component.itemId = '123';
        component.item = { ...mockItem, uuid: '456', uid: null };
        component.ngOnChanges({
            item: { ...mockItem, uuid: '456', uid: null }
        } as unknown as SimpleChanges);

        expect(component.itemId).toEqual('456');
    });

    it('GIVEN uid are available but uuid is null THEN uid should be updated to component item id', () => {
        component.itemId = '123';
        component.item = { ...mockItem, uuid: null, uid: '666' };
        component.ngOnChanges({
            item: { ...mockItem, uuid: null, uid: '666' }
        } as unknown as SimpleChanges);

        expect(component.itemId).toEqual('666');
    });
});
