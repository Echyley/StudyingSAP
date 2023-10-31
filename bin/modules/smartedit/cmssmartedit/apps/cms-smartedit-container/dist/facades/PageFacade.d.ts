import { CrossFrameEventService, ICatalogService, ISharedDataService, IUriContext, IUrlService, CmsitemsRestService, ICMSPage } from 'smarteditcommons';
export declare class PageFacade {
    private cmsitemsRestService;
    private crossFrameEventService;
    private sharedDataService;
    private urlService;
    private catalogService;
    constructor(cmsitemsRestService: CmsitemsRestService, crossFrameEventService: CrossFrameEventService, sharedDataService: ISharedDataService, urlService: IUrlService, catalogService: ICatalogService);
    /**
     * Determines if a ContentPage with a given label exists in the given catalog and catalog version
     *
     * @param label The label to search for
     * @param catalogId The catalog ID to search in for the ContentPage
     * @param catalogVersion The catalog version to search in for the ContentPage
     * @return Promise resolving to a boolean determining if the ContentPage exists
     */
    contentPageWithLabelExists(label: string, catalogId: string, catalogVersion: string): Promise<boolean>;
    /**
     * Retrieves the experience and builds a uri context based on its page context
     *
     * @returns the page uriContext
     */
    retrievePageUriContext(): Promise<IUriContext>;
    /**
     * @param page The object representing the CMS page item to create
     * @returns If request is successful, it returns a promise that resolves with the CMS page item object. If
     * the request fails, it resolves with errors from the backend.
     */
    createPage(page: ICMSPage): Promise<ICMSPage>;
    /**
     * Creates a new CMS page item for a given site.
     *
     * @param page The object representing the CMS page item to create
     * @param siteUid The uid of the target site.
     * @returns If request is successful, it returns a promise that resolves with the CMS page item object. If
     * the request fails, it resolves with errors from the backend.
     */
    createPageForSite(page: ICMSPage, siteUid: string): Promise<ICMSPage>;
}
