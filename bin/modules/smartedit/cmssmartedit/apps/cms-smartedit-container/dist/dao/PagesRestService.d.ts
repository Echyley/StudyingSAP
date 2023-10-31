import { RestServiceFactory, ICMSPage } from 'smarteditcommons';
/** Provides REST services for the CMS pages rest endpoint. */
export declare class PagesRestService {
    private restServiceFactory;
    private readonly URI;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Fetches a list of pages for a given array of UIDs.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @returns A promise resolving to a list of pages, or an empty list.
     */
    get(uids: string[]): Promise<ICMSPage[]>;
    /**
     * Fetches a page for a given UID.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @param pageUid A page UID of the page to fetch
     */
    getById(pageUid: string): Promise<ICMSPage>;
    /**
     * Updates a page for a given site, catalog, and catalog version.
     * It uses the current site, catalog and catalog version from the session.
     *
     * @param pageUid The page UID of the page to update.
     * @param payload The page object to be applied to the page resource as it exists on the backend.
     *
     * @returns A promise that resolves to a JSON object representing the updated page.
     */
    update(pageUid: string, payload: ICMSPage): Promise<ICMSPage>;
}
