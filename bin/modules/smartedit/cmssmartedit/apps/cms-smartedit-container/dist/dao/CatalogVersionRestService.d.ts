import { ICloneableCatalogVersion } from 'cmscommons';
import { IUriContext, RestServiceFactory } from 'smarteditcommons';
/**
 * @ngdoc service
 * @name CatalogVersionRestService
 * @description
 *
 * Provides REST services for the CMS catalog version endpoint
 */
export declare class CatalogVersionRestService {
    private restServiceFactory;
    private readonly URI;
    constructor(restServiceFactory: RestServiceFactory);
    /**
     * @ngdoc method
     * @name CatalogVersionRestService#getCloneableTargets
     * @methodOf CatalogVersionRestService
     *
     * @description
     * Fetches all cloneable target catalog versions for a given site+catalog+catalogversion
     *
     * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext UriContext}
     *
     * @returns {Object} A JSON object with a single field 'versions' containing a list of catalog versions, or an empty list.
     */
    getCloneableTargets(uriContext: IUriContext): Promise<ICloneableCatalogVersion>;
}
