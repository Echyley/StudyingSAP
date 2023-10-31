/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { IRetry } from './i-retry';
/**
 * @ngdoc object
 * @name @smartutils.object:EXPONENTIAL_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
export declare const EXPONENTIAL_RETRY_DEFAULT_SETTING: {
    MAX_BACKOFF: number;
    MAX_ATTEMPT: number;
    MIN_BACKOFF: number;
};
/**
 * @ngdoc service
 * @name @smartutils.services:exponentialRetry
 * @description
 * When used by a retry strategy, this service could provide an exponential delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
export declare class ExponentialRetry implements IRetry {
    calculateNextDelay(attemptCount: number, maxBackoff?: number, minBackoff?: number): number;
    canRetry(attemptCount: number, maxAttempt?: number): boolean;
}
