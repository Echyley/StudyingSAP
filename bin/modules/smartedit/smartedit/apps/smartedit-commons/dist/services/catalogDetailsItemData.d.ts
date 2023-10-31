import { InjectionToken } from '@angular/core';
import { ICatalog, ICatalogVersion } from '../dtos';
export interface CatalogDetailsItemData {
    /** The ID of the site the provided catalog is associated with. */
    siteId: string;
    /**  Object representing the provided catalog */
    catalog: ICatalog;
    /** Object representing the provided catalog version. */
    catalogVersion: ICatalogVersion;
    activeCatalogVersion: ICatalogVersion;
}
export declare const CATALOG_DETAILS_ITEM_DATA: InjectionToken<CatalogDetailsItemData>;
