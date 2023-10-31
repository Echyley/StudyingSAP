import { RestServiceFactory } from '@smart/utils';
import { IPermissionsRestServiceQueryData, IPermissionsRestServiceResult } from 'smarteditcommons/dtos';
export declare class PermissionsRestService {
    private readonly URI;
    private readonly resource;
    constructor(restServiceFactory: RestServiceFactory);
    get(queryData: IPermissionsRestServiceQueryData): Promise<IPermissionsRestServiceResult>;
}
