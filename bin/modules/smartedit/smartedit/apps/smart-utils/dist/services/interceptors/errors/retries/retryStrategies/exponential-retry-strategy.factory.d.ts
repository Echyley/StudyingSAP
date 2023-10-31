/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { ExponentialRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';
export declare const ExponentialRetryStrategy: InjectionToken<string>;
export declare function exponentialRetryStrategyFactory(exponentialRetry: ExponentialRetry): Class<IRetryStrategy>;
