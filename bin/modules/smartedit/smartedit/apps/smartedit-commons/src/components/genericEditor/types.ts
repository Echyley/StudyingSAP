/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Payload, TypedMap } from '@smart/utils';
import { ILanguage, IUriContext } from '../../services';

import { GenericEditorState } from './models';

export interface GenericEditorOnSubmitResponse {
    payload: Payload;
    response: Payload;
}

export interface GenericEditorAttribute {
    cmsStructureType: string;
    cmsStructureEnumType?: string;
    qualifier: string;
    i18nKey?: string;
    localized?: boolean;
    editable?: boolean;
    required?: boolean;
    collection?: boolean;
    postfixText?: string;
}

export enum GenericEditorMediaType {
    VIDEO = 'video',
    IMAGE = 'image',
    PDF_DOCUMENT = 'pdf',
    DEFAULT = ''
}

/**
 * **Deprecated since 2105. It will be removed in next release.**
 *
 * custom sanitization function
 * first argument is a property value (not the full model) that may be comple or scalar
 *
 * @deprecated
 */
export type GenericEditorCustomSanitize<T = any> = (
    payload: T,
    sanitize: (input: string) => string
) => T;

/** Describes the field being edited as defined by the structure API described in {@link GenericEditorFactoryService}. */
export interface GenericEditorField extends GenericEditorAttribute {
    editorStackId?: string;
    hasErrors?: boolean;
    hasWarnings?: boolean;
    smarteditComponentType?: string;
    messages?: GenericEditorFieldMessage[];
    /**
     * A boolean to determine if we are in paged mode as opposed to retrieving all items at once.
     */
    paged?: boolean;
    template?: string;
    component?: Type<any>;
    initiated?: string[];
    /** The original array of options (used by {@link OptionsDropdownPopulator}). */
    options?: TypedMap<string[]> | GenericEditorOption;
    hidePrefixLabel?: boolean;
    hideFieldWidget?: boolean;
    dependsOnValue?: string;
    dependsOnField?: string;
    allowMediaType?: GenericEditorMediaType;
    /**
     * **Deprecated since 2105. It will be removed in next release.**
     *
     * @deprecated
     */
    customSanitize?: GenericEditorCustomSanitize;
    requiresUserCheck?: TypedMap<boolean>;
    isUserChecked?: boolean;
    defaultValue?: string;
    params?: TypedMap<string>;
    /**
     * A comma separated list of attributes to include from the model when building the request params.
     * The qualifier of the parent dropdown that this dropdown depends on.
     */
    dependsOn?: string;
    /**
     * The uri used to make a rest call to fetch data (used by {@link UriDropdownPopulator})
     */
    uri?: string;
    /**
     * If a propertyType is defined, the `GenericEditorDropdownComponent` will use the populator associated to it with the following recipe name: `propertyType + "DropdownPopulator"`.
     */
    propertyType?: string;
    /**
     * The name of the attribute to use when setting the id attribute.
     */
    idAttribute?: string;
    /**
     * A list of attributes to use when setting the label attribute.
     */
    labelAttributes?: string[];
    errors?: GenericEditorFieldMessage[];
    control?: FormControl;
    validators?: GenericEditorFieldValidatorMap;

    /**
     * This map is only used for localized fields. Each entry contains a boolean
     * that specifies whether the field should be enabled for a language or not.
     */
    isLanguageEnabledMap?: { [languageId: string]: boolean };
    tooltip?: string;
    labelText?: string;
    placeholder?: string;
    containedTypes?: string[];
}

export interface GenericEditorTab {
    id: string;
    title: string;
    templateUrl?: string;
    component?: Type<any>;
    hasErrors?: boolean;
    active?: boolean;
}

export type GenericEditorFieldsMap = TypedMap<GenericEditorField[]>;

/**
 * Descriptor schema necessary to build a GenericEditorState.
 */
export interface GenericEditorSchema {
    id: string;
    languages: ILanguage[];
    structure: GenericEditorStructure;
    uriContext: IUriContext;
    smarteditComponentType: string;
    targetedQualifier?: string;
}

export interface GenericEditorFieldMessage {
    fromSubmit?: boolean;
    isNonPristine?: boolean;
    message: string;
    subject?: string;
    type?: string;
    uniqId?: string;
    marker?: string;
    format?: string;
    language?: string;
    [index: string]: any;
}

export interface GenericEditorTabConfiguration {
    priority: number;
}

export interface GenericEditorStructure {
    attributes: GenericEditorAttribute[];
    category?: string;
    type?: string;
}

export interface IGenericEditorFactoryOptions {
    content?: Payload;
    contentApi?: string;
    customOnSubmit?: (newContent: Payload) => Promise<GenericEditorOnSubmitResponse>;
    editorStackId?: string;
    id?: string;
    smarteditComponentId?: string;
    smarteditComponentType?: string;
    structure?: GenericEditorStructure;
    structureApi?: string;
    updateCallback?: (pristine: Payload, results: Payload) => void;
    uriContext?: Promise<IUriContext>;
    element: HTMLElement;
}

