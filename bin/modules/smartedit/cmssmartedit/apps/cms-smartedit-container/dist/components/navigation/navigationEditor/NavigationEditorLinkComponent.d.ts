import { CatalogDetailsItemData, IUserTrackingService } from 'smarteditcommons';
export declare class NavigationEditorLinkComponent {
    catalogDetails: CatalogDetailsItemData;
    private userTrackingService;
    constructor(catalogDetails: CatalogDetailsItemData, userTrackingService: IUserTrackingService);
    getLink(): string;
    onClick(): void;
}
