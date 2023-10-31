import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { HttpUtils, IHttpErrorInterceptor } from '@smart/utils';
import { IAlertService } from 'smarteditcommons/services/interfaces';
/**
 * Used for HTTP error code 404 (Not Found) except for an HTML or a language resource. It will display the response.message in an alert message.
 */
export declare class ResourceNotFoundErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private readonly alertService;
    private readonly httpUtils;
    constructor(alertService: IAlertService, httpUtils: HttpUtils);
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<any>;
    private _isLanguageResourceRequest;
}
