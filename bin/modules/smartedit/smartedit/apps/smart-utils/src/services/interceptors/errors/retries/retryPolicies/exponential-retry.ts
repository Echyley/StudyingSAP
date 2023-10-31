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
 * @name @smartutils.object:EXPONENTIAL_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
export const EXPONENTIAL_RETRY_DEFAULT_SETTING = {
    MAX_BACKOFF: 64000,
    MAX_ATTEMPT: 5,
    MIN_BACKOFF: 0
};

/**
 * @ngdoc service
 * @name @smartutils.services:exponentialRetry
 * @description
 * When used by a retry strategy, this service could provide an exponential delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
export class ExponentialRetry implements IRetry {
    calculateNextDelay(attemptCount: number, maxBackoff?: number, minBackoff?: number): number {
        maxBackoff = maxBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
        minBackoff = minBackoff || EXPONENTIAL_RETRY_DEFAULT_SETTING.MIN_BACKOFF;

        const waveShield = minBackoff + Math.random();

        return Math.min(Math.pow(2, attemptCount) * 1000 + waveShield, maxBackoff);
    }

    canRetry(attemptCount: number, maxAttempt?: number): boolean {
        maxAttempt = maxAttempt || EXPONENTIAL_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}
