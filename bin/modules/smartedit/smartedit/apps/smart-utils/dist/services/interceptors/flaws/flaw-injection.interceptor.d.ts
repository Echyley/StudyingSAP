/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LogService } from '../../../services/log.service';
import { HttpUtils } from '../../../utils';
export declare type Predicate = (request: HttpRequest<any>) => boolean;
export declare type RequestHandler = (request: HttpRequest<any>) => HttpRequest<any>;
export declare type ResponseHandler = (event: HttpResponse<any>) => HttpResponse<any>;
/** @internal */
export declare class FlawInjectionInterceptor implements HttpInterceptor {
    private httpUtils;
    private logService;
    private static requestMutations;
    private static responseMutations;
    private static PROBABILITY;
    private flawWindow;
    static registerRequestFlaw(mutation: {
        test: Predicate;
        mutate: RequestHandler;
    }): void;
    static registerResponseFlaw(mutation: {
        test: Predicate;
        mutate: ResponseHandler;
    }): void;
    constructor(httpUtils: HttpUtils, logService: LogService);
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
    private _isGET;
    private _activateWithProbability;
    private _generateResultResponse;
    private _handleFlawWindow;
}
