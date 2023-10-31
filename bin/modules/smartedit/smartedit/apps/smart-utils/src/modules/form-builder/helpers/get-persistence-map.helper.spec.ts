/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbstractForm, FormField, FormGrouping, FormList } from '../models';
import { getPersistenceMap } from './get-persistence-map.helper';

describe('getPersistenceMap', () => {
    const noop: any = {};

    const createField = (persist: boolean) => new FormField(null, noop, { persist } as any);

    const createGroup = (persist: boolean, forms: { [path: string]: AbstractForm }) =>
        new FormGrouping(forms, noop, { persist } as any);

    const createList = (persist: boolean, forms: AbstractForm[]) =>
        new FormList(forms, noop, { persist } as any);

    it('should get the persistance map of a form group', () => {
        const group = createGroup(true, {
            tab: createGroup(false, {
                prop1: createField(true),
                prop2: createField(true)
            }),
            prop3: createGroup(true, {
                prop4: createField(true)
            }),
            prop5: createField(false)
        });

        expect(getPersistenceMap(group)).toEqual({
            prop1: ['tab', 'prop1'],
            prop2: ['tab', 'prop2'],
            prop3: ['prop3'],
            'prop3.prop4': ['prop3', 'prop4']
        });
    });

    it('should get the persistance map of a form array', () => {
        const list = createList(true, [
            createGroup(false, {
                prop1: createGroup(true, {
                    prop2: createField(true)
                }),
                prop3: createField(false)
            }),
            createField(true),
            createField(false)
        ]);

        expect(getPersistenceMap(list)).toEqual({
            prop1: ['0', 'prop1'],
            'prop1.prop2': ['0', 'prop1', 'prop2'],
            1: ['1']
        });
    });

    it('should get the persistance map of a group with a nested array', () => {
        const group = createGroup(true, {
            name: createField(true),
            description: createField(true),
            attributes: createList(true, [
                createGroup(true, {
                    tabs: createGroup(false, {
                        field1: createField(true)
                    })
                }),
                createGroup(true, {
                    tabs: createGroup(false, {
                        field1: createField(true)
                    })
                }),
                createGroup(true, {
                    tabs: createGroup(false, {
                        field1: createField(true)
                    })
                })
            ])
        });

        expect(getPersistenceMap(group)).toEqual({
            name: ['name'],
            description: ['description'],
            attributes: ['attributes'],
            'attributes.0': ['attributes', '0'],
            'attributes.0.field1': ['attributes', '0', 'tabs', 'field1'],
            'attributes.1': ['attributes', '1'],
            'attributes.1.field1': ['attributes', '1', 'tabs', 'field1'],
            'attributes.2': ['attributes', '2'],
            'attributes.2.field1': ['attributes', '2', 'tabs', 'field1']
        });
    });
});
