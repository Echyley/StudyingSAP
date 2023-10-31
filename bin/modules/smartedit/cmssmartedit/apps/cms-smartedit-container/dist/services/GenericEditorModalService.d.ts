import { DialogConfig } from '@fundamental-ngx/core';
import { IModalService, IGenericEditorModalServiceComponent } from 'smarteditcommons';
import { GenericEditorModalComponentErrorCallback, GenericEditorModalComponentSaveCallback } from './genericEditorModalTypes';
/**
 * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
 */
export declare class GenericEditorModalService {
    private modalService;
    constructor(modalService: IModalService);
    /**
     * Function that opens an editor modal. For this method, you must specify an object to contain the edited information, and a save
     * callback that will be triggered once the Save button is clicked.
     */
    open<T = any>(data: IGenericEditorModalServiceComponent, saveCallback?: GenericEditorModalComponentSaveCallback<T>, errorCallback?: GenericEditorModalComponentErrorCallback, config?: DialogConfig): Promise<T>;
}
