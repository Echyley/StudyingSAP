import { CatalogDetailsItemData, IExperienceService, IUserTrackingService } from 'smarteditcommons';
export declare class HomePageLinkComponent {
    private experienceService;
    private readonly userTrackingService;
    private data;
    constructor(experienceService: IExperienceService, userTrackingService: IUserTrackingService, data: CatalogDetailsItemData);
    onClick(): void;
}
