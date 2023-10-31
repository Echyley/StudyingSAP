/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    CatalogDetailsItem,
    CATALOG_DETAILS_COLUMNS,
    ICatalogDetailsService
} from 'smarteditcommons';
import { HomePageLinkComponent } from '../components/HomePageLinkComponent';

/**
 * The catalog details Service makes it possible to add items in form of directive
 * to the catalog details directive
 *
 */
export class CatalogDetailsService implements ICatalogDetailsService {
    private _customItems: { left: CatalogDetailsItem[]; right: CatalogDetailsItem[] } = {
        left: [
            {
                component: HomePageLinkComponent
            }
        ],
        right: []
    };

    /**
     * This method allows to add a new item/items to the template array.
     *
     * @param  items An array that hold a list of items.
     * @param  column The place where the template will be added to. If this value is empty
     * the template will be added to the left side by default. The available places are defined in the
     * constant CATALOG_DETAILS_COLUMNS
     */
    public addItems(items: CatalogDetailsItem[], column?: string): void {
        if (column === CATALOG_DETAILS_COLUMNS.RIGHT) {
            this._customItems.right = this._customItems.right.concat(items);
        } else {
            this._customItems.left = this._customItems.left.splice(0);
            this._customItems.left.splice(this._customItems.left.length - 1, 0, ...items);
        }
    }

    /**
     * This retrieves the list of items currently extending catalog version details components.
     */
    public getItems(): { left: CatalogDetailsItem[]; right: CatalogDetailsItem[] } {
        return this._customItems;
    }
}
