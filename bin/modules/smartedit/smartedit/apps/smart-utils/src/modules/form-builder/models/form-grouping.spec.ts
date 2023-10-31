/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AbstractForm } from './abstract-form';
import { FormField } from './form-field';
import { FormGrouping } from './form-grouping';
import { InputProperties } from './input-properties';

class TypeComponent {}

describe('FormBuilder - FormGrouping', () => {
    let formGrouping: FormGrouping;
    const FORM_DESCR = 'The name is bob';
    beforeEach(() => {
        formGrouping = new FormGrouping(
            {
                tab: new FormGrouping(
                    {
                        name: new FormField(FORM_DESCR, {}, {
                            component: TypeComponent,
                            inputs: new InputProperties({
                                prop1: 'Hello From Field'
                            })
                        } as any)
                    },
                    {},
                    {
                        component: TypeComponent,
                        persist: false
                    } as any
                )
            },
            {},
            {} as any
        );
    });

    it('should initialize a form group and return proper values', () => {
        expect(formGrouping.persist).toBe(true);
        expect(formGrouping.controls).toBeDefined();

        const tab = formGrouping.get('tab') as AbstractForm;
        expect(tab.persist).toBe(false);
        expect(tab.root).toBe(formGrouping);
        expect(tab.parent).toBe(formGrouping);
        expect(formGrouping.controls.tab).toBe(tab);

        const name = formGrouping.get('tab.name') as AbstractForm;
        expect(name.persist).toBe(true);
        expect(name.parent).toBe(tab as FormGrouping);
        expect(name.root).toBe(formGrouping);
        expect(name.getInput('prop1')).toBe('Hello From Field');
    });

    it('should return null if it does not find a path', () => {
        expect(formGrouping.get('to.somewhere.unknown')).toBeNull();
        expect(formGrouping.get('tab.name.strange')).toBeNull();
    });

    it('should get the persisting value from the form group', () => {
        expect(formGrouping.getPersistedValue()).toEqual({
            name: FORM_DESCR
        });
    });

    it('should set errors on the field form', () => {
        const control = formGrouping.get('tab.name') as FormField;
        control.setErrors = jasmine.createSpy('control.setErrors');
        const error = { required: 'Field is required' };

        formGrouping.setNestedErrors([[['tab', 'name'], error]]);

        expect(control.setErrors).toHaveBeenCalledWith(error);
    });

    it('should throw an error if it cannot set error on a path', () => {
        const error = { required: 'Field is required' };

        expect(() => {
            formGrouping.setNestedErrors([[['tab', 'strange'], error]]);
        }).toThrowError('FormGrouping - Path not found when setting nested error: tab,strange');
    });

    it('should be able to add and delete a form dynamically', () => {
        formGrouping.addControl(
            'new',
            new FormField('new form', {}, {
                component: TypeComponent,
                persist: true
            } as any)
        );

        expect(formGrouping.controls.new).toBeDefined();
        expect(formGrouping.getPersistedValue()).toEqual({
            new: 'new form',
            name: FORM_DESCR
        });

        formGrouping.removeControl('new');
        expect(formGrouping.controls.new).toBeUndefined();
        expect(formGrouping.getPersistedValue()).toEqual({
            name: FORM_DESCR
        });
    });

    it('should not do anything if a form was already removed', () => {
        formGrouping.removeControl('tab');
        expect(formGrouping.controls.tab).toBeUndefined();
        formGrouping.removeControl('tab');
    });
});
