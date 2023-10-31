/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpUtils } from '../../utils';
import { HttpErrorInterceptorService } from './http-error-interceptor.service';
/**
 * @ngdoc overview
 * @name httpErrorInterceptorServiceModule
 *
 * @description
 * This module provides the functionality to add custom HTTP error interceptors.
 * Interceptors are used to execute code each time an HTTP request fails.
 */
export declare class HttpErrorInterceptor implements HttpInterceptor {
    private httpErrorInterceptorService;
    private httpUtils;
    constructor(httpErrorInterceptorService: HttpErrorInterceptorService, httpUtils: HttpUtils);
    intercept<T>(request: HttpRequest<T>, next: HttpHandler): Observable<HttpEvent<T>>;
}
