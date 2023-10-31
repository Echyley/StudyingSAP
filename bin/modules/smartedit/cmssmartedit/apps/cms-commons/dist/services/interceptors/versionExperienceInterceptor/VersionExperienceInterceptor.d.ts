import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ISharedDataService } from 'smarteditcommons';
export declare class VersionExperienceInterceptor implements HttpInterceptor {
    private sharedDataService;
    private static MODE_DEFAULT;
    private static MODE_PREVIEW_VERSION;
    private static PREVIEW_DATA_TYPE;
    constructor(sharedDataService: ISharedDataService);
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
    private isGET;
    private isPreviewDataTypeResourceEndpoint;
}
