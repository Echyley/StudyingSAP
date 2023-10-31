/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from '@smart/utils';

import { ILanguage } from '../../services';
import { objectUtils } from '../../utils';
import { GenericEditorAPI } from './types';

export const createApi = (editor: any): GenericEditorAPI =>
    /**
     * The generic editor's api object exposing public functionality
     */
    ({
        /**
         * Overrides the i18n key used bfor the submit button.
         */
        setSubmitButtonText: (_submitButtonText: string): void => {
            editor.submitButtonText = _submitButtonText;
        },

        /**
         * Overrides the i18n key used bfor the submit button.
         */
        setCancelButtonText: (_cancelButtonText: string): void => {
            editor.cancelButtonText = _cancelButtonText;
        },

        /**
         * If set to true, will always show the submit button.
         */
        setAlwaysShowSubmit: (_alwaysShowSubmit: boolean): void => {
            editor.alwaysShowSubmit = _alwaysShowSubmit;
        },

        /**
         * If set to true, will always show the reset button.
         */
        setAlwaysShowReset: (_alwaysShowReset: boolean): void => {
            editor.alwaysShowReset = _alwaysShowReset;
        },

        /**
         * To be executed after reset.
         */
        setOnReset: (_onReset: () => void): void => {
            editor.onReset = _onReset;
        },

        /**
         * Function that passes a preparePayload function to the editor in order to transform the payload prior to submitting (see `GenericEditorFactoryService#preparePayload`)
         * @param preparePayload The function that takes the original payload as argument
         */
        setPreparePayload: (_preparePayload: (payload: Payload) => Promise<Payload>): void => {
            editor.preparePayload = _preparePayload;
        },

        /**
         * Function that passes an updateCallback function to the editor in order to perform an action upon successful submit. It is invoked with two arguments: the pristine object and the response from the server.
         * @param updateCallback the callback invoked upon successful submit.
         */
        setUpdateCallback: (
            _updateCallback: (pristine: Payload, results: Payload) => void
        ): void => {
            editor.updateCallback = _updateCallback;
        },

        /**
         * Function that updates the content of the generic editor without having to reinitialize
         *
         * @param component The component to replace the current model for the generic editor
         */
        updateContent: (component: Payload): void => {
            editor.form && editor.form.patchComponent(component);
        },

        /**
         * Copies of the current model
         * @returns a copy
         */
        getContent: (): Payload =>
            editor.form ? objectUtils.copy<Payload>(editor.form.component) : undefined,

        /**
         * **Deprecated since 1905 - use {@link addContentChangeEvent} instead.**
         *
         * Function triggered everytime the current model changes
         *
         * @deprecated
         */
        onContentChange(): void {
            return;
        },

        /**
         * Method adds a new function to the list of functions triggered everytime the current model changes
         *
         * @param {Function} The function triggered everytime the current model changes
         *
         * @returns The function to unregister the event;
         */
        addContentChangeEvent: (event: () => void): (() => void) => {
            editor.onChangeEvents.push(event);
            return (): void => {
                const index = editor.onChangeEvents.findIndex((e: any) => e === event);

                if (index > -1) {
                    editor.onChangeEvents.splice(index, 1);
                }
            };
        },

        /**
         * Triggers all functions that were added with addContentChangeEvent api method. It provides current content as parameter to every function call.
         */
        triggerContentChangeEvents: (): void => {
            editor.onChangeEvents.forEach((event: any) => {
                event(objectUtils.copy<Payload>(editor.form.component));
            });
        },

        /**
         * Function that clears all validation messages in the editor
         */
        clearMessages: (): void => {
            editor.form.removeValidationMessages();
        },

        /**
         * Causes the genericEditor to switch to the tab containing a qualifier of the given name.
         * @param qualifier the qualifier contained in the tab we want to switch to.
         */
        switchToTabContainingQualifier: (qualifier: string): void => {
            editor.targetedQualifier = qualifier;
        },

        /** Currently used by clone components to open editor in dirty mode. */

        considerFormDirty: (): void => {
            editor.initialDirty = true;
        },

        setInProgress: (isInProgress: boolean): void => {
            editor.inProgress = isInProgress;
        },

        /**
         * Returns true to inform that the submit button delegated to the invoker should be disabled.
         * @returns true if submit is disabled
         */
        isSubmitDisabled: (): boolean => editor.isSubmitDisabled(),

        /**
         * Function that returns a promise resolving to language descriptors. If defined, will be resolved
         * when the generic editor is initialized to override what languages are used for localized elements
         * within the editor.
         * @returns a promise resolving to language descriptors. Each descriptor provides the following
         * language properties: isocode, nativeName, name, active, and required.
         */
        getLanguages: (): Promise<ILanguage[]> => null,

        /**
         * If set to true, will always enable the submit button.
         */
        setAlwaysEnableSubmit: (alwaysEnableSubmit: boolean): void => {
            editor.alwaysEnableSubmit = alwaysEnableSubmit;
        }
    });
