/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc service
 * @name @smartutils.services:IRetryStrategy
 */
export interface IRetryStrategy {
    firstFastRetry: boolean;
    attemptCount: number;
    /**
     * @ngdoc method
     * @name @smartutils.services:IRetryStrategy#canRetry
     * @methodOf @smartutils.services:IRetryStrategy
     *
     * @description
     * Function that must return a {Boolean} if the current request must be retried
     *
     * @return {Boolean} true if the current request must be retried
     */
    canRetry(): boolean;
    /**
     * @ngdoc method
     * @name @smartutils.services:IRetryStrategy#canRetry
     * @methodOf @smartutils.services:IRetryStrategy
     *
     * @description
     * Function that returns the next delay time {Number}
     *
     * @return {Number} delay the delay until the next retry
     */
    calculateNextDelay(): number;
}
