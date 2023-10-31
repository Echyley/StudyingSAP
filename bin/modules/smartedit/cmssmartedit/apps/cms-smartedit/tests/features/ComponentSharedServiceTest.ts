/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentSharedService } from 'cmssmartedit/services';
import { ComponentInfoService } from 'cmssmartedit/services/ComponentInfoService';
import { annotationService, GatewayProxied } from 'smarteditcommons';

describe('ComponentSharedService', () => {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    const COMPONENT_NAME = 'some component name';
    const COMPONENT_TYPE = 'some component type';
    const COMPONENT_UID = 'some component uid';
    const COMPONENT_UUID = 'some component uuid';
    const SLOT_UUID_1 = 'some slot uuid_1';
    const SLOT_UUID_2 = 'some slot uuid_2';

    const SLOTS_NOT_FOUND_EXCEPTION_MSG =
        'ComponentSharedService::isComponentShared - Component must have slots property.';

    const COMPONENT_WITH_NO_SLOTS = {
        name: COMPONENT_NAME,
        typeCode: COMPONENT_TYPE,
        itemtype: COMPONENT_TYPE,
        uid: COMPONENT_UID,
        uuid: COMPONENT_UUID,
        visible: true,
        cloneable: true,
        slots: undefined,
        catalogVersion: undefined
    };

    const NON_SHARED_COMPONENT = {
        name: COMPONENT_NAME,
        typeCode: COMPONENT_TYPE,
        itemtype: COMPONENT_TYPE,
        uid: COMPONENT_UID,
        uuid: COMPONENT_UUID,
        visible: true,
        cloneable: true,
        slots: [SLOT_UUID_1],
        catalogVersion: undefined
    };

    const SHARED_COMPONENT = {
        name: COMPONENT_NAME,
        typeCode: COMPONENT_TYPE,
        itemtype: COMPONENT_TYPE,
        uid: COMPONENT_UID,
        uuid: COMPONENT_UUID,
        visible: true,
        cloneable: true,
        slots: [SLOT_UUID_1, SLOT_UUID_2],
        catalogVersion: undefined
    };

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    let componentInfoService: jasmine.SpyObj<ComponentInfoService>;
    let service: ComponentSharedService;

    beforeEach(() => {
        componentInfoService = jasmine.createSpyObj('componentSharedService', ['getById']);
        service = new ComponentSharedService(componentInfoService);
    });

    it('WHEN component is set-up THEN it must be proxied', () => {
        const decoratorObj = annotationService.getClassAnnotation(
            ComponentSharedService,
            GatewayProxied
        );
        expect(decoratorObj).toEqual([]);
    });

    it('GIVEN component with no slots WHEN isSlotShared is called THEN it must throw an exception', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(COMPONENT_WITH_NO_SLOTS));

        // WHEN
        await service.isComponentShared(COMPONENT_WITH_NO_SLOTS).catch((error) => {
            // THEN
            expect(error.message).toEqual(SLOTS_NOT_FOUND_EXCEPTION_MSG);
        });
    });

    it('GIVEN uuid of component with no slots WHEN isSlotShared is called THEN it must throw an exception', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(COMPONENT_WITH_NO_SLOTS));

        // WHEN
        await service.isComponentShared(COMPONENT_UUID).catch((error) => {
            // THEN
            expect(error.message).toEqual(SLOTS_NOT_FOUND_EXCEPTION_MSG);
        });
    });

    it('GIVEN non-shared component WHEN isSlotShared is called THEN it must return false', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(NON_SHARED_COMPONENT));
        // WHEN
        const isShared = await service.isComponentShared(NON_SHARED_COMPONENT);

        // THEN
        expect(isShared).toBe(false);
    });

    it('GIVEN uuid of non-shared component WHEN isSlotShared is called THEN it must return false', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(NON_SHARED_COMPONENT));

        // WHEN
        const isShared = await service.isComponentShared(COMPONENT_UUID);

        // THEN
        expect(isShared).toBe(false);
    });

    it('GIVEN shared component WHEN isSlotShared is called THEN it must return true', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(SHARED_COMPONENT));

        // WHEN
        const isShared = await service.isComponentShared(SHARED_COMPONENT);

        // THEN
        expect(isShared).toBe(true);
    });

    it('GIVEN uuid of shared component WHEN isSlotShared is called THEN it must return true', async () => {
        // GIVEN
        componentInfoService.getById.and.returnValue(Promise.resolve(SHARED_COMPONENT));

        // WHEN
        const isShared = await service.isComponentShared(COMPONENT_UUID);

        // THEN
        expect(isShared).toBe(true);
    });
});
