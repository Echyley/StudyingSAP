/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Inject, Injectable, Optional } from '@angular/core';
import { ValidatorFn } from '@angular/forms';

import { VALIDATOR_MAP } from './injection-tokens';
import { Registry } from './registry';

export interface ValidatorMap {
    [name: string]: (...args: any[]) => ValidatorFn;
}

/**
 * A registry for synchronous validators.
 */
@Injectable({
    providedIn: 'root'
})
export class ValidatorRegistryService extends Registry<(...args: any[]) => ValidatorFn> {
    constructor(
        @Optional()
        @Inject(VALIDATOR_MAP)
        validators: ValidatorMap
    ) {
        super(validators);
    }
}
