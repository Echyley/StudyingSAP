/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/* tslint:disable:max-classes-per-file */

/**
 * @internal
 * Internal property on the constructor used for adding decorator metadata
 * so that it can be later picked up after component compilation.
 */
export const FORM_PROP = Symbol('_form_prop_');

/**
 * Base data PropDecorator.
 * @internal
 */
export class PropDecorator {
    constructor(public property: string) {}
}

/**
 * @internal
 */
export class InputPropDecorator extends PropDecorator {
    public alias: string;
    constructor(property: string, alias: string | null) {
        super(property);
        this.alias = alias ? alias : this.property;
    }
}

/**
 * @internal
 */
export class FormPropDecorator extends PropDecorator {}

/**
 * Used for tagging dynamic inputs and adding them to the FORM_PROP property
 * of the target constructor.
 */
function makePropertyDecorator<T>(factory: (key: string) => PropDecorator) {
    return <C>(target: C, key: string): void => {
        const ctor = (target as any).constructor;
        if (!ctor[FORM_PROP]) {
            ctor[FORM_PROP] = [];
        }
        ctor[FORM_PROP].push(factory(key));
    };
}

/**
 * Injects the AbstractForm for the dynamic form component.
 */
export function DynamicForm(): <C>(target: C, key: string) => void {
    return makePropertyDecorator((key) => new FormPropDecorator(key));
}

/**
 * Injects a property of the AbstractForm for the dynamic form component.
 * Inputs are assigned from the FormSchema's 'inputs' property.
 * NOTE:
 * Property values are only available ngOnInit or onDynamicInputChange.
 * @param alias Use this alias to target a property of the AbstractForm. Defaults
 * to the assigned class property.
 * @example
 * <pre>
 *     @Component({ ... })
 *     export class DynamicFormComponent {
 *         @DynamicInput()
 *         property: string
 *     }
 * <pre>
 */
export const DynamicInput = (alias: string | null = null): (<C>(target: C, key: string) => void) =>
    makePropertyDecorator((key) => new InputPropDecorator(key, alias));
