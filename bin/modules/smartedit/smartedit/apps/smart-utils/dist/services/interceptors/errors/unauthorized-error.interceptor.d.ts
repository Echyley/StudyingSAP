/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
import { TypedMap } from '../../../dtos';
import { IAuthenticationService, IEventService } from '../../../interfaces';
import { HttpUtils, PromiseUtils } from '../../../utils';
import { LogService } from '../../log.service';
import { IHttpErrorInterceptor } from '../i-http-error.interceptor';
export declare const GET_REQUESTS_ON_HOLD_MAP: TypedMap<Promise<HttpEvent<any>>>;
/**
 * @ngdoc service
 * @name @smartutils.services:unauthorizedErrorInterceptor
 * @description
 * Used for HTTP error code 401 (Forbidden). It will display the login modal.
 */
export declare class UnauthorizedErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private readonly httpClient;
    private readonly authenticationService;
    private readonly promiseUtils;
    private readonly httpUtils;
    private readonly eventService;
    private readonly logService;
    private promisesToResolve;
    private readonly rejectedUrls;
    constructor(httpClient: HttpClient, authenticationService: IAuthenticationService, promiseUtils: PromiseUtils, httpUtils: HttpUtils, WHO_AM_I_RESOURCE_URI: string, eventService: IEventService, logService: LogService);
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<any>;
    private isUrlNotRejected;
}
