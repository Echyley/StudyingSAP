/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
export interface BackendError {
    message: string;
    type: string;
}
export declare enum StatusText {
    UNKNOW_ERROR = "Unknown Error"
}
export interface IHttpErrorInterceptor<T = any> {
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<HttpEvent<T> | HttpErrorResponse>;
}
