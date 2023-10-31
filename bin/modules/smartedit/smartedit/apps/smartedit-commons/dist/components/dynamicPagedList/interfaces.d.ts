import { Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { Page } from '../../dtos';
import { IDropdownMenuItem } from '../dropdown/dropdownMenu/IDropdownMenuItem';
import { PagedList } from '../interfaces/PagedList';
export interface DynamicPagedListApi {
    reloadItems: () => Promise<Page<any>>;
}
export declare type DynamicPagedListDropdownItem = any;
export interface DynamicPagedListColumnKey {
    /** A printable column name. */
    i18n: string;
    property: string;
    sortable: boolean;
    /**
     * A component to be rendered within the column.
     * Can be injected with {@link DATA_TABLE_COMPONENT_DATA} token which provides data about column and item.
     */
    component?: Type<any>;
}
export interface DynamicPagedListInjectedContext {
    dropdownItems?: IDropdownMenuItem[];
    onLink?: (uid: string) => void;
    permissionForDropdownItems?: string;
    uriContext?: TypedMap<string>;
}
export interface SortStatus {
    internalSortBy: string;
    reversed: boolean;
    currentPage: number;
}
/**
 * Configuration object interface for {@link DynamicPagedListComponent}.
 */
export interface DynamicPagedListConfig extends PagedList {
    /**
     * An array of object(s) that detemine the properties of each column.
     * It requires a property - unique id of the column, i18n - displayable column name and sortable - optional flag that enables column sorting.
     * The properties must match one at least one of the descriptors' keys and will be used as the columns of the table. The related i18n keys are used for the column headers' title.
     * In order for sorting to work, the REST endpoint must support for sorting of that field.
     */
    keys: DynamicPagedListColumnKey[];
    /**
     * An object containing list of query params that needed to be passed to the uri.
     */
    queryParams: TypedMap<string>;
    /**
     * The uri to fetch the list from. The REST end point represented by uri must support paging and accept parameters such as currentPage, pageSize, mask and sort for fetching and fitering paged data.
     *
     * for example: If the uri = '/someuri', then the dynamic-paged-list component will fetch paged information by making call to the API such as:
     *
     *      /someuri?currentPage=0&pageSize=10
     *
     *      /someuri?currentPage=0&pageSize=10&mask=home
     *
     *      /someuri?currentPage=0&pageSize=10&sort=name:asc
     *
     */
    uri: string;
}
