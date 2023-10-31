import { ClassAnnotationFactory, MethodAnnotationFactory } from '../annotation.service';
import { LogService } from '../log.service';
import { CacheAction } from './cache-action';
import { CacheService } from './cache.service';
import { EvictionTag } from './eviction-tag';
/**
 * @ngdoc object
 * @name @smartutils.object:@CacheConfig
 * @description
 * Class level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for setting
 *  class level cache configuration to be merged into method specific {@link @smartutils.object:@Cached @Cached} and
 *  {@link @smartutils.object:@InvalidateCache @InvalidateCache} configurations.
 * @param {object} cacheConfig the configuration fo this cache
 * @param {cacheAction} cacheConfig.actions the list of {@link @smartutils.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link @smartutils.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
export declare const CacheConfig: (args: {
    actions?: CacheAction[];
    tags?: EvictionTag[];
}) => ClassDecorator;
export declare function CacheConfigAnnotationFactory(logService: LogService): ClassAnnotationFactory;
/**
 * @ngdoc object
 * @name @smartutils.object:@Cached
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for performing
 * invocation arguments sensitive method caching.
 * <br/> This annotation must only be used on methods returning promises.
 * @param {object} cacheConfig the configuration fo this cache
 * <br/> This configuration will be merged with a class level {@link @smartutils.object:@CacheConfig @acheConfig} if any.
 * @throws if no {@link @smartutils.object:CacheAction CacheAction} is found in the resulting merge
 * @param {cacheAction} cacheConfig.actions the list of {@link @smartutils.object:CacheAction CacheAction} characterizing this cache.
 * @param {EvictionTag[]} cacheConfig.tags a list of {@link @smartutils.object:EvictionTag EvictionTag} to control the eviction behaviour of this cache.
 */
export declare const Cached: (args?: {
    actions: CacheAction[];
    tags?: EvictionTag[];
}) => MethodDecorator;
export declare function CachedAnnotationFactory(cacheService: CacheService): MethodAnnotationFactory;
/**
 * @ngdoc object
 * @name @smartutils.object:@InvalidateCache
 * @description
 * Method level typescript {@link http://www.typescriptlang.org/docs/handbook/decorators.html decorator factory} responsible for
 * invalidating all caches either directly or indirectly declaring the {@link @smartutils.object:EvictionTag eviction tag} passed as argument.
 * if no eviction tag is passed as argument, defaults to the optional eviction tags passed to the class through {@link @smartutils.object:@CacheConfig @CacheConfig}.
 *
 * @param {EvictionTag} evictionTag the {@link @smartutils.object:EvictionTag eviction tag}.
 */
export declare const InvalidateCache: (tag?: EvictionTag) => any;
export declare function InvalidateCacheAnnotationFactory(cacheService: CacheService): MethodAnnotationFactory;
