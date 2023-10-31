/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[seMediaAdvancedPropertiesCustom]'
})
export class MediaAdvancedPropertiesCustomDirective {
    constructor(public viewContainerRef: ViewContainerRef) {}
}
