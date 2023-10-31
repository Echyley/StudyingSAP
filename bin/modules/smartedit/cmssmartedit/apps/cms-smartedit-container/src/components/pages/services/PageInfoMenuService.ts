/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CMSItemStructure, CMSItemStructureField } from 'cmscommons';
import { isNil, cloneDeep, remove, concat } from 'lodash';
import { LogService, PageVersionSelectionService, IPageService, ICMSPage } from 'smarteditcommons';
import { TypeStructureRestService } from '../../../dao/TypeStructureRestService';
import { DisplayConditionsFacade } from '../../../facades';
import { PageEditorModalService } from './PageEditorModalService';

export interface PageInfoForViewing extends ICMSPage {
    template: string;
    primaryPage: string | null;
    displayCondition: string;
    content: ICMSPage;
    localizedType: string;
    restrictionsCriteria: string | null;
}

/**
 * This service is used to provide all the information necessary to properly display the Page Info Menu.
 */
@Injectable()
export class PageInfoMenuService {
    private ALL_RESTRICTIONS_CRITERIA_LABEL = 'se.cms.restrictions.criteria.all';
    private ANY_RESTRICTIONS_CRITERIA_LABEL = 'se.cms.restrictions.criteria.any';

    private isPageEditorOpened: boolean;

    constructor(
        private displayConditionsFacade: DisplayConditionsFacade,
        private logService: LogService,
        private pageEditorModalService: PageEditorModalService,
        private pageService: IPageService,
        private pageVersionSelectionService: PageVersionSelectionService,
        private translateService: TranslateService,
        private typeStructureRestService: TypeStructureRestService
    ) {
        this.isPageEditorOpened = false;
    }

    public openPageEditor(pageInfo: ICMSPage): void {
        if (isNil(pageInfo)) {
            this.logService.warn(
                `[${this.constructor.name}] - Cannot open page editor. Provided page is empty.`
            );
            return;
        }

        if (!this.isPageEditorOpened) {
            this.isPageEditorOpened = true;

            this.pageEditorModalService.open(pageInfo).finally(() => {
                this.isPageEditorOpened = false;
            });
        }
    }

    /**
     * Retrieves the information for the current page and prepares it to be displayed in the Page Info Menu.
     */
    public async getCurrentPageInfo(): Promise<PageInfoForViewing | void> {
        try {
            const pageInfo = await this.pageService.getCurrentPageInfoByVersion(
                this.getCurrentPageVersionId()
            );
            const primaryPageName = await this.getPrimaryPageName(pageInfo);
            pageInfo.template = pageInfo.masterTemplateId;

            const pageInfoForViewing = cloneDeep(pageInfo) as PageInfoForViewing;
            pageInfoForViewing.content = pageInfo;
            pageInfoForViewing.primaryPage = primaryPageName;
            pageInfoForViewing.localizedType = pageInfo.typeCode;
            pageInfoForViewing.displayCondition = this.getPageDisplayCondition(pageInfo);
            pageInfoForViewing.restrictionsCriteria = this.getPageRestrictionsCriteria(pageInfo);

            // Note: In the previous implementation of the page info, there was a call to the catalog service to retrieve the URI context
            // and assign it to the page content. However, that field doesn't seem to be in use anymore, so it was removed.
            return pageInfoForViewing;
        } catch {
            this.logService.warn(
                `[${this.constructor.name}] - Cannot retrieve page info. Please try again later.`
            );
            return null;
        }
    }

