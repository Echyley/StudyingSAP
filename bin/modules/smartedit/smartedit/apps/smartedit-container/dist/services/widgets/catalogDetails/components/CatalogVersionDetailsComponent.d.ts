import { OnInit } from '@angular/core';
import { CatalogDetailsItem, ICatalog, ICatalogDetailsService, ICatalogVersion } from 'smarteditcommons';
export declare class CatalogVersionDetailsComponent implements OnInit {
    private catalogDetailsService;
    catalog: ICatalog;
    catalogVersion: ICatalogVersion;
    activeCatalogVersion: ICatalogVersion;
    siteId: string;
    leftItems: CatalogDetailsItem[];
    rightItems: CatalogDetailsItem[];
    constructor(catalogDetailsService: ICatalogDetailsService);
    ngOnInit(): void;
}
