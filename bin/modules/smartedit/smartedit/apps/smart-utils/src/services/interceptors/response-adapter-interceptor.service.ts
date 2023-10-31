/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { HttpPaginationResponseAdapter } from '../../utils/http-pagination-response-adapter';

/**
 * @ngdoc service
 * @name @smartutils.services:responseAdapterInterceptor
 *
 * @description
 *
 * Interceptor used to normalize the response of paginated resources. Some API consumers select data with 'result' and 'response' key.
 * This interceptor purpose is to adapt such payload.
 */

@Injectable()
export class ResponseAdapterInterceptor implements HttpInterceptor {
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(map(HttpPaginationResponseAdapter.transform));
    }
}
