/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { SimpleRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';

export const DefaultRetryStrategy = new InjectionToken<string>('DefaultRetryStrategy');

export function defaultRetryStrategyFactory(simpleRetry: SimpleRetry): Class<IRetryStrategy> {
    return class implements IRetryStrategy {
        public firstFastRetry = true;
        public attemptCount = 0;

        canRetry(): boolean {
            return simpleRetry.canRetry(this.attemptCount);
        }

        calculateNextDelay(): number {
            return simpleRetry.calculateNextDelay();
        }
    };
}
