import { TranslateService } from '@ngx-translate/core';
import { CrossFrameEventService, IAlertService, IConfirmationModalService, ValidationError, ICMSPage } from 'smarteditcommons';
import { PageRestoredAlertService } from '../../actionableAlert';
import { GenericEditorModalService } from '../../GenericEditorModalService';
export declare class PageRestoreModalService {
    private alertService;
    private confirmationModalService;
    private genericEditorModalService;
    private crossFrameEventService;
    private pageRestoredAlertService;
    private translate;
    constructor(alertService: IAlertService, confirmationModalService: IConfirmationModalService, genericEditorModalService: GenericEditorModalService, crossFrameEventService: CrossFrameEventService, pageRestoredAlertService: PageRestoredAlertService, translate: TranslateService);
    /**
     * Handles validation errors depending of their type - show error alert for unsupported errors,
     * show confirmation message for non actionable errors, otherwise show modal with detailed errors
     */
    handleRestoreValidationErrors(pageInfo: ICMSPage, errors: ValidationError[]): void;
    private showRestoreEditor;
    private forceErrorsDisplayInEditor;
    private buildStructure;
    private getRestoreEditorStructureFields;
    private showConfirmationMessage;
    private showErrorAlert;
    private getActionableErrors;
    private getNonActionableError;
    private isSupportedError;
    private isEqualTo;
}
