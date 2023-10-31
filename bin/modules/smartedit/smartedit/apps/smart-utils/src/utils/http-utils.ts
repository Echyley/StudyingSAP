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
    HttpHeaders,
    HttpParams,
    HttpRequest,
    HttpResponse,
    HttpResponseBase
} from '@angular/common/http';
import * as lodash from 'lodash';
import { from, throwError, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Payload, TypedMap } from '../dtos';
import { stringUtils } from './string-utils';

export class HttpUtils {
    isGET(request: HttpRequest<any>): boolean {
        return request.method === 'GET';
    }

    isRequestOfAccept(request: HttpRequest<any>, accept: string): boolean {
        return (
            !!request.headers &&
            !!request.headers.get('Accept') &&
            (request.headers.get('Accept') || '').includes(accept)
        );
    }

    isResponseOfContentType(response: HttpResponseBase, contentType: string): boolean {
        return (
            !!response.headers &&
            !!response.headers.get('Content-type') &&
            (response.headers.get('Content-type') || '').indexOf(contentType) === 0
        );
    }

    isHTMLRequest(request: HttpRequest<any>, response?: HttpResponseBase): boolean {
        return (
            this.isGET(request) &&
            (this.isRequestOfAccept(request, 'text/html') ||
                /.+\.html$/.test(request.url) ||
                /.+\.html\?/.test(request.url))
        );
    }

    isJSONRequest(request: HttpRequest<any>, response?: HttpResponseBase): boolean {
        return (
            this.isGET(request) &&
            ((response && this.isResponseOfContentType(response, 'json')) ||
                /.+\.json$/.test(request.url))
        );
    }

    isJSRequest(request: HttpRequest<any>): boolean {
        return this.isGET(request) && /.+\.js$/.test(request.url);
    }

    isCRUDRequest(request: HttpRequest<any>, response?: HttpErrorResponse): boolean {
        return (
            !this.isHTMLRequest(request, response) &&
            !this.isJSONRequest(request, response) &&
            !this.isJSRequest(request)
        );
    }

    transformHttpParams(params: HttpParams, substitutionMap: TypedMap<string>): HttpParams {
        return new HttpParams({
            fromObject: JSON.parse(
                stringUtils.replaceAll(
                    JSON.stringify(this.copyHttpParamsOrHeaders(params)),
                    substitutionMap
                )
            )
        });
    }

    copyHttpParamsOrHeaders(params: HttpParams | HttpHeaders): TypedMap<string | string[]> {
        const copy = {} as TypedMap<string | string[]>;
        params.keys().forEach((key) => {
            const values = params.getAll(key);
            if (values !== null) {
                copy[key] = values.length > 1 ? values : values[0];
            }
        });
        return copy;
    }

    buildHttpResponse<T = any>(
        originalRequest: HttpRequest<T>,
        _statusAndPayload:
            | [number, Payload | Payload[]]
            | PromiseLike<[number, Payload | Payload[]]>
    ): Observable<HttpEvent<any> | never> {
        const statusAndPayloadPromise = Promise.resolve(_statusAndPayload);

        return from(statusAndPayloadPromise).pipe(
            switchMap((statusAndPayload: [number, Payload | Payload[]]) => {
                const status = statusAndPayload[0];
                const body = statusAndPayload[1];
                const requestClone = originalRequest.clone({
                    body
                });
                lodash.merge(requestClone, { status });

                if (200 <= status && status < 300) {
                    return new Observable<HttpEvent<any>>((ob) => {
                        ob.next(new HttpResponse(requestClone));
                    });
                } else {
                    return throwError(
                        new HttpErrorResponse(lodash.merge(requestClone, { error: body }))
                    );
                }
            })
        );
    }
}

export const httpUtils = new HttpUtils();
