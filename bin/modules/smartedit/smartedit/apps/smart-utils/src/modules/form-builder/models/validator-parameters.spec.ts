/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ValidatorParameters } from './validator-parameters';

describe('ValidatorParameters', () => {
    it('it should initialize with proper defaults', () => {
        const parameters = new ValidatorParameters();

        expect(parameters.validators).toEqual({});
        expect(parameters.asyncValidators).toEqual({});
    });

    it('it should get synchronous and asynchronous parameters', () => {
        const UNIQUE_VALUE = 'http-address';
        const parameters = new ValidatorParameters(
            {
                required: true,
                dontAdd: undefined,
                false: false
            },
            {
                unique: UNIQUE_VALUE
            }
        );

        expect(parameters.validators).toEqual({
            required: true,
            false: false
        });
        expect(parameters.has('required')).toBeTruthy();
        expect(parameters.asyncValidators).toEqual({
            unique: UNIQUE_VALUE
        });
        expect(parameters.hasAsync('unique')).toBeTruthy();
        expect(parameters.getAsync('unique')).toBe(UNIQUE_VALUE);
    });
});
