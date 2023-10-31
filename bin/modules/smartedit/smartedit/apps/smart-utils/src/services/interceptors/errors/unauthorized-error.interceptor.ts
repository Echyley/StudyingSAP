/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { EVENT_SERVICE, REAUTH_STARTED, WHO_AM_I_RESOURCE_URI_TOKEN } from '../../../constants';
import { TypedMap } from '../../../dtos';
import { IAuthenticationService, IEventService } from '../../../interfaces';
import { Deferred, HttpUtils, PromiseUtils } from '../../../utils';
import { LogService } from '../../log.service';
import { IHttpErrorInterceptor } from '../i-http-error.interceptor';

// map used by HttpAuthInterceptor to avoid replay identical requests being held because of 401
export const GET_REQUESTS_ON_HOLD_MAP: TypedMap<Promise<HttpEvent<any>>> = {};

/**
 * @ngdoc service
 * @name @smartutils.services:unauthorizedErrorInterceptor
 * @description
 * Used for HTTP error code 401 (Forbidden). It will display the login modal.
 */
@Injectable()
export class UnauthorizedErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private promisesToResolve: TypedMap<{ requestIdentifier: string; deferred: Deferred<T> }[]> =
        {}; // key: auth entry point, value: array of deferred
    private readonly rejectedUrls: (string | RegExp)[] = [/authenticate/];

    constructor(
        private readonly httpClient: HttpClient,
        private readonly authenticationService: IAuthenticationService,
        private readonly promiseUtils: PromiseUtils,
        private readonly httpUtils: HttpUtils,
        @Inject(WHO_AM_I_RESOURCE_URI_TOKEN) WHO_AM_I_RESOURCE_URI: string,
        @Inject(EVENT_SERVICE) private readonly eventService: IEventService,
        private readonly logService: LogService
    ) {
        this.rejectedUrls.push(WHO_AM_I_RESOURCE_URI);
    }

    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean {
        return (
            response.status === 401 &&
            (request.url
                ? this.httpUtils.isCRUDRequest(request, response) &&
                  this.isUrlNotRejected(request.url)
                : true)
        );
    }
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<any> {
        const deferred = this.promiseUtils.defer<T>();

        const deferredPromise = deferred.promise.then(() =>
            this.httpClient.request(request).toPromise()
        );

        this.authenticationService.isAuthEntryPoint(request.url).then((isAuthEntryPoint) => {
            if (!isAuthEntryPoint) {
                this.authenticationService
                    .filterEntryPoints(request.url)
                    .then((entryPoints: string[]) => {
                        const entryPoint = entryPoints[0];
                        this.promisesToResolve[entryPoint] =
                            this.promisesToResolve[entryPoint] || [];
                        this.promisesToResolve[entryPoint].push({
                            requestIdentifier: request.url,
                            deferred
                        });
                        if (this.httpUtils.isGET(request)) {
                            GET_REQUESTS_ON_HOLD_MAP[request.url] = deferredPromise;
                        }
                        this.authenticationService
                            .isReAuthInProgress(entryPoint)
                            .then((isReAuthInProgress) => {
                                if (!isReAuthInProgress) {
                                    this.authenticationService
                                        .setReAuthInProgress(entryPoint)
                                        .then(() => {
                                            const promisesToResolve = this.promisesToResolve;
                                            this.eventService
                                                .publish(REAUTH_STARTED)
                                                .catch((reason) => {
                                                    this.logService.debug(
                                                        `UnauthorizedErrorInterceptor - responseError: ${reason}`
                                                    );
                                                });
                                            this.authenticationService
                                                .authenticate(request.url)
                                                .then(
                                                    function (): void {
                                                        promisesToResolve[this].forEach(
                                                            (record) => {
                                                                delete GET_REQUESTS_ON_HOLD_MAP[
                                                                    record.requestIdentifier
                                                                ];
                                                                record.deferred.resolve();
                                                            }
                                                        );
                                                        promisesToResolve[this] = [];
                                                    }.bind(entryPoint),
                                                    function (): void {
                                                        promisesToResolve[this].forEach(
                                                            (record) => {
                                                                delete GET_REQUESTS_ON_HOLD_MAP[
                                                                    record.requestIdentifier
                                                                ];
                                                                record.deferred.reject();
                                                            }
                                                        );
                                                        promisesToResolve[this] = [];
                                                    }.bind(entryPoint)
                                                );
                                        });
                                }
                            });
                    });
            } else {
                deferred.reject(response);
            }
        });

        return deferredPromise;
    }

    private isUrlNotRejected(url: string): boolean {
        return !this.rejectedUrls.some((rejectedUrl) =>
            typeof rejectedUrl === 'string' ? url.indexOf(rejectedUrl) === 0 : rejectedUrl.test(url)
        );
    }
}
