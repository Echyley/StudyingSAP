/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as lodash from 'lodash';
import {
    catalogEvictionTag,
    pageChangeEvictionTag,
    rarelyChangingContent,
    Cached,
    ContentCatalogRestService,
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID,
    GatewayProxied,
    IBaseCatalog,
    IBaseCatalogs,
    IBaseCatalogVersion,
    ICatalog,
    ICatalogService,
    ICatalogVersion,
    ISharedDataService,
    ISite,
    IUriContext,
    IUrlService,
    LogService,
    ProductCatalogRestService,
    IStorageService,
    EXPERIENCE_STORAGE_KEY
} from 'smarteditcommons';
import { SiteService } from './SiteService';

/** @internal */
@GatewayProxied()
@Injectable()
export class CatalogService extends ICatalogService {
    private SELECTED_SITE_COOKIE_NAME = 'seselectedsite';
    constructor(
        private logService: LogService,
        private sharedDataService: ISharedDataService,
        private siteService: SiteService,
        private urlService: IUrlService,
        private contentCatalogRestService: ContentCatalogRestService,
        private productCatalogRestService: ProductCatalogRestService,
        private storageService: IStorageService
    ) {
        super();
    }

    getContentCatalogsForSite(siteUID: string): Promise<IBaseCatalog[]> {
        return this.contentCatalogRestService
            .get({
                siteUID
            })
            .then((catalogs: IBaseCatalogs) => catalogs.catalogs);
    }

    getCatalogByVersion(siteUID: string, catalogVersionName: string): Promise<IBaseCatalog[]> {
        return this.getContentCatalogsForSite(siteUID).then((catalogs: IBaseCatalog[]) =>
            catalogs.filter((catalog) =>
                catalog.versions.some(
                    (currentCatalogVersion: IBaseCatalogVersion) =>
                        currentCatalogVersion.version === catalogVersionName
                )
            )
        );
    }

    isContentCatalogVersionNonActive(_uriContext?: IUriContext): Promise<boolean> {
        return this._getContext(_uriContext).then((uriContext: IUriContext) =>
            this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then((catalogs) => {
                const currentCatalog = catalogs.find(
                    (catalog) => catalog.catalogId === uriContext[CONTEXT_CATALOG]
                );
                const currentCatalogVersion = currentCatalog
                    ? currentCatalog.versions.find(
                          (catalogVersion) =>
                              catalogVersion.version === uriContext[CONTEXT_CATALOG_VERSION]
                      )
                    : null;

                if (!currentCatalogVersion) {
                    throw new Error(
                        `Invalid uriContext ${uriContext}, cannot find catalog version.`
                    );
                }

                return !currentCatalogVersion.active;
            })
        );
    }

    getContentCatalogActiveVersion(_uriContext?: IUriContext): Promise<string> {
        return this._getContext(_uriContext).then((uriContext: IUriContext) =>
            this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then((catalogs) => {
                const currentCatalog = catalogs.find(
                    (catalog) => catalog.catalogId === uriContext[CONTEXT_CATALOG]
                );

                const activeCatalogVersion = currentCatalog
                    ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                    : null;

                if (!activeCatalogVersion) {
                    throw new Error(
                        `Invalid uriContext ${uriContext}, cannot find catalog version.`
                    );
                }

                return activeCatalogVersion.version;
            })
        );
    }

    getActiveContentCatalogVersionByCatalogId(contentCatalogId: string): Promise<string> {
        return this._getContext().then((uriContext: IUriContext) =>
            this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then((catalogs) => {
                const currentCatalog = catalogs.find(
                    (catalog) => catalog.catalogId === contentCatalogId
                );

                const currentCatalogVersion = currentCatalog
                    ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                    : null;

                if (!currentCatalogVersion) {
                    throw new Error(
                        `Invalid content catalog ${contentCatalogId}, cannot find any active catalog version.`
                    );
                }

                return currentCatalogVersion.version;
            })
        );
    }

    getContentCatalogVersion(_uriContext?: IUriContext): Promise<IBaseCatalogVersion> {
        return this._getContext(_uriContext).then((uriContext: IUriContext) =>
            this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then((catalogs) => {
                const catalog = catalogs.find((c) => c.catalogId === uriContext[CONTEXT_CATALOG]);
                if (!catalog) {
                    throw new Error(
                        'no catalog ' +
                            uriContext[CONTEXT_CATALOG] +
                            ' found for site ' +
                            uriContext[CONTEXT_SITE_ID]
                    );
                }
                const catalogVersion = catalog.versions.find(
                    (version) => version.version === uriContext[CONTEXT_CATALOG_VERSION]
                );
                if (!catalogVersion) {
                    throw new Error(
                        `no catalogVersion ${uriContext[CONTEXT_CATALOG_VERSION]} for catalog ${uriContext[CONTEXT_CATALOG]} and site ${uriContext[CONTEXT_SITE_ID]}`
                    );
                }
                catalogVersion.catalogName = catalog.name;
                catalogVersion.catalogId = catalog.catalogId;
                return catalogVersion;
            })
        );
    }

