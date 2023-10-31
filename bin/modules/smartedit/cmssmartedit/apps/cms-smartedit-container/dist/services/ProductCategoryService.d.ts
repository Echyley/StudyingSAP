import { LanguageService, Page, Pageable, RestServiceFactory, SelectItem, TypedMap } from 'smarteditcommons';
interface IProductCatalogInfo {
    catalogId: string;
    catalogVersion: string;
    siteUID: string;
}
export declare type IProductCategorySearchPayload = IProductCatalogInfo & Pageable;
export interface ProductCategoryPage extends Page<IProductCategory> {
    productCategories: IProductCategory[];
}
export declare type ProductCategorySelectItem = IProductCategory & SelectItem;
export interface IProductCategory {
    catalogId: string;
    catalogVersion: string;
    code: string;
    description: TypedMap<string>;
    id: string;
    name: TypedMap<string>;
    technicalUniqueId: string;
    uid: string;
}
export declare const PRODUCT_CATEGORY_RESOURCE_BASE_URI = "/cmssmarteditwebservices/v1/sites/:siteUID/categories";
export declare const PRODUCT_CATEGORY_RESOURCE_URI: string;
export declare const PRODUCT_CATEGORY_SEARCH_RESOURCE_URI = "/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/categories";
export declare class ProductCategoryService {
    private languageService;
    private productCategoryService;
    private productCategorySearchService;
    constructor(restServiceFactory: RestServiceFactory, languageService: LanguageService);
    getCategoryById(siteUID: string, categoryUID: string): Promise<IProductCategory>;
    getCategories(payload: IProductCategorySearchPayload): Promise<ProductCategoryPage>;
    private _validateProductCatalogInfo;
}
export {};
