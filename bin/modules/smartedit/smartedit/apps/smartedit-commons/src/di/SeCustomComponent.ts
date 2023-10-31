/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injector } from '@angular/core';
import { createCustomElement } from '@angular/elements';
import { functionsUtils } from '@smart/utils';
import { diNameUtils } from './DINameUtils';
import { SeComponentConstructor } from './types';

/**
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory}
 * used to declare a Smartedit custom component from a dependency injection standpoint.
 */
export const SeCustomComponent = function () {
    return function <T extends SeComponentConstructor>(componentConstructor: T): T {
        const componentName = functionsUtils.getConstructorName(componentConstructor);

        /* forbiddenNameSpaces window._:false */
        const definition = window.__smartedit__.getDecoratorPayload(
            'Component',
            componentConstructor
        );

        if (!definition) {
            throw new Error(
                `@SeCustomComponent ${componentName} should only be used on a @Component decorated class`
            );
        }

        const selector = parseComponentSelector(definition.selector, componentConstructor);
        componentConstructor.selector = selector;

        seCustomComponents.unshift(componentConstructor);

        return componentConstructor;
    };
};

export const parseComponentSelector = function (
    selector: string,
    seContructor: SeComponentConstructor
): string {
    if (!selector) {
        return window.smarteditLodash.kebabCase(diNameUtils.buildComponentName(seContructor));
    } else {
        return selector;
    }
};
export const seCustomComponents: SeComponentConstructor[] = [];

export function registerCustomComponents(injector: Injector): void {
    // create a custom element for every @SeComponent flagged with custom:true
    seCustomComponents.forEach((myConstructor: SeComponentConstructor) => {
        if (!customElements.get(myConstructor.selector)) {
            // Convert to a custom element.
            const customComponent = createCustomElement(myConstructor, { injector });
            // Register the custom element with the browser.
            customElements.define(myConstructor.selector, customComponent as any);
        }
    });
}
