/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';

/**
 * FLOAT PRECISION
 */
/* @internal  */
export const DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION = '0.01';
@Component({
    selector: 'se-float',
    templateUrl: './FloatComponent.html'
})
export class FloatComponent {
    public precision: string = DEFAULT_GENERIC_EDITOR_FLOAT_PRECISION;

    constructor(@Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>) {}
}
