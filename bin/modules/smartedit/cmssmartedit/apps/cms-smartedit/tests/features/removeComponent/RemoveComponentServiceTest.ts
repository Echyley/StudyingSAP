/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { COMPONENT_REMOVED_EVENT, ICMSComponent } from 'cmscommons';
import { ComponentInfoService } from 'cmssmartedit/services/ComponentInfoService';
import { RemoveComponentService } from 'cmssmartedit/services/RemoveComponentServiceInner';
import {
    IAlertService,
    IRenderService,
    IRestService,
    IRestServiceFactory,
    SystemEventService,
    functionsUtils
} from 'smarteditcommons';

/* jshint unused:false, undef:false */
describe('RemoveComponentService', () => {
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let removeComponentService: RemoveComponentService;
    let restServiceForRemoveComponent: jasmine.SpyObj<IRestService<void>>;
    let renderService: jasmine.SpyObj<IRenderService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let componentInfoService: jasmine.SpyObj<ComponentInfoService>;

    beforeEach(() => {
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish'
        ]);

        restServiceForRemoveComponent = jasmine.createSpyObj('restServiceForRemoveComponent', [
            'remove'
        ]);

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.callFake((): any => restServiceForRemoveComponent);

        componentInfoService = jasmine.createSpyObj('componentInfoService', ['getById']);
        componentInfoService.getById.and.callFake((componentUuid, forceRetrieval) => {
            const returnValue: any = forceRetrieval
                ? COMPONENT_TO_REMOVE_UPDATED
                : COMPONENT_TO_REMOVE;
            return Promise.resolve(returnValue);
        });

        renderService = jasmine.createSpyObj('renderService', ['renderSlots']);
        renderService.renderSlots.and.returnValue(Promise.resolve());

        alertService = jasmine.createSpyObj('alertService', ['showDanger']);

        removeComponentService = new RemoveComponentService(
            restServiceFactory,
            alertService,
            componentInfoService,
            renderService,
            systemEventService
        );
    });

    const payload = {
        slotId: 'testSlotId',
        componentId: 'testContainerId'
    };

    const COMPONENT_ID = 'testComponentId';
    const COMPONENT_UUID = 'testComponentUuid';
    const COMPONENT_TYPE = 'componentType';
    const SLOT_ID = 'testSlotId';
    const SLOT_UUID = 'testSlotUuid';
    const COMPONENT_TO_REMOVE_INFO = {
        slotId: SLOT_ID,
        slotUuid: SLOT_UUID,
        componentId: COMPONENT_ID,
        componentType: COMPONENT_TYPE,
        componentUuid: COMPONENT_UUID,
        slotOperationRelatedId: 'testContainerId',
        slotOperationRelatedType: 'testContainerType'
    };

    const COMPONENT_TO_REMOVE = {
        someProperty: 'some value',
        slots: [SLOT_UUID]
    };

    const COMPONENT_TO_REMOVE_UPDATED = ({
        someProperty: 'some value',
        slots: []
    } as unknown) as ICMSComponent;

    it('should remove a component from a slot AND return the removed component', async () => {
        // GIVEN
        restServiceForRemoveComponent.remove.and.returnValue(Promise.resolve());

        // WHEN
        const component = await removeComponentService.removeComponent(COMPONENT_TO_REMOVE_INFO);

        // THEN

        expect(restServiceForRemoveComponent.remove).toHaveBeenCalledWith(payload);
        expect(renderService.renderSlots).toHaveBeenCalledWith(SLOT_ID);
        expect(component).toBe(COMPONENT_TO_REMOVE_UPDATED);
        expect(componentInfoService.getById).toHaveBeenCalledTimes(2);
        expect(componentInfoService.getById).toHaveBeenCalledWith(
            COMPONENT_TO_REMOVE_INFO.componentUuid
        );
        expect(componentInfoService.getById).toHaveBeenCalledWith(
            COMPONENT_TO_REMOVE_INFO.componentUuid,
            true
        );
    });

    it('should not remove a component from a slot', async () => {
        // GIVEN
        restServiceForRemoveComponent.remove.and.returnValue(Promise.reject());

        try {
            // WHEN
            await removeComponentService.removeComponent(COMPONENT_TO_REMOVE_INFO);

            functionsUtils.assertFail();
        } catch {
            // THEN
            expect(restServiceForRemoveComponent.remove).toHaveBeenCalledWith(payload);
            expect(alertService.showDanger).toHaveBeenCalled();
            expect(renderService.renderSlots).not.toHaveBeenCalled();
            expect(componentInfoService.getById).not.toHaveBeenCalled();
        }
    });

    it('GIVEN component can be removed WHEN removeComponent is called THEN it must send a COMPONENT_REMOVED_EVENT event', async () => {
        // GIVEN
        restServiceForRemoveComponent.remove.and.returnValue(Promise.resolve());

        // WHEN
        await removeComponentService.removeComponent(COMPONENT_TO_REMOVE_INFO);

        // THEN
        expect(systemEventService.publish).toHaveBeenCalledWith(
            COMPONENT_REMOVED_EVENT,
            COMPONENT_TO_REMOVE
        );
        expect(componentInfoService.getById).toHaveBeenCalledTimes(2);
        expect(componentInfoService.getById).toHaveBeenCalledWith(
            COMPONENT_TO_REMOVE_INFO.componentUuid
        );
        expect(componentInfoService.getById).toHaveBeenCalledWith(
            COMPONENT_TO_REMOVE_INFO.componentUuid,
            true
        );
    });

    it('GIVEN component cannot be removed WHEN removeComponent is called THEN it must not send a COMPONENT_REMOVED_EVENT event', async () => {
        // GIVEN
        restServiceForRemoveComponent.remove.and.returnValue(Promise.reject());
        try {
            // WHEN
            await removeComponentService.removeComponent(COMPONENT_TO_REMOVE_INFO);

            functionsUtils.assertFail();
        } catch {
            // THEN
            expect(systemEventService.publish).not.toHaveBeenCalled();
            expect(componentInfoService.getById).not.toHaveBeenCalled();
        }
    });
});
