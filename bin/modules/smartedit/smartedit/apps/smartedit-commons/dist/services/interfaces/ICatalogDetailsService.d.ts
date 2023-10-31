import { Type } from '@angular/core';
export interface CatalogDetailsItem {
    component: Type<any>;
}
export declare abstract class ICatalogDetailsService {
    addItems(items: CatalogDetailsItem[], column?: string): void;
    getItems(): {
        left: CatalogDetailsItem[];
        right: CatalogDetailsItem[];
    };
}
