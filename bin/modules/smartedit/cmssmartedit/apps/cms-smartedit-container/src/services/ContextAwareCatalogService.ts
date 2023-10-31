/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PAGES_LIST_RESOURCE_URI } from 'cmscommons';
import {
    ICatalogService,
    IExperience,
    ISharedDataService,
    PRODUCT_LIST_RESOURCE_API,
    PRODUCT_RESOURCE_API,
    EXPERIENCE_STORAGE_KEY
} from 'smarteditcommons';
import {
    PRODUCT_CATEGORY_RESOURCE_BASE_URI,
    PRODUCT_CATEGORY_SEARCH_RESOURCE_URI
} from './ProductCategoryService';

/** Used to determine an URI for fetching the data for Dropdown Populators. */
@Injectable()
export class ContextAwareCatalogService {
    constructor(
        private catalogService: ICatalogService,
        private sharedDataService: ISharedDataService
    ) {}

    public getProductCategorySearchUri(productCatalogId: string): Promise<string> {
        return this.getSearchUriByProductCatalogIdAndUriConstant(
            productCatalogId,
            PRODUCT_CATEGORY_SEARCH_RESOURCE_URI
        );
    }

    public getProductCategoryItemUri(): Promise<string> {
        return this.getItemUriByUriConstant(PRODUCT_CATEGORY_RESOURCE_BASE_URI);
    }

    public getProductSearchUri(productCatalogId: string): Promise<string> {
        return this.getSearchUriByProductCatalogIdAndUriConstant(
            productCatalogId,
            PRODUCT_LIST_RESOURCE_API
        );
    }

    public async getProductItemUri(): Promise<string> {
        const uri = await this.getItemUriByUriConstant(PRODUCT_RESOURCE_API);

        return uri.replace('/:productUID', '');
    }

    public async getContentPageSearchUri(): Promise<string> {
        const uri = await this.getContentPageUri();

        return `${uri}?typeCode=ContentPage`;
    }

    public getContentPageItemUri(): Promise<string> {
        return this.getContentPageUri();
    }

    private async getSearchUriByProductCatalogIdAndUriConstant(
        productCatalogId: string,
        uriConstant: string
    ): Promise<string> {
        const catalogVersion = await this.catalogService.getActiveProductCatalogVersionByCatalogId(
            productCatalogId
        );

        return uriConstant
            .replace(/:catalogId/gi, productCatalogId)
            .replace(/:catalogVersion/gi, catalogVersion);
    }

    private getItemUriByUriConstant(uriConstant: string): Promise<string> {
        return Promise.resolve(uriConstant.replace(/:siteUID/gi, 'CURRENT_CONTEXT_SITE_ID'));
    }

    private async getContentPageUri(): Promise<string> {
        const {
            catalogDescriptor: { catalogId }
        } = (await this.sharedDataService.get(EXPERIENCE_STORAGE_KEY)) as IExperience;

        const catalogVersion = await this.catalogService.getActiveContentCatalogVersionByCatalogId(
            catalogId
        );

        return PAGES_LIST_RESOURCE_URI.replace(/CURRENT_CONTEXT_CATALOG/gi, catalogId).replace(
            /CURRENT_CONTEXT_CATALOG_VERSION/gi,
            catalogVersion
        );
    }
}
