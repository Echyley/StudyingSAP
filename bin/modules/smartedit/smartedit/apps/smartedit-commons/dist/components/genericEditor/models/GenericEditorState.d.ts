import { FormGrouping } from '@smart/utils';
import { Payload } from '../../../dtos';
import { ILanguage, IUriContext } from '../../../services';
import { GenericEditorField, GenericEditorFieldMessage, GenericEditorTab } from '../types';
/**
 * @internal
 * Holds the entire state of the generic editor.
 * Provides method to query and mutate the generic editor state.
 * The GenericEditorState is created by the GenericEditorStateBuilderService.
 */
export declare class GenericEditorState {
    readonly id: string;
    readonly group: FormGrouping;
    component: any;
    private proxiedComponent;
    pristine: any;
    readonly tabs: GenericEditorTab[];
    readonly fields: GenericEditorField[];
    readonly languages: ILanguage[];
    readonly parameters: IUriContext;
    /**
     * @internal
     */
    private _bcPristine;
    /**
     * @internal
     */
    private _formFields;
    /**
     * @internal
     */
    private _qualifierFieldMap;
    constructor(id: string, group: FormGrouping, component: any, proxiedComponent: any, pristine: any, tabs: GenericEditorTab[], fields: GenericEditorField[], languages: ILanguage[], parameters: IUriContext);
    /**
     * Removes all validation (local, outside or server) errors from fieds and tabs.
     */
    removeValidationMessages: () => void;
    /**
     * Removes validation errors generated in frontend, not the ones sent by outside or server.
     * Removes errors only from fields, not tabs.
     */
    removeFrontEndValidationMessages(): void;
    /**
     * Checks if this validation message belongs to the current generic editor by seeing if the generic editor
     * has the qualifier.
     *
     * TODO: It assumes that the qualifier is unique in every genericeditor.
     *
     * @param {GenericEditorFieldMessage} validationMessage
     * @return {boolean}
     */
    validationMessageBelongsToCurrentInstance(validationMessage: GenericEditorFieldMessage): boolean;
    /**
     * @param {GenericEditorFieldMessage[]} messages
     * @return {GenericEditorFieldMessage[]}
     */
    collectUnrelatedValidationMessages(messages: GenericEditorFieldMessage[]): GenericEditorFieldMessage[];
    /**
     * Collects validation errors on all the form fields.
     * Returns the list of errors or empty list.
     * Each error contains the following properties:
     * type - VALIDATION_MESSAGE_TYPES
     * subject - the field qualifier.
     * message - error message.
     * fromSubmit - contains true if the error is related to submit operation, false otherwise.
     * isNonPristine - contains true if the field was modified (at least once) by the user, false otherwise.
     * language - optional language iso code.
     */
    watchErrors(formElement: HTMLFormElement): void;
    collectFrontEndValidationErrors(comesFromSubmit: boolean, formElement: HTMLFormElement): GenericEditorFieldMessage[];
    /**
     * Displays validation errors for fields and changes error states for all tabs.
     * TODO: move validation to fields.
     */
    displayValidationMessages(validationMessages: GenericEditorFieldMessage[], keepAllErrors: boolean): Promise<void>;
    isDirty(qualifier?: string, language?: string): boolean;
    fieldsAreUserChecked(): boolean;
    /**
     * Updates the component with the patching component.
     */
    patchComponent(value: Payload): void;
    /**
     * Get sanitized payload to be sent to the backend.
     *
     * **Deprecated since 2105. It will be removed in next release.**
     *
     * @deprecated
     */
    sanitizedPayload(payload?: Payload): Payload;
    switchToTabContainingQualifier(targetedQualifier?: string): void;
    private _getParseValidationMessage;
    /**
     * @internal
     * Sees if it contains validation message type.
     */
    private _containsValidationMessageType;
    /**
     * @internal
     * Checks if validation message type is of type ValidationError or Warning.
     */
    private _isValidationMessageType;
    /**
     * @internal
     * Builds a comparable data object.
     */
    private _buildComparable;
    /**
     * @internal
     */
    private _buildFieldComparable;
    /**
     * @internal
     * Get all leaf nodes of the form.
     */
    private _buildFormFieldsArray;
}
