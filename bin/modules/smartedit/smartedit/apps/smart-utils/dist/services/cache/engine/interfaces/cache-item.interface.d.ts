/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Cloneable } from '../../../../dtos';
export interface ICacheItem<T extends Cloneable> {
    cache: T;
    expirationAge: number;
    evictionTags: string[];
    id: string;
    refreshAge: number;
    timestamp: number;
}
