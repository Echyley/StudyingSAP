/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IGenericEditorModalServiceComponent } from 'smarteditcommons';
import { GenericEditorModalComponent, GenericEditorUnrelatedErrorMessage } from './components';

export type GenericEditorModalComponentSaveCallback<T = any> = (item: T) => void;
export type GenericEditorModalComponentErrorCallback = (
    messages: GenericEditorUnrelatedErrorMessage[],
    instance: GenericEditorModalComponent
) => void;

export interface IGenericEditorModalComponentData {
    data: IGenericEditorModalServiceComponent;
    saveCallback?: GenericEditorModalComponentSaveCallback;
    errorCallback?: GenericEditorModalComponentErrorCallback;
}
