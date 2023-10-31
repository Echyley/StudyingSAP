/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
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
export const SIMPLE_RETRY_DEFAULT_SETTING = {
    MAX_ATTEMPT: 5,
    MIN_BACKOFF: 0,
    RETRY_INTERVAL: 500
};

/**
 * @ngdoc service
 * @name @smartutils.services:simpleRetry
 * @description
 * When used by a retry strategy, this service could provide a simple fixed delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
export class SimpleRetry implements IRetry {
    calculateNextDelay(retryInterval?: number, minBackoff?: number): number {
        minBackoff = minBackoff || SIMPLE_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
        retryInterval = retryInterval || SIMPLE_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;

        const waveShield = minBackoff + Math.random();
        return retryInterval + waveShield;
    }

    canRetry(attemptCount: number, _maxAttempt?: number): boolean {
        const maxAttempt = _maxAttempt || SIMPLE_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}
