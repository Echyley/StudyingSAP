import { IAlertService, IConfirmationModalService, IExperienceService, IPageInfoService, IPageVersion, PageVersioningService, PageVersionSelectionService } from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';
/**
 * Used to manage a page version.
 */
export declare class ManagePageVersionService {
    private alertService;
    private experienceService;
    private confirmationModalService;
    private genericEditorModalService;
    private pageInfoService;
    private pageVersioningService;
    private pageVersionSelectionService;
    constructor(alertService: IAlertService, experienceService: IExperienceService, confirmationModalService: IConfirmationModalService, genericEditorModalService: GenericEditorModalService, pageInfoService: IPageInfoService, pageVersioningService: PageVersioningService, pageVersionSelectionService: PageVersionSelectionService);
    createPageVersion(): Promise<IPageVersion>;
    editPageVersion(versionDetails: IPageVersion): Promise<IPageVersion>;
    deletePageVersion(versionId: string): Promise<void>;
    /**
     * Returns an object that contains the information to be displayed and edited in the modal.
     *
     * @param pageUuid the uuid of the page.
     * @param content the content to be populated in the editor, null for create mode.
     */
    private getComponentDataForEditor;
}
