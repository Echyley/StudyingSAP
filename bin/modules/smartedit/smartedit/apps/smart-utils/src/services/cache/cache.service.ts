/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Inject, Injectable } from '@angular/core';
import * as lodash from 'lodash';
import { EVENT_SERVICE } from '../../constants';
import { Cloneable } from '../../dtos';
import { IEventService } from '../../interfaces';
import { FunctionsUtils, StringUtils } from '../../utils';
import { LogService } from '../log.service';
import { CacheAction } from './cache-action';
import { CacheEngine, DefaultCacheTiming, ICacheItem, ICacheTiming, IMetadata } from './engine';
import { EvictionTag } from './eviction-tag';

export type ICachePredicate = (cacheActions: CacheAction[]) => boolean;

/** @internal */
interface IPredicateRegistry {
    test: ICachePredicate;
    cacheTiming: ICacheTiming;
}

/**
 * @ngdoc service
 * @name @smartutils.services:CacheService
 * @description
 * Service to which the {@link @smartutils.object:@Cached @Cached} and {@link @smartutils.object:@InvalidateCache @InvalidateCache} annotations delegate to perform service method level caching.
 * It is not handled explicitly except for its evict method.
 */
@Injectable()
export class CacheService {
    private predicatesRegistry: IPredicateRegistry[] = [];
    private eventListeners: string[] = [];

    private defaultCacheTiming = new DefaultCacheTiming(24 * 60 * 60 * 1000, 12 * 60 * 60 * 1000);

    constructor(
        private logService: LogService,
        private stringUtils: StringUtils,
        private functionsUtils: FunctionsUtils,
        @Inject(EVENT_SERVICE) private eventService: IEventService,
        private cacheEngine: CacheEngine
    ) {}

    /**
     * @ngdoc method
     * @name @smartutils.services:CacheService#register
     * @methodOf @smartutils.services:CacheService
     *
     * @description
     * Register a new predicate with it's associated cacheTiming.
     * Each time the @Cache annotation is handled, the CacheService try to find a matching cacheTiming for the given cacheActions.
     *
     * @param {ICachePredicate} test This function takes the cacheActions {@link @smartutils.object:CacheAction CacheAction} argument, and must return a Boolean that is true if the given cacheActions match the predicate.
     * @param {ICacheTiming} cacheTiming This function is used to call setAge(item: ICacheItem<any>) on the cached item.
     *
     * @return {CacheService} CacheService The CacheService instance.
     *
     * @example
     * ```ts
     * export class CustomCacheTiming implements ICacheTiming {
     * 	private expirationAge: number;
     * 	private refreshAge: number;
     *  constructor(expirationAge: number, refreshAge: number) {
     * 		// The cached response is discarded if it is older than the expiration age.
     * 		this.expirationAge = expirationAge;
     * 		// maximum age for the cached response to be considered "fresh."
     * 		this.refreshAge = refreshAge;
     * 	}
     * 	setAge(item: ICacheItem<any>): void {
     * 		item.expirationAge = this.expirationAge;
     * 		item.refreshAge = this.refreshAge;
     * 	}
     * 	};
     * 	const customCacheTiming = new CustomCacheTiming(30 * 60000, 15 * 60000);
     * 	const customContentPredicate: ICachePredicate = (cacheActions: CacheAction[]) => {
     * 		return cacheActions.find((cacheAction) => cacheAction.name === 'CUSTOM_TAG') !== null;
     * 	};
     * this.register(customContentPredicate, customCacheTiming);
     * ```
     */
    public register(test: ICachePredicate, cacheTiming: ICacheTiming): CacheService {
        this.predicatesRegistry.unshift({
            test,
            cacheTiming
        });
        return this;
    }

    /**
     * public method but only meant to be used by @Cache annotation
     */
    public handle<T extends IMetadata>(
        service: any,
        methodName: string,
        preboundMethod: (...args: any[]) => Promise<T>,
        invocationArguments: any[],
        cacheActions: CacheAction[],
        tags: EvictionTag[]
    ): Promise<T> {
        const constructorName = this.functionsUtils.getInstanceConstructorName(service);
        const cachedItemId: string =
            window.btoa(constructorName + methodName) +
            this.stringUtils.encode(invocationArguments);

        const _item: ICacheItem<any> | null = this.cacheEngine.getItemById(cachedItemId);
        let item: ICacheItem<any>;

        if (!_item) {
            const partialItem: Pick<
                ICacheItem<any>,
                'id' | 'timestamp' | 'evictionTags' | 'cache'
            > = _item || {
                id: cachedItemId,
                timestamp: new Date().getTime(),
                evictionTags: this.collectEventNamesFromTags(tags),
                cache: null
            };

            const cacheTiming: ICacheTiming | null =
                this.findCacheTimingByCacheActions(cacheActions);
            if (!cacheTiming) {
                throw new Error('CacheService::handle - No predicate match.');
            }
            item = cacheTiming.setAge(partialItem);

            this.cacheEngine.addItem(
                item,
                cacheTiming,
                preboundMethod.bind(
                    undefined,
                    ...Array.prototype.slice.call(invocationArguments)
                ) as <G>() => Promise<G>
            );

            this.listenForEvictionByTags(tags);
        } else {
            item = _item;
        }

        return this.cacheEngine.handle(item);
    }

    /**
     * @ngdoc method
     * @name @smartutils.services:CacheService#evict
     * @methodOf  @smartutils.services:CacheService
     * @description
     * Will evict the entire cache of all methods of all services referencing either directly or indirectly the given {@link @smartutils.object:EvictionTag EvictionTags}
     * @param {...EvictionTag[]} evictionTags the {@link @smartutils.object:EvictionTag EvictionTags}
     */
    public evict(...evictionTags: EvictionTag[]): void {
        const tags: string[] = this.collectEventNamesFromTags(evictionTags);
        this.cacheEngine.evict(...tags);
        tags.forEach((tag) => this.eventService.publish(tag));
    }

    protected listenForEvictionByTags(tags: EvictionTag[]): void {
        this.collectEventNamesFromTags(tags)
            .filter((eventId) => this.eventListeners.indexOf(eventId) === -1)
            .forEach((eventId) => {
                this.logService.debug(`registering event listener ${eventId}`);
                this.eventListeners.push(eventId);
                this.eventService.subscribe(eventId, (evt: string, data?: Cloneable) => {
                    this.logService.debug(`cleaning cache on event ${eventId}`);
                    this.cacheEngine.evict(eventId);
                    return Promise.resolve<Cloneable>({});
                });
            });
    }

    protected collectEventNamesFromTags(tags: EvictionTag[]): string[] {
        if (tags && tags.length) {
            return lodash.union(...tags.map((t) => this.collectEventNamesFromTag(t)));
        } else {
            return [];
        }
    }

    protected collectEventNamesFromTag(tag: EvictionTag): string[] {
        return lodash.union(
            [tag.event],
            ...(tag.relatedTags ? tag.relatedTags.map((t) => this.collectEventNamesFromTag(t)) : [])
        );
    }

    protected findCacheTimingByCacheActions(cacheActions: CacheAction[]): ICacheTiming | null {
        const predicate: IPredicateRegistry | undefined = this.predicatesRegistry.find(
            (cacheTimingPredicate) => cacheTimingPredicate.test(cacheActions)
        );
        return predicate ? predicate.cacheTiming : this.defaultCacheTiming;
    }
}
