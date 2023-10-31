/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { LIBRARY_NAME } from '../../../constants';
import { TypedMap } from '../../../dtos';
import { IAlertService } from '../../../interfaces';
import { Class } from '../../../types';
import { BooleanUtils } from '../../../utils';
import { IHttpErrorInterceptor } from '../i-http-error.interceptor';
import {
    clientErrorPredicate,
    noInternetConnectionErrorPredicate,
    readPredicate,
    retriableErrorPredicate,
    serverErrorPredicate,
    timeoutErrorPredicate,
    DefaultRetryStrategy,
    ExponentialRetryStrategy,
    IRetryStrategy,
    LinearRetryStrategy,
    OperationContextService
} from './retries';

export type RetryPredicate = (
    request: HttpRequest<any>,
    response: HttpErrorResponse,
    operationContext?: string
) => boolean;

export const OPERATION_CONTEXT_TOKEN = `${LIBRARY_NAME}_OPERATION_CONTEXT`;

/**
 * @ngdoc service
 * @name @smartutils.services:retryInterceptor
 *
 * @description
 * The retryInterceptor provides the functionality to register a set of predicates with their associated retry strategies.
 * Each time an HTTP request fails, the service try to find a matching retry strategy for the given response.
 */
@Injectable()
export class RetryInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private TRANSLATE_NAMESPACE = 'se.gracefuldegradation.';

    private predicatesRegistry: {
        predicate: RetryPredicate;
        retryStrategy: Class<IRetryStrategy>;
    }[] = [];

    private requestToRetryTegistry: TypedMap<IRetryStrategy> = {};

    constructor(
        private httpClient: HttpClient,
        private translate: TranslateService,
        private operationContextService: OperationContextService,
        private alertService: IAlertService,
        booleanUtils: BooleanUtils,
        @Inject(DefaultRetryStrategy) defaultRetryStrategy: Class<IRetryStrategy>,
        @Inject(ExponentialRetryStrategy) exponentialRetryStrategy: Class<IRetryStrategy>,
        @Inject(LinearRetryStrategy) linearRetryStrategy: Class<IRetryStrategy>,
        @Inject(OPERATION_CONTEXT_TOKEN) private OPERATION_CONTEXT: TypedMap<string>
    ) {
        this.register(noInternetConnectionErrorPredicate, exponentialRetryStrategy)
            .register(
                booleanUtils.isAnyTruthy(clientErrorPredicate, timeoutErrorPredicate),
                defaultRetryStrategy
            )
            .register(
                booleanUtils.areAllTruthy(readPredicate, retriableErrorPredicate),
                defaultRetryStrategy
            )
            .register(serverErrorPredicate, exponentialRetryStrategy);
    }

    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean {
        return this.findMatchingStrategy(request, response) !== null;
    }

    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<any> {
        let retryStrategy = this.retrieveRetryStrategy(request);
        if (!retryStrategy) {
            const StrategyHolder = this.findMatchingStrategy(request, response);
            if (StrategyHolder) {
                this.alertService.showWarning({
                    message: this.translate.instant(this.TRANSLATE_NAMESPACE + 'stillworking'),
                    minWidth: '',
                    mousePersist: true,
                    duration: 1000,
                    dismissible: true,
                    width: '300px'
                });
                retryStrategy = new StrategyHolder();
                retryStrategy.attemptCount = 0;
                this.storeRetryStrategy(request, retryStrategy);
            } else {
                return Promise.reject(response);
            }
        }
        return this.handleRetry(retryStrategy, request, response);
    }

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
    register(predicate: RetryPredicate, retryStrategy: Class<IRetryStrategy>): RetryInterceptor<T> {
        if (typeof predicate !== 'function') {
            throw new Error('retryInterceptor.register error: predicate must be a function');
        }
        if (typeof retryStrategy !== 'function') {
            throw new Error('retryInterceptor.register error: retryStrategy must be a function');
        }
        this.predicatesRegistry.unshift({
            predicate,
            retryStrategy
        });
        return this;
    }

    /**
     * Find a matching strategy for the given response and (optional) operationContext
     * If not provided, the default operationContext is OPERATION_CONTEXT.INTERACTIVE
     *
     * @param {Object} response The http response object
     *
     * @return {Function} The matching retryStrategy
     */
    private findMatchingStrategy(
        request: HttpRequest<any>,
        response: HttpErrorResponse
    ): Class<IRetryStrategy> | null {
        const operationContext =
            this.operationContextService.findOperationContext(request.url) ||
            this.OPERATION_CONTEXT.INTERACTIVE;
        const matchStrategy = this.predicatesRegistry.find((predicateObj) =>
            predicateObj.predicate(request, response, operationContext)
        );
        return matchStrategy ? matchStrategy.retryStrategy : null;
    }

    private handleRetry(
        retryStrategy: IRetryStrategy,
        request: HttpRequest<any>,
        response: HttpErrorResponse
    ): Promise<any> {
        retryStrategy.attemptCount++;
        if (retryStrategy.canRetry()) {
            const delay = retryStrategy.firstFastRetry ? 0 : retryStrategy.calculateNextDelay();
            retryStrategy.firstFastRetry = false;
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    this.httpClient
                        .request(request)
                        .toPromise()
                        .then(
                            (result: any) => {
                                this.removeRetryStrategy(request);
                                return resolve(result);
                            },
                            (error: any) => reject(error)
                        );
                }, delay);
            });
        } else {
            this.alertService.showDanger({
                message: this.translate.instant(this.TRANSLATE_NAMESPACE + 'somethingwrong'),
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            return Promise.reject(response);
        }
    }

    private storeRetryStrategy(request: HttpRequest<any>, retryStrategy: IRetryStrategy): void {
        this.requestToRetryTegistry[this.getRequestUUID(request)] = retryStrategy;
    }
    private removeRetryStrategy(request: HttpRequest<any>): void {
        delete this.requestToRetryTegistry[this.getRequestUUID(request)];
    }

    private retrieveRetryStrategy(request: HttpRequest<any>): IRetryStrategy {
        return this.requestToRetryTegistry[this.getRequestUUID(request)];
    }

    private getRequestUUID(request: HttpRequest<any>): string {
        return request.clone().toString();
    }
}
