/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DialogConfig } from '@fundamental-ngx/core';
import { Payload } from 'smarteditcommons';
import { IGenericEditorModalServiceComponent } from './IGenericEditorModalServiceComponent';
/**
 * Convenience service to open an editor modal window for a given component type and component ID.
 *
 * Example:
 * We pass information about component to open method, and the component editor in form of modal appears.
 */
export abstract class IEditorModalService {
    /**
     * Proxy function which delegates opening an editor modal for a given component type and component ID to the
     * SmartEdit container.
     *
     * @param componentAttributes The details of the component to be created/edited
     * @param componentAttributes.smarteditComponentUuid An optional universally unique UUID of the component if the component is being edited.
     * @param componentAttributes.smarteditComponentId An optional universally unique ID of the component if the component is being edited.
     * @param componentAttributes.smarteditComponentType The component type
     * @param componentAttributes.smarteditCatalogVersionUuid The smartedit catalog version UUID to add the component to.
     * @param componentAttributes.catalogVersionUuid The catalog version UUID to add the component to.
     * @param componentAttributes.initialDirty Is the component dirty.
     * @param componentAttributes.content An optional content for create operation. It's ignored if componentAttributes.smarteditComponentUuid is defined.
     * @param targetSlotId The ID of the slot in which the component is placed.
     * @param position The position in a given slot where the component should be placed.
     * @param targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     * @param saveCallback The optional function that is executed if the user clicks the Save button and the modal closes successfully. The function provides one parameter: item that has been saved.
     * @param editorStackId The string that identifies the stack of editors being edited together.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    open<T = any>(
        componentAttributes: IComponent,
        targetSlotId?: string,
        position?: number,
        targetedQualifier?: string,
        saveCallback?: (item: any) => void,
        editorStackId?: string,
        config?: DialogConfig
    ): Promise<T> {
        'proxyFunction';
        return null;
    }

    /**
     * Proxy function which delegates opening an editor modal for a given component type and component ID to the
     * SmartEdit container.
     *
     * @param componentType The type of component as defined in the platform.
     * @param componentUuid The UUID of the component as defined in the database.
     * @param targetedQualifier Causes the genericEditor to switch to the tab containing a qualifier of the given name.
     * @param saveCallback The optional function that is executed if the user clicks the Save button and the modal closes successfully. The function provides one parameter: item that has been saved.
     * @param editorStackId The string that identifies the stack of editors being edited together.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    openAndRerenderSlot(
        componentType: string,
        componentUuid: string,
        targetedQualifier?: string,
        config?: DialogConfig,
        saveCallback?: (item: any) => void,
        editorStackId?: string
    ): Promise<any> {
        'proxyFunction';
        return null;
    }

    /**
     * Proxy function which delegates opening an generic editor modal for a given IGenericEditorModalServiceComponent data object
     *
     * @param componentData Object that contains all parameters for generic editor.
     * @param saveCallback the save callback that is triggered after submit.
     * @param errorCallback the error callback that is triggered after submit.
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    openGenericEditor(
        data: IGenericEditorModalServiceComponent,
        saveCallback?: () => void,
        errorCallback?: () => void,
        config?: DialogConfig
    ): Promise<any> {
        'proxyFunction';
        return null;
    }
}

export interface IComponent {
    smarteditComponentUuid?: string;
    smarteditComponentType: string;
    catalogVersionUuid?: string;
    smarteditCatalogVersionUuid?: string;
    smarteditComponentId?: string;
    smarteditElementUuid?: string;
    content?: Payload;
    initialDirty?: boolean;
}
