import { IRestService, IRestServiceFactory } from 'smarteditcommons';
import { DelegateRestService } from './DelegateRestServiceInner';
/** @internal */
export declare class RestServiceFactory implements IRestServiceFactory {
    private delegateRestService;
    constructor(delegateRestService: DelegateRestService);
    get<T>(uri: string, identifier?: string): IRestService<T>;
}
