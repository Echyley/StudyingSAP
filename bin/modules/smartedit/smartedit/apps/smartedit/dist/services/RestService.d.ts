import { IRestOptions, IRestService, Page, Pageable, Payload, SearchParams } from 'smarteditcommons';
import { DelegateRestService } from './DelegateRestServiceInner';
/** @internal */
export declare class RestService<T> implements IRestService<T> {
    private delegateRestService;
    private uri;
    private identifier;
    private metadataActivated;
    constructor(delegateRestService: DelegateRestService, uri: string, identifier: string);
    getById<S extends T = T>(id: string, options?: IRestOptions): Promise<S>;
    get<S extends T = T>(searchParams: Payload, options?: IRestOptions): Promise<S>;
    update<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    patch<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    save<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    query<S extends T = T>(searchParams: Payload, options?: IRestOptions): Promise<S[]>;
    page<S extends Page<T>>(pageable: Pageable, options?: IRestOptions): Promise<S>;
    remove(payload: Payload, options?: IRestOptions): Promise<void>;
    queryByPost<S extends T = T>(payload: Payload, searchParams?: SearchParams, options?: IRestOptions): Promise<S>;
    activateMetadata(): void;
}
