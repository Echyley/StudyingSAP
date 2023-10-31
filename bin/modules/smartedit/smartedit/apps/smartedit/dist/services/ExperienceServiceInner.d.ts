import { IExperienceService, IPreviewService, LogService, SmarteditRoutingService } from 'smarteditcommons';
/** @internal */
export declare class ExperienceService extends IExperienceService {
    private routingService;
    private logService;
    private previewService;
    constructor(routingService: SmarteditRoutingService, logService: LogService, previewService: IPreviewService);
    buildRefreshedPreviewUrl(): Promise<string>;
}
