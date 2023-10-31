/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Inject, Injectable, Optional } from '@angular/core';

import { ComponentType } from '../models';
import { COMPONENT_MAP } from './injection-tokens';
import { Registry } from './registry';

export interface ComponentTypeMap {
    [name: string]: ComponentType;
}
/**
 * A registry for form components.
 */
@Injectable({
    providedIn: 'root'
})
export class ComponentRegistryService extends Registry<ComponentType> {
    constructor(
        @Optional()
        @Inject(COMPONENT_MAP)
        types: ComponentTypeMap
    ) {
        super(types);
    }
}
