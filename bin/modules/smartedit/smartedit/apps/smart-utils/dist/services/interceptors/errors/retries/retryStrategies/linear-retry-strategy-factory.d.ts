/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { LinearRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';
export declare const LinearRetryStrategy: InjectionToken<string>;
export declare function linearRetryStrategyFactory(linearRetry: LinearRetry): Class<IRetryStrategy>;
