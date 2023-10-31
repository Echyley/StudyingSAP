/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { isUndefined, omitBy } from 'lodash';

/**
 * ValidatorParameters holds data to the synchronous and
 * asynchronous validators configuration for a FormField.
 */
export class ValidatorParameters {
    public validators: { [index: string]: any } = {};
    public asyncValidators: { [index: string]: any } = {};

    constructor(
        validators: { [index: string]: any } = {},
        asyncValidators: { [index: string]: any } = {}
    ) {
        this.validators = this._omitUndefinedValues(validators);
        this.asyncValidators = this._omitUndefinedValues(asyncValidators);
    }

    /**
     * Determines if synchronous validator exists.
     *
     * @param name The name of the synchronous validator.
     * @returns A boolean if it has that parameter.
     */
    has(name: string): boolean {
        return this.validators.hasOwnProperty(name);
    }

    /**
     * Returns parameters of the synchronous validator.
     *
     * @param name The name of the synchronous validator.
     * @returns The param of the validator.
     */
    get(name: string): null | any {
        if (!this.has(name)) {
            return null;
        }
        return this.validators[name];
    }

    /**
     * Determines if asynchronous validator exists.
     *
     * @param name The name of the asynchronous validator.
     * @returns A boolean if it has that parameter.
     */
    hasAsync(name: string): boolean {
        return this.asyncValidators.hasOwnProperty(name);
    }

    /**
     * Returns parameters of the asynchronous validator.
     *
     * @param name The name of the asynchronous validator.
     * @returns The param of the validator.
     */
    getAsync(name: string): null | any {
        if (!this.hasAsync(name)) {
            return null;
        }
        return this.asyncValidators[name];
    }

    /**
     * @internal
     * Returns a object with all those keys that have
     * undefined values.
     */
    private _omitUndefinedValues(object: { [index: string]: any }): { [index: string]: any } {
        return omitBy(object, isUndefined);
    }
}
