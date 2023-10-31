/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Primitive } from './primitive';
export interface Pageable {
    [param: string]: Primitive | Primitive[] | undefined;
    currentPage: number;
    mask?: string;
    pageSize?: number;
    sort?: string;
}
