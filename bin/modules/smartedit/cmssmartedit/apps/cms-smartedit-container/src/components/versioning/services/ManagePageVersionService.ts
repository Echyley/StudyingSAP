/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    ConfirmationModalConfig,
    IAlertService,
    IConfirmationModalService,
    IExperienceService,
    IPageInfoService,
    Nullable,
    Payload,
    IPageVersion,
    PageVersioningService,
    PageVersionSelectionService,
    IGenericEditorModalServiceComponent
} from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';

/**
 * Used to manage a page version.
 */
@Injectable()
export class ManagePageVersionService {
    constructor(
        private alertService: IAlertService,
        private experienceService: IExperienceService,
        private confirmationModalService: IConfirmationModalService,
        private genericEditorModalService: GenericEditorModalService,
        private pageInfoService: IPageInfoService,
        private pageVersioningService: PageVersioningService,
        private pageVersionSelectionService: PageVersionSelectionService
    ) {}

    public async createPageVersion(): Promise<IPageVersion> {
        const pageUuid = await this.pageInfoService.getPageUUID();
        const componentData = this.getComponentDataForEditor(pageUuid, null);

        return this.genericEditorModalService.open(componentData, async (result: IPageVersion) => {
            const experience: Payload = {
                versionId: result.uid
            };
            await this.experienceService.updateExperience(experience);

            this.alertService.showSuccess('se.cms.versions.create.alert.success');
            this.pageVersionSelectionService.selectPageVersion(result);
        });
    }

    public async editPageVersion(versionDetails: IPageVersion): Promise<IPageVersion> {
        const pageUuid = await this.pageInfoService.getPageUUID();
        const componentData = this.getComponentDataForEditor(pageUuid, versionDetails);

        return this.genericEditorModalService.open(componentData, (result: IPageVersion) => {
            const selectedVersion = this.pageVersionSelectionService.getSelectedPageVersion();
            if (selectedVersion && selectedVersion.uid === result.uid) {
                this.pageVersionSelectionService.updatePageVersionDetails(result);
            }
        });
    }

    public async deletePageVersion(versionId: string): Promise<void> {
        const pageUuid = await this.pageInfoService.getPageUUID();

        await this.confirmationModalService.confirm({
            title: 'se.cms.actionitem.page.version.delete.confirmation.title',
            description: 'se.cms.actionitem.page.version.delete.confirmation.description'
        } as ConfirmationModalConfig);

        await this.pageVersioningService.deletePageVersion(pageUuid, versionId);

        this.alertService.showSuccess('se.cms.versions.delete.alert.success');

        // reload experience to display current page if deleting the current
        const selectedVersion = this.pageVersionSelectionService.getSelectedPageVersion();
        if (selectedVersion && selectedVersion.uid === versionId) {
            await this.experienceService.updateExperience();
            this.pageVersionSelectionService.deselectPageVersion();
        }
    }

    /**
     * Returns an object that contains the information to be displayed and edited in the modal.
     *
     * @param pageUuid the uuid of the page.
     * @param content the content to be populated in the editor, null for create mode.
     */
    private getComponentDataForEditor(
        pageUuid: string,
        content: Nullable<IPageVersion>
    ): IGenericEditorModalServiceComponent {
        const componentData: IGenericEditorModalServiceComponent = {
            title: content ? 'se.cms.versions.edit' : 'se.cms.versions.create',
            structure: {
                attributes: [
                    {
                        cmsStructureType: 'ShortString',
                        qualifier: 'label',
                        i18nKey: 'se.cms.versions.editor.label.name',
                        required: true
                    },
                    {
                        cmsStructureType: 'ShortString',
                        qualifier: 'description',
                        i18nKey: 'se.cms.versions.editor.description.name'
                    }
                ]
            },
            contentApi: this.pageVersioningService.getResourceURI().replace(':pageUuid', pageUuid)
        };

        if (content) {
            componentData.content = content;
            componentData.componentUuid = content.uid;
            componentData.componentType = 'versioning';
        }

        return componentData;
    }
}
