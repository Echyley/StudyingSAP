/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { IRetry } from './i-retry';
/**
 * @ngdoc object
 * @name @smartutils.object:SIMPLE_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
export declare const SIMPLE_RETRY_DEFAULT_SETTING: {
    MAX_ATTEMPT: number;
    MIN_BACKOFF: number;
    RETRY_INTERVAL: number;
};
/**
 * @ngdoc service
 * @name @smartutils.services:simpleRetry
 * @description
 * When used by a retry strategy, this service could provide a simple fixed delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
export declare class SimpleRetry implements IRetry {
    calculateNextDelay(retryInterval?: number, minBackoff?: number): number;
    canRetry(attemptCount: number, _maxAttempt?: number): boolean;
}
