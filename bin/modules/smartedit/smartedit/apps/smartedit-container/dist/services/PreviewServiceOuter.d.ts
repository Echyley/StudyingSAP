import { IPreviewData, IPreviewService, LogService, RestServiceFactory, UrlUtils } from 'smarteditcommons';
import { LoadConfigManagerService } from './bootstrap/LoadConfigManagerService';
/** @internal */
export declare class PreviewService extends IPreviewService {
    private log;
    private loadConfigManagerService;
    private restServiceFactory;
    private previewRestService;
    private previewByticketRestService;
    private domain;
    private ticketIdIdentifier;
    constructor(log: LogService, loadConfigManagerService: LoadConfigManagerService, restServiceFactory: RestServiceFactory, urlUtils: UrlUtils);
    createPreview(previewData: IPreviewData): Promise<IPreviewData>;
    updatePreview(previewData: IPreviewData): Promise<IPreviewData>;
    getResourcePathFromPreviewUrl(previewUrl: string): Promise<string>;
    private prepareRestService;
    private validatePreviewDataAttributes;
}
