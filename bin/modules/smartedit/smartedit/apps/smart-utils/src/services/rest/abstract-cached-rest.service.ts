/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Page, Pageable, Payload } from '../../dtos';
import { Cached, InvalidateCache } from '../cache';
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
export abstract class AbstractCachedRestService<T> implements IRestService<T> {
    protected innerRestService: IRestService<T>;

    constructor(restServiceFactory: IRestServiceFactory, uri: string, identifier?: string) {
        this.innerRestService = restServiceFactory.get(uri, identifier);
        this.innerRestService.activateMetadata && this.innerRestService.activateMetadata();
    }

    @StripResponseHeaders
    @Cached()
    getById<S extends T = T>(identifier: string, options?: IRestOptions): Promise<S | null> {
        return this.innerRestService.getById<S>(identifier, options);
    }

    @StripResponseHeaders
    @Cached()
    get<S extends T = T>(searchParams?: SearchParams, options?: IRestOptions): Promise<S | null> {
        return this.innerRestService.get(searchParams, options);
    }

    @StripResponseHeaders
    @Cached()
    query<S extends T = T>(searchParams: SearchParams, options?: IRestOptions): Promise<S[]> {
        return this.innerRestService.query(searchParams, options);
    }

    @StripResponseHeaders
    @Cached()
    page<S extends Page<T>>(searchParams: Pageable, options?: IRestOptions): Promise<S> {
        return this.innerRestService.page(searchParams, options);
    }

    @StripResponseHeaders
    @InvalidateCache()
    update<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S> {
        return this.innerRestService.update(payload, options);
    }

    @StripResponseHeaders
    @InvalidateCache()
    patch<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S> {
        return this.innerRestService.patch(payload, options);
    }

    @InvalidateCache()
    remove<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<void> {
        return this.innerRestService.remove(payload, options);
    }

    @StripResponseHeaders
    save<S extends T = T>(payload: Payload, options?: IRestOptions): Promise<S> {
        return this.innerRestService.save(payload, options);
    }

    @StripResponseHeaders
    @Cached()
    queryByPost<S extends T = T>(
        payload: Payload,
        searchParams?: SearchParams,
        options?: IRestOptions
    ): Promise<S | null> {
        return this.innerRestService.queryByPost(payload, searchParams, options);
    }
}

export function StripResponseHeaders(
    target: any,
    propertyName: string,
    descriptor: TypedPropertyDescriptor<(...x: any[]) => any>
): any {
    const originalMethod = descriptor.value;

    if (originalMethod) {
        descriptor.value = function (...args: []): any {
            return originalMethod.apply(this, args).then((response: any) => {
                delete response.headers;
                return response;
            });
        };
    }
}
