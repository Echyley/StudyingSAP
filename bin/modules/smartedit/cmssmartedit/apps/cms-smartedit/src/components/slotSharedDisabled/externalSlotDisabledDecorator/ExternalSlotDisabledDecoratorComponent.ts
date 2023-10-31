/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';
import { AbstractDecorator, SeCustomComponent } from 'smarteditcommons';

@SeCustomComponent()
@Component({
    selector: 'external-slot-disabled-decorator', // At some point it should be prefixed with "se-". I leave it as it is because I am not sure about the impact of changing the selector.
    templateUrl: './ExternalSlotDisabledDecoratorComponent.html'
})
export class ExternalSlotDisabledDecoratorComponent extends AbstractDecorator {
    constructor() {
        super();
    }
}
