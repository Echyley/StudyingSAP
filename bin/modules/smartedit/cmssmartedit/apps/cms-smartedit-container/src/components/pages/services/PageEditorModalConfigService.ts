/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import {
    GenericEditorAttribute,
    CmsitemsRestService,
    IPageService,
    ICMSPage,
    IGenericEditorModalServiceComponent
} from 'smarteditcommons';
import { ContextAwarePageStructureService } from '../../../services';

@Injectable()
export class PageEditorModalConfigService {
    constructor(
        private cmsitemsRestService: CmsitemsRestService,
        private pageService: IPageService,
        private contextAwarePageStructureService: ContextAwarePageStructureService,
        private contextAwareEditableItemService: IContextAwareEditableItemService,
        private translateService: TranslateService
    ) {}

    /** Creates a config for given Page that can be used to open a modal.  */
    public async create(page: ICMSPage): Promise<IGenericEditorModalServiceComponent> {
        const config: IGenericEditorModalServiceComponent = {
            title: 'se.cms.pageeditormodal.editpagetab.title',
            componentUuid: page.uuid,
            componentType: page.typeCode
        };

        // set content
        const [cmsPage, isPagePrimary] = await Promise.all([
            this.cmsitemsRestService.getById(page.uuid),
            this.pageService.isPagePrimary(page.uid)
        ]);
        config.content = cmsPage;
        config.content.template = cmsPage.masterTemplateId;

        // set structure
        const fields = await this.contextAwarePageStructureService.getPageStructureForPageEditing(
            config.content.typeCode as string,
            config.content.uid as string
        );
        config.structure = fields;

        if (isPagePrimary) {
            config.structure.attributes = this.filterPrimaryPageAttributes(
                config.structure.attributes
            );
        }

        if (!!page.uid) {
            await this.ensureReadOnlyMode(page, config);
        }

        return config;
    }

    private filterPrimaryPageAttributes(
        attributes: GenericEditorAttribute[]
    ): GenericEditorAttribute[] {
        return attributes.filter((field) => this.isPrimaryPageAttribute(field));
    }

    private isPrimaryPageAttribute(attribute: GenericEditorAttribute): boolean {
        return (
            attribute.qualifier !== 'restrictions' &&
            attribute.qualifier !== 'onlyOneRestrictionMustApply'
        );
    }

    private async ensureReadOnlyMode(
        page: ICMSPage,
        config: IGenericEditorModalServiceComponent
    ): Promise<IGenericEditorModalServiceComponent> {
        const isEditable = await this.contextAwareEditableItemService.isItemEditable(page.uid);
        config.readOnlyMode = !isEditable;
        if (config.readOnlyMode) {
            config.messages = [
                {
                    type: 'info',
                    message: this.translateService.instant(
                        'se.cms.pageeditormodal.page.readonly.message'
                    )
                }
            ];
        }
        return config;
    }
}
