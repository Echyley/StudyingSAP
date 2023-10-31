import { IProduct, LanguageService, Page, Pageable, RestServiceFactory } from 'smarteditcommons';
/**
 * Interface used by ProductService for product search
 */
export interface IProductSearch {
    /**
     * id of the catalog
     */
    catalogId: string;
    /**
     * version of the catalog
     */
    catalogVersion: string;
    siteUID?: string;
}
export interface ProductPage extends Page<IProduct> {
    products: IProduct[];
}
/**
 * The ProductService provides is used to access products from the product catalog
 */
export declare class ProductService {
    private restServiceFactory;
    private languageService;
    private readonly productService;
    private readonly productListService;
    constructor(restServiceFactory: RestServiceFactory, languageService: LanguageService);
    /**
     * Returns a list of Products from the catalog that match the given mask
     */
    findProducts(productSearch: IProductSearch, pageable: Pageable): Promise<ProductPage>;
    /**
     * Returns a Product that matches the given siteUID and productUID
     */
    getProductById(siteUID: string, productUID: string): Promise<IProduct>;
    private _validateProductCatalogInfo;
}
