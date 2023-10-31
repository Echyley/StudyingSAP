/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import * as lo from 'lodash';

export function isBlankValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const isBlank = control.value && control.value.length > 0 && '' === lo.trim(control.value);
        return isBlank ? { isBlank: { value: control.value } } : null;
    };
}
