/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Observable } from "rxjs";
import { ISharedDataService } from "smarteditcommons";
export declare class MerchandisingExperienceInterceptor implements HttpInterceptor {
    private sharedDataService;
    private static readonly MERCHCMSWEBSERVICES_PATH;
    constructor(sharedDataService: ISharedDataService);
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
}
