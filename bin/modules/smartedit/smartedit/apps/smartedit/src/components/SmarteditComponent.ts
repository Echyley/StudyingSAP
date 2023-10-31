/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Injector } from '@angular/core';
import { registerCustomComponents, SMARTEDIT_COMPONENT_NAME } from 'smarteditcommons';

@Component({
    selector: SMARTEDIT_COMPONENT_NAME,
    template: ``
})
export class SmarteditComponent {
    constructor(injector: Injector) {
        registerCustomComponents(injector);
    }
}
