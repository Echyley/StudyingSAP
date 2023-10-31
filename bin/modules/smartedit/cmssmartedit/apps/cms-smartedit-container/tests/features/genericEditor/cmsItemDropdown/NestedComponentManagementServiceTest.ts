/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NestedComponentManagementService } from 'cmssmarteditcontainer/components/cmsComponents/cmsItemDropdown/services';
import { CMSItem, IEditorModalService } from 'smarteditcommons';

describe('NestedComponentManagementServiceTest', () => {
    const EXPECTED_RESULT = {
        uid: 'edited cms item id'
    } as CMSItem;
    const STACK_ID = 'some stack id';

    let editorModalService: jasmine.SpyObj<IEditorModalService>;

    let service: NestedComponentManagementService;
    beforeEach(() => {
        editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
            'open'
        ]);

        service = new NestedComponentManagementService(editorModalService);
    });

    beforeEach(() => {
        editorModalService.open.and.returnValue(EXPECTED_RESULT as any);
    });

    it('WHEN openNestedComponentEditor is called with visibility as false THEN the editor is properly opened', async () => {
        // GIVEN
        const COMPONENT_TYPE = 'Some Component Type';
        const COMPONENT_UUID = 'some component uuid';
        const CATALOG_VERSION = 'some catalog version uuid';

        const componentInfo = {
            componentType: COMPONENT_TYPE,
            componentUuid: COMPONENT_UUID,
            content: {
                visible: false,
                catalogVersion: CATALOG_VERSION
            }
        };

        const expectedComponentData = {
            smarteditComponentUuid: COMPONENT_UUID,
            smarteditComponentType: COMPONENT_TYPE,
            content: {
                typeCode: COMPONENT_TYPE,
                itemtype: COMPONENT_TYPE,
                catalogVersion: CATALOG_VERSION,
                visible: false
            }
        };

        // WHEN
        const result = await service.openNestedComponentEditor(componentInfo, STACK_ID);

        // THEN
        expect(result).toBe(EXPECTED_RESULT);
        expect(editorModalService.open).toHaveBeenCalledWith(
            expectedComponentData,
            null,
            null,
            null,
            null,
            STACK_ID
        );
    });

    it('WHEN openNestedComponentEditor is called THEN the editor is properly opened with the default values', async () => {
        // GIVEN
        const COMPONENT_TYPE = 'Some Component Type';
        const COMPONENT_UUID = 'some component uuid';
        const CATALOG_VERSION = 'some catalog version uuid';

        const componentInfo = {
            componentType: COMPONENT_TYPE,
            componentUuid: COMPONENT_UUID,
            content: {
                catalogVersion: CATALOG_VERSION
            }
        };

        const expectedComponentData = {
            smarteditComponentUuid: COMPONENT_UUID,
            smarteditComponentType: COMPONENT_TYPE,
            content: {
                typeCode: COMPONENT_TYPE,
                itemtype: COMPONENT_TYPE,
                catalogVersion: CATALOG_VERSION,
                visible: true
            }
        };

        // WHEN
        const result = await service.openNestedComponentEditor(componentInfo, STACK_ID);

        // THEN
        expect(result).toBe(EXPECTED_RESULT);
        expect(editorModalService.open).toHaveBeenCalledWith(
            expectedComponentData,
            null,
            null,
            null,
            null,
            STACK_ID
        );
    });
});
