import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { StringUtils } from '@smart/utils';
import { Observable } from 'rxjs';
import { ISharedDataService } from 'smarteditcommons/services/interfaces';
import { HttpUtils } from 'smarteditcommons/utils';
/**
 * A HTTP request interceptor which intercepts all 'cmswebservices/catalogs' requests and adds the current catalog and version
 * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
 */
export declare class ExperienceInterceptor implements HttpInterceptor {
    private readonly sharedDataService;
    private readonly stringUtils;
    private readonly httpUtils;
    constructor(sharedDataService: ISharedDataService, stringUtils: StringUtils, httpUtils: HttpUtils);
    /**
     * Interceptor method which gets called with a http config object, intercepts any 'cmswebservices/catalogs' requests and adds
     * the current catalog and version
     * from any URI which define the variables 'CURRENT_CONTEXT_CATALOG' and 'CURRENT_CONTEXT_CATALOG_VERSION' in the URL.
     * If the request URI contains any of 'PAGE_CONTEXT_SITE_ID', 'PAGE_CONTEXT_CATALOG' or 'PAGE_CONTEXT_CATALOG_VERSION',
     * then it is replaced by the siteId/catalogId/catalogVersion of the current page in context.
     *
     * The catalog name and catalog versions of the current experience and the page loaded are stored in the shared data service object called 'experience' during preview initialization
     * and here we retrieve those details and set it to headers.
     */
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>;
}
