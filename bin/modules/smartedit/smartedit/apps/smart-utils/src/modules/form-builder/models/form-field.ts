/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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
export class FormField extends FormControl implements AbstractForm {
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

    constructor(
        value: any = {},
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
        super(value, validatorOrOpts);

        this.component = component;
        this.inputs = inputs;
        this.inputChanges = inputs.changes;
        this.validatorParams = validatorParams;
        this.persist = persist;
    }

    /**
     * @inheritdoc
     * @param key
     * @param value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void {
        this.inputs.set(key, value);
    }
    /**
     * @inheritdoc
     * @param key
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined {
        return this.inputs.get<T>(key);
    }

    /**
     * @inheritdoc
     * @return any
     */
    getPersistedValue(): any {
        if (!this.persist) {
            return undefined;
        }
        return this.value === undefined ? null : this.value;
    }
}
