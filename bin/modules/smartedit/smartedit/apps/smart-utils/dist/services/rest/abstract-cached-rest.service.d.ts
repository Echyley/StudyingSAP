/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Page, Pageable, Payload } from '../../dtos';
import { IRestOptions, IRestService, SearchParams } from './i-rest-service';
import { IRestServiceFactory } from './i-rest-service.factory';
/**
 * @ngdoc service
 * @name @smartutils.service:AbstractCachedRestService
 *
 * @description
 * Base class to implement Cache enabled {@link @smartutils.interfaces:IRestService IRestServices}.
 * <br/>Implementing classes just need declare a class level {@link @smartutils.object:@CacheConfig @CacheConfig} annotation
 * with at least one {@link @smartutils.object:CacheAction CacheAction} and one {@link @smartutils.object:EvictionTag EvictionTag}.
 * <br/>Cache policies called by the set of {@link @smartutils.object:CacheAction CacheActions} will have access to
 * REST call response headers being added to the response under "headers" property.
 * <br/>Those headers are then stripped from the response.
 *
 * <h2>Usage</h2>
 * <pre>
 * &#64;CacheConfig({actions: [rarelyChangingContent], tags: [userEvictionTag]})
 * &#64;SeInjectable()
 * export class ProductCatalogRestService extends AbstractCachedRestService<IBaseCatalogs> {
 * 	constructor(restServiceFactory: IRestServiceFactory) {
 * 		super(restServiceFactory, '/productcatalogs');
 * 	}
 * }
 * </pre>
 */
export declare abstract class AbstractCachedRestService<T> implements IRestService<T> {
    protected innerRestService: IRestService<T>;
    constructor(restServiceFactory: IRestServiceFactory, uri: string, identifier?: string);
    getById<S extends T = T>(identifier: string, options?: IRestOptions): Promise<S | null>;
    get<S extends T = T>(searchParams?: SearchParams, options?: IRestOptions): Promise<S | null>;
    query<S extends T = T>(searchParams: SearchParams, options?: IRestOptions): Promise<S[]>;
    page<S extends Page<T>>(searchParams: Pageable, options?: IRestOptions): Promise<S>;
    update<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    patch<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    remove<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<void>;
    save<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S>;
    queryByPost<S extends T = T>(payload: Payload, searchParams?: SearchParams, options?: IRestOptions): Promise<S | null>;
}
export declare function StripResponseHeaders(target: any, propertyName: string, descriptor: TypedPropertyDescriptor<(...x: any[]) => any>): any;