export type GenericEditorPredicate = (structure: GenericEditorStructure) => boolean | string;

export interface GenericEditorInfo {
    editorStackId?: string;
    editorId: string;
    component: Payload;
    componentType: string;
}

export interface GenericEditorMapping {
    structureTypeMatcher: string | ((...args: any[]) => boolean);
    componentTypeMatcher: string | ((...args: any[]) => boolean);
    discriminatorMatcher: string | ((...args: any[]) => boolean);
    value: any;
}
export type GenericEditorFieldValidatorPredicate = (
    id: string,
    structure: GenericEditorField,
    required: boolean,
    component: Payload
) => boolean;

export type GenericEditorFieldValidatorMap = TypedMap<GenericEditorFieldValidatorPredicate>;

export interface GenericEditorMappingConfiguration {
    template?: string;
    component?: Type<any>;
    /**
     * **Deprecated since 2105. It will be removed in next release.**
     *
     * @deprecated
     */
    customSanitize?: GenericEditorCustomSanitize;
    precision?: string;
    i18nKey?: string;
    hidePrefixLabel?: boolean;
    hideFieldWidget?: boolean;
    validators?: GenericEditorFieldValidatorMap;
    dependsOnValue?: string;
    dependsOnField?: string;
    tooltip?: string;
    allowMediaType?: GenericEditorMediaType;
}

export interface BackendValidationErrors {
    errors: GenericEditorFieldMessage[];
}
export interface BackendErrorResponse<T> {
    error: T;
}
export interface GenericEditorAPI {
    setSubmitButtonText: (_submitButtonText: string) => void;
    setCancelButtonText: (_cancelButtonText: string) => void;
    setAlwaysShowSubmit: (_alwaysShowSubmit: boolean) => void;
    setAlwaysShowReset: (_alwaysShowReset: boolean) => void;
    setOnReset: (_onReset: () => void) => void;
    setPreparePayload: (_preparePayload: (payload: Payload) => Promise<Payload>) => void;
    setUpdateCallback: (_updateCallback: (pristine: Payload, results: Payload) => void) => void;
    updateContent: (component: Payload) => void;
    getContent: () => Payload;
    onContentChange: () => void;
    addContentChangeEvent: (event: () => void) => () => void;
    triggerContentChangeEvents: () => void;
    clearMessages: () => void;
    switchToTabContainingQualifier: (qualifier: string) => void;
    considerFormDirty: () => void;
    setInProgress: (isInProgress: boolean) => void;
    isSubmitDisabled: () => boolean;
    getLanguages: () => Promise<ILanguage[]>;
    setAlwaysEnableSubmit: (alwaysEnableSubmit: boolean) => void;
}

export interface IGenericEditor {
    readonly id: string;
    readonly form: GenericEditorState;
    readonly api: GenericEditorAPI;
    /**
     * Handler for "Cancel" button click event.
     *
     * If false, `reset` method will not be called.
     * There is no need to call `reset` method for modal components when that component is closed.
     */
    readonly onReset: () => boolean | void;
    readonly alwaysShowSubmit: boolean;
    readonly alwaysShowReset: boolean;
    readonly editorStackId: string;
    readonly hasFrontEndValidationErrors: boolean;
    readonly submitButtonText: string;
    readonly cancelButtonText: string;
    readonly parameters: IUriContext;
    readonly inProgress: boolean;
    readonly smarteditComponentType: string;
    readonly smarteditComponentId: string;
    readonly updateCallback: (pristine: Payload, results: Payload) => void;
    readonly structure: GenericEditorStructure;
    readonly uriContext: Promise<IUriContext>;
    readonly editorStructureService: any;
    readonly editorCRUDService: any;
    readonly initialContent: Payload;
    readonly pristine: Payload;
    readonly initialDirty: boolean;
    readonly targetedQualifier: string;

    _finalize(): void;
    init(): Promise<void>;
    isDirty(): boolean;
    reset(pristine?: Payload): Promise<any>;
    submit(newContent: Payload): Promise<any>;
    getComponent(): Payload;
    isValid(comesFromSubmit?: boolean): boolean;
    isSubmitDisabled(): boolean;
    watchFormErrors(form: HTMLFormElement): void;
    fetch(): Promise<any>;
    load(): Promise<any>;
    popEditorFromStack(): void;
    refreshOptions(field: GenericEditorField, qualifier: string, search: string): Promise<void>;
}

export type IGenericEditorConstructor = new (conf: IGenericEditorFactoryOptions) => IGenericEditor;

export interface GenericEditorOption {
    id?: string;
    label?: string | TypedMap<string>;
    [key: string]: any;
}

export interface GenericEditorWidgetData<T> {
    id: string;
    field: GenericEditorField;
    qualifier: string;
    model: T;
    editor?: IGenericEditor;
    isFieldDisabled: () => boolean;
}
