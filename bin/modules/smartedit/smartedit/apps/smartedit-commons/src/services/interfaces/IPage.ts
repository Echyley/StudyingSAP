/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pagination } from '@smart/utils';

export interface IPage<T> {
    [index: string]: T[] | Pagination;
    pagination: Pagination;
    response: T[];
}
