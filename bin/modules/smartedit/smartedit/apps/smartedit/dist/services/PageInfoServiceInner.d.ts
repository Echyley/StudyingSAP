/// <reference types="jquery" />
import { IPageInfoService, IRestServiceFactory, LogService } from 'smarteditcommons';
/** @internal */
export declare class PageInfoService extends IPageInfoService {
    private readonly yjQuery;
    private readonly restServiceFactory;
    private readonly logService;
    static PATTERN_SMARTEDIT_CATALOG_VERSION_UUID: RegExp;
    private static PATTERN_SMARTEDIT_PAGE_UID;
    private static PATTERN_SMARTEDIT_PAGE_UUID;
    private readonly pageTemplateRestService;
    constructor(yjQuery: JQueryStatic, restServiceFactory: IRestServiceFactory, logService: LogService);
    /**
     * When the time comes to deprecate these 3 functions from componentHandlerService in the inner app, we will need
     * to migrate their implementations to here.
     */
    getPageUID(): Promise<string>;
    getPageUUID(): Promise<string>;
    getCatalogVersionUUIDFromPage(): Promise<string>;
    isSameCatalogVersionOfPageAndPageTemplate(): Promise<boolean>;
    /**
     * @param pattern Pattern of class names to search for
     *
     * @returns  Class attributes from the body element of the storefront
     */
    getBodyClassAttributeByRegEx(pattern: RegExp): string;
    /** @internal */
    try(func: () => string): Promise<string>;
}
