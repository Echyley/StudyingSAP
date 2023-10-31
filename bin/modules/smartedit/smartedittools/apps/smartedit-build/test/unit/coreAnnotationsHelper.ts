/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { Injector } from '@angular/core';
import {
    CachedAnnotationFactory,
    CacheConfigAnnotationFactory,
    CacheService,
    GatewayProxiedAnnotationFactory,
    GatewayProxy,
    InvalidateCacheAnnotationFactory,
    LogService,
    OperationContextAnnotationFactory,
    OperationContextService,
    OPERATION_CONTEXT
} from 'smarteditcommons';

interface CoreAnnotationsHelperMocks {
    cacheService: jasmine.SpyObj<CacheService>;
    gatewayProxy: jasmine.SpyObj<GatewayProxy>;
    operationContextService: jasmine.SpyObj<OperationContextService>;
}

class CoreAnnotationsHelper {
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
                invocationArguments: any
            ) => {
                return method.apply(undefined, invocationArguments);
            }
        );
        CachedAnnotationFactory(cacheService);

        InvalidateCacheAnnotationFactory(cacheService);

        return cacheService;
    }

    initGatewayProxied(): jasmine.SpyObj<any> {
        const gatewayProxy: GatewayProxy = jasmine.createSpyObj<any>('gatewayProxy', [
            'initForService'
        ]);

        GatewayProxiedAnnotationFactory(gatewayProxy);
        return gatewayProxy;
    }

    initOperationContextService(): jasmine.SpyObj<OperationContextService> {
        const injector = jasmine.createSpyObj<Injector>('Injector', ['get']);
        const operationContextService: jasmine.SpyObj<
            OperationContextService
        > = jasmine.createSpyObj<OperationContextService>('operationContextService', ['register']);

        OperationContextAnnotationFactory(injector, operationContextService, OPERATION_CONTEXT);
        return operationContextService;
    }

    init(): CoreAnnotationsHelperMocks {
        return {
            cacheService: this.initCached(),
            gatewayProxy: this.initGatewayProxied(),
            operationContextService: this.initOperationContextService()
        };
    }
}

export const coreAnnotationsHelper = new CoreAnnotationsHelper();
