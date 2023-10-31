/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ICacheItem, ICacheTiming } from './interfaces';
export declare class DefaultCacheTiming implements ICacheTiming {
    private expirationAge;
    private refreshAge;
    constructor(expirationAge: number, refreshAge: number);
    setAge(_item: Pick<ICacheItem<any>, 'id' | 'timestamp' | 'evictionTags' | 'cache'>): ICacheItem<any>;
}
