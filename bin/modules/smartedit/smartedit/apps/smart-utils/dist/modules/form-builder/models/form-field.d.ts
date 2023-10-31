/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AbstractControlOptions, FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { AbstractForm } from './abstract-form';
import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';
/**
 * A leaf node of forms.
 */
export declare class FormField extends FormControl implements AbstractForm {
    /**
     * @inheritdoc
     */
    readonly value: any;
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
    constructor(value: any, validatorOrOpts: AbstractControlOptions, { component, inputs, validatorParams, persist }: {
        component: ComponentType;
        inputs: InputProperties;
        validatorParams: ValidatorParameters;
        persist: boolean;
    });
    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void;
    /**
     * @inheritdoc
     * @param key
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined;
    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue(): any;
}
