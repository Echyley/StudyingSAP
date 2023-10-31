import { ISite, RestServiceFactory } from 'smarteditcommons';
/**
 * The Site Service fetches all sites configured on the hybris platform using REST calls to the cmswebservices sites API.
 */
export declare class SiteService {
    private cache;
    private siteRestService;
    private sitesForCatalogsRestService;
    private readonly SITES_FOR_CATALOGS_URI;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * Fetches a list of sites for which user has at-least read access to one of the non-active catalog versions.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    getAccessibleSites(): Promise<ISite[]>;
    /**
     * Fetches a list of sites configured for accessible sites. The list of sites fetched using REST calls through
     * the cmswebservices sites API.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    getSites(): Promise<ISite[]>;
    /**
     * Fetches a site, configured on the hybris platform, by its uid. The sites fetched using REST calls through
     * cmswebservices sites API.
     *
     * @param uid unique site ID
     * @returns A promise of [ISite]{@link ISite}.
     */
    getSiteById(uid: string): Promise<ISite>;
}
