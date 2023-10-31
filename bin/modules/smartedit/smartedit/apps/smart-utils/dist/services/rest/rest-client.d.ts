/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Page, Pageable, Payload } from '../../dtos';
import { IRestOptions, IRestService, SearchParams } from './i-rest-service';
export declare type ObjectWithHeaders<T> = T & {
    headers?: HttpHeaders;
};
/** @internal */
export declare class RestClient<T> implements IRestService<T> {
    private httpClient;
    readonly url: string;
    private identifierName;
    private readonly DEFAULT_HEADERS;
    private readonly DEFAULT_OPTIONS;
    private metadataActivated;
    constructor(httpClient: HttpClient, url: string, identifierName: string);
    getById<S extends T = T>(identifier: string, options?: IRestOptions): Promise<ObjectWithHeaders<S> | null>;
    get<S extends T = T>(searchParams: SearchParams, options?: IRestOptions): Promise<ObjectWithHeaders<S> | null>;
    query<S extends T = T>(searchParams?: SearchParams, options?: IRestOptions): Promise<ObjectWithHeaders<S[]>>;
    page<S extends Page<T>>(pageable: Pageable, options?: IRestOptions): Promise<ObjectWithHeaders<S>>;
    update<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    save<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    patch<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    remove(payload: string | Payload, options?: IRestOptions): Promise<any>;
    queryByPost<S extends T = T>(payload: Payload, searchParams: SearchParams, options?: IRestOptions): Promise<ObjectWithHeaders<S> | null>;
    getMethodForSingleInstance: (name: 'getById' | 'get' | 'update' | 'save' | 'remove' | 'patch') => (params: any) => Promise<any>;
    getMethodForArray: (name: 'query') => (params: SearchParams) => Promise<T[] | null>;
    activateMetadata(): void;
    private convertToTypeMapOfString;
    private formatQueryString;
    private addHeadersToBody;
    private interpolateParamsInURL;
    private determineTrueQueryStringParams;
    private sortByKeys;
    private performIdentifierCheck;
    private buildUrlWithIdentifier;
    private buildRequestHeaders;
}
