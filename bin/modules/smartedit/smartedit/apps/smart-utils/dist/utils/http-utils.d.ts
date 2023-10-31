/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpEvent, HttpHeaders, HttpParams, HttpRequest, HttpResponseBase } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payload, TypedMap } from '../dtos';
export declare class HttpUtils {
    isGET(request: HttpRequest<any>): boolean;
    isRequestOfAccept(request: HttpRequest<any>, accept: string): boolean;
    isResponseOfContentType(response: HttpResponseBase, contentType: string): boolean;
    isHTMLRequest(request: HttpRequest<any>, response?: HttpResponseBase): boolean;
    isJSONRequest(request: HttpRequest<any>, response?: HttpResponseBase): boolean;
    isJSRequest(request: HttpRequest<any>): boolean;
    isCRUDRequest(request: HttpRequest<any>, response?: HttpErrorResponse): boolean;
    transformHttpParams(params: HttpParams, substitutionMap: TypedMap<string>): HttpParams;
    copyHttpParamsOrHeaders(params: HttpParams | HttpHeaders): TypedMap<string | string[]>;
    buildHttpResponse<T = any>(originalRequest: HttpRequest<T>, _statusAndPayload: [number, Payload | Payload[]] | PromiseLike<[number, Payload | Payload[]]>): Observable<HttpEvent<any> | never>;
}
export declare const httpUtils: HttpUtils;
