/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useClass:false */
import { InjectionToken } from '@angular/core';
import { Page, Payload } from '@smart/utils';

import { GenericEditorField, GenericEditorOption } from '../../../types';
import { DropdownPopulatorInterface } from './DropdownPopulatorInterface';

export interface IDropdownPopulator {
    isPaged(): boolean;
    fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]>;
    fetchPage<T = any>(
        payload: DropdownPopulatorPagePayload
    ): Promise<DropdownPopulatorFetchPageResponse<T>>;
    populateAttributes(
        items: GenericEditorOption[],
        idAttribute: string,
        orderedLabelAttributes: string[]
    ): GenericEditorOption[];
    search(items: GenericEditorOption[], searchTerm: string): Promise<GenericEditorOption[]>;
    getItem(payload: DropdownPopulatorItemPayload): Promise<GenericEditorOption>;
}

/* @internal */
export interface DropdownPopulatorPayload extends DropdownPopulatorItemPayload {
    /** The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link SeDropdownComponent}). */
    selection: GenericEditorOption;
    /** The search key when the user types in the dropdown (optional). */
    search: string;
}

/* @internal */
export interface DropdownPopulatorItemPayload {
    /** The id of the item to fetch. */
    id?: string;
    /**
     * The field descriptor from {@link GenericEditorFactoryService} containing information about the dropdown.
     */
    field: GenericEditorField;
    /** The model used when building query params on attributes defined in `payload.field.dependsOn`. */
    model: Payload;
    /** Cms Items URI for fetching the data. */
    uri?: string;
}

/* @internal */
export interface DropdownPopulatorPagePayload extends DropdownPopulatorPayload {
    /**  Number of items in the page. */
    pageSize: number;
    currentPage: number;
}

/* @internal */
export interface DropdownPopulatorFetchPageResponse<T = any> extends Page<T> {
    field: GenericEditorField;
    [index: string]: any;
}

/**
 * Used to register Custom Populators that will be available for `GenericEditorDropdownServiceFactory`.
 *
 * A custom populator can be registered by providing the name of that populator without "DropdownPopulator" suffix
 * in the following properties of {@link GenericEditorField}.
 *
 * - propertType - e.g. `MyCustomDropdownPopulator` -> { propertyType: 'myCustom' }
 *
 * - cmsStrutureType - e.g. `MyCustomDropdownPopulator` -> { cmsStructureType: 'myCustom' }
 *
 * - smarteditComponentType - e.g. `MyCustomDropdownPopulator` -> { smarteditComponentType: 'myCustom' }
 *
 * - smarteditComponentType + qualifier - e.g. `MyCustomProductDropdownPopulator` { smarteditComponentType: 'myCustom', qualifier: 'product' }
 *
 * Note: The value of those properties is case insensitive.
 *
 * ### Example
 *
 *      \@NgModule({
 *          imports: [],
 *          providers: [
 *              {
 *                  provide: CustomDropdownPopulatorsToken,
 *                  useClass: MyCustomDropdownPopulator,
 *                  multi: true
 *               }
 *          ]
 *      })
 *      export class ExtensionModule {};
 */
export const CustomDropdownPopulatorsToken = new InjectionToken<DropdownPopulatorInterface[]>(
    'CustomDropdownPopulatorsToken'
);
