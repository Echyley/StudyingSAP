/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';

/**
 * Describes Dropdown Menu Item for {@link DropdownMenuComponent}.
 */
export interface IDropdownMenuItem {
    /**
     * i18n.
     */
    key?: string;
    /**
     * Icon css class.
     */
    icon?: string;
    /**
     * Component that will be rendered.
     */
    component?: Type<any>;
    /**
     * Custom css class that is appended to the dropdown item
     */
    customCss?: string;
    /**
     * When provided, the Item will be rendered by default component ({@link DropdownMenuItemDefaultComponent}).
     *
     * This function will be called with two arguments when the item is clicked.
     * The first one is [selectedItem]{@link DropdownMenuComponent#selectedItem}, the second one is the clicked item itself.
     *
     * Either one of callback, template, templateUrl or component must be present.
     */
    callback?(...args: any[]): void;
    /**
     * Predicate for displaying the Item.
     */
    condition?(...args: any[]): boolean;
}
