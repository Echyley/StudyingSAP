/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpClient, HttpErrorResponse, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { stringUtils, BackendError, IHttpErrorInterceptor, LogService } from '@smart/utils';
import { PREVIEW_RESOURCE_URI, EXPERIENCE_STORAGE_KEY } from '../../../utils/smarteditconstants';
import { ISharedDataService } from '../../interfaces';

export type PageIdAwareObject<T> = T & { pageId?: number };

/**
 * Used for HTTP error code 400 from the Preview API when the pageId is not found in the context. The request will
 * be replayed without the pageId.
 *
 * This can happen in a few different scenarios. For instance, you are on electronics catalog, on some custom page called XYZ.
 * If you use the experience selector and switch to apparel catalog, it will try to create a new preview ticket
 * with apparel catalog and pageId of XYZ. Since XYZ doesn't exist in apparel, it will fail. So we remove the page ID
 * and create a preview for homepage as a default/fallback.
 */
@Injectable()
export class PreviewErrorInterceptor<T = any>
    implements IHttpErrorInterceptor<PageIdAwareObject<T>>
{
    constructor(
        private readonly injector: Injector,
        private readonly logService: LogService,
        private readonly sharedDataService: ISharedDataService
    ) {}
    predicate(request: HttpRequest<PageIdAwareObject<T>>, response: HttpErrorResponse): boolean {
        return (
            response.status === 400 &&
            request.url.indexOf(PREVIEW_RESOURCE_URI) > -1 &&
            !stringUtils.isBlank(request.body.pageId) &&
            this._hasUnknownIdentifierError(response.error.errors)
        );
    }
    responseError(
        request: HttpRequest<PageIdAwareObject<T>>,
        response: HttpErrorResponse
    ): Promise<HttpEvent<PageIdAwareObject<T>>> {
        this.logService.info(
            'The error 400 above on preview is expected in some scenarios, typically when switching catalogs from experience selector.'
        );
        this.logService.info(
            'Removing the pageId [' +
                (request.body as any).pageId +
                '] and creating a preview for homepage'
        );

        delete (request.body as any).pageId;
        this.sharedDataService.update(EXPERIENCE_STORAGE_KEY, function (experience) {
            delete experience.pageId;
            return experience;
        });

        this.injector.get('iframeManagerService').setCurrentLocation(null);
        return this.injector.get(HttpClient).request<PageIdAwareObject<T>>(request).toPromise();
    }

    private _hasUnknownIdentifierError(errors: BackendError[]): boolean {
        const unknownIdentifierErrors = errors.filter(function (error) {
            return error.type === 'UnknownIdentifierError';
        });
        return !!unknownIdentifierErrors.length;
    }
}
