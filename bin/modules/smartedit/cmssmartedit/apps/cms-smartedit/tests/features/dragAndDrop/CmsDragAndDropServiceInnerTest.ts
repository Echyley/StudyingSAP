/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import {
    AssetsService,
    CmsDragAndDropDragInfo,
    SlotInfo,
    IPageContentSlotsComponentsRestService as IPageContentSlotsRestService
} from 'cmscommons';
import { ComponentEditingFacade } from 'cmssmartedit/services';
import {
    CmsDragAndDropCachedComponentHint,
    CmsDragAndDropService
} from 'cmssmartedit/services/dragAndDrop/CmsDragAndDropServiceInner';
import { cloneDeep } from 'lodash';
import {
    DragAndDropService,
    GatewayFactory,
    IAlertService,
    IBrowserService,
    IWaitDialogService,
    MessageGateway,
    SystemEventService,
    ISlotRestrictionsService,
    IComponentHandlerService,
    CrossFrameEventService
} from 'smarteditcommons';
import { jQueryHelper } from 'testhelpers';

describe('CmsDragAndDropServiceInner', () => {
    const DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';
    const COMPONENT_IN_SLOT_STATUS = {
        ALLOWED: 'allowed',
        DISALLOWED: 'disallowed',
        MAYBEALLOWED: 'mayBeAllowed'
    };
    const domain = '';

    let alertService: jasmine.SpyObj<IAlertService>;
    let assetsService: jasmine.SpyObj<AssetsService>;
    let browserService: jasmine.SpyObj<IBrowserService>;
    let componentEditingFacade: jasmine.SpyObj<ComponentEditingFacade>;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let dragAndDropService: jasmine.SpyObj<DragAndDropService>;
    let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
    let gateway: jasmine.SpyObj<MessageGateway>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let pageContentSlotsComponentsRestService: jasmine.SpyObj<IPageContentSlotsRestService>;
    let slotRestrictionsService: jasmine.SpyObj<ISlotRestrictionsService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let waitDialogService: jasmine.SpyObj<IWaitDialogService>;
    let jq: jasmine.SpyObj<JQueryStatic>;

    let cmsDragAndDropService: CmsDragAndDropService;
    let cmsDragAndDropServiceAny: any;
    let highlightedHint: CmsDragAndDropCachedComponentHint;
    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', ['showDanger']);

        assetsService = jasmine.createSpyObj<AssetsService>('assetsService', ['getAssetsRoot']);

        browserService = jasmine.createSpyObj<IBrowserService>('browserService', ['isSafari']);

        componentEditingFacade = jasmine.createSpyObj<ComponentEditingFacade>(
            'componentEditingFacade',
            [
                'addNewComponentToSlot',
                'cloneExistingComponentToSlot',
                'moveComponent',
                'addExistingComponentToSlot'
            ]
        );

        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            [
                'getSlotOperationRelatedId',
                'getSlotOperationRelatedUuid',
                'getSlotOperationRelatedType',
                'getId',
                'getUuid',
                'getOverlay',
                'getCatalogVersionUuid',
                'getComponentPositionInSlot',
                'getComponentUnderSlot',
                'getComponent'
            ]
        );
        componentHandlerService.getCatalogVersionUuid.and.returnValue('ANY_UUID');

        dragAndDropService = jasmine.createSpyObj<DragAndDropService>('dragAndDropService', [
            'register',
            'apply',
            'update',
            'unregister',
            'markDragStarted',
            'markDragStopped'
        ]);

        gateway = jasmine.createSpyObj<MessageGateway>('gateway', ['subscribe']);
        gatewayFactory = jasmine.createSpyObj<GatewayFactory>('gatewayFactory', ['createGateway']);
        gatewayFactory.createGateway.and.returnValue(gateway);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        translateService.instant.and.callFake((key: string) => key);

        pageContentSlotsComponentsRestService = jasmine.createSpyObj<IPageContentSlotsRestService>(
            'pageContentSlotsComponentsRestService',
            ['getComponentsForSlot']
        );
        pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(null);

        slotRestrictionsService = jasmine.createSpyObj<ISlotRestrictionsService>(
            'slotRestrictionsService',
            ['isComponentAllowedInSlot', 'isSlotEditable']
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync',
            'publish'
        ]);
        systemEventService.publishAsync.and.returnValue(Promise.resolve(true));
        systemEventService.publish.and.returnValue(Promise.resolve(true));
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );
        waitDialogService = jasmine.createSpyObj<IWaitDialogService>('waitDialogService', [
            'showWaitModal',
            'hideWaitModal'
        ]);

        jq = jQueryHelper.jQuery() as jasmine.SpyObj<JQueryStatic>;

        cmsDragAndDropService = new CmsDragAndDropService(
            alertService,
            assetsService,
            browserService,
            componentEditingFacade,
            componentHandlerService,
            dragAndDropService,
            gatewayFactory,
            translateService,
            pageContentSlotsComponentsRestService,
            slotRestrictionsService,
            systemEventService,
            waitDialogService,
            jq,
            domain,
            crossFrameEventService
        );
        cmsDragAndDropServiceAny = cmsDragAndDropService;
        (cmsDragAndDropService as any)._window = {
            pageYOffset: 1000
        } as Window;
    });

    it('WHEN cmsDragAndDropService is created THEN the service creates a gateway to communicate with the other frame', () => {
        // THEN
        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('cmsDragAndDrop');
        expect(cmsDragAndDropServiceAny.gateway).toBe(gateway);
    });

    describe('register', () => {
        it('WHEN register is called THEN the right configuration is stored in the base drag and drop service', () => {
            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            expect(arg.id).toBe(DRAG_AND_DROP_ID);
            expect(arg.sourceSelector).toEqual([
                "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type!='ContentSlot'] .movebutton",
                '.movebutton'
            ]);
            expect(arg.targetSelector).toBe(
                "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type='ContentSlot']"
            );
            expect(arg.enableScrolling).toBe(true);
        });

        it('WHEN register is called THEN the right onStart callback is registered', () => {
            // GIVEN
            const expectedResult: any = 'someResult';
            spyOn(cmsDragAndDropServiceAny, 'onStart').and.returnValue(expectedResult);

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            const result = arg.startCallback(null);
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onStop callback is registered', () => {
            // GIVEN
            const expectedResult = 'someResult';
            spyOn(cmsDragAndDropServiceAny, 'onStop').and.returnValue(expectedResult);

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.stopCallback(null);
            expect(cmsDragAndDropServiceAny.onStop).toHaveBeenCalled();
        });

        it('WHEN register is called THEN the right onDragEnter callback is registered', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onDragEnter').and.returnValue(undefined);

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.dragEnterCallback(null);
            expect(cmsDragAndDropServiceAny.onDragEnter).toHaveBeenCalled();
        });

        it('WHEN register is called THEN the right onDragOver callback is registered', async () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onDragOver').and.returnValue(Promise.resolve());

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            await ((arg.dragOverCallback(null) as unknown) as Promise<void>);
            expect(cmsDragAndDropServiceAny.onDragOver).toHaveBeenCalled();
        });

        it('WHEN register is called THEN the right onDragEnd callback is registered', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onStop').and.returnValue(undefined);

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.stopCallback(null);
            expect(cmsDragAndDropServiceAny.onStop).toHaveBeenCalled();
        });

        it('WHEN register is called THEN the right onDrop callback is registered', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'onDrop').and.returnValue(undefined);

            // WHEN
            cmsDragAndDropService.register();

            // THEN
            expect(dragAndDropService.register).toHaveBeenCalled();
            const arg = dragAndDropService.register.calls.argsFor(0)[0];
            arg.dropCallback(null);
            expect(cmsDragAndDropServiceAny.onDrop).toHaveBeenCalled();
        });
    });

    it('WHEN unregister is called THEN the service is cleaned', () => {
        spyOn(cmsDragAndDropServiceAny, 'overlayRenderedUnSubscribeFn');
        spyOn(cmsDragAndDropServiceAny, 'componentRemovedUnSubscribeFn');

        // WHEN
        cmsDragAndDropService.unregister();

        // THEN
        expect(dragAndDropService.unregister).toHaveBeenCalledWith([DRAG_AND_DROP_ID]);

        expect(cmsDragAndDropServiceAny.overlayRenderedUnSubscribeFn).toHaveBeenCalled();
        expect(cmsDragAndDropServiceAny.componentRemovedUnSubscribeFn).toHaveBeenCalled();
    });

    describe('apply', () => {
        beforeEach(() => {
            spyOn(cmsDragAndDropServiceAny, 'addUIHelpers');
            spyOn(cmsDragAndDropServiceAny, 'initializeDragOperation');
            spyOn(cmsDragAndDropServiceAny, 'cleanDragOperation');
        });

        it('WHEN apply is called THEN the page is prepared for the drag and drop operations', () => {
            // WHEN
            cmsDragAndDropService.apply();

            // THEN
            expect(dragAndDropService.apply).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.addUIHelpers).toHaveBeenCalled();
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'overlayRerendered',
                jasmine.any(Function)
            );
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'COMPONENT_REMOVED_EVENT',
                jasmine.any(Function)
            );
            expect(gateway.subscribe).toHaveBeenCalledWith(
                'CMS_DRAG_STARTED',
                jasmine.any(Function)
            );
            expect(gateway.subscribe).toHaveBeenCalledWith(
                'CMS_DRAG_STOPPED',
                jasmine.any(Function)
            );
        });

        it('WHEN drag is started in the outer frame THEN the right callback is called', () => {
            // WHEN
            const eventId = '';
            const data = {} as CmsDragAndDropDragInfo;
            cmsDragAndDropService.apply();
            const callback = gateway.subscribe.calls.argsFor(0)[1] as (
                eventId: string,
                data: CmsDragAndDropDragInfo
            ) => void;
            callback(eventId, data);

            // THEN
            expect(dragAndDropService.markDragStarted).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.initializeDragOperation).toHaveBeenCalledWith(data);
        });

        it('WHEN drop is stopped from the outer frame THEN the right callback is called', () => {
            // WHEN
            cmsDragAndDropService.apply();
            const callback = gateway.subscribe.calls.argsFor(1)[1] as () => void;
            callback();

            // THEN
            expect(dragAndDropService.markDragStopped).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.cleanDragOperation).toHaveBeenCalled();
        });
    });

    describe('Event Handlers', () => {
        type MockSlot = { id?: string; isAllowed?: boolean } & jasmine.SpyObj<JQuery<HTMLElement>>;
        type MockComponent = { id?: string; original?: MockComponent } & jasmine.SpyObj<
            JQuery<HTMLElement>
        >;
        type MockHint = { id?: string; original?: MockHint } & jasmine.SpyObj<JQuery<HTMLElement>>;
        type MockOtherComponent = { id?: string; hints: any } & jasmine.SpyObj<JQuery<HTMLElement>>;

        let event: Event;
        let component: MockComponent;
        let hint: MockHint;
        let otherComponent: MockOtherComponent;
        let otherHint: MockHint;
        let slot: MockSlot;
        let componentId: string;
        let componentType: string;
        let slotId: string;
        let initialValues: { slot: MockSlot; hint: MockHint; component: MockComponent };
        let componentUuid: string;
        let slotUuid: string;

        beforeEach(() => {
            componentId = 'some component id';
            componentUuid = 'some component Uuid';
            componentType = 'some component type';
            slotId = 'some slot id';
            slotUuid = 'some slot id';

            event = {
                target: new EventTarget()
            } as Event;

            slot = jasmine.createSpyObj<JQuery<HTMLElement>>('slot', [
                'closest',
                'addClass',
                'removeClass'
            ]);
            slot.id = 'initial slot ID';
            slot.isAllowed = true;

            component = jasmine.createSpyObj<JQuery<HTMLElement>>('component', [
                'closest',
                'addClass',
                'removeClass',
                'attr'
            ]);
            component.id = 'initial component ID';
            component.original = component;

            hint = jasmine.createSpyObj('hint', ['addClass', 'removeClass']);
            hint.id = 'some hint id';
            hint.original = hint;

            component.closest.and.callFake((componentSelector: string) => {
                if (
                    componentSelector ===
                    ".smartEditComponentX[data-smartedit-component-type!='ContentSlot']"
                ) {
                    return component;
                } else {
                    return slot;
                }
            });

            component.attr.and.callFake(((arg: string) => {
                if (arg === 'data-component-id') {
                    return componentId;
                } else if (arg === 'data-component-uuid') {
                    return componentUuid;
                } else if (arg === 'data-component-type') {
                    return componentType;
                } else {
                    return slotId;
                }
            }) as any);

            componentHandlerService.getSlotOperationRelatedId.and.returnValue(componentId);
            componentHandlerService.getSlotOperationRelatedUuid.and.returnValue(componentUuid);
            componentHandlerService.getSlotOperationRelatedType.and.returnValue(componentType);
            componentHandlerService.getId.and.returnValue(slotId);
            componentHandlerService.getUuid.and.returnValue(slotUuid);

            initialValues = {
                hint,
                component,
                slot
            };

            cmsDragAndDropServiceAny.highlightedHint = initialValues.hint;
            cmsDragAndDropServiceAny.highlightedComponent = initialValues.component;
            cmsDragAndDropServiceAny.highlightedSlot = initialValues.slot;

            cmsDragAndDropServiceAny.dragInfo = {
                componentId: 'dragged component'
            };

            otherComponent = jasmine.createSpyObj<MockOtherComponent>('otherComponent', [
                'addClass'
            ]);
            otherComponent.id = 'other component ID';

            cmsDragAndDropServiceAny.cachedSlots = {};
            cmsDragAndDropServiceAny.cachedSlots[slotId] = {
                components: [component, otherComponent]
            };

            otherHint = jasmine.createSpyObj('otherHint', ['addClass', 'removeClass']);
            otherHint.id = 'other hint id';
            otherHint.original = otherHint;

            otherComponent.hints = [initialValues.hint, otherHint];
        });

        it('WHEN onStart is called THEN it prepares the page for the drag operation', () => {
            // GIVEN
            const expectedDragInfo = {
                componentId,
                componentUuid,
                componentType,
                slotId,
                slotUuid,
                slotOperationRelatedId: componentId,
                slotOperationRelatedType: componentType
            };
            spyOn(cmsDragAndDropServiceAny, 'initializeDragOperation');
            spyOn(cmsDragAndDropServiceAny, 'getSelector').and.returnValue(component);

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            cmsDragAndDropServiceAny.onStart(event);

            // THEN
            expect(component.addClass).toHaveBeenCalledWith('component_dragged');
            expect(cmsDragAndDropServiceAny.initializeDragOperation).toHaveBeenCalledWith(
                expectedDragInfo
            );
        });

        it('GIVEN the cursor enters a slot WHEN onDragEnter is called THEN the slot is highlighted', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'highlightSlot');

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            cmsDragAndDropServiceAny.onDragEnter(event);

            // THEN
            expect(cmsDragAndDropServiceAny.highlightSlot).toHaveBeenCalledWith(event);
        });

        it('GIVEN the cursor enters a slot and if the component allowed in slot status is mayBeAllowed WHEN onDragEnter is called THEN the css classes are added to the slot', async () => {
            // GIVEN
            ((jq as any) as jasmine.Spy).and.callFake(() => ({
                closest: (selector: string) => {
                    if (
                        selector ===
                        ".smartEditComponentX[data-smartedit-component-type='ContentSlot']"
                    ) {
                        return slot;
                    }
                    return {};
                }
            }));

            cmsDragAndDropServiceAny.highlightedSlot.isAllowed = undefined;
            cmsDragAndDropServiceAny.highlightedSlot.id = 'initial slot ID';
            slotRestrictionsService.isComponentAllowedInSlot.and.returnValue(
                Promise.resolve<any>(COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED)
            );
            slotRestrictionsService.isSlotEditable.and.returnValue(Promise.resolve(true));
            cmsDragAndDropServiceAny.cachedSlots[slotId].id = slotId;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragEnter(event);

            // THEN
            expect(slot.addClass).toHaveBeenCalledWith('over-slot-enabled');
            expect(slot.addClass).toHaveBeenCalledWith('over-slot-maybeenabled');
        });

        it('GIVEN the cursor is over a slot and the hints are already highlighted WHEN onDragOver is called THEN nothing is done', async () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.returnValue(true);
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint');
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent');

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedComponent).not.toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(initialValues.hint);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(initialValues.component);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the hint changes WHEN onDragOver is called THEN the hints are updated', async () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.callFake(
                (evt: any, item: any) => {
                    if (item === initialValues.hint) {
                        return false;
                    } else {
                        return true;
                    }
                }
            );
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint');
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent');

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedComponent).not.toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(initialValues.hint);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(initialValues.component);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the component changes WHEN onDragOver is called THEN the component hints are updated', async () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.callFake(
                (evt: any, item: any) => {
                    if (item === initialValues.hint) {
                        return false;
                    } else if (item === initialValues.component) {
                        return false;
                    } else if (item === otherComponent) {
                        return true;
                    }

                    return true;
                }
            );
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent').and.callThrough();

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedComponent).toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(otherHint);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(otherComponent);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the slot changes WHEN onDragOver is called THEN the slot hints are updated', async () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.callFake(
                (evt: any, item: any) => {
                    if (item === initialValues.hint) {
                        return false;
                    } else if (item === initialValues.component) {
                        return false;
                    } else if (item === otherComponent) {
                        return true;
                    }

                    return true;
                }
            );
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent').and.callThrough();

            cmsDragAndDropServiceAny.cachedSlots = {};
            cmsDragAndDropServiceAny.cachedSlots[slotId] = {
                components: [component, otherComponent]
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedComponent).toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(otherHint);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(otherComponent);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(initialValues.slot);

            expect(cmsDragAndDropServiceAny.highlightedHint.addClass).toHaveBeenCalledWith(
                'overlayDropzone--hovered'
            );
        });

        it('GIVEN the cursor is over a slot and if the component allowed in slot status is mayBeAllowed and the slot has no components WHEN onDragOver is called THEN the entire slot has to be in yellow', async () => {
            // GIVEN
            ((jq as unknown) as jasmine.Spy).and.callFake(() => ({
                closest: (selector: string) => {
                    if (
                        selector ===
                        ".smartEditComponentX[data-smartedit-component-type='ContentSlot']"
                    ) {
                        (slot as any).id = slotId;
                        return slot;
                    }
                    return {};
                }
            }));

            componentHandlerService.getId.and.callFake((_slot: any) => {
                if (_slot.id === slotId) {
                    return slotId;
                } else if (_slot.id === initialValues.slot.id) {
                    return initialValues.slot.id;
                }
                return slotId;
            });
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.callFake(
                (evt: any, item: any) => {
                    if (item === initialValues.hint) {
                        return false;
                    } else if (item === initialValues.component) {
                        return false;
                    } else if (item === otherComponent) {
                        return true;
                    }

                    return true;
                }
            );
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedSlot').and.callThrough();
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent').and.callThrough();

            initialValues.slot = cloneDeep(slot);
            cmsDragAndDropServiceAny.highlightedSlot.original = initialValues.slot;
            cmsDragAndDropServiceAny.highlightedSlot.isAllowed = undefined;

            slotRestrictionsService.isComponentAllowedInSlot.and.returnValue(
                Promise.resolve<any>(COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED)
            );
            slotRestrictionsService.isSlotEditable.and.returnValue(Promise.resolve(true));

            cmsDragAndDropServiceAny.cachedSlots[slotId] = {
                id: slotId,
                components: [], // slot is empty with no components
                original: slot
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(slot.addClass).toHaveBeenCalledWith('over-slot-enabled');
            expect(slot.addClass).toHaveBeenCalledWith('over-slot-maybeenabled');

            expect(cmsDragAndDropServiceAny.clearHighlightedSlot).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.clearHighlightedComponent).toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(null);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(null);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(
                cmsDragAndDropServiceAny.cachedSlots[slotId]
            );
        });

        it('GIVEN the cursor is over the dragged element WHEN onDragOver is called THEN the component is highlighted', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.dragInfo.slotOperationRelatedId = initialValues.component.id;
            cmsDragAndDropServiceAny.highlightedComponent = null;
            componentHandlerService.getSlotOperationRelatedId.and.returnValue(
                (component as any).id
            );
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.callFake(
                (evt: any, item: any) => {
                    if (item === initialValues.hint) {
                        return false;
                    } else if (item === initialValues.component) {
                        return true;
                    } else if (item === otherComponent) {
                        return false;
                    }

                    return true;
                }
            );
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent').and.callThrough();

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDragOver(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();

            expect(cmsDragAndDropServiceAny.highlightedHint).toBe(null);
            expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(component);
            expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(initialValues.slot);

            expect(component.addClass).toHaveBeenCalledWith('component_dragged_hovered');
        });

        it('WHEN mouse leaves drag area THEN the slot is cleared', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.returnValue(false);
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedSlot').and.callThrough();
            const currentSlot = jasmine.createSpyObj('highlightedSlot', ['removeClass']);
            currentSlot.original = currentSlot;
            cmsDragAndDropServiceAny.highlightedSlot = currentSlot;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            cmsDragAndDropServiceAny.onDragLeave();

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedSlot).toHaveBeenCalled();
            expect(systemEventService.publish.calls.count()).toBe(2);
            expect(systemEventService.publish.calls.argsFor(0)[0]).toEqual('HIDE_SLOT_MENU');
            expect(systemEventService.publish.calls.argsFor(1)[0]).toEqual('CMS_DRAG_LEAVE');
        });

        it('GIVEN the mouse is still in a drag area WHEN a drag leave event is triggered THEN the slot is kept highlighted', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'isMouseInRegion').and.returnValue(true);
            spyOn(cmsDragAndDropServiceAny, 'clearHighlightedSlot');

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            cmsDragAndDropServiceAny.onDragLeave(event);

            // THEN
            expect(cmsDragAndDropServiceAny.clearHighlightedSlot).not.toHaveBeenCalled();
        });

        it('WHEN onStop is called THEN it cleans the drag operation', () => {
            // GIVEN
            spyOn(cmsDragAndDropServiceAny, 'cleanDragOperation');
            spyOn(cmsDragAndDropServiceAny, 'getSelector').and.returnValue(component);

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            cmsDragAndDropServiceAny.onStop(event);

            // THEN
            expect(systemEventService.publish).toHaveBeenCalledWith(
                'contractChangeListenerRestartProcess'
            );
            expect(cmsDragAndDropServiceAny.cleanDragOperation).toHaveBeenCalledWith(component);
        });
    });

    describe('onDrop', () => {
        let event: Event;
        let dragInfo: CmsDragAndDropDragInfo;
        let targetSlotId: string;
        let targetSlotUuid: string;
        let slotInfo: SlotInfo;

        beforeEach(() => {
            spyOn(cmsDragAndDropServiceAny, 'scrollToModifiedSlot');
            componentEditingFacade.addNewComponentToSlot.and.returnValue(Promise.resolve());
            componentEditingFacade.addExistingComponentToSlot.and.returnValue(Promise.resolve());
            componentEditingFacade.moveComponent.and.returnValue(Promise.resolve());

            const expectedResult = 'someResult';
            spyOn(cmsDragAndDropServiceAny, 'onStop').and.returnValue(expectedResult);

            dragInfo = {
                slotId: 'some slot id',
                componentId: 'some component id',
                componentType: 'some component type',
                slotOperationRelatedId: 'some component id'
            } as CmsDragAndDropDragInfo;
            cmsDragAndDropServiceAny.dragInfo = dragInfo;
            cmsDragAndDropServiceAny.highlightedSlot = {
                isAllowed: true,
                components: ['some component']
            };

            event = {
                target: new EventTarget()
            } as Event;

            targetSlotId = 'some target slot id';
            targetSlotUuid = 'some target slot uuid';

            slotInfo = {
                targetSlotId,
                targetSlotUUId: targetSlotUuid
            };

            componentHandlerService.getId.and.returnValue(targetSlotId);
            componentHandlerService.getUuid.and.returnValue(targetSlotUuid);

            highlightedHint = {
                position: 2,
                original: null,
                rect: null
            };
        });

        it('WHEN a component is dropped outside a drop area THEN nothing happens', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.highlightedSlot = null;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(alertService.showDanger).not.toHaveBeenCalled();
        });

        it('WHEN a component is dropped in an invalid slot THEN an alert is displayed', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.highlightedSlot.isAllowed = false;
            const expectedTranslation = 'se.drag.and.drop.not.valid.component.type';
            const expectedResult = {
                message: expectedTranslation,
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(alertService.showDanger).toHaveBeenCalledWith(expectedResult);
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.onStop).not.toHaveBeenCalled();
        });

        it('WHEN a new component is dropped THEN the generic editor modal is displayed for it.', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.dragInfo.slotId = null;
            cmsDragAndDropServiceAny.dragInfo.componentId = null;
            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledWith(
                slotInfo,
                'ANY_UUID',
                dragInfo.componentType,
                expectedPosition
            );
            expect(componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped into a slot that already has an instance THEN an error message is displayed.', async () => {
            // GIVEN
            componentEditingFacade.addExistingComponentToSlot.and.returnValue(Promise.reject());
            cmsDragAndDropServiceAny.dragInfo.slotId = null;
            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position;
            const expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(
                targetSlotId,
                expectedDragInfo,
                expectedPosition
            );
            expect(cmsDragAndDropServiceAny.onStop).toHaveBeenCalled();
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped THEN the generic editor modal is displayed for it.', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.dragInfo.slotId = null;
            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position;
            const expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(
                targetSlotId,
                expectedDragInfo,
                expectedPosition
            );
            expect(componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved between slots THEN page is updated accordingly.', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
                dragInfo.slotId,
                targetSlotId,
                dragInfo.componentId,
                expectedPosition
            );
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved within the same slot before THEN page is updated accordingly.', async () => {
            // GIVEN
            const currentPosition = 3;
            componentHandlerService.getComponentPositionInSlot.and.returnValue(currentPosition);

            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position;
            cmsDragAndDropServiceAny.dragInfo.slotId = targetSlotId;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
                dragInfo.slotId,
                targetSlotId,
                dragInfo.componentId,
                expectedPosition
            );
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved within the same slot after THEN page is updated accordingly.', async () => {
            // GIVEN
            const currentPosition = 1;
            componentHandlerService.getComponentPositionInSlot.and.returnValue(currentPosition);

            cmsDragAndDropServiceAny.highlightedHint = highlightedHint;
            const expectedPosition = highlightedHint.position - 1;
            cmsDragAndDropServiceAny.dragInfo.slotId = targetSlotId;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.moveComponent).toHaveBeenCalledWith(
                dragInfo.slotId,
                targetSlotId,
                dragInfo.componentId,
                expectedPosition
            );
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is dropped on an empty slot THEN the generic editor modal is displayed for it.', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.dragInfo.slotId = null;
            cmsDragAndDropServiceAny.dragInfo.componentId = null;
            cmsDragAndDropServiceAny.highlightedSlot.components = [];
            const expectedPosition = 0;

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledWith(
                slotInfo,
                'ANY_UUID',
                dragInfo.componentType,
                expectedPosition
            );
            expect(componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped on an empty slot THEN the generic editor modal is displayed for it.', async () => {
            // GIVEN
            cmsDragAndDropServiceAny.dragInfo.slotId = null;
            cmsDragAndDropServiceAny.highlightedSlot.components = [];
            const expectedPosition = 0;
            const expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await cmsDragAndDropServiceAny.onDrop(event);

            // THEN
            expect(componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(
                targetSlotId,
                expectedDragInfo,
                expectedPosition
            );
            expect(componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropServiceAny.scrollToModifiedSlot).toHaveBeenCalledWith(
                targetSlotId
            );
            expect(waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
        });
    });

    it('WHEN a hint is cleared THEN the classes are removed', () => {
        // GIVEN
        const currentHint = jasmine.createSpyObj('currentHint', ['removeClass']);
        currentHint.original = currentHint;
        cmsDragAndDropServiceAny.highlightedHint = currentHint;

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        cmsDragAndDropServiceAny.clearHighlightedHint();

        // THEN
        expect(currentHint.removeClass).toHaveBeenCalledWith('overlayDropzone--hovered');
        expect(currentHint.removeClass).toHaveBeenCalledWith('overlayDropzone--mayBeAllowed');
        expect(cmsDragAndDropServiceAny.highlightedHint).toBe(null);
    });

    it('WHEN a component is cleared THEN the classes are removed', () => {
        // GIVEN
        spyOn(cmsDragAndDropServiceAny, 'clearHighlightedHint');

        const currentComponent = jasmine.createSpyObj('highlightedComponent', ['removeClass']);
        currentComponent.original = currentComponent;
        cmsDragAndDropServiceAny.highlightedComponent = currentComponent;

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        cmsDragAndDropServiceAny.clearHighlightedComponent();

        // THEN
        expect(cmsDragAndDropServiceAny.clearHighlightedHint).toHaveBeenCalled();
        expect(currentComponent.removeClass).toHaveBeenCalledWith('component_dragged_hovered');
        expect(cmsDragAndDropServiceAny.highlightedComponent).toBe(null);
    });

    it('WHEN a slot is cleared THEN the classes are removed', () => {
        // GIVEN
        spyOn(cmsDragAndDropServiceAny, 'clearHighlightedComponent');
        const currentSlot = jasmine.createSpyObj('highlightedSlot', ['removeClass']);
        currentSlot.original = currentSlot;
        cmsDragAndDropServiceAny.highlightedSlot = currentSlot;

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        cmsDragAndDropServiceAny.clearHighlightedSlot();

        // THEN
        expect(cmsDragAndDropServiceAny.clearHighlightedComponent).toHaveBeenCalled();
        expect(currentSlot.removeClass).toHaveBeenCalledWith('over-slot-enabled');
        expect(currentSlot.removeClass).toHaveBeenCalledWith('over-slot-disabled');
        expect(currentSlot.removeClass).toHaveBeenCalledWith('over-slot-maybeenabled');
        expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(null);
    });

    // Helper Methods
    it('WHEN onOverlayUpdate is called THEN update is called', async () => {
        // GIVEN
        spyOn(cmsDragAndDropService, 'update');

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        await cmsDragAndDropServiceAny.onOverlayUpdate();

        // THEN
        expect(cmsDragAndDropService.update).toHaveBeenCalled();
    });

    it('WHEN update is called THEN the page is refreshed', () => {
        // GIVEN'
        spyOn(cmsDragAndDropServiceAny, 'addUIHelpers');
        spyOn(cmsDragAndDropServiceAny, 'cacheElements');

        // WHEN
        cmsDragAndDropService.update();

        // THEN
        expect(dragAndDropService.update).toHaveBeenCalledWith(DRAG_AND_DROP_ID);
        expect(cmsDragAndDropServiceAny.addUIHelpers).toHaveBeenCalled();
        expect(cmsDragAndDropServiceAny.cacheElements).toHaveBeenCalled();
    });

    it('WHEN initializeDragOperation is called THEN the page is prepared for dragging components', () => {
        // GIVEN
        const dragInfo = 'some drag info';
        const overlay = jasmine.createSpyObj('overlay', ['addClass']);
        componentHandlerService.getOverlay.and.returnValue(overlay);
        spyOn(cmsDragAndDropServiceAny, 'cacheElements');

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        cmsDragAndDropServiceAny.initializeDragOperation(dragInfo);

        // THEN
        expect(cmsDragAndDropServiceAny.cacheElements).toHaveBeenCalled();
        expect(cmsDragAndDropServiceAny.dragInfo).toBe(dragInfo);
        expect(overlay.addClass).toHaveBeenCalledWith('smarteditoverlay_dndRendering');
        expect(systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STARTED');
    });

    it('WHEN cleanDragOperation is called THEN the page is cleaned up', () => {
        // GIVEN
        const draggedComponent = jasmine.createSpyObj('draggedComponent', ['removeClass']);
        const overlay = jasmine.createSpyObj('overlay', ['removeClass']);
        componentHandlerService.getOverlay.and.returnValue(overlay);
        spyOn(cmsDragAndDropServiceAny, 'clearHighlightedSlot');

        // WHEN
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        cmsDragAndDropServiceAny.cleanDragOperation(draggedComponent);

        // THEN
        expect(cmsDragAndDropServiceAny.clearHighlightedSlot).toHaveBeenCalled();
        expect(draggedComponent.removeClass).toHaveBeenCalledWith('component_dragged');
        expect(overlay.removeClass).toHaveBeenCalledWith('smarteditoverlay_dndRendering');
        expect(systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STOPPED');
        expect(cmsDragAndDropServiceAny.dragInfo).toBe(null);
        expect(cmsDragAndDropServiceAny.cachedSlots).toEqual({});
        expect(cmsDragAndDropServiceAny.highlightedSlot).toBe(null);
    });
});
