/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { Payload, TypedMap } from '../../../dtos';
import { LogService } from '../../../services/log.service';
import { HttpUtils, UrlUtils } from '../../../utils';
import { HttpBackendService } from './http-backend.service';

/*
 * This is the place where the entries through HttpBackenService invocations are being used.
 * All outbound http requests are funneled through here, when a match from HttpBackenService entries
 * is found, the request is intercepted and the specified mock is returned with especified status code.
 * If no match is found, the http request is effectively sent over the wire
 */
@Injectable()
export class BackendInterceptor implements HttpInterceptor {
    constructor(
        private httpBackendService: HttpBackendService,
        private httpUtils: HttpUtils,
        private urlUtils: UrlUtils,
        private logService: LogService
    ) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const backendMockRespond = this.httpBackendService.findMatchingMock(request);

        if (!backendMockRespond) {
            return next.handle(request);
        }

        let response: [number, Payload | Payload[]] | PromiseLike<[number, Payload | Payload[]]>;

        if (typeof backendMockRespond === 'object') {
            response = [200, lodash.cloneDeep(backendMockRespond)];
        } else {
            // if (typeof backendMockRespond === 'function')
            let data: string | null = null;
            if (request.method === 'GET') {
                data = decodeURIComponent(
                    this.urlUtils.getQueryString(
                        this.httpUtils.copyHttpParamsOrHeaders(request.params)
                    )
                );
            } else if (
                request.headers.get('Content-Type') === 'application/x-www-form-urlencoded'
            ) {
                data = request.body; // it is a query string
            } else if (request.method === 'POST' || request.method === 'PUT') {
                data = JSON.stringify(request.body);
            }
            const headers = this.httpUtils.copyHttpParamsOrHeaders(
                request.headers
            ) as TypedMap<string>;

            response = backendMockRespond(
                request.method,
                decodeURIComponent(request.urlWithParams),
                data,
                headers
            );
        }
        this.logService.debug(`backend ${status} response for ${request.url}: `);

        return this.httpUtils.buildHttpResponse(request, response).pipe(take(1));
    }
}
