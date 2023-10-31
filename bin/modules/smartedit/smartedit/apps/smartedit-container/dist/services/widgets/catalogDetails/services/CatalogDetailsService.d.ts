import { CatalogDetailsItem, ICatalogDetailsService } from 'smarteditcommons';
/**
 * The catalog details Service makes it possible to add items in form of directive
 * to the catalog details directive
 *
 */
export declare class CatalogDetailsService implements ICatalogDetailsService {
    private _customItems;
    /**
     * This method allows to add a new item/items to the template array.
     *
     * @param  items An array that hold a list of items.
     * @param  column The place where the template will be added to. If this value is empty
     * the template will be added to the left side by default. The available places are defined in the
     * constant CATALOG_DETAILS_COLUMNS
     */
    addItems(items: CatalogDetailsItem[], column?: string): void;
    /**
     * This retrieves the list of items currently extending catalog version details components.
     */
    getItems(): {
        left: CatalogDetailsItem[];
        right: CatalogDetailsItem[];
    };
}
