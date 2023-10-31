/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
import { Class } from '../../../../../types';
import { SimpleRetry } from '../retryPolicies';
import { IRetryStrategy } from './i-retry-strategy';
export declare const DefaultRetryStrategy: InjectionToken<string>;
export declare function defaultRetryStrategyFactory(simpleRetry: SimpleRetry): Class<IRetryStrategy>;
