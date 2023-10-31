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
 * @name @smartutils.object:LINEAR_RETRY_DEFAULT_SETTING
 *
 * @description
 * The setting object to be used as default values for retry.
 */
export const LINEAR_RETRY_DEFAULT_SETTING = {
    MAX_ATTEMPT: 5,
    MAX_BACKOFF: 32000,
    MIN_BACKOFF: 0,
    RETRY_INTERVAL: 500
};

/**
 * @ngdoc service
 * @name @smartutils.services:linearRetry
 * @description
 * When used by a retry strategy, this service could provide a linear delay time to be used by the strategy before the next request is sent. The service also provides functionality to check if it is possible to perform a next retry.
 */
export class LinearRetry implements IRetry {
    calculateNextDelay(
        attemptCount: number,
        retryInterval?: number,
        maxBackoff?: number,
        minBackoff?: number
    ): number {
        maxBackoff = maxBackoff || LINEAR_RETRY_DEFAULT_SETTING.MAX_BACKOFF;
        minBackoff = minBackoff || LINEAR_RETRY_DEFAULT_SETTING.MIN_BACKOFF;
        retryInterval = retryInterval || LINEAR_RETRY_DEFAULT_SETTING.RETRY_INTERVAL;

        const waveShield = minBackoff + Math.random();
        return Math.min(attemptCount * retryInterval + waveShield, maxBackoff);
    }

    canRetry(attemptCount: number, maxAttempt?: number): boolean {
        maxAttempt = maxAttempt || LINEAR_RETRY_DEFAULT_SETTING.MAX_ATTEMPT;
        return attemptCount <= maxAttempt;
    }
}
