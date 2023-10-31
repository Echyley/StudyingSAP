/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injector } from '@angular/core';
import { Observable } from 'rxjs';
import { IAuthenticationService } from '../../interfaces';
import { HttpUtils } from '../../utils';
/**
 * @ngdoc service
 * @name @smartutils.httpAuthInterceptor
 *
 * @description
 * Makes it possible to perform global authentication by intercepting requests before they are forwarded to the server
 * and responses before they are forwarded to the application code.
 *
 */
export declare class HttpAuthInterceptor implements HttpInterceptor {
    private authenticationService;
    private injector;
    private httpUtils;
    private I18N_RESOURCE_URI;
    constructor(authenticationService: IAuthenticationService, injector: Injector, httpUtils: HttpUtils, I18N_RESOURCE_URI: string);
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
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
    private _isCRUDRequest;
    private _isGetRequest;
    private _handleGetRequest;
    private _addAuthToResponse;
    private _handleCUDRequest;
}
