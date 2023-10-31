/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Inject, Injectable, Optional } from '@angular/core';
import { AsyncValidatorFn } from '@angular/forms';

import { ASYNC_VALIDATOR_MAP } from './injection-tokens';
import { Registry } from './registry';

export interface AsyncValidatorMap {
    [name: string]: (...args: any[]) => AsyncValidatorFn;
}

/**
 * A registry for asynchronous validators.
 */
@Injectable({
    providedIn: 'root'
})
export class AsyncValidatorRegistryService extends Registry<(...args: any[]) => AsyncValidatorFn> {
    constructor(
        @Optional()
        @Inject(ASYNC_VALIDATOR_MAP)
        asyncValidators: AsyncValidatorMap
    ) {
        super(asyncValidators);
    }
}
