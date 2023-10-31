/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { TypedMap } from '../../../dtos';
import { IAlertService } from '../../../interfaces';
import { Class } from '../../../types';
import { BooleanUtils } from '../../../utils';
import { IHttpErrorInterceptor } from '../i-http-error.interceptor';
import { IRetryStrategy, OperationContextService } from './retries';
export declare type RetryPredicate = (request: HttpRequest<any>, response: HttpErrorResponse, operationContext?: string) => boolean;
export declare const OPERATION_CONTEXT_TOKEN: string;
/**
 * @ngdoc service
 * @name @smartutils.services:retryInterceptor
 *
 * @description
 * The retryInterceptor provides the functionality to register a set of predicates with their associated retry strategies.
 * Each time an HTTP request fails, the service try to find a matching retry strategy for the given response.
 */
export declare class RetryInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private httpClient;
    private translate;
    private operationContextService;
    private alertService;
    private OPERATION_CONTEXT;
    private TRANSLATE_NAMESPACE;
    private predicatesRegistry;
    private requestToRetryTegistry;
    constructor(httpClient: HttpClient, translate: TranslateService, operationContextService: OperationContextService, alertService: IAlertService, booleanUtils: BooleanUtils, defaultRetryStrategy: Class<IRetryStrategy>, exponentialRetryStrategy: Class<IRetryStrategy>, linearRetryStrategy: Class<IRetryStrategy>, OPERATION_CONTEXT: TypedMap<string>);
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<any>;
    /**
     * @ngdoc method
     * @name @smartutils.services:retryInterceptor#register
     * @methodOf @smartutils.services:retryInterceptor
     *
     * @description
     * Register a new predicate with it's associated strategyHolder.
     *
     * @param {Function} predicate This function takes the 'response' {Object} argument and an (optional) operationContext {String}. This function must return a Boolean that is true if the given response match the predicate.
     * @param {Function} retryStrategy This function will be instanciated at run-time. See {@link @smartutils.services:IRetryStrategy IRetryStrategy}.
     *
     * @return {Object} retryInterceptor The retryInterceptor service.
     *
     * @example
     * ```js
     *      var customPredicate = function(request, response, operationContext) {
     *          return response.status === 500 && operationContext === OPERATION_CONTEXT.TOOLING;
     *      };
     *      var StrategyHolder = function() {
     *          // set the firstFastRetry value to true for the retry made immediately only for the very first retry (subsequent retries will remain subject to the calculateNextDelay response)
     *          this.firstFastRetry = true;
     *      };
     *      StrategyHolder.prototype.canRetry = function() {
     *          // this function must return a {Boolean} if the given request must be retried.
     *          // use this.attemptCount value to determine if the function should return true or false
     *      };
     *      StrategyHolder.prototype.calculateNextDelay = function() {
     *          // this function must return the next delay time {Number}
     *          // use this.attemptCount value to determine the next delay value
     *      };
     *      retryInterceptor.register(customPredicate, StrategyHolder);
     * ```
     */
    register(predicate: RetryPredicate, retryStrategy: Class<IRetryStrategy>): RetryInterceptor<T>;
    /**
     * Find a matching strategy for the given response and (optional) operationContext
     * If not provided, the default operationContext is OPERATION_CONTEXT.INTERACTIVE
     *
     * @param {Object} response The http response object
     *
     * @return {Function} The matching retryStrategy
     */
    private findMatchingStrategy;
    private handleRetry;
    private storeRetryStrategy;
    private removeRetryStrategy;
    private retrieveRetryStrategy;
    private getRequestUUID;
}
