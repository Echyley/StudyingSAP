/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { ControlValueAccessor } from '@angular/forms';

/**
 * @ngdoc object
 * @name utils.object:BaseValueAccessor
 * @description
 *
 * Class implementing {@link https://angular.io/api/forms/ControlValueAccessor ControlValueAccessor} interface used to create custom Angular inputs that
 * can be integrated with Angular Forms and.
 */

export abstract class BaseValueAccessor<T> implements ControlValueAccessor {
    public disabled = false;
    public value: T = null as unknown as T;

    public onChange(item: T): void {
        // Is set by registerOnChange method
    }
    public onTouched(): void {
        // Is set by registerOnTouched method
    }

    registerOnChange(fn: (item: T) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    writeValue(item: T): void {
        this.value = item;
    }
}
