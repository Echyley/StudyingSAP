/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import * as lo from 'lodash';
import { functionsUtils } from '../../utils';
import {
    annotationService,
    ClassAnnotationFactory,
    MethodAnnotationFactory
} from '../annotation.service';
import { LogService } from '../log.service';
import { CacheAction } from './cache-action';
import { CacheService } from './cache.service';
import { IMetadata } from './engine';
import { EvictionTag } from './eviction-tag';

/// ////////////////////////////////////////////////////////////////////////////
/// ////////////////////////////// CACHE CONFIG ////////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////

const cacheConfigAnnotationName = 'CacheConfig';

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

export const CacheConfig = annotationService.getClassAnnotationFactory(
    cacheConfigAnnotationName
) as (args: { actions?: CacheAction[]; tags?: EvictionTag[] }) => ClassDecorator;

export function CacheConfigAnnotationFactory(logService: LogService): ClassAnnotationFactory {
    'ngInject';
    return annotationService.setClassAnnotationFactory(
        cacheConfigAnnotationName,
        (factoryArguments: [{ actions: CacheAction[]; tags?: EvictionTag[] }]) =>
            function (
                instance: any,
                originalConstructor: (...x: any[]) => any,
                invocationArguments: any[]
            ): any {
                instance = new (originalConstructor.bind(instance))(...invocationArguments);

                instance.cacheConfig = factoryArguments[0];

                logService.debug(
                    `adding cache config ${JSON.stringify(
                        instance.cacheConfig
                    )} to class ${functionsUtils.getInstanceConstructorName(instance)}`,
                    instance
                );

                return instance;
            }
    );
}

/// ////////////////////////////////////////////////////////////////////////////
/// ///////////////////////////////// CACHE ////////////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////

const CachedAnnotationName = 'Cached';

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

export const Cached = annotationService.getMethodAnnotationFactory(
    CachedAnnotationName
) as (args?: { actions: CacheAction[]; tags?: EvictionTag[] }) => MethodDecorator;

export function CachedAnnotationFactory(cacheService: CacheService): MethodAnnotationFactory {
    'ngInject';
    const result = annotationService.setMethodAnnotationFactory(
        CachedAnnotationName,
        (factoryArguments: [{ actions: CacheAction[]; tags: EvictionTag[] }]) =>
            function (
                target: any,
                propertyName: string,
                originalMethod: (...x: any[]) => any,
                invocationArguments: IArguments
            ): Promise<IMetadata> {
                let actions: CacheAction[] = [];
                let tags: EvictionTag[] = [];

                if (factoryArguments[0]) {
                    actions = factoryArguments[0].actions;
                    tags = factoryArguments[0].tags;
                }

                if (target.cacheConfig) {
                    if (target.cacheConfig.actions) {
                        actions = lo.uniq(actions.concat(target.cacheConfig.actions));
                    }
                    if (target.cacheConfig.tags) {
                        tags = lo.uniq(tags.concat(target.cacheConfig.tags));
                    }
                }

                if (!actions.length) {
                    const constructorName = functionsUtils.getInstanceConstructorName(target);
                    throw new Error(
                        `method ${propertyName} of ${constructorName} is @Cached annotated but no CacheAction is specified either through @Cached or through class level @CacheConfig annotation`
                    );
                }
                return cacheService.handle(
                    target,
                    propertyName,
                    originalMethod,
                    Array.prototype.slice.apply(invocationArguments),
                    actions,
                    tags
                );
            }
    );

    return result;
}
/// ////////////////////////////////////////////////////////////////////////////
/// /////////////////////////// INVALIDATE CACHE ///////////////////////////////
/// ////////////////////////////////////////////////////////////////////////////

const InvalidateCacheName = 'InvalidateCache';
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

export const InvalidateCache = function (tag?: EvictionTag): any {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return annotationService.getMethodAnnotationFactory(InvalidateCacheName)(tag);
};

export function InvalidateCacheAnnotationFactory(
    cacheService: CacheService
): MethodAnnotationFactory {
    'ngInject';
    return annotationService.setMethodAnnotationFactory(
        InvalidateCacheName,
        (factoryArguments: [EvictionTag]) =>
            function (
                target: any,
                propertyName: string,
                originalMethod: (...x: any[]) => any,
                invocationArguments: IArguments
            ): any {
                let tags: EvictionTag[] = [];

                const tag: EvictionTag = factoryArguments[0];
                if (!tag) {
                    if (target.cacheConfig && target.cacheConfig.tags) {
                        tags = target.cacheConfig.tags;
                    }
                } else {
                    tags = [tag];
                }

                if (!tags.length) {
                    throw new Error(
                        `method ${propertyName} of ${target.constructor.name} is @InvalidateCache annotated but no EvictionTag is specified either through @InvalidateCache or through class level @CacheConfig annotation`
                    );
                }

                // eslint-disable-next-line prefer-spread
                const returnedObject = originalMethod.apply(
                    undefined,
                    Array.prototype.slice.call(invocationArguments)
                );
                if (returnedObject && returnedObject.then) {
                    return returnedObject.then((value: any) => {
                        cacheService.evict(...tags);
                        return value;
                    });
                } else {
                    cacheService.evict(...tags);
                    return returnedObject;
                }
            }
    );
}
