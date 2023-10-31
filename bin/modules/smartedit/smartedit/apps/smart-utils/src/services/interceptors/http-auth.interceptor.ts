/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
    HttpResponse
} from '@angular/common/http';
import { Inject, Injectable, Injector } from '@angular/core';
import { from, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { I18N_RESOURCE_URI_TOKEN } from '../../constants';
import { IAuthenticationService, IAuthToken, IStorageService } from '../../interfaces';
import { HttpUtils } from '../../utils';
import { GET_REQUESTS_ON_HOLD_MAP } from './errors/unauthorized-error.interceptor';

/**
 * @ngdoc service
 * @name @smartutils.httpAuthInterceptor
 *
 * @description
 * Makes it possible to perform global authentication by intercepting requests before they are forwarded to the server
 * and responses before they are forwarded to the application code.
 *
 */
@Injectable()
export class HttpAuthInterceptor implements HttpInterceptor {
    constructor(
        private authenticationService: IAuthenticationService,
        private injector: Injector,
        private httpUtils: HttpUtils,
        @Inject(I18N_RESOURCE_URI_TOKEN) private I18N_RESOURCE_URI: string
    ) {}

    /**
     * @ngdoc method
     * @name @smartutils.httpAuthInterceptor#request
     * @methodOf @smartutils.httpAuthInterceptor
     *
     * @description
     * Interceptor method which gets called with a http config object, intercepts any request made using httpClient service.
     * A call to any REST resource will be intercepted by this method, which then adds an authentication token to the request
     * and then forwards it to the REST resource.
     *
     */
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (!this._isCRUDRequest(request)) {
            return next.handle(request);
        }

        if (this._isGetRequest(request)) {
            return this._handleGetRequest(request);
        }
        return this._handleCUDRequest(request, next);
    }

    private _isCRUDRequest(request: HttpRequest<any>): boolean {
        return (
            !request.url.includes(this.I18N_RESOURCE_URI) && this.httpUtils.isCRUDRequest(request)
        );
    }

    private _isGetRequest(request: HttpRequest<any>): boolean {
        return this.httpUtils.isGET(request) && !!GET_REQUESTS_ON_HOLD_MAP[request.url];
    }

    private _handleGetRequest(request: HttpRequest<any>): Observable<HttpEvent<any>> {
        return new Observable((obj) => {
            GET_REQUESTS_ON_HOLD_MAP[request.url].then((body) => {
                obj.next(new HttpResponse<any>({ status: 200, body }));
            });
        });
    }

    private _addAuthToResponse(
        request: HttpRequest<any>,
        next: HttpHandler,
        entryPoints: string[]
    ): Observable<HttpEvent<any>> {
        return from<Promise<IAuthToken>>(
            this.injector.get(IStorageService).getAuthToken(entryPoints[0])
        ).pipe(
            switchMap<IAuthToken, Observable<HttpEvent<any>>>((authToken: IAuthToken) => {
                if (!authToken) {
                    return next.handle(request);
                }

                const authReq = request.clone({
                    headers: request.headers.set(
                        'Authorization',
                        authToken.token_type + ' ' + authToken.access_token
                    )
                });
                return next.handle(authReq);
            })
        );
    }

    private _handleCUDRequest(
        request: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        return from(this.authenticationService.filterEntryPoints(request.url)).pipe(
            switchMap<string[], Observable<HttpEvent<any>>>((entryPoints: string[]) => {
                if (!(entryPoints && entryPoints.length)) {
                    return next.handle(request);
                }

                return this._addAuthToResponse(request, next, entryPoints);
            })
        );
    }
}
