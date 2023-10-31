/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken } from '@angular/core';
import { Page } from '../../../../services';
import { CLICK_DROPDOWN, LINKED_DROPDOWN } from '../../../../utils';
import { FetchStrategy } from '../../../select';
import { GenericEditorField, GenericEditorOption } from '../../types';
import { DropdownPopulatorFetchPageResponse, IDropdownPopulator } from './populators';

export const LINKED_DROPDOWN_TOKEN = new InjectionToken<string>(LINKED_DROPDOWN);
export const DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN = new InjectionToken<string>(
    'DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN'
);
export const CLICK_DROPDOWN_TOKEN = new InjectionToken<string>(CLICK_DROPDOWN);

/**
 * @internal
 * @ignore
 */
export interface GenericEditorDropdownConfiguration {
    field: GenericEditorField;
    qualifier: string;
    model: any;
    id: string;
    onClickOtherDropdown?: (key?: string, qualifier?: string) => void;
}

export interface IPopulatorName {
    options: string;
    uri: string;
    propertyType: string;
    cmsStructureType: string;
    smarteditComponentType: {
        withQualifier: string;
        withQualifierForDowngradedService: string;
        withoutQualifier: string;
    };
}

/**
 * @internal
 * @ignore
 */
export interface IGenericEditorDropdownService {
    field: GenericEditorField;
    qualifier: string;
    model: any;
    initialized: boolean;
    isMultiDropdown: boolean;
    isPaged: boolean;
    items: GenericEditorOption[];
    selection: any;
    fetchStrategy: FetchStrategy<any>;
    populator: IDropdownPopulator;
    init(): void;
    onClick(): void;
    triggerAction(): void;
    reset(): void;
    fetchPage(
        search: string,
        pageSize: number,
        currentPage: number,
        selectedItems?: any
    ): Promise<DropdownPopulatorFetchPageResponse | void>;
    fetchAll(search?: string): PromiseLike<GenericEditorOption[]>;
    limitToNonSelectedItems(page: any, selectedItems: any): any;
    isInclude(element: Page<any>, arr: any): boolean;
}

/**
 * @internal
 * @ignore
 */
export abstract class IGenericEditorDropdownServiceConstructor {
    constructor(conf: GenericEditorDropdownConfiguration) {
        //
    }
}

export type IGenericEditorDropdownSelectedOption = GenericEditorOption;
export interface IGenericEditorDropdownSelectedOptionEventData<
    T extends IGenericEditorDropdownSelectedOption = IGenericEditorDropdownSelectedOption
> {
    qualifier: string;
    optionObject: T;
}
