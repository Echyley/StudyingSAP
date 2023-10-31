/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';

@Component({
    selector: 'se-email',
    templateUrl: './EmailComponent.html'
})
export class EmailComponent {
    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>) {}
}
