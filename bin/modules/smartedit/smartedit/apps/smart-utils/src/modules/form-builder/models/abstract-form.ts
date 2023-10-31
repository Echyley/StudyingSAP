/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AbstractControl } from '@angular/forms';
import { Observable } from 'rxjs';

import { InputProperties, InputPropertyChange } from './input-properties';
import { ComponentType } from './types';
import { ValidatorParameters } from './validator-parameters';

export interface AbstractForm extends AbstractControl {
    /**
     * Defines the component used to render the form
     */
    readonly component: ComponentType;

    /**
     * Inputs to be set onto the the dynamic form components.
     */
    readonly inputs: InputProperties;

    /**
     * Listen onto any property changes triggered by 'setInput' method.
     *
     * @type {Observable<InputPropertyChange>}
     */
    readonly inputChanges: Observable<InputPropertyChange>;

    /**
     * validatorParams` holds data to the synch and
     * asynch validators configuration for any
     * FormField, FormGroup and FormList.
     */
    readonly validatorParams: ValidatorParameters;

    /**
     * Used to determine if current form property is permanent. If
     * property does not persist then it will not be part of the
     * generated object model.
     */
    readonly persist: boolean;

    /**
     * Get mapped values of fields to be persisted.
     */
    getPersistedValue(): any;

    /**
     * Sets property and marks components for changes if property is decorated
     * with FormProperty.
     *
     * @param {keyof T} key
     * @param {T[keyof T]} value
     */
    setInput<T>(key: keyof T, value: T[keyof T]): void;

    /**
     * Retrieves a property.
     *
     * @param {keyof T} key
     * @returns {T[keyof T] | undefined}
     */
    getInput<T>(key: keyof T): T[keyof T] | undefined;
}