    getCurrentSiteID(): Promise<string> {
        return this.storageService.getValueFromLocalStorage(this.SELECTED_SITE_COOKIE_NAME, false);
    }

    /**
     * Finds the ID of the default site configured for the provided content catalog.
     * @param contentCatalogId The UID of content catalog for which to retrieve its default site ID.
     * @returns The ID of the default site found.
     */
    getDefaultSiteForContentCatalog(contentCatalogId: string): Promise<ISite> {
        return (this.siteService.getSites() as unknown as Promise<ISite[]>).then((sites) => {
            const defaultSitesForCatalog = sites.filter((site) => {
                // ContentCatalogs in the site object are sorted. The last one is considered
                // the default one for a given site.
                const siteDefaultContentCatalog = lodash.last(site.contentCatalogs);
                return siteDefaultContentCatalog && siteDefaultContentCatalog === contentCatalogId;
            });

            if (defaultSitesForCatalog.length === 0) {
                this.logService.warn(
                    `[catalogService] - No default site found for content catalog ${contentCatalogId}`
                );
            } else if (defaultSitesForCatalog.length > 1) {
                this.logService.warn(
                    `[catalogService] - Many default sites found for content catalog ${contentCatalogId}`
                );
            }

            return defaultSitesForCatalog[0];
        });
    }

    /*
     *  Only the '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs' API can get data about the catalogVersions.
     * For performance, start with the current site. If current site does not have catalogVersionUuid, loop through all
     * sites.
     * */
    async getCatalogVersionByUuid(
        catalogVersionUuid: string,
        siteId?: string
    ): Promise<ICatalogVersion> {
        const contentCatalogsGrouped = await this.getContentCatalogsGrouped(
            catalogVersionUuid,
            siteId
        );
        const catalogs = lodash.reduce(
            contentCatalogsGrouped,
            (allCatalogs, siteCatalogs) => allCatalogs.concat(siteCatalogs),
            []
        );

        const catalogVersionFound = lodash
            .flatten(
                catalogs.map((catalog) =>
                    lodash.cloneDeep(catalog.versions).map((version: ICatalogVersion) => {
                        version.catalogName = catalog.name;
                        version.catalogId = catalog.catalogId;
                        return version;
                    })
                )
            )
            .filter(
                (version) =>
                    catalogVersionUuid === version.uuid &&
                    (!siteId || siteId === version.siteDescriptor.uid)
            )[0];

        if (!catalogVersionFound) {
            const errorMessage =
                'Cannot find catalog version with UUID ' +
                catalogVersionUuid +
                (siteId ? ' in site ' + siteId : '');
            throw new Error(errorMessage);
        }

        return this.getCurrentSiteID().then((defaultSiteID) => {
            catalogVersionFound.siteId = defaultSiteID;
            return catalogVersionFound;
        });
    }

    getAllContentCatalogsGroupedById(): Promise<ICatalog[][]> {
        return (this.siteService.getSites() as unknown as Promise<ISite[]>).then(
            (sites: ISite[]) => {
                const promisesToResolve = sites.map((site: ISite) =>
                    this.getContentCatalogsForSite(site.uid).then((catalogs: ICatalog[]) => {
                        catalogs.forEach((catalog) => {
                            catalog.versions = catalog.versions.map(
                                (catalogVersion: ICatalogVersion) => {
                                    catalogVersion.siteDescriptor = site;
                                    return catalogVersion;
                                }
                            );
                        });

                        return catalogs;
                    })
                );

                return Promise.all(promisesToResolve);
            }
        );
    }

    getProductCatalogsBySiteKey(siteUIDKey: string): Promise<IBaseCatalog[]> {
        return this._getContext().then((uriContext: IUriContext) =>
            this.getProductCatalogsForSite(uriContext[siteUIDKey])
        );
    }

