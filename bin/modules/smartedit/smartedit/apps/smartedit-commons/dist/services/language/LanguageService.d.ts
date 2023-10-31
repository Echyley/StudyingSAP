import { Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BrowserService, IEventService, ILanguage, ILanguageServiceConstants, IRestService, IStorageService, LanguageService as SmartutilsLanguageService, LogService, PromiseUtils } from '@smart/utils';
export declare class LanguageService extends SmartutilsLanguageService {
    protected logService: LogService;
    protected translateService: TranslateService;
    protected promiseUtils: PromiseUtils;
    protected eventService: IEventService;
    protected browserService: BrowserService;
    protected storageService: IStorageService;
    protected injector: Injector;
    protected languageServiceConstants: ILanguageServiceConstants;
    constructor(logService: LogService, translateService: TranslateService, promiseUtils: PromiseUtils, eventService: IEventService, browserService: BrowserService, storageService: IStorageService, injector: Injector, languageServiceConstants: ILanguageServiceConstants);
    /**
     * Fetches a list of language descriptors for the specified storefront site UID.
     * The object containing the list of sites is fetched using REST calls to the cmswebservices languages API.
     */
    getLanguagesForSite(siteUID: string): Promise<ILanguage[]>;
    protected get languageRestService(): IRestService<{
        languages: ILanguage[];
    }>;
}
