import { IDropdownMenuItemData } from 'smarteditcommons';
import { CatalogNavigateToSite } from '../services/CatalogNavigateToSite';
export declare class CatalogHierarchyNodeMenuItemComponent {
    private activateSite;
    uid: string;
    name: string;
    constructor(activateSite: CatalogNavigateToSite, data: IDropdownMenuItemData);
    onSiteSelect(): void;
}
