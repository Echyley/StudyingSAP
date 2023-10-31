/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { IAlertService } from '../../../interfaces';
import { IHttpErrorInterceptor } from '../i-http-error.interceptor';
/**
 * @ngdoc service
 * @name permissionErrorInterceptorModule.service:permissionErrorInterceptor
 * @description
 * Used for HTTP error code 403. Displays the alert message for permission error.
 */
export declare class PermissionErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private alertService;
    constructor(alertService: IAlertService);
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<HttpErrorResponse>;
}
