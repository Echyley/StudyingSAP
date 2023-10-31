import { InjectionToken } from '@angular/core';
import { Page } from '../../../../services';
import { FetchStrategy } from '../../../select';
import { GenericEditorField, GenericEditorOption } from '../../types';
import { DropdownPopulatorFetchPageResponse, IDropdownPopulator } from './populators';
export declare const LINKED_DROPDOWN_TOKEN: InjectionToken<string>;
export declare const DROPDOWN_IMPLEMENTATION_SUFFIX_TOKEN: InjectionToken<string>;
export declare const CLICK_DROPDOWN_TOKEN: InjectionToken<string>;
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
    fetchPage(search: string, pageSize: number, currentPage: number, selectedItems?: any): Promise<DropdownPopulatorFetchPageResponse | void>;
    fetchAll(search?: string): PromiseLike<GenericEditorOption[]>;
    limitToNonSelectedItems(page: any, selectedItems: any): any;
    isInclude(element: Page<any>, arr: any): boolean;
}
/**
 * @internal
 * @ignore
 */
export declare abstract class IGenericEditorDropdownServiceConstructor {
    constructor(conf: GenericEditorDropdownConfiguration);
}
export declare type IGenericEditorDropdownSelectedOption = GenericEditorOption;
export interface IGenericEditorDropdownSelectedOptionEventData<T extends IGenericEditorDropdownSelectedOption = IGenericEditorDropdownSelectedOption> {
    qualifier: string;
    optionObject: T;
}
