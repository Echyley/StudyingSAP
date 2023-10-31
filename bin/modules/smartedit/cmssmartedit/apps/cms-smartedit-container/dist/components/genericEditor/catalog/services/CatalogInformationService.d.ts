import { ICatalogService, IdWithLabel, ISharedDataService, Page, TypedMap, ProductSelectItem, SelectItem } from 'smarteditcommons';
import { ProductService, IProductSearch } from 'smarteditcontainer';
import { ProductCategorySelectItem, ProductCategoryService } from '../../../../services/ProductCategoryService';
export interface ProductCatalog {
    id: string;
    name: TypedMap<string>;
    versions: IdWithLabel[];
}
export declare type ProductsFetchStrategy = CatalogFetchStrategy<ProductSelectItem>;
export declare type CategoriesFetchStrategy = CatalogFetchStrategy<ProductCategorySelectItem>;
export interface CatalogFetchStrategy<T extends SelectItem = SelectItem> {
    fetchPage: (catalogInfo: IProductSearch, mask: string, pageSize: number, currentPage: number) => Promise<Page<T>>;
    fetchEntity: (uid: string) => Promise<T>;
}
/**
 * Supplies Fetch Strategies for Product and Category Selectors.
 * These strategies are used to fetch items in a paged way or to fetch an individual item.
 * Ultimately, the strategies are utilized by Select Component.
 */
export declare class CatalogInformationService {
    private catalogService;
    private sharedDataService;
    private productCategoryService;
    private productService;
    private _productsFetchStrategy;
    private _categoriesFetchStrategy;
    private cachedSiteUid;
    private parsedCatalogs;
    constructor(catalogService: ICatalogService, sharedDataService: ISharedDataService, productCategoryService: ProductCategoryService, productService: ProductService);
    /**
     * Strategy necessary to display products in a paged way.
     * It contains a method to retrieve pages of products and another method to retrieve individual products.
     * Such strategy is necessary to work with products in SelectComponent.
     */
    get productsFetchStrategy(): ProductsFetchStrategy;
    /**
     * Strategy necessary to display categories in a paged way.
     * It contains a method to retrieve pages of categories and another method to retrieve individual categories.
     * Such strategy is necessary to work with categories in a SelectComponent.
     */
    get categoriesFetchStrategy(): CategoriesFetchStrategy;
    makeGetProductCatalogsInformation(): () => Promise<ProductCatalog[]>;
    /**
     *
     * Retrieves the information of the product catalogs available in the current site.
     *
     * @returns A promise that resolves to an array containing the information of all the product catalogs available in the current site.
     */
    getProductCatalogsInformation(): Promise<ProductCatalog[]>;
    private init;
    private productsFetchPage;
    private productsFetchEntity;
    private categoriesFetchPage;
    private categoriesFetchEntity;
    private getSiteUid;
    private catalogsToProductCatalogById;
}
