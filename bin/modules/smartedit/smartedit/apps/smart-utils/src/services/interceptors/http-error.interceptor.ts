/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { throwError, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
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
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    constructor(
        private httpErrorInterceptorService: HttpErrorInterceptorService,
        private httpUtils: HttpUtils
    ) {}

    intercept<T>(request: HttpRequest<T>, next: HttpHandler): Observable<HttpEvent<T>> {
        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse, caught: Observable<HttpEvent<T>>) => {
                if (!this.httpUtils.isHTMLRequest(request, error)) {
                    return this.httpErrorInterceptorService.responseError(request, error);
                } else {
                    return throwError(error);
                }
            })
        );
    }
}
