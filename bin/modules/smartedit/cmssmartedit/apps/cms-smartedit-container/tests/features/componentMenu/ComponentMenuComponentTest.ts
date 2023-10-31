/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentMenuComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/ComponentMenuComponent';
import {
    OPEN_COMPONENT_EVENT,
    RESET_COMPONENT_MENU_EVENT
} from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/constants';
import { ComponentsTabComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/tabs/ComponentsTabComponent';
import { ComponentTypesTabComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/tabs/ComponentTypesTabComponent';
import {
    ToolbarItemInternal,
    CrossFrameEventService,
    EVENTS,
    SMARTEDIT_DRAG_AND_DROP_EVENTS,
    OVERLAY_DISABLED_EVENT,
    DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME,
    ComponentMenuService
} from 'smarteditcommons';

describe('ComponentMenuComponent', () => {
    let component: ComponentMenuComponent;
    let componentAny: any;
    let injectedData: ToolbarItemInternal;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let componentMenuService: jasmine.SpyObj<ComponentMenuService>;

    let unregisterDragEndEvent: jasmine.Spy;
    let unregisterDragStartEvent: jasmine.Spy;
    let unregisterOpenComponentEvent: jasmine.Spy;
    let unregisterOverlapEvent: jasmine.Spy;
    let unregisterPageChangeEvent: jasmine.Spy;

    beforeEach(() => {
        injectedData = {
            isOpen: true
        } as ToolbarItemInternal;

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish', 'subscribe']
        );
        crossFrameEventService.subscribe.and.callFake((eventName) => {
            switch (eventName) {
                case EVENTS.PAGE_CHANGE:
                    unregisterPageChangeEvent = jasmine.createSpy();
                    return unregisterPageChangeEvent;
                case OPEN_COMPONENT_EVENT:
                    unregisterOpenComponentEvent = jasmine.createSpy();
                    return unregisterOpenComponentEvent;
                case SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START:
                    unregisterDragStartEvent = jasmine.createSpy();
                    return unregisterDragStartEvent;
                case OVERLAY_DISABLED_EVENT:
                    unregisterOverlapEvent = jasmine.createSpy();
                    return unregisterOverlapEvent;
                case SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END:
                    unregisterDragEndEvent = jasmine.createSpy();
                    return unregisterDragEndEvent;
            }
        });

        componentMenuService = jasmine.createSpyObj<ComponentMenuService>('componentMenuService', [
            'hasMultipleContentCatalogs'
        ]);
        componentMenuService.hasMultipleContentCatalogs.and.returnValue(Promise.resolve(true));

        component = new ComponentMenuComponent(
            injectedData,
            crossFrameEventService,
            componentMenuService
        );
        componentAny = component;
    });

    it('WHEN initialized THEN it should call initialization method and register events', async () => {
        spyOn(componentAny, 'initializeComponentMenu');

        await component.ngOnInit();

        expect(componentAny.initializeComponentMenu).toHaveBeenCalled();
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            EVENTS.PAGE_CHANGE,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            OPEN_COMPONENT_EVENT,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            OVERLAY_DISABLED_EVENT,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END,
            jasmine.any(Function)
        );
        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START,
            jasmine.any(Function)
        );
    });

    it('GIVEN component is initialized WHEN it gets destroyed THEN it should unregister event listeners', async () => {
        await component.ngOnInit();

        component.ngOnDestroy();

        expect(unregisterDragEndEvent).toHaveBeenCalled();
        expect(unregisterDragStartEvent).toHaveBeenCalled();
        expect(unregisterOpenComponentEvent).toHaveBeenCalled();
        expect(unregisterOverlapEvent).toHaveBeenCalled();
        expect(unregisterPageChangeEvent).toHaveBeenCalled();
    });

    describe('Registered event callbacks', () => {
        const getEventCallback = (eventName: string) => {
            return crossFrameEventService.subscribe.calls.allArgs().find(([event]) => {
                return event === eventName;
            })[1] as () => void;
        };
        beforeEach(async () => {
            await component.ngOnInit();
        });

        it('WHEN EVENTS.PAGE_CHANGE event is fired THEN it should call initialization', () => {
            spyOn(componentAny, 'initializeComponentMenu');
            const callback = getEventCallback(EVENTS.PAGE_CHANGE);

            callback();

            expect(componentAny.initializeComponentMenu).toHaveBeenCalled();
        });

        it('WHEN OPEN_COMPONENT_EVENT event is fired THEN it should call resetComponent with open flag equal true', () => {
            spyOn(componentAny, 'resetComponentMenu');
            const callback = getEventCallback(OPEN_COMPONENT_EVENT);

            callback();

            expect(componentAny.resetComponentMenu).toHaveBeenCalledWith(true);
        });

        it('WHEN OVERLAY_DISABLED_EVENT event is fired THEN it should call closeMenu', () => {
            spyOn(componentAny, 'closeMenu');
            const callback = getEventCallback(OVERLAY_DISABLED_EVENT);

            callback();

            expect(componentAny.closeMenu).toHaveBeenCalled();
        });

        it('WHEN SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START event is fired THEN it should call closeMenu and set dragging to true', () => {
            spyOn(componentAny, 'closeMenu');
            const callback = getEventCallback(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START);

            callback();

            expect(component.isDragging).toEqual(true);
            expect(componentAny.closeMenu).toHaveBeenCalled();
        });

        it('WHEN SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END event is fired THEN it should set dragging to false and publish event', () => {
            const callback = getEventCallback(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END);

            callback();

            expect(component.isDragging).toEqual(false);
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.END
            );
        });
    });

    describe('initializeComponentMenu', () => {
        it('should get has multiple catalog value, set tablist, update model and reset component menu', async () => {
            spyOn(componentAny, 'resetComponentMenu');

            await componentAny.initializeComponentMenu();

            expect(component.hasMultipleContentCatalogs).toEqual(true);
            expect(component.tabsList).toEqual([
                {
                    id: 'componentTypesTab',
                    title: 'se.cms.compomentmenu.tabs.componenttypes',
                    component: ComponentTypesTabComponent,
                    hasErrors: false
                },
                {
                    id: 'componentsTab',
                    title: 'se.cms.compomentmenu.tabs.customizedcomp',
                    component: ComponentsTabComponent,
                    hasErrors: false
                }
            ]);
            expect(component.model).toEqual({
                componentsTab: { hasMultipleContentCatalogs: true },
                isOpen: true
            });

            expect(componentAny.resetComponentMenu).toHaveBeenCalledWith(true);
            expect(componentMenuService.hasMultipleContentCatalogs).toHaveBeenCalled();
        });
    });

    describe('resetComponentMenu', () => {
        it('GIVEN tab list is defined WHEN toolbar item is not opened THEN it should update tablist to set which tab is active, update model and publish event', async () => {
            // initialize tablist
            await component.ngOnInit();
            componentAny.toolbarItem.isOpen = false;

            componentAny.resetComponentMenu(componentAny.toolbarItem.isOpen);

            expect(component.tabsList).toEqual([
                {
                    id: 'componentTypesTab',
                    title: 'se.cms.compomentmenu.tabs.componenttypes',
                    component: ComponentTypesTabComponent,
                    hasErrors: false,
                    active: true
                },
                {
                    id: 'componentsTab',
                    title: 'se.cms.compomentmenu.tabs.customizedcomp',
                    component: ComponentsTabComponent,
                    hasErrors: false,
                    active: false
                }
            ]);
            expect(component.model).toEqual({
                componentsTab: { hasMultipleContentCatalogs: true },
                isOpen: false
            });
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(RESET_COMPONENT_MENU_EVENT);
        });

        it('GIVEN tab list is defined WHEN toolbar item is opened THEN it should update model and publish event', async () => {
            // initialize tablist
            await component.ngOnInit();
            componentAny.toolbarItem.isOpen = true;

            componentAny.resetComponentMenu(componentAny.toolbarItem.isOpen);

            expect(component.tabsList).toEqual([
                {
                    id: 'componentTypesTab',
                    title: 'se.cms.compomentmenu.tabs.componenttypes',
                    component: ComponentTypesTabComponent,
                    hasErrors: false
                },
                {
                    id: 'componentsTab',
                    title: 'se.cms.compomentmenu.tabs.customizedcomp',
                    component: ComponentsTabComponent,
                    hasErrors: false
                }
            ]);
            expect(component.model).toEqual({
                componentsTab: { hasMultipleContentCatalogs: true },
                isOpen: true
            });
            expect(crossFrameEventService.publish).toHaveBeenCalledWith(RESET_COMPONENT_MENU_EVENT);
        });
    });
});
