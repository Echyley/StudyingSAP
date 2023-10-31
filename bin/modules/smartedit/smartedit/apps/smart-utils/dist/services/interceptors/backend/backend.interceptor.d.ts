/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LogService } from '../../../services/log.service';
import { HttpUtils, UrlUtils } from '../../../utils';
import { HttpBackendService } from './http-backend.service';
export declare class BackendInterceptor implements HttpInterceptor {
    private httpBackendService;
    private httpUtils;
    private urlUtils;
    private logService;
    constructor(httpBackendService: HttpBackendService, httpUtils: HttpUtils, urlUtils: UrlUtils, logService: LogService);
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
}
