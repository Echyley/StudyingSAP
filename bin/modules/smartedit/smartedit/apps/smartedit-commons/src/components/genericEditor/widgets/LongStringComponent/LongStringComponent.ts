/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';
import { GenericEditorWidgetData } from '../../types';

@Component({
    template: `
        <textarea
            class="fd-form__control"
            style="width: 100%;"
            [ngClass]="{ 'is-invalid': data.field.hasErrors, 'is-warning': data.field.hasWarnings }"
            [placeholder]="data.field.tooltip || '' | translate"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"></textarea>
    `,
    selector: 'se-long-string'
})
export class LongStringComponent {
    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>) {}
}
