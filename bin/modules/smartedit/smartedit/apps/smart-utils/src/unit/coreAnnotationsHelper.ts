/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { Injector } from '@angular/core';
import {
    CachedAnnotationFactory,
    CacheConfigAnnotationFactory,
    CacheService,
    InvalidateCacheAnnotationFactory,
    LogService,
    OperationContextAnnotationFactory,
    OperationContextService
} from '../services';

export interface CoreAnnotationsHelperMocks {
    cacheService: jasmine.SpyObj<CacheService>;
    operationContextService: jasmine.SpyObj<OperationContextService>;
}

export class CoreAnnotationsHelper {
    initCached(): jasmine.SpyObj<CacheService> {
        ///////////////////////////////////////////////////////

        const logService = jasmine.createSpyObj<LogService>('logService', ['debug']);

        CacheConfigAnnotationFactory(logService);

        ///////////////////////////////////////////////////////

        const cacheService: jasmine.SpyObj<CacheService> = jasmine.createSpyObj<CacheService>(
            'cacheService',
            ['handle', 'evict']
        );
        cacheService.handle.and.callFake(
            (
                target: any,
                methdName: string,
                method: (...args: any[]) => any,
                invocationArguments: IArguments[]
            ) => method.apply(undefined, Array.prototype.slice.call(invocationArguments))
        );
        CachedAnnotationFactory(cacheService);

        InvalidateCacheAnnotationFactory(cacheService);

        return cacheService;
    }

    initOperationContextService(): jasmine.SpyObj<OperationContextService> {
        const injector = jasmine.createSpyObj<Injector>('Injector', ['get']);
        const operationContextService: jasmine.SpyObj<OperationContextService> =
            jasmine.createSpyObj<OperationContextService>('operationContextService', ['register']);

        OperationContextAnnotationFactory(injector, operationContextService, {});
        return operationContextService;
    }

    init(): CoreAnnotationsHelperMocks {
        return {
            cacheService: this.initCached(),
            operationContextService: this.initOperationContextService()
        };
    }
}

export const coreAnnotationsHelper = new CoreAnnotationsHelper();
