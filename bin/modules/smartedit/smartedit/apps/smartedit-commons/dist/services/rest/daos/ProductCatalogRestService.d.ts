import { AbstractCachedRestService, RestServiceFactory } from '@smart/utils';
import { IBaseCatalogs } from 'smarteditcommons/dtos/ICatalog';
export declare class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
    constructor(restServiceFactory: RestServiceFactory);
}
