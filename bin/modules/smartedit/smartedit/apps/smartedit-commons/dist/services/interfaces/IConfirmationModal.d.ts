import { TypedMap } from '@smart/utils';
import { MessageType } from '../../components/message';
/**
 * Describes configuration of Confirmation Modal.
 */
export interface ConfirmationModalConfig {
    /**
     * Message string to be displayed in the confirmation modal
     */
    description: string;
    /**
     * Object containing translations for description
     */
    descriptionPlaceholders?: TypedMap<string>;
    /**
     * Confirmation modal title
     */
    title?: string;
    /**
     * Flags whether only confirm button should be displayed
     */
    showOkButtonOnly?: boolean;
    /** Styled Message such as info, danger, warning displayed above description to indicate the operation importance.  */
    message?: {
        id?: string;
        text: string;
        type: MessageType;
    };
}
