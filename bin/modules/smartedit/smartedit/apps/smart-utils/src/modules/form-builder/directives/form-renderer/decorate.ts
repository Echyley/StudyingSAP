/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ComponentRef } from '@angular/core';
import { Subscription } from 'rxjs';

import { FormPropDecorator, FORM_PROP, InputPropDecorator, PropDecorator } from '../../decorators';
import { AbstractForm, DynamicInputChange } from '../../models';

/**
 * @internal
 * Trigger property changes for the component and mark for check
 * for those components that have onPush change detection strategy.
 *
 * @param {ComponentRef<any>} componentRef
 */
const onChange = ({ changeDetectorRef, instance }: ComponentRef<DynamicInputChange>): void => {
    instance.onDynamicInputChange && instance.onDynamicInputChange();
    changeDetectorRef.markForCheck();
};

/**
 * @internal
 * Decorates the components with the decorators that were put into places.
 * The idea is made similar to how Angular decorates their properties with inputs.
 */
export const decorate = (componentRef: ComponentRef<any>, form: AbstractForm): Subscription => {
    const instance = componentRef.instance;
    const decorators = instance.constructor[FORM_PROP] as PropDecorator[];

    if (!Array.isArray(decorators)) {
        return new Subscription();
    }

    const props = new Map<string, InputPropDecorator>();

    decorators.forEach((decorator) => {
        const property = decorator.property;
        if (decorator instanceof InputPropDecorator) {
            const alias = decorator.alias;
            if (form.getInput(alias) === undefined && instance[property] !== undefined) {
                form.setInput(alias, instance[property]);
            }
            instance[property] = form.getInput(alias);
            props.set(alias, decorator);
        } else if (decorator instanceof FormPropDecorator) {
            instance[property] = form;
        }
    });

    onChange(componentRef);
    return form.inputChanges.subscribe(({ key, value }) => {
        const decorator = props.get(key);

        if (!decorator) {
            return;
        }

        instance[decorator.property] = value;
        onChange(componentRef);
    });
};
