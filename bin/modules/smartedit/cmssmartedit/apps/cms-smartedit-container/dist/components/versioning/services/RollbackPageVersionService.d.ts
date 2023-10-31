import { IAlertService, IConfirmationModalService, IExperienceService, IPageInfoService, LogService, SystemEventService, PageVersioningService, IPageVersion, PageVersionSelectionService } from 'smarteditcommons';
/**
 * This service is used to rollback a page version from the toolbar context.
 */
export declare class RollbackPageVersionService {
    private logService;
    private alertService;
    private confirmationModalService;
    private experienceService;
    private pageInfoService;
    private pageVersioningService;
    private pageVersionSelectionService;
    private systemEventService;
    constructor(logService: LogService, alertService: IAlertService, confirmationModalService: IConfirmationModalService, experienceService: IExperienceService, pageInfoService: IPageInfoService, pageVersioningService: PageVersioningService, pageVersionSelectionService: PageVersionSelectionService, systemEventService: SystemEventService);
    rollbackPageVersion(version?: IPageVersion): Promise<void>;
    private showConfirmationModal;
    private performRollback;
}
