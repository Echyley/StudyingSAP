import { AbstractCachedRestService, RestServiceFactory } from '@smart/utils';
import { IBaseCatalogs } from 'smarteditcommons/dtos';
export declare class ContentCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
    constructor(restServiceFactory: RestServiceFactory);
}
