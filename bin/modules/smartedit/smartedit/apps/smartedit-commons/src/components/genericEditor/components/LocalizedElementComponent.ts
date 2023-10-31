/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, DoCheck } from '@angular/core';
import {
    DynamicForm,
    DynamicInput,
    DynamicInputChange,
    FormGrouping,
    ISessionService,
    Payload,
    TypedMap
} from '@smart/utils';

import { ILanguage } from '../../../services';
import { VALIDATION_MESSAGE_TYPES } from '../../../utils';
import { GenericEditorField, GenericEditorFieldMessage, GenericEditorTab } from '../types';
import { GenericEditorFieldWrapperComponent } from './GenericEditorFieldWrapperComponent';

@Component({
    selector: 'se-localized-element',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    templateUrl: './LocalizedElementComponent.html'
})
export class LocalizedElementComponent implements DoCheck, DynamicInputChange {
    @DynamicForm()
    form: FormGrouping;

    @DynamicInput()
    field: GenericEditorField;

    @DynamicInput()
    component: Payload;

    @DynamicInput()
    languages: ILanguage[];

    tabs: GenericEditorTab[];
    private _previousMessages: GenericEditorFieldMessage[];

    constructor(private sessionService: ISessionService) {}

    onDynamicInputChange(): void {
        this._createLocalizedTabs();
    }

    /**
     * TODO: Could probably remove this method after replacing native HTML, validation with
     * Angular validation.
     */
    ngDoCheck(): void {
        if (this.tabs && this.field.messages !== this._previousMessages) {
            this._previousMessages = this.field.messages;

            const messageMap: TypedMap<boolean> = this.field.messages
                ? this.field.messages
                      .filter(
                          (message: GenericEditorFieldMessage) =>
                              message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR
                      )
                      .reduce((holder: TypedMap<boolean>, next) => {
                          holder[next.language] = true;
                          return holder;
                      }, {})
                : {};
            this.tabs = this.tabs.map(function (tab) {
                const message = messageMap[tab.id];
                tab.hasErrors = message !== undefined ? message : false;
                return tab;
            });
        }
    }

    /**
     * Map fields to localized tabs.
     *
     * @returns {Promise<void>}
     * @private
     */
    private async _createLocalizedTabs(): Promise<void> {
        this.field.isLanguageEnabledMap = {};
        const { readableLanguages, writeableLanguages } =
            await this.sessionService.getCurrentUser();

        const readableSet = new Set(readableLanguages);
        const writeable = new Set(writeableLanguages);

        this.tabs = this.languages
            .filter((language: ILanguage) => readableSet.has(language.isocode))
            .map(({ isocode, required }: ILanguage) => {
                this.field.isLanguageEnabledMap[isocode] = writeable.has(isocode);

                const title = `${isocode.toUpperCase()}${
                    this.field.editable && this.field.required && required ? '*' : ''
                }`;
                return {
                    title,
                    id: isocode,
                    component: GenericEditorFieldWrapperComponent
                };
            });
    }
}
