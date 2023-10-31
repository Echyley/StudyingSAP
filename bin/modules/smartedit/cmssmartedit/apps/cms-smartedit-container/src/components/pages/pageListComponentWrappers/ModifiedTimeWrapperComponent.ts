/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { BaseWrapperComponent } from './BaseWrapperComponent';

@Component({
    selector: 'se-modified-time-wrapper',
    template: `<div>{{ item.modifiedtime | date : 'short' }}</div>`
})
export class ModifiedTimeWrapperComponent extends BaseWrapperComponent {}
