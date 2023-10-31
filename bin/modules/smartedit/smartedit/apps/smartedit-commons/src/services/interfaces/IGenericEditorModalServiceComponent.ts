/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from '@smart/utils';
import { GenericEditorStructure } from '../../components/genericEditor';

/**
 * Describes the data used by GenericEditorModalComponent.
 */
export interface IGenericEditorModalServiceComponent {
    componentUuid?: string;
    componentType?: string;
    title: string;
    content?: Payload;
    structure?: GenericEditorStructure;
    contentApi?: string;
    targetedQualifier?: string;
    editorStackId?: string;
    cancelLabel?: string;
    saveLabel?: string;
    initialDirty?: boolean;
    readOnlyMode?: boolean;
    messages?: {
        type: string;
        message: string;
    }[];
    saveCallback?: (item: Payload) => void;
    // eslint-disable-next-line @typescript-eslint/ban-types
    errorCallback?: (messages: object[], form: any) => void;
}
