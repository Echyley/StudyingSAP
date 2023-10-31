/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AfterViewChecked, ChangeDetectorRef, Component, ViewRef } from '@angular/core';
import { DynamicForm, DynamicInput, FormField, Payload } from '@smart/utils';

import { GenericEditorField } from '../../types';

/**
 * TODO: Some parts of the generic editor field can be moved up to this component.
 * and then we could dynamic inject which form we should put the control in.
 */
@Component({
    selector: 'se-ge-dynamic-field',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    template: `
        <se-generic-editor-field
            [formControl]="form"
            [field]="field"
            [model]="component"
            [qualifier]="qualifier"
            [id]="id"></se-generic-editor-field>
    `
})
export class GenericEditorDynamicFieldComponent implements AfterViewChecked {
    @DynamicForm()
    form: FormField;

    @DynamicInput()
    component: Payload;

    @DynamicInput()
    field: GenericEditorField;

    @DynamicInput()
    qualifier: string;

    @DynamicInput()
    id: string;

    constructor(private readonly cdr: ChangeDetectorRef) {}

    ngAfterViewChecked(): void {
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
