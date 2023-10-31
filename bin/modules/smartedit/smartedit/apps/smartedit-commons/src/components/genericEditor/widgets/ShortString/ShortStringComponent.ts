/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';
import { GenericEditorWidgetData } from '../../types';

@Component({
    template: `
        <input
            type="text"
            fd-form-control
            [state]="data.field.hasErrors ? 'error' : ''"
            id="{{ data.field.qualifier }}-shortstring"
            [attr.name]="data.field.qualifier"
            [disabled]="data.isFieldDisabled()"
            [(ngModel)]="data.model[data.qualifier]"
            [ngClass]="{
                'is-warning': data.field.hasWarnings
            }" />
    `,
    selector: 'se-short-string'
})
export class ShortStringComponent {
    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>) {}
}
