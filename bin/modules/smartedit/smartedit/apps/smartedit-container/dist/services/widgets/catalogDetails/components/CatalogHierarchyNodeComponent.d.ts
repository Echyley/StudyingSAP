import { EventEmitter, OnChanges } from '@angular/core';
import { IBaseCatalog, IDropdownMenuItem, IParentCatalog } from 'smarteditcommons';
import { CatalogNavigateToSite } from '../services/CatalogNavigateToSite';
import './CatalogHierarchyNodeComponent.scss';
export declare class CatalogHierarchyNodeComponent implements OnChanges {
    private navigateToSite;
    index: number;
    catalog: IBaseCatalog & IParentCatalog;
    siteId: string;
    isLast: boolean;
    siteSelect: EventEmitter<void>;
    dropdownItems: IDropdownMenuItem[];
    constructor(navigateToSite: CatalogNavigateToSite);
    ngOnChanges(): void;
    onNavigateToSite(siteUid: string): void;
    get hasOneSite(): boolean;
    getDropdownItems(): IDropdownMenuItem[];
}
