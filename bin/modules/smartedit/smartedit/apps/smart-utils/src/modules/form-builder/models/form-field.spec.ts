/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { FormField } from './form-field';
import { InputProperties } from './input-properties';

describe('FormBuilder - FormField', () => {
    let field: FormField;
    beforeEach(() => {
        field = new FormField('hello', {}, {
            inputs: new InputProperties({
                prop: 'world'
            })
        } as any);
    });

    it('should return the initialized values', () => {
        expect(field.getPersistedValue()).toBe('hello');
        expect(field.root).toBe(field);
    });
});
