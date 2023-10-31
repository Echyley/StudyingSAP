/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import * as lodash from 'lodash';
import { from, Observable } from 'rxjs';
import { Class } from '../../types';
import { Deferred, PromiseUtils } from '../../utils';
import { IHttpErrorInterceptor } from './i-http-error.interceptor';

/**
 * @ngdoc service
 * @name @smartutils.services:httpErrorInterceptorService
 *
 * @description
 * The httpErrorInterceptorService provides the functionality to add custom HTTP error interceptors.
 * An interceptor can be an {Object} or an Angular Factory and must be represented by a pair of functions:
 * - predicate(request, response) {Function} that must return true if the response is associated to the interceptor. Important: The predicate must be designed to fulfill a specific function. It must not be defined for generic use.
 * - responseError(request, response) {Function} function called if the current response error matches the predicate. It must return a {Promise} with the resolved or rejected response.
 *
 * Each time an HTTP request fails, the service iterates through all registered interceptors. It sequentially calls the responseError function for all interceptors that have a predicate returning true for the current response error. If an interceptor modifies the response, the next interceptor that is called will have the modified response.
 * The last interceptor added to the service will be the first interceptor called. This makes it possible to override default interceptors.
 * If an interceptor resolves the response, the service service stops the iteration.
 */
@Injectable()
export class HttpErrorInterceptorService {
    private _errorInterceptors: IHttpErrorInterceptor[] = [];

    constructor(private injector: Injector, private promiseUtils: PromiseUtils) {}

    /**
     * @ngdoc method
     * @name @smartutils.services:httpErrorInterceptorService#addInterceptor
     * @methodOf @smartutils.services:httpErrorInterceptorService
     *
     * @description
     * Add a new error interceptor
     *
     * @param {Object|String} interceptor The interceptor {Object} or angular Factory
     *
     * @returns {Function} Function to call to unregister the interceptor from the service
     *
     * @example
     * ```js
     *      // Add a new interceptor with an instance of IHttpErrorInterceptor:
     *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor({
     *          predicate: function(request, response) {
     *              return response.status === 400;
     *          },
     *          responseError: function(request, response) {
     *              alertService.showDanger({
     *                  message: response.message
     *              });
     *              return Promise.reject(response);// FIXME: update doc
     *          }
     *      });
     *
     *      // Add an interceptor with a class of IHttpErrorInterceptor:
     *      var unregisterCustomInterceptor = httpErrorInterceptorService.addInterceptor(CustomErrorInterceptor);
     *
     *      // Unregister the interceptor:
     *      unregisterCustomInterceptor();
     * ```
     */

    addInterceptors(
        interceptorClasses: (Class<IHttpErrorInterceptor> | IHttpErrorInterceptor)[]
    ): void {
        interceptorClasses.forEach((InterceptorClass) => {
            this.addInterceptor(InterceptorClass);
        });
    }

    addInterceptor(_interceptor: Class<IHttpErrorInterceptor> | IHttpErrorInterceptor): () => void {
        let interceptor: IHttpErrorInterceptor;
        if (
            (_interceptor as IHttpErrorInterceptor).predicate ||
            (_interceptor as IHttpErrorInterceptor).responseError
        ) {
            interceptor = _interceptor as IHttpErrorInterceptor;
        } else {
            interceptor = this.injector.get(_interceptor);
        }
        this._validateInterceptor(interceptor);
        this._errorInterceptors.unshift(interceptor);

        return (): void => {
            this._errorInterceptors.splice(this._errorInterceptors.indexOf(interceptor), 1);
        };
    }

    responseError<T>(
        request: HttpRequest<T>,
        response: HttpErrorResponse
    ): Observable<HttpEvent<T>> {
        const matchingErrorInterceptors = this._errorInterceptors.filter(
            (errorInterceptor) => errorInterceptor.predicate(request, response) === true
        );
        const _interceptorsDeferred: Deferred<any> = this.promiseUtils.defer<any>();
        if (matchingErrorInterceptors.length) {
            this._iterateErrorInterceptors<T>(
                request.clone(),
                lodash.cloneDeep(response),
                matchingErrorInterceptors,
                _interceptorsDeferred
            );
        } else {
            _interceptorsDeferred.reject(response);
        }
        return from(_interceptorsDeferred.promise);
    }

    private _iterateErrorInterceptors<T>(
        request: HttpRequest<T>,
        response: HttpErrorResponse,
        interceptors: IHttpErrorInterceptor[],
        _interceptorsDeferred: Deferred<any>,
        idx = 0
    ): void {
        if (idx === interceptors.length) {
            _interceptorsDeferred.reject(response);
        } else {
            const iterateFn = this._iterateErrorInterceptors.bind(this);
            // FIXME: fully convert this part to Observable chaining
            Promise.resolve(interceptors[idx].responseError(request, response)).then(
                (interceptedResponse) => {
                    _interceptorsDeferred.resolve(interceptedResponse);
                },
                (interceptedResponse) => {
                    iterateFn(
                        request,
                        interceptedResponse,
                        interceptors,
                        _interceptorsDeferred,
                        ++idx
                    );
                }
            );
        }
    }

    /**
     * @ignore
     * Validate if the provided interceptor respects the Interface (predicate and responseError functions are mandatory).
     * @param {Object|String} interceptor The interceptor {Object} or angular Factory
     */
    private _validateInterceptor(interceptor: IHttpErrorInterceptor): void {
        if (!interceptor.predicate || typeof interceptor.predicate !== 'function') {
            throw new Error(
                'httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.predicate'
            );
        }
        if (!interceptor.responseError || typeof interceptor.responseError !== 'function') {
            throw new Error(
                'httpErrorInterceptorService.addInterceptor.error.interceptor.has.no.responseError'
            );
        }
    }
}
