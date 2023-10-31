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
export declare abstract class BaseValueAccessor<T> implements ControlValueAccessor {
    disabled: boolean;
    value: T;
    onChange(item: T): void;
    onTouched(): void;
    registerOnChange(fn: (item: T) => void): void;
    registerOnTouched(fn: () => void): void;
    setDisabledState(isDisabled: boolean): void;
    writeValue(item: T): void;
}
