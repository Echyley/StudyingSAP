import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ISharedDataService } from 'smarteditcommons';
export declare class BaseSiteHeaderInterceptor implements HttpInterceptor {
    private sharedDataService;
    private static HEADER_NAME;
    private static PERSONALIZATION_ENDPOINT;
    constructor(sharedDataService: ISharedDataService);
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
}
