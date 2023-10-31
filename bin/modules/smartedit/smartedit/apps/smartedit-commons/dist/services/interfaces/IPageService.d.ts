import { Nullable } from '../../dtos';
import { ICMSPage, CmsApprovalStatus, IPage, IUriContext } from './';
export declare abstract class IPageService {
    /**
     * Retrieves the page corresponding to the given page UID in the current contextual
     * site + catalog + catalog version.
     */
    getPageById(pageUid: string): Promise<ICMSPage>;
    /**
     * Retrieves the page information of the page identified by the given uuid.
     */
    getPageByUuid(pageUuid: string): Promise<ICMSPage>;
    /**
     * Retrieves the page information of the page that is currently loaded.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the current page
     */
    getCurrentPageInfo(): Promise<ICMSPage>;
    /**
     * Retrieves a version, as identified by the provided version id, of the page information that is currently loaded.
     *
     * @param versionId The ID of the page version to load.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the version selected of the current page
     */
    getCurrentPageInfoByVersion(versionId: string | null): Promise<ICMSPage>;
    /**
     * Determines if a page belonging to the current contextual site+catalog+catalogversion is primary.
     */
    isPagePrimary(pageUid: string): Promise<boolean>;
    /**
     * Determines if a page belonging to the provided contextual site+catalog+catalogversion is primary.
     *
     * @param uriContext The uriContext for the pageId
     */
    isPagePrimaryWithContext(pageUid: string, uriContext: IUriContext): Promise<boolean>;
    /**
     * Retrieves the primary page of the given variation page in the current site+catalog+catalogversion.
     *
     * @param variationPageId The UID of the variation page for which to find its primary page.
     *
     * @returns A promise that resolves to the page object or undefined if no primary page was found.
     */
    getPrimaryPage(variationPageUid: string): Promise<Nullable<ICMSPage>>;
    /**
     * Returns true if primary page exists for a given page type
     */
    primaryPageForPageTypeExists(pageTypeCode: string, uriParams?: IUriContext): Promise<boolean>;
    /**
     * Fetches a pagination page for list of pages for a given site+catalog+catalogversion and page
     * @returns A promise that resolves to pagination with array of pages
     */
    getPaginatedPrimaryPagesForPageType(pageTypeCode: string, uriParams?: IUriContext, fetchPageParams?: {
        search: string;
        pageSize: number;
        currentPage: number;
    }): Promise<IPage<ICMSPage>>;
    /**
     * Retrieves the variation pages of the given primary page in the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves an array of variation pages or an empty list if none are found.
     */
    getVariationPages(primaryPageUid: string): Promise<ICMSPage[]>;
    /**
     * Updates the page corresponding to the given page UID with the payload provided for the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves to the JSON page object as it now exists in the backend
     */
    updatePageById(pageUid: string, payload: ICMSPage): Promise<ICMSPage>;
    /**
     * This method will forcefully update the page approval status (as long as the current user has the right permissions) of the page loaded
     * in the current context to the given status.
     *
     * @returns If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    forcePageApprovalStatus(newPageStatus: CmsApprovalStatus): Promise<ICMSPage>;
    /**
     * This method is used to determine whether the given page is approved (and can be synched).
     */
    isPageApproved(pageParam: string | ICMSPage): Promise<boolean>;
    /**
     * Returns the uriContext populated with the siteId, catalogId and catalogVersion taken from $routeParams and fallback to the currentExperience
     * Note: From the page list, $routeParams are defined. From the storefront, $routeParams are undefined.
     */
    buildUriContextForCurrentPage(siteId: Nullable<string>, catalogId: Nullable<string>, catalogVersion: Nullable<string>): Promise<IUriContext>;
}
