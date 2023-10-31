/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { CacheService, OperationContextService } from '../services';
export interface CoreAnnotationsHelperMocks {
    cacheService: jasmine.SpyObj<CacheService>;
    operationContextService: jasmine.SpyObj<OperationContextService>;
}
export declare class CoreAnnotationsHelper {
    initCached(): jasmine.SpyObj<CacheService>;
    initOperationContextService(): jasmine.SpyObj<OperationContextService>;
    init(): CoreAnnotationsHelperMocks;
}
export declare const coreAnnotationsHelper: CoreAnnotationsHelper;
