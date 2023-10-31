import { IRestService, IRestServiceFactory, IUriContext } from 'smarteditcommons';
import { ISyncStatus, ISyncJob } from '../dtos';
export declare class SynchronizationResourceService {
    private restServiceFactory;
    constructor(restServiceFactory: IRestServiceFactory);
    getPageSynchronizationGetRestService(uriContext: IUriContext): IRestService<ISyncStatus>;
    getPageSynchronizationPostRestService(uriContext: IUriContext): IRestService<ISyncJob>;
}
