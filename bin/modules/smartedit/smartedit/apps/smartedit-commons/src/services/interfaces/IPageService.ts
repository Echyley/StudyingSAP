/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Nullable } from '../../dtos';
import { ICMSPage, CmsApprovalStatus, IPage, IUriContext } from './';

export abstract class IPageService {
    /**
     * Retrieves the page corresponding to the given page UID in the current contextual
     * site + catalog + catalog version.
     */
    public getPageById(pageUid: string): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the page information of the page identified by the given uuid.
     */
    public getPageByUuid(pageUuid: string): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the page information of the page that is currently loaded.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the current page
     */
    public getCurrentPageInfo(): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves a version, as identified by the provided version id, of the page information that is currently loaded.
     *
     * @param versionId The ID of the page version to load.
     *
     * @returns A promise that resolves to a CMS Item object containing
     * information related to the version selected of the current page
     */
    public getCurrentPageInfoByVersion(versionId: string | null): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * Determines if a page belonging to the current contextual site+catalog+catalogversion is primary.
     */
    public isPagePrimary(pageUid: string): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Determines if a page belonging to the provided contextual site+catalog+catalogversion is primary.
     *
     * @param uriContext The uriContext for the pageId
     */
    public isPagePrimaryWithContext(pageUid: string, uriContext: IUriContext): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the primary page of the given variation page in the current site+catalog+catalogversion.
     *
     * @param variationPageId The UID of the variation page for which to find its primary page.
     *
     * @returns A promise that resolves to the page object or undefined if no primary page was found.
     */
    public getPrimaryPage(variationPageUid: string): Promise<Nullable<ICMSPage>> {
        'proxyFunction';
        return null;
    }

    /**
     * Returns true if primary page exists for a given page type
     */

    public primaryPageForPageTypeExists(
        pageTypeCode: string,
        uriParams?: IUriContext
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Fetches a pagination page for list of pages for a given site+catalog+catalogversion and page
     * @returns A promise that resolves to pagination with array of pages
     */
    public getPaginatedPrimaryPagesForPageType(
        pageTypeCode: string,
        uriParams?: IUriContext,
        fetchPageParams?: {
            search: string;
            pageSize: number;
            currentPage: number;
        }
    ): Promise<IPage<ICMSPage>> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the variation pages of the given primary page in the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves an array of variation pages or an empty list if none are found.
     */
    public getVariationPages(primaryPageUid: string): Promise<ICMSPage[]> {
        'proxyFunction';
        return null;
    }

    /**
     * Updates the page corresponding to the given page UID with the payload provided for the current site+catalog+catalogversion.
     *
     * @returns A promise that resolves to the JSON page object as it now exists in the backend
     */
    public updatePageById(pageUid: string, payload: ICMSPage): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * This method will forcefully update the page approval status (as long as the current user has the right permissions) of the page loaded
     * in the current context to the given status.
     *
     * @returns If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
     * request fails, it resolves with errors from the backend.
     */
    public forcePageApprovalStatus(newPageStatus: CmsApprovalStatus): Promise<ICMSPage> {
        'proxyFunction';
        return null;
    }

    /**
     * This method is used to determine whether the given page is approved (and can be synched).
     */
    public isPageApproved(pageParam: string | ICMSPage): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Returns the uriContext populated with the siteId, catalogId and catalogVersion taken from $routeParams and fallback to the currentExperience
     * Note: From the page list, $routeParams are defined. From the storefront, $routeParams are undefined.
     */
    public buildUriContextForCurrentPage(
        siteId: Nullable<string>,
        catalogId: Nullable<string>,
        catalogVersion: Nullable<string>
    ): Promise<IUriContext> {
        'proxyFunction';
        return null;
    }
}
