/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken } from '@angular/core';
import { ComponentAttributes } from './AbstractDecorator';

export interface ContextualMenuItemData {
    componentAttributes: ComponentAttributes;
    /**
     * Used to prevent disappearing of currently highlighted decorator when moving the cursor to another one.
     *
     * When I click on button so that the popover is displayed and then I move the cursor outside the decorator, the current decorator dissapears and removes the popover from the DOM.
     * When the popover is opened, the decorator must be displayed unless I click outside.
     */
    setRemainOpen: (key: string, remainOpen: boolean) => void;
}

export const CONTEXTUAL_MENU_ITEM_DATA = new InjectionToken<ContextualMenuItemData>(
    'CONTEXTUAL_MENU_ITEM_DATA'
);