    /* =====================================================================================================================
      * `getProductCatalogsBySite` is to get product catalogs by site value
      * `siteUIDValue` - is the site value rather than site key
      * if you want to get product catalogs by site key, please refer to function `getProductCatalogsBySiteKey`
       =====================================================================================================================
    */
    // eslint-disable-next-line @typescript-eslint/member-ordering
    @Cached({ actions: [rarelyChangingContent], tags: [catalogEvictionTag] })
    getProductCatalogsForSite(siteUIDValue: string): Promise<IBaseCatalog[]> {
        return this.productCatalogRestService
            .get({
                siteUID: siteUIDValue
            })
            .then((catalogs: IBaseCatalogs) => catalogs.catalogs);
    }

    getActiveProductCatalogVersionByCatalogId(productCatalogId: string): Promise<string> {
        return this.getProductCatalogsBySiteKey(CONTEXT_SITE_ID).then((catalogs) => {
            const currentCatalog = catalogs.find(
                (catalog) => catalog.catalogId === productCatalogId
            );

            const currentCatalogVersion = currentCatalog
                ? currentCatalog.versions.find((catalogVersion) => catalogVersion.active)
                : null;

            if (!currentCatalogVersion) {
                throw new Error(
                    `Invalid product catalog ${productCatalogId}, cannot find any active catalog version.`
                );
            }

            return currentCatalogVersion.version;
        });
    }

    // =====================================================================================================================
    //  Helper Methods
    // =====================================================================================================================

    getCatalogVersionUUid(_uriContext?: IUriContext): Promise<string> {
        return this.getContentCatalogVersion(_uriContext).then(
            (catalogVersion) => catalogVersion.uuid
        );
    }

    retrieveUriContext(_uriContext?: IUriContext): Promise<IUriContext> {
        return this._getContext(_uriContext);
    }

    returnActiveCatalogVersionUIDs(catalogs: ICatalog[]): string[] {
        return catalogs.reduce((accumulator, catalog) => {
            accumulator.push(catalog.versions.find((version) => version.active).uuid);
            return accumulator;
        }, []);
    }

    // eslint-disable-next-line @typescript-eslint/member-ordering
    @Cached({ actions: [rarelyChangingContent], tags: [pageChangeEvictionTag] })
    isCurrentCatalogMultiCountry(): Promise<boolean> {
        return this.sharedDataService.get(EXPERIENCE_STORAGE_KEY).then((experience: any) => {
            if (experience && experience.siteDescriptor && experience.catalogDescriptor) {
                const siteId = experience.siteDescriptor.uid;
                const catalogId = experience.catalogDescriptor.catalogId;
                return this.getContentCatalogsForSite(siteId).then((catalogs) => {
                    const catalog = catalogs.find(
                        (obj: IBaseCatalog) => obj.catalogId === catalogId
                    );
                    return Promise.resolve(
                        catalog && catalog.parents && catalog.parents.length ? true : false
                    );
                });
            }
            return false;
        });
    }

    private _getContext(_uriContext?: IUriContext): Promise<IUriContext> {
        // TODO: once refactored by Nick, use definition of experience
        return _uriContext
            ? Promise.resolve(_uriContext)
            : this.sharedDataService.get(EXPERIENCE_STORAGE_KEY).then((experience: any) => {
                  if (!experience) {
                      throw new Error(
                          'catalogService was not provided with a uriContext and could not retrive an experience from sharedDataService'
                      );
                  }
                  return this.urlService.buildUriContext(
                      experience.siteDescriptor.uid,
                      experience.catalogDescriptor.catalogId,
                      experience.catalogDescriptor.catalogVersion
                  );
              });
    }

    private async getContentCatalogsGrouped(
        catalogVersionUuid: string,
        siteId?: string
    ): Promise<ICatalog[][]> {
        let contentCatalogsGrouped: ICatalog[][] = [];

        const currentSiteId = await this.getCurrentSiteID();
        if (siteId !== undefined || currentSiteId !== undefined) {
            const siteCatalogs = await this.getContentCatalogsForSite(
                siteId !== undefined ? siteId : currentSiteId
            );
            const site = await this.siteService.getSiteById(
                siteId !== undefined ? siteId : currentSiteId
            );
            let existed = false;
            siteCatalogs.forEach((catalog) => {
                catalog.versions.forEach((version) => {
                    if (version.uuid === catalogVersionUuid) {
                        version.siteDescriptor = site;
                        existed = true;
                    }
                });
            });
            if (existed) {
                contentCatalogsGrouped.push(siteCatalogs);
            }
        }

        if (contentCatalogsGrouped.length === 0) {
            contentCatalogsGrouped = await this.getAllContentCatalogsGroupedById();
        }

        return contentCatalogsGrouped;
    }
}
