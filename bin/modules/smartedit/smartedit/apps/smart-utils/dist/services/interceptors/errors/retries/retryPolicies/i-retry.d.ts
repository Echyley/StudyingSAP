/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc service
 * @name @smartutils.services:IRetry
 * @description
 * Interface for retry policies
 */
export interface IRetry {
    /**
     * @ngdoc method
     * @name @smartutils.services:IRetry#calculateNextDelay
     * @methodOf @smartutils.services:IRetry
     *
     * @description
     * This method will calculate the next delay time.
     *
     * @param {Number} attemptCount The current number of retry attempts
     * @param {Number =} maxBackoff The maximum delay between two retries
     * @param {Number =} minBackoff The minimum delay between two retries
     *
     * @return {Number} The next delay value
     */
    calculateNextDelay(attemptCount: number, maxBackoff?: number, minBackoff?: number): number;
    /**
     * @ngdoc method
     * @name @smartutils.services:IRetry#canRetry
     * @methodOf @smartutils.services:IRetry
     *
     * @description
     * This method returns true if it is valid to perform another retry, otherwise, it returns false.
     *
     * @param {Number} attemptCount The current number of retry attempts
     * @param {Number =} maxAttempt The maximum number of retry attempts
     *
     * @return {Boolean} is valid to perform another retry?
     */
    canRetry(attemptCount: number, maxAttempt?: number): boolean;
}
