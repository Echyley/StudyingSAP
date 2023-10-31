import { ContentCatalogRestService, IBaseCatalog, IBaseCatalogVersion, ICatalog, ICatalogService, ICatalogVersion, ISharedDataService, ISite, IUriContext, IUrlService, LogService, ProductCatalogRestService, IStorageService } from 'smarteditcommons';
import { SiteService } from './SiteService';
/** @internal */
export declare class CatalogService extends ICatalogService {
    private logService;
    private sharedDataService;
    private siteService;
    private urlService;
    private contentCatalogRestService;
    private productCatalogRestService;
    private storageService;
    private SELECTED_SITE_COOKIE_NAME;
    constructor(logService: LogService, sharedDataService: ISharedDataService, siteService: SiteService, urlService: IUrlService, contentCatalogRestService: ContentCatalogRestService, productCatalogRestService: ProductCatalogRestService, storageService: IStorageService);
    getContentCatalogsForSite(siteUID: string): Promise<IBaseCatalog[]>;
    getCatalogByVersion(siteUID: string, catalogVersionName: string): Promise<IBaseCatalog[]>;
    isContentCatalogVersionNonActive(_uriContext?: IUriContext): Promise<boolean>;
    getContentCatalogActiveVersion(_uriContext?: IUriContext): Promise<string>;
    getActiveContentCatalogVersionByCatalogId(contentCatalogId: string): Promise<string>;
    getContentCatalogVersion(_uriContext?: IUriContext): Promise<IBaseCatalogVersion>;
    getCurrentSiteID(): Promise<string>;
    /**
     * Finds the ID of the default site configured for the provided content catalog.
     * @param contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns The ID of the default site found.
     */
    getDefaultSiteForContentCatalog(contentCatalogId: string): Promise<ISite>;
    getCatalogVersionByUuid(catalogVersionUuid: string, siteId?: string): Promise<ICatalogVersion>;
    getAllContentCatalogsGroupedById(): Promise<ICatalog[][]>;
    getProductCatalogsBySiteKey(siteUIDKey: string): Promise<IBaseCatalog[]>;
    getProductCatalogsForSite(siteUIDValue: string): Promise<IBaseCatalog[]>;
    getActiveProductCatalogVersionByCatalogId(productCatalogId: string): Promise<string>;
    getCatalogVersionUUid(_uriContext?: IUriContext): Promise<string>;
    retrieveUriContext(_uriContext?: IUriContext): Promise<IUriContext>;
    returnActiveCatalogVersionUIDs(catalogs: ICatalog[]): string[];
    isCurrentCatalogMultiCountry(): Promise<boolean>;
    private _getContext;
    private getContentCatalogsGrouped;
}
