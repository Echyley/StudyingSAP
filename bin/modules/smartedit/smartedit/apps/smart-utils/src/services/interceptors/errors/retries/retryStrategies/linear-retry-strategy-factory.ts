/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { LinearRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';

export const LinearRetryStrategy = new InjectionToken<string>('LinearRetryStrategy');

export function linearRetryStrategyFactory(linearRetry: LinearRetry): Class<IRetryStrategy> {
    return class implements IRetryStrategy {
        public firstFastRetry = true;
        public attemptCount = 0;

        canRetry(): boolean {
            return linearRetry.canRetry(this.attemptCount);
        }

        calculateNextDelay(): number {
            return linearRetry.calculateNextDelay(this.attemptCount);
        }
    };
}
