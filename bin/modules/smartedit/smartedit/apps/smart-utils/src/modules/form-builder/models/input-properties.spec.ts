/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InputProperties } from './input-properties';

describe('FormBuilder - FormProperties', () => {
    let formProperties: InputProperties;
    beforeEach(() => {
        formProperties = new InputProperties({
            property1: 'property1-tacos',
            property2: 'property2-more-tacos'
        });
    });

    it('should initialize inputs', () => {
        expect(formProperties.get('property1')).toBe('property1-tacos');
        expect(formProperties.get('property2')).toBe('property2-more-tacos');
        expect(formProperties.get('property3')).toBeUndefined();
    });

    it('should broadcast changes if a perperty was changed or added', () => {
        const sub = formProperties.changes.subscribe(({ key, value }) => {
            expect(key).toBe('property1');
            expect(value).toBe('new value');
        });

        formProperties.set('property1', 'new value');
        sub.unsubscribe();
    });
});
