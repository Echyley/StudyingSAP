/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    ConfirmationModalConfig,
    EVENT_CONTENT_CATALOG_UPDATE,
    IAlertService,
    IConfirmationModalService,
    IExperienceService,
    IPageInfoService,
    LogService,
    SystemEventService,
    PageVersioningService,
    IPageVersion,
    PageVersionSelectionService
} from 'smarteditcommons';

/**
 * This service is used to rollback a page version from the toolbar context.
 */
@Injectable()
export class RollbackPageVersionService {
    constructor(
        private logService: LogService,
        private alertService: IAlertService,
        private confirmationModalService: IConfirmationModalService,
        private experienceService: IExperienceService,
        private pageInfoService: IPageInfoService,
        private pageVersioningService: PageVersioningService,
        private pageVersionSelectionService: PageVersionSelectionService,
        private systemEventService: SystemEventService
    ) {}

    public async rollbackPageVersion(version?: IPageVersion): Promise<void> {
        const pageVersion = version || this.pageVersionSelectionService.getSelectedPageVersion();
        if (!!pageVersion) {
            const TRANSLATE_NS = 'se.cms.actionitem.page.version.rollback.confirmation';
            const pageUuid = await this.pageInfoService.getPageUUID();
            await this.showConfirmationModal(pageVersion.label, TRANSLATE_NS);
            await this.performRollback(pageUuid, pageVersion);
        }
    }

    // Warning! This method is patched in personalization module, be careful when modifying it.
    private showConfirmationModal(versionLabel: string, translateNs: string): Promise<any> {
        return this.confirmationModalService.confirm({
            title: `${translateNs}.title`,
            description: `${translateNs}.description`,
            descriptionPlaceholders: {
                versionLabel
            }
        } as ConfirmationModalConfig);
    }

    private async performRollback(pageUuid: string, pageVersion: IPageVersion): Promise<void> {
        try {
            await this.pageVersioningService.rollbackPageVersion(pageUuid, pageVersion.uid);

            // invalidate the content catalog cache: a rollback of a page could replace the existing homepage.
            this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE);
            this.alertService.showSuccess('se.cms.versions.rollback.alert.success');
            // reload experience
            await this.experienceService.updateExperience({});
            this.pageVersionSelectionService.deselectPageVersion(false);
        } catch {
            this.logService.error(
                'RollbackPageVersionService::performRollback - unable to perform page rollback'
            );
        }
    }
}
