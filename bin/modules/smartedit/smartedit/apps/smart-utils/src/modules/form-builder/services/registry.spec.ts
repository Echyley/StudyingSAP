/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Registry } from './registry';

class ExtendedRegistry extends Registry<string> {}

describe('FormBuilder - Registry', () => {
    let registry: ExtendedRegistry;

    it('should intialize a registry with items', () => {
        registry = new ExtendedRegistry({
            item1: 'tacos',
            item2: 'ramen'
        });

        expect(registry.get('item1')).toBe('tacos');
        expect(registry.get('item2')).toBe('ramen');
    });

    it('should add items to a registry', () => {
        registry = new ExtendedRegistry({});
        registry.add('item1', 'skewers');

        expect(registry.get('item1')).toBe('skewers');
    });

    it('should throw an error when an item is overwritten', () => {
        registry = new ExtendedRegistry({
            item1: 'tacos'
        });

        expect(() => {
            registry.add('item1', 'skewers');
        }).toThrowError(
            `ExtendedRegistry: is overriding an element named 'item1' in its registry.`
        );
    });

    it('should throw an error when anitem is not found', () => {
        registry = new ExtendedRegistry({});

        expect(() => {
            registry.get('item1');
        }).toThrowError(`ExtendedRegistry: does not have 'item1' in its registry.`);
    });
});
