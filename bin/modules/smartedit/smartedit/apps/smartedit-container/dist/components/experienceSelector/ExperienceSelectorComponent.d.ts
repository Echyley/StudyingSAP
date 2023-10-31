import { EventEmitter } from '@angular/core';
import { GenericEditorAPI, ICatalogService, IExperience, IExperienceService, IIframeClickDetectionService, IPreviewData, ISharedDataService, Payload, SystemEventService, L10nPipe, IUserTrackingService } from 'smarteditcommons';
import { IframeManagerService } from '../../services/iframe/IframeManagerService';
import { SiteService } from '../../services/SiteService';
export interface IExperienceContent extends Payload {
    activeSite: string;
    language: string;
    pageId: string;
    previewCatalog: string;
    productCatalogVersions: string[];
    time: string;
}
export declare class ExperienceSelectorComponent {
    private systemEventService;
    private siteService;
    private sharedDataService;
    private iframeClickDetectionService;
    private iframeManagerService;
    private experienceService;
    private catalogService;
    private readonly l10nPipe;
    private readonly userTrackingService;
    experience: IExperience;
    dropdownStatus: {
        isOpen: boolean;
    };
    resetExperienceSelector?: () => void;
    resetExperienceSelectorChange: EventEmitter<() => void>;
    smarteditComponentType: string;
    structureApi: string;
    contentApi: string;
    content: IExperienceContent;
    modalHeaderTitle: string;
    smarteditComponentId: string;
    isReady: boolean;
    private siteCatalogs;
    private unRegCloseExperienceFn;
    private unRegFn;
    constructor(systemEventService: SystemEventService, siteService: SiteService, sharedDataService: ISharedDataService, iframeClickDetectionService: IIframeClickDetectionService, iframeManagerService: IframeManagerService, experienceService: IExperienceService, catalogService: ICatalogService, l10nPipe: L10nPipe, userTrackingService: IUserTrackingService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    preparePayload(experienceContent: IExperienceContent): Promise<IPreviewData>;
    updateCallback(payload: IExperienceContent, response: IPreviewData): Promise<void>;
    getApi($api: GenericEditorAPI): void;
    private _getProductCatalogsByUuids;
    private resetExperienceSelectorFn;
}
