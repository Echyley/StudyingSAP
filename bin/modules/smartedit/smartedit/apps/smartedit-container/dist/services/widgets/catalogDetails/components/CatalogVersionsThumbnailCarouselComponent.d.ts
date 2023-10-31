import { ICatalog, ICatalogVersion, IExperienceService } from 'smarteditcommons';
export declare class CatalogVersionsThumbnailCarouselComponent {
    private experienceService;
    catalog: ICatalog;
    catalogVersion: ICatalogVersion;
    siteId: string;
    selectedVersion: ICatalogVersion;
    constructor(experienceService: IExperienceService);
    onClick(): void;
}
