/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Component } from '@angular/core';
import { values } from 'lodash';

import { DynamicForm } from '../../decorators';
import { AbstractForm, FormGrouping } from '../../models';

/**
 * Default component used for listing forms for a group form.
 */
@Component({
    selector: 'form-lister',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    templateUrl: './form-lister.component.html'
})
export class FormListerComponent {
    @DynamicForm()
    form!: FormGrouping;

    get forms(): AbstractForm[] {
        return values(this.form.controls);
    }
}
