/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    DynamicForm,
    DynamicInput,
    FormPropDecorator,
    FORM_PROP,
    InputPropDecorator,
    PropDecorator
} from './decorators';

class TestComponent {
    @DynamicForm()
    form: any;
    @DynamicInput()
    property1: any;
    @DynamicInput('spam')
    property2: any;
}

const getDecorator = <T extends PropDecorator>(decorators: PropDecorator[], property: string): T =>
    decorators.find((decorator) => decorator.property === property) as any;

describe('FormBuilder - Component Decorators', () => {
    let decorators: PropDecorator[];
    let component: TestComponent;

    beforeAll(() => {
        component = new TestComponent();
        decorators = (component.constructor as any)[FORM_PROP];
    });

    it('should have a form decorator on the contructor', () => {
        const decorator = getDecorator<FormPropDecorator>(decorators, 'form');

        expect(decorator.property).toBe('form');
    });

    it('should have a property decorator on the contructor', () => {
        const decorator = getDecorator<InputPropDecorator>(decorators, 'property1');

        expect(decorator.property).toBe('property1');
        expect(decorator.alias).toBe('property1');
    });

    it('should have a property decorator with an alias on the constructor', () => {
        const decorator = getDecorator<InputPropDecorator>(decorators, 'property2');

        expect(decorator.property).toBe('property2');
        expect(decorator.alias).toBe('spam');
    });
});
