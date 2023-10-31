import { IGenericEditorModalServiceComponent } from 'smarteditcommons';
import { GenericEditorModalComponent, GenericEditorUnrelatedErrorMessage } from './components';
export declare type GenericEditorModalComponentSaveCallback<T = any> = (item: T) => void;
export declare type GenericEditorModalComponentErrorCallback = (messages: GenericEditorUnrelatedErrorMessage[], instance: GenericEditorModalComponent) => void;
export interface IGenericEditorModalComponentData {
    data: IGenericEditorModalServiceComponent;
    saveCallback?: GenericEditorModalComponentSaveCallback;
    errorCallback?: GenericEditorModalComponentErrorCallback;
}
