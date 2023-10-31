/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
    HttpResponse
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LogService } from '../../../services/log.service';
import { HttpUtils } from '../../../utils';

export type Predicate = (request: HttpRequest<any>) => boolean;
export type RequestHandler = (request: HttpRequest<any>) => HttpRequest<any>;
export type ResponseHandler = (event: HttpResponse<any>) => HttpResponse<any>;

/*
 * interceptor that will inject flaw into outbound and inbound http calls.
 * It is mainly used to validate reliability and consitency of test frameworks
 */
/** @internal */
@Injectable()
export class FlawInjectionInterceptor implements HttpInterceptor {
    private static requestMutations: { test: Predicate; mutate: RequestHandler }[] = [];
    private static responseMutations: { test: Predicate; mutate: ResponseHandler }[] = [];

    /*
     * probability of flaw occurrence ranging from 0 to 1
     */
    private static PROBABILITY = 0;

    private flawWindow: any;

    static registerRequestFlaw(mutation: { test: Predicate; mutate: RequestHandler }): void {
        this.requestMutations.push(mutation);
    }

    static registerResponseFlaw(mutation: { test: Predicate; mutate: ResponseHandler }): void {
        this.responseMutations.push(mutation);
    }

    constructor(private httpUtils: HttpUtils, private logService: LogService) {
        this.flawWindow = window;
        this.flawWindow.allRequests = 0;
        this.flawWindow.flawedRequests = 0;
        this.flawWindow.allResponses = 0;
        this.flawWindow.flawedResponses = 0;
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (
            FlawInjectionInterceptor.PROBABILITY !== 0 &&
            this.httpUtils.isCRUDRequest(request) &&
            !this._isGET(request)
        ) {
            return this._handleFlawWindow(request, next);
        }

        return next.handle(request);
    }

    private _isGET(config: HttpRequest<any>): boolean {
        return config.method === 'GET';
    }

    private _activateWithProbability(probabilityTrue: number): boolean {
        return Math.random() >= 1.0 - probabilityTrue;
    }

    private _generateResultResponse(
        request: HttpRequest<any>,
        result: Observable<HttpEvent<any>>
    ): Observable<HttpEvent<any>> {
        return result.pipe(
            map((event: HttpEvent<any>) => {
                if (
                    event instanceof HttpResponse &&
                    this._activateWithProbability(FlawInjectionInterceptor.PROBABILITY)
                ) {
                    this.flawWindow.flawedResponses++;

                    const responseMutation = FlawInjectionInterceptor.responseMutations.find(
                        (mutation) => mutation.test(request)
                    );
                    if (responseMutation && event instanceof HttpResponse) {
                        this.logService.error(`FLAWED RESPONSE-"${request.url}`);
                        return responseMutation.mutate(event);
                    }
                }
                return event;
            })
        );
    }

    private _handleFlawWindow(
        request: HttpRequest<any>,
        next: HttpHandler
    ): Observable<HttpEvent<any>> {
        let result: Observable<HttpEvent<any>>;
        this.flawWindow.allRequests++;
        if (this._activateWithProbability(FlawInjectionInterceptor.PROBABILITY)) {
            this.flawWindow.flawedRequests++;

            const requestMutation = FlawInjectionInterceptor.requestMutations.find((mutation) =>
                mutation.test(request)
            );
            if (requestMutation) {
                this.logService.error(`FLAWED REQUEST-"${request.url}`);
                result = next.handle(requestMutation.mutate(request));
            }
        }

        result = next.handle(request);
        return this._generateResultResponse(request, result);
    }
}
