import { InjectionToken, Type } from '@angular/core';
import { TypedMap } from '@smart/utils';
import { PagedList } from '../interfaces/PagedList';
export interface ClientPagedListCellComponentData {
    key: ClientPagedListColumnKey;
    item: ClientPagedListItem;
}
/**
 * An Injection Token used to retrieve information about "item" and "key" from within rendered component.
 */
export declare const CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN: InjectionToken<ClientPagedListCellComponentData>;
/**
 * Column descriptor.
 */
export interface ClientPagedListColumnKey {
    /**
     * The property must match at least one of the descriptors keys and will be used as the columns of the table.
     * Used for mapping {@link ClientPagedListItem} object key to the cell text.
     */
    property: string;
    /**
     * Used for the column headers title.
     */
    i18n: string;
    /**
     * A component that will be rendered for each cell of this column.
     * "item" and "key" are accessible via {@link CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN}.
     *
     * ### Example
     *
     *      \@Component({
     *          selector: 'my-client-paged-list-cell',
     *          template: `<div>{{ data.item[data.key.property] }}</div>`
     *      })
     *      export class MyClientPagedListCellComponent {
     *          constructor(@Inject(CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN), public data: ClientPagedListCellComponentData) {}
     *      }
     *
     */
    component?: Type<any>;
}
/**
 * Row item descriptor.
 * The Key corresponds to the column name where value is the text displayed in a cell.
 */
export interface ClientPagedListItem extends TypedMap<any> {
    /**
     * If provided an Icon will be displayed with a tooltip below.
     * Tooltip shows the text how many restrictions are applied for the Page.
     */
    icon?: {
        /**
         * URL of an icon
         */
        src: string;
        /**
         * Number of restrictions that are displayed in the tooltip text.
         */
        numberOfRestrictions: number;
    };
}
/**
 * Required by ClientPagedListComponent
 */
export interface ClientPagedList extends PagedList {
    items: ClientPagedListItem[];
    keys: ClientPagedListColumnKey[];
    /**
     * An array of {@link ClientPagedListItem} object keys that will determine which fields the "filterByFieldPipe"
     * will use to filter through the items.
     */
    itemFilterKeys?: string[];
    /**
     * @deprecated
     * **Deprecated since 2005, use {@link ClientPagedListColumnKey.component}.**
     *
     * An object that contains HTML renderers for specific keys property. A renderer is a function that returns a HTML string. This function has access to the current "item", "key" and the injected context(as $ctrl.injectedContext).
     */
    renderers?: TypedMap<(item: ClientPagedListItem, key: ClientPagedListColumnKey) => string>;
    /**
     * @deprecated
     * **Deprecated since 2005, use {@link ClientPagedListColumnKey.component}.**
     *
     * An object that exposes values or functions to the directive. It can be used by the custom HTML renderers to bind a function to a click event for example.
     */
    injectedContext?: TypedMap<(...args: any[]) => any>;
    /**
     * The query used to filter the list.
     */
    query: string;
}
