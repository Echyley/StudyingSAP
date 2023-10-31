import { ICatalogService, ISharedDataService } from 'smarteditcommons';
/** Used to determine an URI for fetching the data for Dropdown Populators. */
export declare class ContextAwareCatalogService {
    private catalogService;
    private sharedDataService;
    constructor(catalogService: ICatalogService, sharedDataService: ISharedDataService);
    getProductCategorySearchUri(productCatalogId: string): Promise<string>;
    getProductCategoryItemUri(): Promise<string>;
    getProductSearchUri(productCatalogId: string): Promise<string>;
    getProductItemUri(): Promise<string>;
    getContentPageSearchUri(): Promise<string>;
    getContentPageItemUri(): Promise<string>;
    private getSearchUriByProductCatalogIdAndUriConstant;
    private getItemUriByUriConstant;
    private getContentPageUri;
}
