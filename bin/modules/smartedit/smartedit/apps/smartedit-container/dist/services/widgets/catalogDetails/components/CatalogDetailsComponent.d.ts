import { OnInit } from '@angular/core';
import { ICatalog, ICatalogVersion, ModalService } from 'smarteditcommons';
import './CatalogDetailsComponent.scss';
export declare class CatalogDetailsComponent implements OnInit {
    private modalService;
    catalog: ICatalog;
    isCatalogForCurrentSite: boolean;
    siteId: string;
    activeCatalogVersion: ICatalogVersion;
    sortedCatalogVersions: ICatalogVersion[];
    collapsibleConfiguration: {
        expandedByDefault: boolean;
    };
    catalogDividerImage: string;
    constructor(modalService: ModalService);
    ngOnInit(): void;
    onOpenCatalogHierarchy(): void;
    private getSortedCatalogVersions;
}
