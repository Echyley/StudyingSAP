/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CMSItemStructure, CMSItemStructureField } from 'cmscommons';
import { difference, concat } from 'lodash';
import {
    ConfirmationModalConfig,
    CrossFrameEventService,
    EVENTS,
    EVENT_CONTENT_CATALOG_UPDATE,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    IAlertService,
    IConfirmationModalService,
    ValidationError,
    ICMSPage
} from 'smarteditcommons';
import { PageRestoredAlertService } from '../../actionableAlert';
import { GenericEditorModalComponent } from '../../components/GenericEditorModalComponent';
import { GenericEditorModalService } from '../../GenericEditorModalService';
import {
    ActionableErrorMapping,
    ACTIONABLE_ERRORS,
    ErrorMapping,
    NonActionableErrorMapping,
    NON_ACTIONABLE_ERRORS
} from './PageRestoreModalConstants';

@Injectable()
export class PageRestoreModalService {
    constructor(
        private alertService: IAlertService,
        private confirmationModalService: IConfirmationModalService,
        private genericEditorModalService: GenericEditorModalService,
        private crossFrameEventService: CrossFrameEventService,
        private pageRestoredAlertService: PageRestoredAlertService,
        private translate: TranslateService
    ) {}

    /**
     * Handles validation errors depending of their type - show error alert for unsupported errors,
     * show confirmation message for non actionable errors, otherwise show modal with detailed errors
     */
    public handleRestoreValidationErrors(pageInfo: ICMSPage, errors: ValidationError[]): void {
        const actionableErrors: ValidationError[] = this.getActionableErrors(errors);
        const nonActionableErrors: ValidationError[] = this.getNonActionableError(errors);
        const unsupportedErrors: ValidationError[] = difference(
            errors,
            actionableErrors,
            nonActionableErrors
        );

        if (unsupportedErrors.length > 0) {
            this.showErrorAlert(unsupportedErrors);
        } else if (nonActionableErrors.length > 0) {
            this.showConfirmationMessage(nonActionableErrors);
        } else {
            this.showRestoreEditor(pageInfo, actionableErrors).then(() => {
                this.crossFrameEventService.publish(EVENT_CONTENT_CATALOG_UPDATE);
            });
        }
    }

    private showRestoreEditor(pageInfo: ICMSPage, errors: ValidationError[]): Promise<void> {
        let structureFields = this.getRestoreEditorStructureFields(errors);
        const editorData = {
            content: pageInfo,
            title: 'se.cms.page.restore.page.title',
            componentId: pageInfo.uid,
            componentUuid: pageInfo.uuid,
            componentType: pageInfo.typeCode,
            structure: this.buildStructure(structureFields)
        };

        this.forceErrorsDisplayInEditor(pageInfo.componentUuid, errors);

        return this.genericEditorModalService.open(
            editorData,
            () => {
                this.pageRestoredAlertService.displayPageRestoredSuccessAlert(pageInfo);
                this.crossFrameEventService.publish(EVENTS.PAGE_RESTORED);
            },
            (newErrors: ValidationError[], ge: GenericEditorModalComponent) => {
                const actionableErrors: ValidationError[] = this.getActionableErrors(newErrors);
                const unsupportedErrors: ValidationError[] = difference(
                    newErrors,
                    actionableErrors
                );

                if (unsupportedErrors.length > 0) {
                    this.showErrorAlert(unsupportedErrors);
                } else {
                    structureFields = concat(
                        structureFields,
                        this.getRestoreEditorStructureFields(newErrors)
                    );
                    ge.structure = this.buildStructure(structureFields);
                    this.forceErrorsDisplayInEditor(pageInfo.componentUuid, newErrors);
                }
            },
            {
                dialogPanelClass: 'modal-stretched'
            }
        );
    }

    private forceErrorsDisplayInEditor(editorId: string, errors: ValidationError[]): void {
        setTimeout(() => {
            this.crossFrameEventService.publish(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                {
                    messages: errors,
                    targetGenericEditorId: editorId
                }
            );
        }, 0);
    }

    private buildStructure(structureFields: CMSItemStructureField[]): CMSItemStructure {
        return {
            attributes: structureFields,
            category: 'PAGE'
        } as CMSItemStructure;
    }

    private getRestoreEditorStructureFields(errors: ValidationError[]): CMSItemStructureField[] {
        return errors.reduce((accumulator: CMSItemStructureField[], currentError) => {
            const errorMapping = ACTIONABLE_ERRORS.find(
                (actionableErrorMapping: ActionableErrorMapping) =>
                    this.isSupportedError(currentError) &&
                    this.isEqualTo(currentError, actionableErrorMapping)
            );

            accumulator.push(errorMapping.structure);
            return accumulator;
        }, []);
    }

    private showConfirmationMessage(errors: ValidationError[]): void {
        const message = errors.reduce((accumulator, currentError) => {
            const errorMapping = NON_ACTIONABLE_ERRORS.find(
                (nonActionableErrorMapping: NonActionableErrorMapping) =>
                    this.isSupportedError(currentError) &&
                    this.isEqualTo(currentError, nonActionableErrorMapping)
            );

            return accumulator + this.translate.instant(errorMapping.messageKey) + ' ';
        }, '');

        const modalConfig: ConfirmationModalConfig = {
            description: message.trim(),
            showOkButtonOnly: true,
            title: 'se.cms.page.restore.error.confirmationmodal.title'
        };

        this.confirmationModalService.confirm(modalConfig);
    }

    private showErrorAlert(errors: ValidationError[]): void {
        const errorMessage = errors.reduce(
            (accumulator, currentError) => accumulator + currentError.message + ' ',
            ''
        );

        this.alertService.showDanger(errorMessage.trim());
    }

    private getActionableErrors(errors: ValidationError[]): ValidationError[] {
        return errors.filter((error) =>
            ACTIONABLE_ERRORS.some(
                (actionableErrorMapping: ActionableErrorMapping) =>
                    this.isSupportedError(error) && this.isEqualTo(error, actionableErrorMapping)
            )
        );
    }

    private getNonActionableError(errors: ValidationError[]): ValidationError[] {
        return errors.filter((error) =>
            NON_ACTIONABLE_ERRORS.some(
                (nonActionableErrorMapping: NonActionableErrorMapping) =>
                    this.isSupportedError(error) && this.isEqualTo(error, nonActionableErrorMapping)
            )
        );
    }

    private isSupportedError(error: ValidationError): boolean {
        return error.type === 'ValidationError';
    }

    private isEqualTo(error1: ValidationError, error2: ErrorMapping): boolean {
        return error1.subject === error2.subject && error1.errorCode === error2.errorCode;
    }
}
