import { Pagination } from '@smart/utils';
export interface IPage<T> {
    [index: string]: T[] | Pagination;
    pagination: Pagination;
    response: T[];
}
