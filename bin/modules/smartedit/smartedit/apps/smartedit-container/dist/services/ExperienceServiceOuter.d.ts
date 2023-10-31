import { CrossFrameEventService, ICatalogService, IDefaultExperienceParams, IExperience, IExperienceParams, IExperienceService, IPreviewService, ISharedDataService, IStorageManager, IStoragePropertiesService, LanguageService, LogService, Payload, SmarteditRoutingService } from 'smarteditcommons';
import { IframeManagerService } from './iframe/IframeManagerService';
import { SiteService } from './SiteService';
/** @internal */
export declare class ExperienceService extends IExperienceService {
    seStorageManager: IStorageManager;
    storagePropertiesService: IStoragePropertiesService;
    private logService;
    private crossFrameEventService;
    private siteService;
    private previewService;
    private catalogService;
    private languageService;
    private sharedDataService;
    private iframeManagerService;
    private routingService;
    private previousExperience;
    private experienceStorage;
    private storageLoaded$;
    constructor(seStorageManager: IStorageManager, storagePropertiesService: IStoragePropertiesService, logService: LogService, crossFrameEventService: CrossFrameEventService, siteService: SiteService, previewService: IPreviewService, catalogService: ICatalogService, languageService: LanguageService, sharedDataService: ISharedDataService, iframeManagerService: IframeManagerService, routingService: SmarteditRoutingService);
    /**
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     */
    buildAndSetExperience(params: IExperienceParams): Promise<IExperience>;
    /**
     * Used to update the page ID stored in the current experience and reloads the page to make the changes visible.
     *
     * @param newPageID the ID of the page that must be stored in the current experience.
     *
     */
    updateExperiencePageId(newPageID: string): Promise<any>;
    /**
     * Used to update the experience with the parameters provided and reloads the page to make the changes visible.
     *
     * @param params The object containing the paratements for the experience to be loaded.
     * @param params.siteId the ID of the site that must be stored in the current experience.
     * @param params.catalogId the ID of the catalog that must be stored in the current experience.
     * @param params.catalogVersion the version of the catalog that must be stored in the current experience.
     * @param params.pageId the ID of the page that must be stored in the current experience.
     *
     */
    loadExperience(params: IDefaultExperienceParams): Promise<any>;
    reloadPage(): void;
    updateExperiencePageContext(pageCatalogVersionUuid: string, pageId: string): Promise<IExperience>;
    getCurrentExperience(): Promise<IExperience>;
    setCurrentExperience(experience: IExperience): Promise<IExperience>;
    hasCatalogVersionChanged(): Promise<boolean>;
    initializeExperience(): Promise<IExperience>;
    updateExperience(newExperience?: Payload): Promise<IExperience>;
    compareWithCurrentExperience(experience: IDefaultExperienceParams): Promise<boolean>;
}
