/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { DialogConfig } from '@fundamental-ngx/core';
import { IModalService, IGenericEditorModalServiceComponent } from 'smarteditcommons';
import { GenericEditorModalComponent } from './components/GenericEditorModalComponent';
import {
    GenericEditorModalComponentErrorCallback,
    GenericEditorModalComponentSaveCallback,
    IGenericEditorModalComponentData
} from './genericEditorModalTypes';

/**
 * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
 */
@Injectable()
export class GenericEditorModalService {
    constructor(private modalService: IModalService) {}

    /**
     * Function that opens an editor modal. For this method, you must specify an object to contain the edited information, and a save
     * callback that will be triggered once the Save button is clicked.
     */
    public open<T = any>(
        data: IGenericEditorModalServiceComponent,
        saveCallback?: GenericEditorModalComponentSaveCallback<T>,
        errorCallback?: GenericEditorModalComponentErrorCallback,
        config?: DialogConfig
    ): Promise<T> {
        const modalConfig: DialogConfig = config ? { ...config } : {};

        modalConfig.dialogPanelClass = `modal-lg ${modalConfig.dialogPanelClass || ''}`.trimEnd();
        modalConfig.escKeyCloseable = false;
        modalConfig.focusTrapped = false;
        modalConfig.backdropClickCloseable = false;
        const ref = this.modalService.open<IGenericEditorModalComponentData>({
            component: GenericEditorModalComponent,
            data: {
                data,
                saveCallback,
                errorCallback
            },
            config: modalConfig,
            templateConfig: {
                title: data.title,
                isDismissButtonVisible: true,
                titleSuffix: 'se.cms.editor.title.suffix'
            }
        });

        return new Promise((resolve, reject) => ref.afterClosed.subscribe(resolve, reject));
    }
}
