import { IEventService } from '../../interfaces';
import { FunctionsUtils, StringUtils } from '../../utils';
import { LogService } from '../log.service';
import { CacheAction } from './cache-action';
import { CacheEngine, ICacheTiming, IMetadata } from './engine';
import { EvictionTag } from './eviction-tag';
export declare type ICachePredicate = (cacheActions: CacheAction[]) => boolean;
/**
 * @ngdoc service
 * @name @smartutils.services:CacheService
 * @description
 * Service to which the {@link @smartutils.object:@Cached @Cached} and {@link @smartutils.object:@InvalidateCache @InvalidateCache} annotations delegate to perform service method level caching.
 * It is not handled explicitly except for its evict method.
 */
export declare class CacheService {
    private logService;
    private stringUtils;
    private functionsUtils;
    private eventService;
    private cacheEngine;
    private predicatesRegistry;
    private eventListeners;
    private defaultCacheTiming;
    constructor(logService: LogService, stringUtils: StringUtils, functionsUtils: FunctionsUtils, eventService: IEventService, cacheEngine: CacheEngine);
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
    register(test: ICachePredicate, cacheTiming: ICacheTiming): CacheService;
    /**
     * public method but only meant to be used by @Cache annotation
     */
    handle<T extends IMetadata>(service: any, methodName: string, preboundMethod: (...args: any[]) => Promise<T>, invocationArguments: any[], cacheActions: CacheAction[], tags: EvictionTag[]): Promise<T>;
    /**
     * @ngdoc method
     * @name @smartutils.services:CacheService#evict
     * @methodOf  @smartutils.services:CacheService
     * @description
     * Will evict the entire cache of all methods of all services referencing either directly or indirectly the given {@link @smartutils.object:EvictionTag EvictionTags}
     * @param {...EvictionTag[]} evictionTags the {@link @smartutils.object:EvictionTag EvictionTags}
     */
    evict(...evictionTags: EvictionTag[]): void;
    protected listenForEvictionByTags(tags: EvictionTag[]): void;
    protected collectEventNamesFromTags(tags: EvictionTag[]): string[];
    protected collectEventNamesFromTag(tag: EvictionTag): string[];
    protected findCacheTimingByCacheActions(cacheActions: CacheAction[]): ICacheTiming | null;
}
