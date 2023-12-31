/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    authorizationEvictionTag,
    rarelyChangingContent,
    Cached,
    IRestService,
    ISite,
    OperationContextRegistered,
    RestServiceFactory,
    SITES_RESOURCE_URI
} from 'smarteditcommons';

/** @internal */
interface ISiteDTO {
    sites: ISite[];
}
/**
 * The Site Service fetches all sites configured on the hybris platform using REST calls to the cmswebservices sites API.
 */

@OperationContextRegistered('SITES_RESOURCE_URI', 'CMS')
export class SiteService {
    private cache: ISite[];
    private siteRestService: IRestService<ISiteDTO>;
    private sitesForCatalogsRestService: IRestService<ISiteDTO>;

    private readonly SITES_FOR_CATALOGS_URI = '/cmswebservices/v1/sites/catalogs';

    constructor(restServiceFactory: RestServiceFactory) {
        this.cache = null;
        this.siteRestService = restServiceFactory.get<ISiteDTO>(SITES_RESOURCE_URI);
        this.sitesForCatalogsRestService = restServiceFactory.get<ISiteDTO>(
            this.SITES_FOR_CATALOGS_URI
        );
    }

    /**
     * Fetches a list of sites for which user has at-least read access to one of the non-active catalog versions.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [authorizationEvictionTag] })
    getAccessibleSites(): Promise<ISite[]> {
        return this.siteRestService.get({}).then((sitesDTO: ISiteDTO) => sitesDTO.sites);
    }

    /**
     * Fetches a list of sites configured for accessible sites. The list of sites fetched using REST calls through
     * the cmswebservices sites API.
     *
     * @returns A promise of [ISite]{@link ISite} array.
     */
    @Cached({ actions: [rarelyChangingContent], tags: [authorizationEvictionTag] })
    getSites(): Promise<ISite[]> {
        //  Uses two REST API calls because of multicountry. The first call gives all the sites for which the user has permissions to.
        return this.getAccessibleSites().then((sites) => {
            const catalogIds: string[] = sites.reduce(
                (catalogs: string[], site: ISite) => [...(catalogs || []), ...site.contentCatalogs],
                []
            );

            // The call with catalogIds gives all the corresponding sites in the hierarchy.
            return this.sitesForCatalogsRestService
                .save({
                    catalogIds
                })

                .then((allSites: ISiteDTO) => {
                    this.cache = allSites.sites;
                    return this.cache;
                });
        });
    }

    /**
     * Fetches a site, configured on the hybris platform, by its uid. The sites fetched using REST calls through
     * cmswebservices sites API.
     *
     * @param uid unique site ID
     * @returns A promise of [ISite]{@link ISite}.
     */
    getSiteById(uid: string): Promise<ISite> {
        return this.getSites().then((sites: ISite[]) => sites.find((site) => site.uid === uid));
    }
}
