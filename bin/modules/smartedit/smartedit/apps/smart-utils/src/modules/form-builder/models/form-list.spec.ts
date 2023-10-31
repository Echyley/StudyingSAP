/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FormControl, FormGroup } from '@angular/forms';
import { FormField } from './form-field';
import { FormGrouping } from './form-grouping';
import { FormList } from './form-list';
import { InputProperties } from './input-properties';

class TypeComponent {}

describe('FormList', () => {
    let formList: FormList;
    const noop: any = {};

    beforeEach(() => {
        const field = new FormField('value1', noop, {} as any);

        const forms = [
            new FormGrouping({ prop1: field }, {}, {} as any),
            new FormField('value2', noop, {} as any),
            new FormField('value3', noop, {} as any),
            new FormField('value4', noop, { persist: false } as any)
        ];

        formList = new FormList(forms, {}, { component: TypeComponent } as any);
    });

    it('should initialize with properties', () => {
        expect(formList.persist).toBeTruthy();
        expect(formList.component).toBe(TypeComponent);
        expect(formList.inputs instanceof InputProperties).toBeTruthy();
        expect(formList.length).toBe(4);

        formList.controls.forEach((form) => {
            expect(form.parent).toBe(formList);
        });
    });

    it('should initialize with empty form list', () => {
        const list = new FormList([], noop, {} as any);
        expect(list.length).toBe(0);
    });

    it('should get persisted value', () => {
        expect(formList.getPersistedValue()).toEqual([
            {
                prop1: 'value1'
            },
            'value2',
            'value3'
        ]);
    });

    // can be removed
    it('should add a form element to the array', () => {
        const field = new FormField(new FormControl('value2'), noop, {} as any);

        formList.push(field);

        const last = formList.size() - 1;

        expect(field.parent).toBe(formList);
        expect(formList.at(last)).toBe(field);
    });

    // can be removed
    it('should remove an element at an index', () => {
        const last = formList.size() - 1;
        const newSize = last;

        formList.removeAt(last);

        expect(formList.size()).toBe(newSize);
        expect(formList.length).toBe(newSize);
    });

    it('should swap elements in the list', () => {
        const last = formList.size() - 1;

        expect(formList.at(0) instanceof FormGrouping).toBeTruthy();
        expect(formList.at(last) instanceof FormField).toBeTruthy();

        expect(formList.at(0) instanceof FormGroup).toBeTruthy();
        expect(formList.at(last) instanceof FormControl).toBeTruthy();

        formList.swapFormElements(0, last);

        expect(formList.at(last) instanceof FormGrouping).toBeTruthy();
        expect(formList.at(0) instanceof FormField).toBeTruthy();

        expect(formList.at(last) instanceof FormGroup).toBeTruthy();
        expect(formList.at(0) instanceof FormControl).toBeTruthy();
    });

    it('should not swap if a or b are out of bounds', () => {
        formList.swapFormElements(-1, 0);
        formList.swapFormElements(0, formList.size());

        const last = formList.size() - 1;
        expect(formList.at(0) instanceof FormGrouping).toBeTruthy();
        expect(formList.at(last) instanceof FormField).toBeTruthy();
        expect(formList.at(0) instanceof FormGroup).toBeTruthy();
        expect(formList.at(last) instanceof FormControl).toBeTruthy();
    });

    it('should not swap if a and b are equal', () => {
        formList.swapFormElements(0, 0);

        expect(formList.at(0) instanceof FormGrouping).toBeTruthy();
        expect(formList.at(0) instanceof FormGroup).toBeTruthy();
    });

    it('should move a form to another position', () => {
        formList.moveFormElement(0, 2);

        expect(formList.getPersistedValue()).toEqual([
            'value2',
            'value3',
            {
                prop1: 'value1'
            }
        ]);
    });

    it('should not move forms if to and from values are out of bounds', () => {
        formList.moveFormElement(-1, 0);
        formList.moveFormElement(0, 4);

        expect(formList.getPersistedValue()).toEqual([
            {
                prop1: 'value1'
            },
            'value2',
            'value3'
        ]);
    });

    it('should not move forms if to and from values are the same', () => {
        formList.moveFormElement(0, 0);

        expect(formList.getPersistedValue()).toEqual([
            {
                prop1: 'value1'
            },
            'value2',
            'value3'
        ]);
    });
});
