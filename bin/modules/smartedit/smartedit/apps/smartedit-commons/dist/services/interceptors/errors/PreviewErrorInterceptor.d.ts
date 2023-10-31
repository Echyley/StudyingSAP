import { HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injector } from '@angular/core';
import { IHttpErrorInterceptor, LogService } from '@smart/utils';
import { ISharedDataService } from '../../interfaces';
export declare type PageIdAwareObject<T> = T & {
    pageId?: number;
};
/**
 * Used for HTTP error code 400 from the Preview API when the pageId is not found in the context. The request will
 * be replayed without the pageId.
 *
 * This can happen in a few different scenarios. For instance, you are on electronics catalog, on some custom page called XYZ.
 * If you use the experience selector and switch to apparel catalog, it will try to create a new preview ticket
 * with apparel catalog and pageId of XYZ. Since XYZ doesn't exist in apparel, it will fail. So we remove the page ID
 * and create a preview for homepage as a default/fallback.
 */
export declare class PreviewErrorInterceptor<T = any> implements IHttpErrorInterceptor<PageIdAwareObject<T>> {
    private readonly injector;
    private readonly logService;
    private readonly sharedDataService;
    constructor(injector: Injector, logService: LogService, sharedDataService: ISharedDataService);
    predicate(request: HttpRequest<PageIdAwareObject<T>>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<PageIdAwareObject<T>>, response: HttpErrorResponse): Promise<HttpEvent<PageIdAwareObject<T>>>;
    private _hasUnknownIdentifierError;
}
