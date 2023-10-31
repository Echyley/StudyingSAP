/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AbstractControl, AbstractControlOptions, FormGroup, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { AbstractForm } from './abstract-form';
import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';
/**
 * A FormGrouping is used to encapsulate form data
 * of objects.
 */
export declare class FormGrouping extends FormGroup implements AbstractForm {
    /**
     * @inheritdoc
     * @override
     */
    readonly controls: {
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
    constructor(controls: {
        [key: string]: AbstractControl;
    }, validatorOrOpts: AbstractControlOptions, { component, inputs, validatorParams, persist }: {
        component: ComponentType;
        inputs: InputProperties;
        validatorParams: ValidatorParameters;
        persist: boolean;
    });
    /**
     * @inheritdoc
     * @param {keyof T} key
     * @param {T[keyof T]} value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void;
    /**
     * @inheritdoc
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined}
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined;
    /**
     * Manually sets nested errors to each FormControl.
     *
     * Note: Method should be called on the next rendering cycle and not on the initialization of the form. Should be
     * used to enforce backend validation.
     *
     * @param errors
     */
    setNestedErrors(errors?: [string[] | string, ValidationErrors][]): void;
    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue(): any;
}
