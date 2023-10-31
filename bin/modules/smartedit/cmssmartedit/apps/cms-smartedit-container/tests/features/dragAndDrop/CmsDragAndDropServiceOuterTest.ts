/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    DRAG_AND_DROP_EVENTS,
    IComponentEditingFacade,
    IPageContentSlotsComponentsRestService
} from 'cmscommons';
import { CmsDragAndDropService, CmsPageTreeDropElement } from 'cmssmarteditcontainer/services';
import {
    COMPONENT_IN_SLOT_STATUS,
    CrossFrameEventService,
    DragAndDropService,
    GatewayFactory,
    ISharedDataService,
    ISlotRestrictionsService,
    IWaitDialogService,
    MessageGateway,
    SystemEventService
} from 'smarteditcommons';

describe('CmsDragAndDropServiceOuter', () => {
    const ID_ATTRIBUTE = 'data-smartedit-component-id';
    const UUID_ATTRIBUTE = 'data-smartedit-component-uuid';
    const TYPE_ATTRIBUTE = 'data-smartedit-component-type';
    const CMS_DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

    const TARGET_SELECTOR = '.se-page-tree .se-page-tree-drop-area';
    const SOURCE_SELECTOR = ".smartEditComponent[data-smartedit-component-type!='ContentSlot']";

    const SCROLL_AREA_SELECTOR = '.se-page-tree-scroll-area';
    const ALLOWED_DROP_CLASS = 'se-page-tree-slot-allowed-drop';
    const DRAGGED_COMPONENT_CLASS = 'se-page-tree-component-dragged';
    const NODE_SMARTEDIT_ELEMENT_UUID = 'node-smartedit-element-uuid';
    const SLOT_ID = 'smartedit-slot-id';
    const SLOT_UUID = 'smartedit-slot-uuid';
    const SLOT_CATALOG_VERSION_UUID = 'smartedit-slot-catalog-version-uuid';
    const COMPONENT_ID = 'smartedit-component-id';
    const COMPONENT_UUID = 'smartedit-component-uuid';
    const COMPONENT_TYPE = 'smartedit-component-type';
    const PAGE_TREE_DRAG_AND_DROP_ID = 'se.cms.page.tree.dragAndDrop';
    const PAGE_TREE_SOURCE_SELECTOR = '.se-page-tree-component-node';
    const SE_PAGE_TREE_SLOT = 'SE-PAGE-TREE-SLOT';
    const SE_PAGE_TREE_COMPONENT = 'SE-PAGE-TREE-COMPONENT';
    const DROP_EFFECT_COPY = 'copy';
    const DROP_EFFECT_NONE = 'none';

    const highlightedElement: CmsPageTreeDropElement = {
        original: null,
        id: 'element-data-uuid',
        isSlot: true,
        slotId: 'slot_id',
        slotUuid: 'slot_uuid',
        slotCatalogVersion: 'slot_catalog_version',
        componentId: 'component_id',
        componentUuid: 'component_uuid',
        isAllowed: false
    };
    const componentInfo = {
        id: 'component_id',
        uuid: 'component_uuid',
        type: 'component_type',
        slotUuid: 'slot_uuid',
        slotId: 'slot_id',
        containerId: 'container_id',
        conteinerType: 'container_type'
    };

    const targetSlot = {
        slotId: 'target_slot_id',
        slotUuid: 'target_slot_uuid',
        slotCatalogVersion: 'target_slot_catalog_version'
    };
    const sourceSlot = {
        slotId: 'source_slot_id',
        slotUuid: 'source_slot_uuid',
        slotCatalogVersion: 'source_slot_catalog_version'
    };

    let dragAndDropService: jasmine.SpyObj<DragAndDropService>;
    let gateway: jasmine.SpyObj<MessageGateway>;
    let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    let yjQuery: any;
    let slotRestrictionsService: jasmine.SpyObj<ISlotRestrictionsService>;
    let waitDialogService: jasmine.SpyObj<IWaitDialogService>;
    let componentEditingFacade: jasmine.SpyObj<IComponentEditingFacade>;
    let pageContentSlotsComponentsRestService: jasmine.SpyObj<IPageContentSlotsComponentsRestService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let scrollable: jasmine.SpyObj<JQuery<HTMLElement>>;

    let cmsDragAndDropService: CmsDragAndDropService;
    let cmsDragAndDropServiceAny: any;
    beforeEach(() => {
        dragAndDropService = jasmine.createSpyObj<DragAndDropService>('dragAndDropService', [
            'register',
            'unregister',
            'apply',
            'update'
        ]);

        gateway = jasmine.createSpyObj<MessageGateway>('gateway', ['publish']);
        gatewayFactory = jasmine.createSpyObj<GatewayFactory>('gatewayFactory', ['createGateway']);
        gatewayFactory.createGateway.and.returnValue(gateway);

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );

        yjQuery = jasmine.createSpy('yjQuery');
        slotRestrictionsService = jasmine.createSpyObj('slotRestrictionsService', [
            'isSlotEditable',
            'determineComponentStatusInSlot'
        ]);
        waitDialogService = jasmine.createSpyObj('waitDialogService', [
            'showWaitModal',
            'hideWaitModal'
        ]);
        componentEditingFacade = jasmine.createSpyObj('componentEditingFacade', [
            'addNewComponentToSlot',
            'cloneExistingComponentToSlot',
            'addExistingComponentToSlot',
            'moveComponent'
        ]);
        pageContentSlotsComponentsRestService = jasmine.createSpyObj(
            'pageContentSlotsComponentsRestService',
            ['getComponentsForSlot']
        );
        scrollable = jasmine.createSpyObj('scrollable', ['scrollTop', 'get']);
        yjQuery.withArgs(SCROLL_AREA_SELECTOR as any).and.returnValue(scrollable);

        cmsDragAndDropService = new CmsDragAndDropService(
            dragAndDropService,
            gatewayFactory,
            sharedDataService,
            systemEventService,
            yjQuery,
            slotRestrictionsService,
            waitDialogService,
            componentEditingFacade,
            pageContentSlotsComponentsRestService,
            crossFrameEventService
        );
        cmsDragAndDropServiceAny = cmsDragAndDropService;
    });

    it('WHEN cmsDragAndDropService is created THEN a gateway is created to communicate with the inner frame', () => {
        // THEN
        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('cmsDragAndDrop');
        expect(cmsDragAndDropServiceAny.gateway).toBe(gateway);
    });

    describe('register', () => {
        it('WHEN register is called THEN it is registered in the base drag and drop service', () => {
            // WHEN
            cmsDragAndDropService.register();

            // THEN
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            expect(dragAndDropService.register).toHaveBeenCalledTimes(2);
            expect(arg.id).toBe(CMS_DRAG_AND_DROP_ID);
            expect(arg.sourceSelector).toBe(SOURCE_SELECTOR);
            expect(arg.targetSelector).toBe(TARGET_SELECTOR);
            expect(arg.enableScrolling).toBe(false);

            const secondCallArg = dragAndDropService.register.calls.argsFor(1)[0];
            expect(secondCallArg.id).toBe(PAGE_TREE_DRAG_AND_DROP_ID);
            expect(secondCallArg.sourceSelector).toBe(PAGE_TREE_SOURCE_SELECTOR);
            expect(secondCallArg.targetSelector).toBe(TARGET_SELECTOR);
            expect(secondCallArg.enableScrolling).toBe(false);
        });

        it('WHEN register is called THEN it is registered with the right onStart callback', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onStart').and.returnValue(undefined);
            spyOn(cmsDragAndDropServiceAny, 'onStartInPageTree').and.returnValue(undefined);
            // WHEN
            cmsDragAndDropService.register();

            // THEN
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.startCallback(null);
            const arg2 = dragAndDropService.register.calls.argsFor(1)[0];
            arg2.startCallback(null);

            expect(cmsDragAndDropServiceAny.onStart).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.onStartInPageTree).toHaveBeenCalled();
        });

        it('WHEN register is called THEN it is registered with the right onStop callback', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onStop').and.returnValue(undefined);
            spyOn(cmsDragAndDropServiceAny, 'onStopInPageTree').and.returnValue(undefined);
            // WHEN
            cmsDragAndDropService.register();

            // THEN
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.stopCallback(null);
            const arg2 = dragAndDropService.register.calls.argsFor(1)[0];
            arg2.stopCallback(null);

            expect(cmsDragAndDropServiceAny.onStop).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.onStopInPageTree).toHaveBeenCalled();
        });
    });

    it('WHEN apply is called THEN the cms service is applied in the base drag and drop service', () => {
        // WHEN
        cmsDragAndDropService.apply();

        // THEN
        expect(dragAndDropService.apply).toHaveBeenCalled();
    });

    it('WHEN update is called THEN the cms service is updated in the base drag and drop service', () => {
        // WHEN
        cmsDragAndDropService.update();

        // THEN
        expect(dragAndDropService.update).toHaveBeenCalledWith(CMS_DRAG_AND_DROP_ID);
    });

    it('WHEN unregister is called THEN the cms service is unregistered from the base drag and drop service', () => {
        // WHEN
        cmsDragAndDropService.unregister();

        // THEN
        expect(dragAndDropService.unregister).toHaveBeenCalledWith([
            CMS_DRAG_AND_DROP_ID,
            PAGE_TREE_DRAG_AND_DROP_ID
        ]);
    });

    it('WHEN drag is started THEN the service informs other components', async () => {
        // GIVEN
        const component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake((arg: string) => {
            if (arg === ID_ATTRIBUTE) {
                return componentInfo.id;
            } else if (arg === UUID_ATTRIBUTE) {
                return componentInfo.uuid;
            } else if (arg === TYPE_ATTRIBUTE) {
                return componentInfo.type;
            }
            throw new Error('attribute not found');
        });

        const event: any = {
            target: 'some target',
            dataTransfer: {
                effectAllowed: 'all'
            }
        };
        const draggedElement = {
            closest: () => component
        };

        spyOn(cmsDragAndDropServiceAny, 'getSelector').and.returnValue(draggedElement);
        spyOn(cmsDragAndDropServiceAny, 'scroll');
        sharedDataService.get.and.returnValue(Promise.resolve(false));

        // WHEN
        await cmsDragAndDropServiceAny.onStart(event);
        cmsDragAndDropServiceAny.onStop();

        // THEN
        const dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: null,
            slotUuid: null,
            cloneOnDrop: false
        };

        expect(cmsDragAndDropServiceAny.gateway.publish).toHaveBeenCalledWith(
            'CMS_DRAG_STARTED',
            dragInfo
        );
        expect(systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STARTED');
        expect(cmsDragAndDropServiceAny.scrollable).toBe(scrollable);
        expect(cmsDragAndDropServiceAny.dragInfo).toEqual(dragInfo);
    });

    it('WHEN drag is stopped THEN the inner frame is informed', () => {
        // WHEN
        cmsDragAndDropServiceAny.onStop();

        // THEN
        expect(cmsDragAndDropServiceAny.gateway.publish).toHaveBeenCalledWith(
            'CMS_DRAG_STOPPED',
            null
        );

        expect(cmsDragAndDropServiceAny.isMouseInBottomScrollArea).toBeFalse();
        expect(cmsDragAndDropServiceAny.isMouseInTopScrollArea).toBeFalse();
    });

    it('WHEN drag component on page tree started THEN the scrollable and dragInfo will be set', async () => {
        // WHEN
        const component = jasmine.createSpyObj('component', ['attr', 'addClass', 'closest']);

        component.attr.and.callFake((arg: string) => {
            if (arg === COMPONENT_ID) {
                return componentInfo.id;
            } else if (arg === COMPONENT_UUID) {
                return componentInfo.uuid;
            } else if (arg === COMPONENT_TYPE) {
                return componentInfo.type;
            } else if (arg === SLOT_UUID) {
                return componentInfo.slotUuid;
            } else if (arg === SLOT_ID) {
                return componentInfo.slotId;
            } else if (arg === 'smartedit-container-id') {
                return componentInfo.containerId;
            } else if (arg === 'smartedit-container-type') {
                return componentInfo.conteinerType;
            }
            throw new Error('attribute not found');
        });

        spyOn(cmsDragAndDropServiceAny, 'scroll');
        sharedDataService.get.and.returnValue(Promise.resolve(false));

        const dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: componentInfo.slotId,
            slotUuid: componentInfo.slotUuid,
            slotOperationRelatedId: componentInfo.containerId,
            slotOperationRelatedType: componentInfo.conteinerType,
            cloneOnDrop: false
        };
        const event: any = {
            target: 'some target',
            dataTransfer: {
                effectAllowed: 'all'
            }
        };
        yjQuery.withArgs(event.target).and.returnValue(component);
        component.closest.and.returnValue(component);

        // THEN
        await cmsDragAndDropServiceAny.onStartInPageTree(event);
        cmsDragAndDropServiceAny.onStop();

        expect(component.addClass).toHaveBeenCalledWith(DRAGGED_COMPONENT_CLASS);
        expect(cmsDragAndDropServiceAny.scrollable).toBe(scrollable);
        expect(cmsDragAndDropServiceAny.dragInfo).toEqual(dragInfo);
        expect(event.dataTransfer.effectAllowed).toEqual(DROP_EFFECT_COPY);
    });

    it('When drag enter the page tree THEN set mouse area and highlight element', () => {
        // WHEN
        const event: any = jasmine.createSpyObj('event', ['preventDefault']);
        event.dataTransfer = {};

        spyOn(cmsDragAndDropServiceAny, 'setMouseArea');
        spyOn(cmsDragAndDropServiceAny, 'highlightElement');

        // THEN
        cmsDragAndDropServiceAny.onDragEnter(event);

        expect(cmsDragAndDropServiceAny.setMouseArea).toHaveBeenCalledWith(event);
        expect(cmsDragAndDropServiceAny.highlightElement).toHaveBeenCalledWith(event);
    });

    it('When drag over the page tree THEN set mouse area and highlight element', () => {
        // WHEN
        const event = jasmine.createSpyObj('event', ['preventDefault']);
        event.dataTransfer = {};

        spyOn(cmsDragAndDropServiceAny, 'setMouseArea');
        spyOn(cmsDragAndDropServiceAny, 'highlightElement');

        // THEN
        cmsDragAndDropServiceAny.onDragOver(event);
        expect(cmsDragAndDropServiceAny.setMouseArea).toHaveBeenCalledWith(event);
        expect(cmsDragAndDropServiceAny.highlightElement).toHaveBeenCalledWith(event);
    });

    it('When drop component on the allowed highlighted slot THEN put the component at first place in slot ', async () => {
        // WHEN
        highlightedElement.isAllowed = true;
        highlightedElement.isSlot = true;
        cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
        spyOn(cmsDragAndDropServiceAny, 'dropElementToSlot');

        // THEN
        await cmsDragAndDropServiceAny.onDrop(null);

        expect(cmsDragAndDropServiceAny.dropElementToSlot).toHaveBeenCalledWith(
            highlightedElement.slotId,
            highlightedElement.slotUuid,
            0,
            highlightedElement.slotCatalogVersion
        );
    });

    it('When drop component before the allowed highlighted component THEN it will put the component ', async () => {
        // WHEN
        highlightedElement.isAllowed = true;
        highlightedElement.isSlot = false;
        highlightedElement.isBottom = false;
        highlightedElement.original = null;
        cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
        spyOn(cmsDragAndDropServiceAny, 'dropElementToSlot');
        spyOn(cmsDragAndDropServiceAny, 'getComponentIndexInSlot').and.returnValue(
            Promise.resolve(2)
        );
        // THEN
        await cmsDragAndDropServiceAny.onDrop(null);

        expect(cmsDragAndDropServiceAny.dropElementToSlot).toHaveBeenCalledWith(
            highlightedElement.slotId,
            highlightedElement.slotUuid,
            2,
            highlightedElement.slotCatalogVersion
        );
    });

    it('When drop component after the allowed highlighted component THEN it will put the component ', async () => {
        // WHEN
        highlightedElement.isAllowed = true;
        highlightedElement.isSlot = false;
        highlightedElement.isBottom = true;
        cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
        spyOn(cmsDragAndDropServiceAny, 'dropElementToSlot');
        spyOn(cmsDragAndDropServiceAny, 'getComponentIndexInSlot').and.returnValue(
            Promise.resolve(2)
        );
        // THEN
        await cmsDragAndDropServiceAny.onDrop(null);

        expect(cmsDragAndDropServiceAny.dropElementToSlot).toHaveBeenCalledWith(
            highlightedElement.slotId,
            highlightedElement.slotUuid,
            3,
            highlightedElement.slotCatalogVersion
        );
    });

    it('When drop component on the not allowed highlighted slot THEN will never put the component in the slot ', async () => {
        // WHEN
        highlightedElement.isAllowed = false;
        highlightedElement.original = null;
        cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
        spyOn(cmsDragAndDropServiceAny, 'dropElementToSlot');

        // THEN
        await cmsDragAndDropServiceAny.onDrop(null);

        expect(cmsDragAndDropServiceAny.dropElementToSlot).toHaveBeenCalledTimes(0);
    });

    it('When drag from component-menu THEN will add a new component to slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: null,
            componentUuid: null,
            componentType: componentInfo.type,
            slotId: null,
            slotUuid: null,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: false
        };

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            0,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledWith(
            { targetSlotId: targetSlot.slotId, targetSlotUUId: targetSlot.slotUuid },
            targetSlot.slotCatalogVersion,
            componentInfo.type,
            0
        );

        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.moveComponent).toHaveBeenCalledTimes(0);
    });

    it('When drag from component-menu THEN will add a existing component to slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: null,
            slotUuid: null,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: false
        };

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            0,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(
            targetSlot.slotId,
            {
                componentId: componentInfo.id,
                componentUuid: componentInfo.uuid,
                componentType: componentInfo.type
            },
            0
        );

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.moveComponent).toHaveBeenCalledTimes(0);
    });

    it('When drag from component-menu THEN will clone a existing component to slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: null,
            slotUuid: null,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: true
        };

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            0,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledWith({
            targetSlotId: targetSlot.slotId,
            dragInfo: {
                componentId: componentInfo.id,
                componentUuid: componentInfo.uuid,
                componentType: componentInfo.type
            },
            position: 0
        });

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.moveComponent).toHaveBeenCalledTimes(0);
    });

    it('When drag from page tree THEN will move component between slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: sourceSlot.slotId,
            slotUuid: sourceSlot.slotUuid,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: false
        };

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            0,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
            sourceSlot.slotId,
            targetSlot.slotId,
            componentInfo.id,
            0
        );

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
    });

    it('When drag from page tree THEN will move component in same slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: targetSlot.slotId,
            slotUuid: targetSlot.slotUuid,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: false
        };
        spyOn(cmsDragAndDropServiceAny, 'getComponentIndexInSlot').and.returnValue(1);

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            4,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
            targetSlot.slotId,
            targetSlot.slotId,
            componentInfo.id,
            3
        );

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
    });

    it('When drag from page tree THEN will not move component in same slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: targetSlot.slotId,
            slotUuid: targetSlot.slotUuid,
            slotOperationRelatedId: null,
            slotOperationRelatedType: null,
            cloneOnDrop: false
        };
        spyOn(cmsDragAndDropServiceAny, 'getComponentIndexInSlot').and.returnValue(3);

        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            4,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.moveComponent).toHaveBeenCalledTimes(0);

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
    });

    it('When drag from page tree THEN will move container in same slot', async () => {
        // WHEN
        cmsDragAndDropServiceAny.dragInfo = {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: sourceSlot.slotId,
            slotUuid: sourceSlot.slotUuid,
            slotOperationRelatedId: componentInfo.containerId,
            slotOperationRelatedType: componentInfo.conteinerType,
            cloneOnDrop: false
        };
        await cmsDragAndDropServiceAny.dropElementToSlot(
            targetSlot.slotId,
            targetSlot.slotUuid,
            0,
            targetSlot.slotCatalogVersion
        );

        expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
            sourceSlot.slotId,
            targetSlot.slotId,
            componentInfo.containerId,
            0
        );

        expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledTimes(0);
        expect(componentEditingFacade.cloneExistingComponentToSlot).toHaveBeenCalledTimes(0);
    });

    describe('onDragLeave', () => {
        const original = jasmine.createSpyObj('original', [
            'removeClass',
            'get',
            'getBoundingClientRect',
            'children'
        ]);
        const dropArea = jasmine.createSpyObj('dropArea', ['css']);

        const event = {};
        beforeEach(() => {
            original.get.and.returnValue(original);
            original.getBoundingClientRect.and.returnValue({});
            highlightedElement.original = original;
            cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
            const element = jasmine.createSpyObj('element', ['attr']);
            element.attr.and.returnValue('element-uuid');
            spyOn(cmsDragAndDropServiceAny, 'getSelector').and.returnValue(element);
            original.children.and.returnValue(dropArea);
        });

        it('When drag leave triggered and mouse is not in event target region THEN clear highlighted Element', () => {
            // WHEN
            highlightedElement.isSlot = false;
            spyOn(cmsDragAndDropServiceAny, 'dragLeaveEventInRegion').and.returnValue(false);
            // THEN
            cmsDragAndDropServiceAny.onDragLeave(event);

            expect(original.removeClass).toHaveBeenCalledWith(ALLOWED_DROP_CLASS);
            expect(dropArea.css).toHaveBeenCalledWith('display', 'none');
            expect(dropArea.css).toHaveBeenCalledWith('top', '');
            expect(dropArea.css).toHaveBeenCalledWith('bottom', '');
            expect(cmsDragAndDropServiceAny.highlightedElement).toBeNull();
            highlightedElement.original = null;
        });

        it('When drag leave triggered and mouse is in event target region THEN do not clear highlighted Element', () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'dragLeaveEventInRegion').and.returnValue(true);

            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedElement');
            // THEN
            cmsDragAndDropServiceAny.onDragLeave(event);

            expect(cmsDragAndDropServiceAny.clearHighlightedElement).not.toHaveBeenCalled();
        });
        afterEach(() => {
            highlightedElement.original = null;
        });
    });

    describe('highlight slot element', () => {
        const element = jasmine.createSpyObj('element', [
            'attr',
            'addClass',
            'parent',
            'prop',
            'closest'
        ]);
        const event: any = jasmine.createSpyObj('event', ['preventDefault']);
        event.dataTransfer = {};
        event.target = {};
        beforeEach(() => {
            cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
            element.parent.and.returnValue(element);
            element.prop.and.returnValue(SE_PAGE_TREE_SLOT);

            element.attr.and.callFake((arg: string) => {
                if (arg === NODE_SMARTEDIT_ELEMENT_UUID) {
                    return NODE_SMARTEDIT_ELEMENT_UUID;
                } else if (arg === SLOT_ID) {
                    return targetSlot.slotId;
                } else if (arg === SLOT_UUID) {
                    return targetSlot.slotUuid;
                } else if (arg === SLOT_CATALOG_VERSION_UUID) {
                    return targetSlot.slotCatalogVersion;
                } else if (arg === 'expanded') {
                    return 'false';
                }
                throw new Error('attribute not found');
            });

            yjQuery.withArgs(event.target).and.returnValue(element);
            element.closest.and.returnValue(element);
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedElement').and.returnValue(undefined);
            spyOn(cmsDragAndDropServiceAny, 'expandedSlotInPageTree').and.returnValue(undefined);
        });

        it('When drag over allowed unexpanded slot THEN highlight slot and expanded it', async () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'isAllowedSlot').and.returnValue(Promise.resolve(true));
            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toEqual({
                original: element,
                id: NODE_SMARTEDIT_ELEMENT_UUID,
                isSlot: true,
                slotId: targetSlot.slotId,
                slotUuid: targetSlot.slotUuid,
                slotCatalogVersion: targetSlot.slotCatalogVersion,
                isAllowed: true
            });
            expect(element.addClass).toHaveBeenCalledWith(ALLOWED_DROP_CLASS);
            expect(cmsDragAndDropServiceAny.expandedSlotInPageTree).toHaveBeenCalledWith(
                false,
                NODE_SMARTEDIT_ELEMENT_UUID
            );
            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_COPY);
        });

        it('When drag over not allowed unexpanded slot THEN do not highlight slot and do not expanded it', async () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'isAllowedSlot').and.returnValue(
                Promise.resolve(false)
            );
            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toEqual({
                original: element,
                id: NODE_SMARTEDIT_ELEMENT_UUID,
                isSlot: true,
                slotId: targetSlot.slotId,
                slotUuid: targetSlot.slotUuid,
                slotCatalogVersion: targetSlot.slotCatalogVersion,
                isAllowed: false
            });

            expect(cmsDragAndDropServiceAny.expandedSlotInPageTree).toHaveBeenCalledWith(
                false,
                NODE_SMARTEDIT_ELEMENT_UUID
            );
            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_NONE);
        });

        it('When drag over same slot THEN change the cursor', async () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'isAllowedSlot').and.returnValue(Promise.resolve(true));
            element.attr.and.callFake((arg: string) => {
                if (arg === NODE_SMARTEDIT_ELEMENT_UUID) {
                    return highlightedElement.id;
                } else if (arg === SLOT_ID) {
                    return highlightedElement.slotId;
                } else if (arg === SLOT_UUID) {
                    return highlightedElement.slotUuid;
                } else if (arg === SLOT_CATALOG_VERSION_UUID) {
                    return highlightedElement.slotCatalogVersion;
                } else if (arg === 'expanded') {
                    return 'false';
                }
                throw new Error('attribute not found');
            });
            highlightedElement.isAllowed = false;

            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toBe(highlightedElement);
            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_NONE);
            expect(cmsDragAndDropServiceAny.isAllowedSlot).toHaveBeenCalledTimes(0);
            expect(cmsDragAndDropServiceAny.clearHighlightedElement).toHaveBeenCalledTimes(0);
            expect(cmsDragAndDropServiceAny.expandedSlotInPageTree).toHaveBeenCalledTimes(0);
        });
    });

    describe('highlight component element', () => {
        const element = jasmine.createSpyObj('element', [
            'attr',
            'addClass',
            'parent',
            'prop',
            'removeClass',
            'closest',
            'children',
            'first',
            'css'
        ]);
        const event = jasmine.createSpyObj('event', ['preventDefault']);
        event.dataTransfer = {};
        event.target = {};

        beforeEach(() => {
            cmsDragAndDropServiceAny.highlightedElement = highlightedElement;
            element.parent.and.returnValue(element);
            element.prop.and.returnValue(SE_PAGE_TREE_COMPONENT);
            element.attr.and.callFake((arg: string) => {
                if (arg === NODE_SMARTEDIT_ELEMENT_UUID) {
                    return NODE_SMARTEDIT_ELEMENT_UUID;
                } else if (arg === SLOT_ID) {
                    return targetSlot.slotId;
                } else if (arg === SLOT_UUID) {
                    return targetSlot.slotUuid;
                } else if (arg === SLOT_CATALOG_VERSION_UUID) {
                    return targetSlot.slotCatalogVersion;
                } else if (arg === COMPONENT_ID) {
                    return componentInfo.id;
                } else if (arg === COMPONENT_UUID) {
                    return componentInfo.uuid;
                }
                throw new Error('attribute not found');
            });

            yjQuery.withArgs(event.target).and.returnValue(element);
            element.closest.and.returnValue(element);
            element.children.and.returnValue(element);
            element.first.and.returnValue(element);
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedElement');
            spyOn(cmsDragAndDropServiceAny, 'isAllowedSlot').and.returnValue(Promise.resolve(true));
        });

        it('When drag over allowed component top region THEN highlight component', async () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInTopHalfRegion').and.returnValue(true);

            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toEqual({
                original: element,
                id: NODE_SMARTEDIT_ELEMENT_UUID,
                isSlot: false,
                slotId: targetSlot.slotId,
                slotUuid: targetSlot.slotUuid,
                slotCatalogVersion: targetSlot.slotCatalogVersion,
                isAllowed: true,
                componentId: componentInfo.id,
                componentUuid: componentInfo.uuid,
                isBottom: false
            });
            expect(element.css).toHaveBeenCalledWith('top', '-5px');
            expect(element.css).toHaveBeenCalledWith('bottom', '');
            expect(element.css).toHaveBeenCalledWith('display', 'flex');
            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_COPY);
        });

        it('When drag over allowed component bottom region THEN highlight component', async () => {
            // WHEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInTopHalfRegion').and.returnValue(false);
            spyOn(cmsDragAndDropServiceAny, 'isMouseInBottomHalfRegion').and.returnValue(true);
            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toEqual({
                original: element,
                id: NODE_SMARTEDIT_ELEMENT_UUID,
                isSlot: false,
                slotId: targetSlot.slotId,
                slotUuid: targetSlot.slotUuid,
                slotCatalogVersion: targetSlot.slotCatalogVersion,
                isAllowed: true,
                componentId: componentInfo.id,
                componentUuid: componentInfo.uuid,
                isBottom: true
            });

            expect(element.css).toHaveBeenCalledWith('top', '');
            expect(element.css).toHaveBeenCalledWith('bottom', '-5px');
            expect(element.css).toHaveBeenCalledWith('display', 'flex');
            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_COPY);
        });

        it('When drag over same allowed component THEN return', async () => {
            // WHEN
            highlightedElement.isAllowed = false;
            element.attr.and.callFake((arg: string) => {
                if (arg === NODE_SMARTEDIT_ELEMENT_UUID) {
                    return highlightedElement.id;
                } else if (arg === SLOT_ID) {
                    return highlightedElement.slotId;
                } else if (arg === SLOT_UUID) {
                    return highlightedElement.slotUuid;
                } else if (arg === SLOT_CATALOG_VERSION_UUID) {
                    return highlightedElement.slotCatalogVersion;
                } else if (arg === COMPONENT_ID) {
                    return componentInfo.id;
                } else if (arg === COMPONENT_UUID) {
                    return componentInfo.uuid;
                }
                throw new Error('attribute not found');
            });
            spyOn(cmsDragAndDropServiceAny, 'isMouseInTopHalfRegion').and.returnValue(false);
            spyOn(cmsDragAndDropServiceAny, 'isMouseInBottomHalfRegion').and.returnValue(true);

            // THEN
            await cmsDragAndDropServiceAny.highlightElement(event);

            expect(cmsDragAndDropServiceAny.highlightedElement).toBe(highlightedElement);
            expect(cmsDragAndDropServiceAny.isAllowedSlot).toHaveBeenCalledTimes(0);

            expect(event.dataTransfer.dropEffect).toEqual(DROP_EFFECT_NONE);
        });
    });

    it('When not editable slot THEN not allowed slot', async () => {
        slotRestrictionsService.isSlotEditable.and.returnValue(Promise.resolve(false));
        slotRestrictionsService.determineComponentStatusInSlot.and.returnValue(
            Promise.resolve(COMPONENT_IN_SLOT_STATUS.ALLOWED)
        );

        const allowed = await cmsDragAndDropServiceAny.isAllowedSlot(componentInfo.slotId);

        expect(allowed).toBeFalse();
    });

    it('When editable and maybe allowed slot THEN allowed slot', async () => {
        slotRestrictionsService.isSlotEditable.and.returnValue(Promise.resolve(true));
        slotRestrictionsService.determineComponentStatusInSlot.and.returnValue(
            Promise.resolve(COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED)
        );

        const allowed = await cmsDragAndDropServiceAny.isAllowedSlot(componentInfo.slotId);

        expect(allowed).toBeTrue();
    });

    describe('mouse in which component/slot top or bottom', () => {
        const element = jasmine.createSpyObj('element', ['get', 'getBoundingClientRect']);
        beforeEach(() => {
            element.get.and.returnValue(element);
            element.getBoundingClientRect.and.returnValue({
                left: 0,
                right: 200,
                top: 0,
                bottom: 200
            });
        });

        it('Get mouse in top region ', () => {
            // WHEN
            const event = new DragEvent('dragOver', { clientX: 50, clientY: 50 });
            // THEN
            const inTopRegion = cmsDragAndDropServiceAny.isMouseInTopHalfRegion(event, element);
            expect(inTopRegion).toBeTrue();
        });

        it('Get mouse in bottom region ', () => {
            // WHEN
            const event = new DragEvent('dragOver', { clientX: 150, clientY: 150 });

            // THEN
            const inBottomRegion = cmsDragAndDropServiceAny.isMouseInBottomHalfRegion(
                event,
                element
            );

            expect(inBottomRegion).toBeTrue();
        });
    });

    describe('setMouseArea', () => {
        const element = jasmine.createSpyObj('element', ['getBoundingClientRect']);
        beforeEach(() => {
            scrollable.get.and.returnValue(element);
            element.getBoundingClientRect.and.returnValue({
                left: 0,
                right: 100,
                top: 0,
                bottom: 600
            });
        });
        it('WHEN mouse in top scroll area THEN set flag', () => {
            // WHEN
            cmsDragAndDropServiceAny.scrollable = scrollable;
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = false;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = true;
            const event = new DragEvent('dragOver', { clientX: 30, clientY: 30 });

            // THEN
            cmsDragAndDropServiceAny.setMouseArea(event);

            expect(cmsDragAndDropServiceAny.isMouseInTopScrollArea).toBeTrue();
            expect(cmsDragAndDropServiceAny.isMouseInBottomScrollArea).toBeFalse();
        });
        it('WHEN mouse in middle scroll area THEN set flag', () => {
            // WHEN
            cmsDragAndDropServiceAny.scrollable = scrollable;
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = true;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = true;
            const event = new DragEvent('dragOver', { clientX: 50, clientY: 250 });

            // THEN
            cmsDragAndDropServiceAny.setMouseArea(event);

            expect(cmsDragAndDropServiceAny.isMouseInTopScrollArea).toBeFalse();
            expect(cmsDragAndDropServiceAny.isMouseInBottomScrollArea).toBeFalse();
        });
        it('WHEN mouse in bottom scroll area THEN set flag', () => {
            // WHEN
            cmsDragAndDropServiceAny.scrollable = scrollable;
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = true;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = false;
            const event = new DragEvent('dragOver', { clientX: 80, clientY: 580 });

            // THEN
            cmsDragAndDropServiceAny.setMouseArea(event);

            expect(cmsDragAndDropServiceAny.isMouseInTopScrollArea).toBeFalse();
            expect(cmsDragAndDropServiceAny.isMouseInBottomScrollArea).toBeTrue();
        });
    });

    describe('scroll', () => {
        beforeEach(() => {
            cmsDragAndDropServiceAny.scrollable = scrollable;
            spyOn(cmsDragAndDropServiceAny._window, 'requestAnimationFrame');
        });

        it('WHEN mouse in top scroll area THEN scroll up', () => {
            // WHEN
            scrollable.scrollTop.and.returnValue(60);
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = true;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = false;

            // THEN
            cmsDragAndDropServiceAny.scroll();
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            expect(scrollable.scrollTop).toHaveBeenCalledWith(50);
        });

        it('WHEN mouse in top scroll area and already scrolled to top THEN not scroll up', () => {
            // WHEN
            scrollable.scrollTop.and.returnValue(0);
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = true;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = false;

            // THEN
            cmsDragAndDropServiceAny.scroll();

            expect(scrollable.scrollTop).toHaveBeenCalledTimes(1);
        });

        it('WHEN mouse in bottom scroll area THEN scroll down', () => {
            // WHEN
            scrollable.scrollTop.and.returnValue(100);
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = false;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = true;
            const element = {
                scrollHeight: 800,
                offsetHeight: 100
            };
            scrollable.get.and.returnValue(element as any);
            // THEN
            cmsDragAndDropServiceAny.scroll();
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            expect(scrollable.scrollTop).toHaveBeenCalledWith(110);
        });

        it('WHEN mouse in bottom scroll area and already scrolled to bottom THEN not scroll ', () => {
            // WHEN
            scrollable.scrollTop.and.returnValue(0);
            cmsDragAndDropServiceAny.isMouseInTopScrollArea = false;
            cmsDragAndDropServiceAny.isMouseInBottomScrollArea = true;
            const element = {
                scrollHeight: 800,
                offsetHeight: 800
            };
            scrollable.get.and.returnValue(element as any);
            // THEN
            cmsDragAndDropServiceAny.scroll();
            expect(scrollable.scrollTop).toHaveBeenCalledTimes(1);
        });
    });
});
