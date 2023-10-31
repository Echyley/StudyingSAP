/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ComponentRef } from '@angular/core';
import { DynamicForm, DynamicInput } from '../../decorators';
import { DynamicInputChange, FormField, InputProperties, ValidatorParameters } from '../../models';

import { decorate } from './decorate';

class FormComponent implements DynamicInputChange {
    @DynamicForm()
    form!: FormField;

    @DynamicInput()
    property!: string;

    @DynamicInput('alias')
    aliasedProperty!: string;

    @DynamicInput()
    nonExistant = 'default';

    @DynamicInput()
    existant = 'default';

    onDynamicInputChange = jasmine.createSpy('onDynamicInputChange');
}

describe('FormBuilder - FormRenderer Decoration', () => {
    let form: FormField;
    let componentRef: ComponentRef<FormComponent>;
    let markForCheck: jasmine.Spy;
    const EXISTANCE_VALUE = 'existant-value';

    beforeEach(() => {
        form = new FormField(
            'word',
            {},
            {
                component: FormComponent,
                inputs: new InputProperties({
                    property: 'property',
                    alias: 'alias',
                    existant: EXISTANCE_VALUE
                }),
                validatorParams: new ValidatorParameters(),
                persist: true
            }
        );

        markForCheck = jasmine.createSpy('markForCheck');

        componentRef = {
            instance: new FormComponent(),
            changeDetectorRef: {
                markForCheck
            } as any
        } as ComponentRef<FormComponent>;
    });

    it('should decorate the component instance', () => {
        const sub = decorate(componentRef, form);

        expect(form.getInput('property')).toBe('property');
        expect(form.getInput('alias')).toBe('alias');
        expect(form.getInput('nonExistant')).toBe('default');
        expect(form.getInput('existant')).toBe(EXISTANCE_VALUE);

        expect(markForCheck).toHaveBeenCalledTimes(1);
        expect(componentRef.instance.onDynamicInputChange).toHaveBeenCalledTimes(1);

        expect(componentRef.instance.form).toBe(form);
        expect(componentRef.instance.form.value).toBe('word');
        expect(componentRef.instance.property).toBe('property');
        expect(componentRef.instance.aliasedProperty).toBe('alias');
        expect(componentRef.instance.nonExistant).toBe('default');
        expect(componentRef.instance.existant).toBe(EXISTANCE_VALUE);

        sub.unsubscribe();
    });

    it('should trigger changes if new values were set to the property', () => {
        const sub = decorate(componentRef, form);

        form.setInput('property', 'new property value');
        form.setInput('alias', 'new value');

        expect(componentRef.instance.property).toBe('new property value');
        expect(componentRef.instance.aliasedProperty).toBe('new value');

        /**
         * 1. On component initialization.
         * 2. On setting property 'property'.
         * 3. On setting property 'alias'.
         */
        expect(markForCheck).toHaveBeenCalledTimes(3);
        expect(componentRef.instance.onDynamicInputChange).toHaveBeenCalledTimes(3);

        sub.unsubscribe();
    });
});
