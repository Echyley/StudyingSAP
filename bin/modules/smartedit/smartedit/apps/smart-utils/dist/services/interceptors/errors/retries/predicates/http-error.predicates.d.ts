/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
export declare function serverErrorPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
export declare function clientErrorPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
export declare function timeoutErrorPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
export declare function retriableErrorPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
export declare function noInternetConnectionErrorPredicate(request: HttpRequest<any>, response: HttpErrorResponse): boolean;
