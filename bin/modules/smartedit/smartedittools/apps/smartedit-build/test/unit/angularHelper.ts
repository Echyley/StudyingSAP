/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SimpleChange, SimpleChanges } from '@angular/core';

import { TypedMap } from 'smarteditcommons';

/**
 * Creates helper function used for simulating ngOnChanges for given directive / component, ensuring type checking.
 * @param directive Directive or Component
 *
 * @example
 * 1. create type for @Input that should be passed to ngOnChanges (for type checking).
 * type Input = Partial<Pick<typeof customComponentOutletDirective, 'componentName'>>;
 *
 * 2. create simulate function
 * const simulateNgOnChanges = createSimulateNgOnChanges<Input>(customComponentOutletDirective);
 *
 * 3. usage
 * simulateNgOnChanges({
 *   componentName: new SimpleChange(undefined, 'MyComponent', true)
 * });
 */
export function createSimulateNgOnChanges<T = Partial<TypedMap<any>>>(
    directive: any
): (inputs: { [key in keyof T]: SimpleChange }) => Promise<void> {
    return async (inputs: { [key in keyof T]: SimpleChange }) => {
        let input: keyof T;
        for (input in inputs) {
            if (typeof inputs[input] !== undefined) {
                const value = inputs[input] as SimpleChange;
                directive[input] = value.currentValue;
            }
        }
        await directive.ngOnChanges(inputs as SimpleChanges);
    };
}
