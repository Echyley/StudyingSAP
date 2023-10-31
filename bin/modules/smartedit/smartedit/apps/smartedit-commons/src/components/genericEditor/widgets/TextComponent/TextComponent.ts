/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';
import { GenericEditorWidgetData } from '../../types';

@Component({
    template: ` <p [id]="data.field.qualifier + '-text'">{{ data.model[data.qualifier] }}</p> `,
    selector: 'se-component-text'
})
export class TextComponent {
    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>) {}
}
