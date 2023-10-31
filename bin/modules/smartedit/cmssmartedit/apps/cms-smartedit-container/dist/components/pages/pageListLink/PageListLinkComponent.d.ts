import { CatalogDetailsItemData, IUserTrackingService } from 'smarteditcommons';
export declare class PageListLinkComponent {
    catalogDetails: CatalogDetailsItemData;
    private userTrackingService;
    constructor(catalogDetails: CatalogDetailsItemData, userTrackingService: IUserTrackingService);
    getLink(): string;
    onClick(): void;
}
