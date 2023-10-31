/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @internal
 * Internal property on the constructor used for adding decorator metadata
 * so that it can be later picked up after component compilation.
 */
export declare const FORM_PROP: unique symbol;
/**
 * Base data PropDecorator.
 * @internal
 */
export declare class PropDecorator {
    property: string;
    constructor(property: string);
}
/**
 * @internal
 */
export declare class InputPropDecorator extends PropDecorator {
    alias: string;
    constructor(property: string, alias: string | null);
}
/**
 * @internal
 */
export declare class FormPropDecorator extends PropDecorator {
}
/**
 * Injects the AbstractForm for the dynamic form component.
 */
export declare function DynamicForm(): <C>(target: C, key: string) => void;
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
export declare const DynamicInput: (alias?: string | null) => <C>(target: C, key: string) => void;
