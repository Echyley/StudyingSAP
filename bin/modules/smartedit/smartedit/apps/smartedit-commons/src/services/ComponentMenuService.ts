/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IBaseCatalog } from '../dtos';
import {
    ICatalogService,
    IExperiencePageContext,
    IExperienceService,
    IStorageService,
    CatalogVersion
} from './interfaces';

/**
 * Provides functionality for Component Menu displayed from toolbar on Storefront.
 * For example it allows to determine Content Catalog Version based on which component within the menu are fetched.
 */
@Injectable()
export class ComponentMenuService {
    private readonly SELECTED_CATALOG_VERSION_COOKIE_NAME = 'se_catalogmenu_catalogversion_cookie';

    constructor(
        private readonly catalogService: ICatalogService,
        private readonly experienceService: IExperienceService,
        private readonly storageService: IStorageService
    ) {}

    public async hasMultipleContentCatalogs(): Promise<boolean> {
        const pageContext = await this.getPageContext();
        const contentCatalogs = await this.getContentCatalogs();
        const contentCatalog = contentCatalogs.find(
            (catalog) => catalog.catalogId === pageContext.catalogId
        );

        return !!contentCatalog.parents && contentCatalog.parents.length > 0;
    }

    /**
     * This method is used to retrieve the content catalogs of the site in the page context.
     */
    public async getContentCatalogs(): Promise<IBaseCatalog[]> {
        const pageContext = await this.getPageContext();
        return pageContext ? this.catalogService.getContentCatalogsForSite(pageContext.siteId) : [];
    }

    /**
     * Gets the list of catalog/catalog versions where components can be retrieved from for this page.
     */
    public async getValidContentCatalogVersions(): Promise<CatalogVersion[]> {
        const pageContext = await this.getPageContext();
        const contentCatalogs = await this.getContentCatalogs();
        // Return 'active' catalog versions for content catalogs, except for the
        // catalog in the current experience.
        let catalogVersions: CatalogVersion[] = [];
        const contentCatalog = contentCatalogs.find(
            (catalog) => catalog.catalogId === pageContext.catalogId
        );
        catalogVersions.push(this.getActiveOrCurrentVersionForCatalog(pageContext, contentCatalog));

        // You can add components from the online version of a parent content catalog to the staged version of a child content catalog
        const parentCatalogVersions = contentCatalog.parents.map((catalog) =>
            this.getActiveOrCurrentVersionForCatalog(pageContext, catalog)
        );
        catalogVersions = [...parentCatalogVersions, ...catalogVersions];

        return catalogVersions;
    }

    // --------------------------------------------------------------------------------------------------
    // Cookie Management Methods
    // --------------------------------------------------------------------------------------------------
    public async getInitialCatalogVersion(
        catalogVersions: CatalogVersion[]
    ): Promise<CatalogVersion> {
        const rawValue = await this.storageService.getValueFromLocalStorage(
            this.SELECTED_CATALOG_VERSION_COOKIE_NAME,
            false
        );

        const selectedCatalogVersionId = typeof rawValue === 'string' ? rawValue : null;

        const selectedCatalogVersion = catalogVersions.find(
            (catalogVersion) => catalogVersion.id === selectedCatalogVersionId
        );

        return selectedCatalogVersion
            ? selectedCatalogVersion
            : catalogVersions[catalogVersions.length - 1];
    }

    public persistCatalogVersion(catalogVersionId: string): Promise<void> {
        return this.storageService.setValueInLocalStorage(
            this.SELECTED_CATALOG_VERSION_COOKIE_NAME,
            catalogVersionId,
            false
        );
    }

    /**
     * Gets the list of catalog/catalog versions where components can be retrieved from for this page.
     */
    private getActiveOrCurrentVersionForCatalog(
        pageContext: IExperiencePageContext,
        catalog: IBaseCatalog
    ): CatalogVersion {
        const catalogVersion = catalog.versions.find((version) => {
            if (pageContext.catalogId === catalog.catalogId) {
                return pageContext.catalogVersion === version.version;
            }
            return version.active;
        });

        return {
            isCurrentCatalog: pageContext.catalogVersion === catalogVersion.version,
            catalogName: catalog.name ? catalog.name : (catalog as any).catalogName,
            catalogId: catalog.catalogId,
            catalogVersionId: catalogVersion.version,
            id: catalogVersion.uuid
        };
    }

    private async getPageContext(): Promise<IExperiencePageContext> {
        const experience = await this.experienceService.getCurrentExperience();
        return experience.pageContext;
    }
}
