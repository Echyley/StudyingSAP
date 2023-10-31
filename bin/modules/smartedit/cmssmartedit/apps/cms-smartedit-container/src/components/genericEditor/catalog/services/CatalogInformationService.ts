/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { values } from 'lodash';
import {
    ICatalogService,
    IdWithLabel,
    IExperience,
    ISharedDataService,
    Page,
    TypedMap,
    ProductSelectItem,
    IBaseCatalog,
    SelectItem,
    EXPERIENCE_STORAGE_KEY
} from 'smarteditcommons';
import { ProductService, IProductSearch } from 'smarteditcontainer';
import {
    ProductCategorySelectItem,
    ProductCategoryService
} from '../../../../services/ProductCategoryService';

export interface ProductCatalog {
    id: string;
    name: TypedMap<string>;
    versions: IdWithLabel[];
}

export type ProductsFetchStrategy = CatalogFetchStrategy<ProductSelectItem>;

export type CategoriesFetchStrategy = CatalogFetchStrategy<ProductCategorySelectItem>;
export interface CatalogFetchStrategy<T extends SelectItem = SelectItem> {
    fetchPage: (
        catalogInfo: IProductSearch,
        mask: string,
        pageSize: number,
        currentPage: number
    ) => Promise<Page<T>>;
    fetchEntity: (uid: string) => Promise<T>;
}

/**
 * Supplies Fetch Strategies for Product and Category Selectors.
 * These strategies are used to fetch items in a paged way or to fetch an individual item.
 * Ultimately, the strategies are utilized by Select Component.
 */
@Injectable()
export class CatalogInformationService {
    private _productsFetchStrategy: ProductsFetchStrategy;
    private _categoriesFetchStrategy: CategoriesFetchStrategy;

    private cachedSiteUid: string;
    private parsedCatalogs: ProductCatalog[];

    constructor(
        private catalogService: ICatalogService,
        private sharedDataService: ISharedDataService,
        private productCategoryService: ProductCategoryService,
        private productService: ProductService
    ) {
        this.init();
    }

    /**
     * Strategy necessary to display products in a paged way.
     * It contains a method to retrieve pages of products and another method to retrieve individual products.
     * Such strategy is necessary to work with products in SelectComponent.
     */
    get productsFetchStrategy(): ProductsFetchStrategy {
        return this._productsFetchStrategy;
    }

    /**
     * Strategy necessary to display categories in a paged way.
     * It contains a method to retrieve pages of categories and another method to retrieve individual categories.
     * Such strategy is necessary to work with categories in a SelectComponent.
     */
    get categoriesFetchStrategy(): CategoriesFetchStrategy {
        return this._categoriesFetchStrategy;
    }

    public makeGetProductCatalogsInformation(): () => Promise<ProductCatalog[]> {
        return (): Promise<ProductCatalog[]> => this.getProductCatalogsInformation();
    }

    /**
     *
     * Retrieves the information of the product catalogs available in the current site.
     *
     * @returns A promise that resolves to an array containing the information of all the product catalogs available in the current site.
     */
    public async getProductCatalogsInformation(): Promise<ProductCatalog[]> {
        const siteUid = await this.getSiteUid();
        if (this.cachedSiteUid === siteUid && this.parsedCatalogs) {
            // Return the cached catalogs only if the site hasn't changed
            // otherwise it's necessary to reload them.
            return this.parsedCatalogs;
        } else {
            this.cachedSiteUid = siteUid;

            const catalogs = await this.catalogService.getProductCatalogsForSite(siteUid);

            const productCatalogById = this.catalogsToProductCatalogById(catalogs);
            this.parsedCatalogs = values(productCatalogById);

            return this.parsedCatalogs;
        }
    }

    private init(): void {
        this._productsFetchStrategy = {
            fetchPage: async (
                catalogInfo: IProductSearch,
                mask: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<ProductSelectItem>> =>
                this.productsFetchPage(catalogInfo, mask, pageSize, currentPage),
            fetchEntity: async (productUID: string): Promise<ProductSelectItem> =>
                this.productsFetchEntity(productUID)
        };

        this._categoriesFetchStrategy = {
            fetchPage: async (
                catalogInfo: IProductSearch,
                mask: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<ProductCategorySelectItem>> =>
                this.categoriesFetchPage(catalogInfo, mask, pageSize, currentPage),
            fetchEntity: async (categoryUID: string): Promise<ProductCategorySelectItem> =>
                this.categoriesFetchEntity(categoryUID)
        };
    }

    private async productsFetchPage(
        catalogInfo: IProductSearch,
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<ProductSelectItem>> {
        const siteUid = await this.getSiteUid();
        catalogInfo.siteUID = siteUid;

        const { products, pagination } = await this.productService.findProducts(catalogInfo, {
            mask,
            pageSize,
            currentPage
        });

        const items: ProductSelectItem[] = products.map((product) => ({
            ...product,
            id: product.uid
        }));
        return {
            pagination,
            results: items
        };
    }

    private async productsFetchEntity(productUid: string): Promise<ProductSelectItem> {
        const siteUid = await this.getSiteUid();
        const product = await this.productService.getProductById(siteUid, productUid);

        return {
            ...product,
            id: product.uid
        };
    }

    private async categoriesFetchPage(
        catalogInfo: IProductSearch,
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<ProductCategorySelectItem>> {
        const siteUid = await this.getSiteUid();
        catalogInfo.siteUID = siteUid;

        const { productCategories, pagination } = await this.productCategoryService.getCategories({
            catalogId: catalogInfo.catalogId,
            catalogVersion: catalogInfo.catalogVersion,
            siteUID: catalogInfo.siteUID,
            mask,
            pageSize,
            currentPage
        });

        const items: ProductCategorySelectItem[] = productCategories.map((category) => ({
            ...category,
            id: category.uid
        }));
        return {
            pagination,
            results: items
        };
    }
    private async categoriesFetchEntity(categoryUID: string): Promise<ProductCategorySelectItem> {
        const siteUid = await this.getSiteUid();
        const category = await this.productCategoryService.getCategoryById(siteUid, categoryUID);
        category.id = category.uid;

        return category;
    }

    private async getSiteUid(): Promise<string> {
        return this.sharedDataService
            .get(EXPERIENCE_STORAGE_KEY)
            .then(({ siteDescriptor: { uid } }: IExperience) => uid);
    }

    private catalogsToProductCatalogById(catalogs: IBaseCatalog[]): TypedMap<ProductCatalog> {
        const productCatalogById: TypedMap<ProductCatalog> = {};
        catalogs.forEach((catalog) => {
            productCatalogById[catalog.catalogId] = {
                id: catalog.catalogId,
                name: catalog.name,
                versions: catalog.versions.map(({ version }) => ({
                    id: version,
                    label: version
                }))
            };
        });
        return productCatalogById;
    }
}
