/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    AbstractControl,
    AbstractControlOptions,
    FormGroup,
    ValidationErrors
} from '@angular/forms';

import { Observable } from 'rxjs';

import { AbstractForm } from './abstract-form';
import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';

/**
 * A FormGrouping is used to encapsulate form data
 * of objects.
 */
export class FormGrouping extends FormGroup implements AbstractForm {
    /**
     * @inheritdoc
     * @override
     */
    readonly controls!: {
        [key: string]: AbstractForm;
    };

    /**
     * @inheritdoc
     */
    readonly component: ComponentType;

    /**
     * @inheritdoc
     */
    readonly inputs: InputProperties;

    /**
     * @inheritdoc
     */
    readonly validatorParams: ValidatorParameters;

    /**
     * @inheritdoc
     */
    readonly persist: boolean;

    /**
     * @inheritdoc
     */
    readonly inputChanges: Observable<InputPropertyChange>;

    constructor(
        controls: {
            [key: string]: AbstractControl;
        },
        validatorOrOpts: AbstractControlOptions = {},
        {
            component,
            inputs = new InputProperties(),
            validatorParams = new ValidatorParameters(),
            persist = true
        }: {
            component: ComponentType;
            inputs: InputProperties;
            validatorParams: ValidatorParameters;
            persist: boolean;
        }
    ) {
        super(controls, validatorOrOpts);

        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }

    /**
     * @inheritdoc
     * @param {keyof T} key
     * @param {T[keyof T]} value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void {
        this.inputs.set(key, value);
    }

    /**
     * @inheritdoc
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined}
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined {
        return this.inputs.get<T>(key);
    }

    /**
     * Manually sets nested errors to each FormControl.
     *
     * Note: Method should be called on the next rendering cycle and not on the initialization of the form. Should be
     * used to enforce backend validation.
     *
     * @param errors
     */
    setNestedErrors(errors: [string[] | string, ValidationErrors][] = []): void {
        errors.forEach(([path, validationErrors]) => {
            const form = this.get(path);

            /**
             * Fail if the form does not exist.
             */
            if (!form) {
                throw new Error(`FormGrouping - Path not found when setting nested error: ${path}`);
            }

            form.setErrors(validationErrors);
        });
    }

    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue(): any {
        return Object.keys(this.controls).reduce((acc, key) => {
            const child = this.controls[key];

            /**
             * Look ahead and if nested does not want to be mapped, merge the nested object with
             * the current object.
             *
             * If it's a field, then it's undefined.
             *
             * If it's a group, then it will be merged.
             */
            if (!child.persist) {
                return { ...acc, ...child.getPersistedValue() };
            }

            acc[key] = child.getPersistedValue();
            return acc;
        }, {} as any);
    }
}
