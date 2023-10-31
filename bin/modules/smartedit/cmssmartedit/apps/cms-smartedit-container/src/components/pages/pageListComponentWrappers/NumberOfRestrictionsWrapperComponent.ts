/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { BaseWrapperComponent } from './BaseWrapperComponent';

@Component({
    selector: 'se-number-of-restrictions-wrapper',
    template: `<se-restrictions-viewer [restrictions]="item.restrictions"></se-restrictions-viewer>`
})
export class NumberOfRestrictionsWrapperComponent extends BaseWrapperComponent {}
