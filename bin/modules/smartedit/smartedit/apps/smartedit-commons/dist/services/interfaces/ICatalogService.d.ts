import { IBaseCatalog, IBaseCatalogVersion, ICatalog, ICatalogVersion } from '../../dtos';
import { ISite } from './ISite';
import { IUriContext } from './IUriContext';
/**
 * The Catalog Service fetches catalogs for a specified site or for all sites registered on the hybris platform using
 * REST calls to the cmswebservices Catalog Version Details API.
 */
export declare abstract class ICatalogService {
    /**
     * Convenience method to return a full `UriContext` to the invoker through a promise.
     *
     *
     * If uriContext is provided, it will be returned as such.
     *
     *
     * If uriContext is not provided, A uriContext will be built from the experience present in {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     * If we fail to find a uriContext in sharedDataService, an exception will be thrown.
     * @returns Wrapped uriContext in a promise
     */
    retrieveUriContext(_uriContext?: IUriContext): Promise<IUriContext>;
    /**
     * Fetches a list of content catalogs for the site that corresponds to the specified site UID.
     *
     * @param siteUID The UID of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getContentCatalogsForSite(siteUID: string): Promise<IBaseCatalog[]>;
    /**
     * Fetches a list of content catalog groupings for all sites.
     *
     * @returns An array of catalog groupings sorted by catalog ID, each of which has a name, a catalog ID, and a list of
     * catalog version descriptors.
     */
    getAllContentCatalogsGroupedById(): Promise<ICatalog[][]>;
    /**
     * Fetches a list of catalogs for the given site UID and a given catalog version.
     *
     * @param siteUID The UID of the site that the catalog versions are to be fetched.
     * @param catalogVersion The version of the catalog that is to be fetched.
     *
     * @returns An array containing the catalog descriptor (if any). Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalogVersion.
     */
    getCatalogByVersion(siteUID: string, catalogVersionName: string): Promise<IBaseCatalog[]>;
    /**
     * Determines whether the catalog version identified by the given uriContext is a non active one
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     *
     * @returns True if the given catalog version is non active
     */
    isContentCatalogVersionNonActive(_uriContext?: IUriContext): Promise<boolean>;
    /**
     * Find the version that is flagged as active for the given uriContext.
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link /smartedit/injectables/SharedDataService.html SharedDataService}.
     *
     * @returns The version name
     */
    getContentCatalogActiveVersion(_uriContext?: IUriContext): Promise<string>;
    /**
     * Finds the version name that is flagged as active for the given content catalog.
     *
     * @param contentCatalogId The UID of content catalog for which to retrieve its active catalog version name.
     * @returns The version name
     */
    getActiveContentCatalogVersionByCatalogId(contentCatalogId: string): Promise<string>;
    /**
     * Finds the current site ID
     * @returns The ID of the current site.
     */
    getCurrentSiteID(): Promise<string>;
    /**
     * Finds the ID of the default site configured for the provided content catalog.
     * @param contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns The ID of the default site found.
     */
    getDefaultSiteForContentCatalog(contentCatalogId: string): Promise<ISite>;
    /**
     * Finds the catalog version given an uriContext object.
     *
     * @param uriContext An object that represents the current context, containing information about the site.
     * @returns A promise that resolves to the catalog version descriptor found.
     */
    getContentCatalogVersion(uriContext: IUriContext): Promise<IBaseCatalogVersion>;
    /**
     * Finds the catalog version descriptor identified by the provided UUID.
     * An exception is thrown if no match is found.
     *
     * @param catalogVersionUuid The UID of the catalog version descriptor to find.
     * @param siteId the ID of the site where to perform the search.
     * If no ID is provided, the search will be performed on all permitted sites.
     * @returns A promise that resolves to the catalog version descriptor found.
     *
     */
    getCatalogVersionByUuid(catalogVersionUuid: string, siteId?: string): Promise<ICatalogVersion>;
    /**
     * Finds the catalog version UUID given an optional urlContext object. The current catalog version UUID from the active experience selector is returned, if the URL is not present in the call.
     *
     * @param urlContext An object that represents the current context, containing information about the site.
     * @returns A promise that resolves to the catalog version uuid.
     */
    getCatalogVersionUUid(_uriContext?: IUriContext): Promise<string>;
    /**
     * Fetches a list of product catalogs for the site that corresponds to the specified site UID key.
     *
     * @param siteUIDKey The UID of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getProductCatalogsBySiteKey(siteUIDKey: string): Promise<IBaseCatalog[]>;
    /**
     * Fetches a list of product catalogs for the site that corresponds to the specified site UID value.
     *
     * @param siteUIDValue The UID value of the site that the catalog versions are to be fetched.
     *
     * @returns An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalog version descriptors.
     */
    getProductCatalogsForSite(siteUIDValue: string): Promise<IBaseCatalog[]>;
    /**
     * Finds the version name that is flagged as active for the given product catalog.
     *
     * @param productCatalogId The UID of product catalog for which to retrieve its active catalog version name.
     * @returns the version name
     */
    getActiveProductCatalogVersionByCatalogId(productCatalogId: string): Promise<string>;
    /**
     * Fetches all the active catalog version uuid's for a provided array of catalogs.
     *
     * @returns An array of catalog version uuid's
     */
    returnActiveCatalogVersionUIDs(catalogs: ICatalog[]): string[];
    /**
     * Determines whether the current catalog from the page context of current experience is multicountry related or not.
     *
     * @returns True if current catalog is multicountry related; Otherwise false.
     */
    isCurrentCatalogMultiCountry(): Promise<boolean>;
}
