/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import { Component } from '@angular/core';
import { HiddenComponentMenuService, SlotContainerService } from 'cmssmartedit/services';
import { ContextualMenuService } from 'smartedit';
import { IContextualMenuButton, IContextualMenuConfiguration } from 'smarteditcommons';

describe('HiddenComponentMenuService', () => {
    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    const COMPONENT_TYPE = 'componentType1';
    const COMPONENT_ID = 'component1';
    const SLOT_ID = 'some slot ID';
    const CONTAINER_ID = 'some container id';
    const CONTAINER_TYPE = 'some container type';
    const conditionFn = jasmine.createSpy('condition');

    const regexpKeys: string[] = [];
    const nameI18nKey = 'nameI18nKey';

    @Component({ selector: 'dummy-component' })
    class DummyComponent {}

    const CONTEXTUAL_MENU_ITEMS: IContextualMenuButton[] = [
        {
            key: 'key1',
            action: {
                component: DummyComponent
            },
            i18nKey: 'ICON1',
            icon: 'icon1.png',
            regexpKeys,
            nameI18nKey
        },
        {
            key: 'key2',
            action: {
                component: DummyComponent
            },
            i18nKey: 'ICON2',
            condition: conditionFn,
            iconIdle: 'icon2.png',
            regexpKeys,
            nameI18nKey
        },
        {
            key: 'key3',
            action: {
                component: DummyComponent
            },
            i18nKey: 'ICON3',
            icon: 'icon3.png',
            regexpKeys,
            nameI18nKey
        }
    ] as IContextualMenuButton[];

    const EXPECTED_COMPONENT_INFO = {
        componentType: 'componentType1',
        componentId: 'component1',
        componentAttributes: {
            smarteditCatalogVersionUuid: undefined as string,
            smarteditComponentId: 'component1',
            smarteditComponentType: undefined as string,
            smarteditComponentUuid: 'SOME UIID',
            smarteditElementUuid: null as string
        },
        containerType: CONTAINER_TYPE,
        containerId: CONTAINER_ID,
        element: null as JQuery<HTMLElement>,
        isComponentHidden: true,
        iLeftBtns: 0,
        slotId: 'some slot ID'
    } as IContextualMenuConfiguration;

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    let hiddenComponentMenuService: HiddenComponentMenuService;
    let slotContainerService: jasmine.SpyObj<SlotContainerService>;
    let contextualMenuService: jasmine.SpyObj<ContextualMenuService>;
    let condition: boolean;
    let component: any;

    // --------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------
    beforeEach(() => {
        conditionFn.calls.reset();
        contextualMenuService = jasmine.createSpyObj('contextualMenuService', [
            'getContextualMenuByType'
        ]);
        slotContainerService = jasmine.createSpyObj('slotContainerService', [
            'getOriginalComponent',
            'getUuid'
        ]);
        slotContainerService = jasmine.createSpyObj('slotContainerService', [
            'getComponentContainer'
        ]);

        slotContainerService.getComponentContainer.and.returnValue(
            Promise.resolve<any>({
                containerId: CONTAINER_ID,
                containerType: CONTAINER_TYPE
            })
        );
        conditionFn.and.callFake(() => condition);

        condition = true;
        component = {
            uid: COMPONENT_ID,
            uuid: 'SOME UIID',
            typeCode: COMPONENT_TYPE
        };

        hiddenComponentMenuService = new HiddenComponentMenuService(
            contextualMenuService,
            slotContainerService
        );
    });

    it(`GIVEN there are contextual menu items defined for the given component
        WHEN getMenuItemsForHiddenComponent is called
        THEN it returns an empty list of items`, async () => {
        // GIVEN
        const buttons: IContextualMenuButton[] = [];
        contextualMenuService.getContextualMenuByType.and.returnValue(buttons);

        // WHEN
        const result = await hiddenComponentMenuService.getItemsForHiddenComponent(
            component,
            SLOT_ID
        );

        // THEN
        expect(result.buttons).toEqual([]);
    });

    it(`GIVEN there are contextual menu items defined for the given component but they are not enabled for hidden components
        WHEN getMenuItemsForHiddenComponent is called
        THEN it returns an empty list of items`, async () => {
        // GIVEN
        contextualMenuService.getContextualMenuByType.and.returnValue(CONTEXTUAL_MENU_ITEMS);

        // WHEN
        const result = await hiddenComponentMenuService.getItemsForHiddenComponent(
            component,
            SLOT_ID
        );

        // THEN
        expect(result.buttons).toEqual([]);
    });

    it(`GIVEN contextual menu has items defined for the given component and they are enabled for hidden components
        WHEN getMenuItemsForHiddenComponent is called
        THEN it returns those items`, async () => {
        // GIVEN
        contextualMenuService.getContextualMenuByType.and.returnValue(CONTEXTUAL_MENU_ITEMS);
        hiddenComponentMenuService.allowItemsInHiddenComponentMenu(['key2']);

        // WHEN
        const result = await hiddenComponentMenuService.getItemsForHiddenComponent(
            component,
            SLOT_ID
        );

        const button: IContextualMenuButton = CONTEXTUAL_MENU_ITEMS.find(
            (item) => item.key === 'key2'
        );
        // THEN
        expect(conditionFn).toHaveBeenCalledWith(EXPECTED_COMPONENT_INFO);
        expect(result.buttons).toEqual([button]);

        expect(result.configuration).toEqual(EXPECTED_COMPONENT_INFO);
    });

    it(`GIVEN contextual menu has items defined for the given component and they are enabled for hidden components but have an invalid condition
        WHEN getMenuItemsForHiddenComponent is called
        THEN it doesnt return those items`, async () => {
        // GIVEN
        contextualMenuService.getContextualMenuByType.and.returnValue(CONTEXTUAL_MENU_ITEMS);
        hiddenComponentMenuService.allowItemsInHiddenComponentMenu(['key2']);
        condition = false;

        // WHEN
        const result = await hiddenComponentMenuService.getItemsForHiddenComponent(
            component,
            SLOT_ID
        );

        // THEN
        expect(conditionFn).toHaveBeenCalledWith(EXPECTED_COMPONENT_INFO);
        expect(result.buttons).toEqual([]);
    });

    it(`GIVEN contextual menu has items defined for the given component and they are enabled for hidden components but don't have a condition
        WHEN getMenuItemsForHiddenComponent is called
        THEN it doesnt return those items`, async () => {
        // GIVEN
        contextualMenuService.getContextualMenuByType.and.returnValue(CONTEXTUAL_MENU_ITEMS);
        hiddenComponentMenuService.allowItemsInHiddenComponentMenu(['key3']);

        // WHEN
        const result = await hiddenComponentMenuService.getItemsForHiddenComponent(
            component,
            SLOT_ID
        );

        // THEN
        expect(conditionFn).not.toHaveBeenCalled();
        expect(result.buttons).toEqual([]);
    });

    it(`GIVEN hiddenComponentMenuService already has allowed items
        WHEN getAllowedItemsInHiddenComponentMenu
        THEN it returns those item keys`, () => {
        // GIVEN

        // WHEN
        const allowedItems = hiddenComponentMenuService.getAllowedItemsInHiddenComponentMenu();

        // THEN
        expect(allowedItems).toEqual([
            'externalcomponentbutton',
            'clonecomponentbutton',
            'se.cms.remove'
        ]);
    });

    it(`GIVEN hiddenComponentMenuService already has allowed items
        WHEN removeAllowedItemsInHiddenComponentMenu
        THEN it removes those items from the allowed items`, () => {
        // GIVEN

        // WHEN
        hiddenComponentMenuService.removeAllowedItemsInHiddenComponentMenu([
            'other_item',
            'externalcomponentbutton'
        ]);
        const allowedItems = hiddenComponentMenuService.getAllowedItemsInHiddenComponentMenu();

        // THEN
        expect(allowedItems).toEqual(['clonecomponentbutton', 'se.cms.remove']);
    });
});
