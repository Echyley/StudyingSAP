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
    errorCallback?: (messages: object[], form: any) => void;
}
