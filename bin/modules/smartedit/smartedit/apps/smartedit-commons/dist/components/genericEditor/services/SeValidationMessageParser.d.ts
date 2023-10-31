import { GenericEditorFieldMessage } from '../types';
export declare const parseValidationMessage: (message: string) => GenericEditorFieldMessage;
/**
 * This service provides the functionality to parse validation messages (errors, warnings) received from the backend.
 * This service is used to parse validation messages (errors, warnings) for parameters such as language and format,
 * which are sent as part of the message itself.
 */
export declare class SeValidationMessageParser {
    /**
     * Parses extra details, such as language and format, from a validation message (error, warning). These details are also
     * stripped out of the final message. This function expects the message to be in the following format:
     *
     * <pre>
     * const message = "Some validation message occurred. Language: [en]. Format: [widescreen]. SomeKey: [SomeVal]."
     * </pre>
     *
     * The resulting message object is as follows:
     * <pre>
     * {
     *     message: "Some validation message occurred."
     *     language: "en",
     *     format: "widescreen",
     *     somekey: "someval"
     * }
     * </pre>
     */
    parse(message: string): GenericEditorFieldMessage;
}
