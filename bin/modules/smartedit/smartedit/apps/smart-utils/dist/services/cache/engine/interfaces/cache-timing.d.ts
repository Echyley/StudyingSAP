/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ICacheItem } from './cache-item.interface';
export interface ICacheTiming {
    setAge(item: Pick<ICacheItem<any>, 'id' | 'timestamp' | 'evictionTags' | 'cache'>): ICacheItem<any>;
}
