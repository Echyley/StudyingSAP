/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { ExponentialRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';

export const ExponentialRetryStrategy = new InjectionToken<string>('ExponentialRetryStrategy');

export function exponentialRetryStrategyFactory(
    exponentialRetry: ExponentialRetry
): Class<IRetryStrategy> {
    return class implements IRetryStrategy {
        public firstFastRetry = true;
        public attemptCount = 0;

        canRetry(): boolean {
            return exponentialRetry.canRetry(this.attemptCount);
        }

        calculateNextDelay(): number {
            return exponentialRetry.calculateNextDelay(this.attemptCount);
        }
    };
}
