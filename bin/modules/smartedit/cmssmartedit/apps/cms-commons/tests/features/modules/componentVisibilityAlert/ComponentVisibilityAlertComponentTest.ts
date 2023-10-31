/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AlertRef } from '@fundamental-ngx/core';
import { IComponentVisibilityAlertService, ComponentVisibilityAlertComponent } from 'cmscommons';
import { IEditorModalService } from 'smarteditcommons';

describe('ComponentVisibilityAlertComponent', () => {
    class MockAlertRef extends AlertRef {
        dismiss = jasmine.createSpy();
        data = {
            component: {},
            message: 'alert message'
        };
    }

    function createMockPayload(scenario: string) {
        return {
            itemId: 'MOCKED_ITEM_ID',
            itemType: 'MOCKED_ITEM_TYPE',
            catalogVersion: 'MOCKED_CATALOG_VERSION',
            slotId: 'MOCKED_SLOT_ID',
            restricted: scenario.includes('WITH_RESTRICTIONS'),
            visible: scenario.includes('VISIBLE')
        };
    }

    let editorModalService: jasmine.SpyObj<IEditorModalService>;
    let componentVisibilityAlertService: jasmine.SpyObj<IComponentVisibilityAlertService>;
    let alertRef: MockAlertRef;

    let component: ComponentVisibilityAlertComponent;
    beforeEach(() => {
        editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
            'openAndRerenderSlot'
        ]);
        componentVisibilityAlertService = jasmine.createSpyObj<IComponentVisibilityAlertService>(
            'componentVisibilityAlertService',
            ['checkAndAlertOnComponentVisibility']
        );
        alertRef = new MockAlertRef();

        component = new ComponentVisibilityAlertComponent(
            editorModalService,
            componentVisibilityAlertService,
            alertRef
        );
    });

    it('sets message properly', () => {
        expect(component.message).toBe('alert message');
    });

    it(`GIVEN link has been clicked THEN it should 
      dismiss Alert AND
      call openAndRerenderSlot AND
      checkAndAlertOnComponentVisibility properly`, async () => {
        const mockComponent = createMockPayload('HIDDEN_WITH_RESTRICTIONS');
        (component as any).component = mockComponent;

        editorModalService.openAndRerenderSlot.and.returnValue(
            Promise.resolve({
                uuid: 'MOCKED_ITEM_ID',
                itemtype: 'MOCKED_ITEM_TYPE',
                catalogVersion: 'MOCKED_CATALOG_VERSION',
                slotId: 'MOCKED_SLOT_ID',
                visible: false,
                restricted: true
            })
        );

        await component.onClick();

        expect(alertRef.dismiss).toHaveBeenCalled();
        expect(editorModalService.openAndRerenderSlot).toHaveBeenCalledWith(
            'MOCKED_ITEM_TYPE',
            'MOCKED_ITEM_ID',
            'visibilityTab'
        );
        expect(
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility
        ).toHaveBeenCalledWith(mockComponent);
    });
});