    /**
     * Retrieves the page structure and adapts the fields to match the order expected in the Page Info Menu.
     */
    public async getPageStructureForViewing(
        pageTypeCode: string,
        isPrimaryPage: boolean
    ): Promise<CMSItemStructure | void> {
        try {
            const structureToMap = await this.typeStructureRestService.getStructureByType(
                pageTypeCode
            );
            const structure = structureToMap.map((field: CMSItemStructureField) => ({
                ...field,
                editable: false
            }));

            // This method needs to ensure that the page structure contains only the fields needed for viewing and in the right order.
            // The final list of attributes will have our fields in the right order. Any other fields in the structure, will appear at the end.
            this.removeFieldFromStructure(structure, 'uid');
            this.removeFieldFromStructure(structure, 'restrictions');

            const fieldsInRightOrder: CMSItemStructureField[] = [];
            const nameField = this.removeFieldFromStructure(structure, 'name');
            nameField.cmsStructureType = 'InfoPageName';
            this.addFieldToStructure(fieldsInRightOrder, nameField);

            this.addFieldToStructure(fieldsInRightOrder, this.buildField('displayCondition'));

            const descriptionFlag = this.structureContainsItemByFieldQualifier(
                structure,
                'description'
            );
            if (descriptionFlag) {
                const descriptionField = this.removeFieldFromStructure(structure, 'description');
                descriptionField.cmsStructureType = 'InfoPageName';
                this.addFieldToStructure(fieldsInRightOrder, descriptionField);
            }

            const titleFlag = this.structureContainsItemByFieldQualifier(structure, 'title');
            if (titleFlag) {
                const titleField = this.removeFieldFromStructure(structure, 'title');
                titleField.cmsStructureType = 'InfoPageName';
                this.addFieldToStructure(fieldsInRightOrder, titleField);
            }

            const labelFlag = this.structureContainsItemByFieldQualifier(structure, 'label');
            if (labelFlag) {
                const labelField = this.removeFieldFromStructure(structure, 'label');
                labelField.cmsStructureType = 'InfoPageName';
                this.addFieldToStructure(fieldsInRightOrder, labelField);
            }

            this.addFieldToStructure(fieldsInRightOrder, this.buildField('localizedType'));
            this.addFieldToStructure(fieldsInRightOrder, this.buildField('template'));

            if (!isPrimaryPage) {
                this.addFieldToStructure(fieldsInRightOrder, this.buildField('primaryPage'));
                this.addFieldToStructure(
                    fieldsInRightOrder,
                    this.buildField('restrictions', 'RestrictionsList')
                );
            }

            this.addFieldToStructure(
                fieldsInRightOrder,
                this.removeFieldFromStructure(structure, 'creationtime')
            );
            this.addFieldToStructure(
                fieldsInRightOrder,
                this.removeFieldFromStructure(structure, 'modifiedtime')
            );

            return {
                attributes: concat(fieldsInRightOrder, structure),
                category: 'PAGE'
            } as CMSItemStructure;
        } catch {
            this.logService.warn(
                `[${this.constructor.name}] - Cannot retrieve page info structure. Please try again later.`
            );
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------
    private addFieldToStructure(
        structure: CMSItemStructureField[],
        field: CMSItemStructureField
    ): void {
        if (field) {
            structure.push(field);
        }
    }

    private buildField(qualifier: string, cmsStructureType = 'ShortString'): CMSItemStructureField {
        return {
            cmsStructureType,
            qualifier,
            i18nKey: 'se.cms.pageinfo.page.' + qualifier.toLocaleLowerCase(),
            editable: false
        };
    }

    private structureContainsItemByFieldQualifier(
        structure: CMSItemStructureField[],
        fieldQualifier: string
    ): boolean {
        return !!structure.find((item) => item.qualifier === fieldQualifier);
    }

    private removeFieldFromStructure(
        structure: CMSItemStructureField[],
        fieldQualifier: string
    ): CMSItemStructureField {
        const removedElements = remove(
            structure,
            (field: CMSItemStructureField) => field.qualifier === fieldQualifier
        );
        return removedElements[0];
    }

    private async getPrimaryPageName(pageInfo: ICMSPage): Promise<string | null> {
        if (this.isVariationPage(pageInfo)) {
            const { name } = await this.displayConditionsFacade.getPrimaryPageForVariationPage(
                pageInfo.uid
            );
            return name || null;
        }
        return null;
    }

    private isVariationPage(pageInfo: ICMSPage): boolean {
        return !pageInfo.defaultPage;
    }

    private getPageDisplayCondition(pageInfo: ICMSPage): string {
        const conditionTranslKey = this.isVariationPage(pageInfo)
            ? 'page.displaycondition.variation'
            : 'page.displaycondition.primary';
        return this.translateService.instant(conditionTranslKey) as string;
    }

    private getPageRestrictionsCriteria(pageInfo: ICMSPage): string | null {
        if (this.isVariationPage(pageInfo) && pageInfo.onlyOneRestrictionMustApply !== undefined) {
            const criteriaTranslKey = pageInfo.onlyOneRestrictionMustApply
                ? this.ANY_RESTRICTIONS_CRITERIA_LABEL
                : this.ALL_RESTRICTIONS_CRITERIA_LABEL;
            return this.translateService.instant(criteriaTranslKey) as string;
        }
        return null;
    }

    private getCurrentPageVersionId(): string | null {
        const pageVersion = this.pageVersionSelectionService.getSelectedPageVersion();
        return pageVersion ? pageVersion.uid : null;
    }
}
