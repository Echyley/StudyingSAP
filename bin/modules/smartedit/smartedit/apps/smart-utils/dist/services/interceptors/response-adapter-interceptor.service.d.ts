/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
/**
 * @ngdoc service
 * @name @smartutils.services:responseAdapterInterceptor
 *
 * @description
 *
 * Interceptor used to normalize the response of paginated resources. Some API consumers select data with 'result' and 'response' key.
 * This interceptor purpose is to adapt such payload.
 */
export declare class ResponseAdapterInterceptor implements HttpInterceptor {
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
}
