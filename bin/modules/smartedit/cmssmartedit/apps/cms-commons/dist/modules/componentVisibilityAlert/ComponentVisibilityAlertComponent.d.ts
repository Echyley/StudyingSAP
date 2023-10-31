import { AlertRef } from '@fundamental-ngx/core';
import { IEditorModalService } from 'smarteditcommons';
import { IComponentVisibilityAlertService } from './IComponentVisibilityAlertService';
export declare class ComponentVisibilityAlertComponent {
    private editorModalService;
    private componentVisibilityAlertService;
    private alertRef;
    hyperlinkLabel: string;
    message: string;
    private component;
    constructor(editorModalService: IEditorModalService, componentVisibilityAlertService: IComponentVisibilityAlertService, alertRef: AlertRef);
    onClick(): Promise<void>;
    private checkProvidedArguments;
}
