/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CMSItemStructureField, StructureTypeCategory } from 'cmscommons';
import { GenericEditorStructure, IPageService } from 'smarteditcommons';
import { TypeStructureRestService } from '../dao/TypeStructureRestService';

const PAGE_STRUCTURE_PRE_ORDER = [
    'typeCode',
    'template',
    'name',
    'description',
    'label',
    'uid',
    'title',
    'fromName',
    'fromEmail'
];

const PAGE_STRUCTURE_POST_ORDER = ['creationtime', 'modifiedtime'];

@Injectable()
export class ContextAwarePageStructureService {
    constructor(
        private typeStructureRestService: TypeStructureRestService,
        private pageService: IPageService
    ) {}

    /**
     * Return the CMS page structure with some modifications for the context of creating a new page.
     * The field order is modified, the created/modified time fields are removed, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the new page to be created
     * @param isPrimary Flag indicating if the new page will be a primary or variation page
     *
     * @returns A modified page structure
     */
    public async getPageStructureForNewPage(
        pageTypeCode: string,
        isPrimary: boolean
    ): Promise<GenericEditorStructure> {
        const fields = await this.getFields(pageTypeCode);
        if (pageTypeCode === 'ContentPage') {
            this.setLabelEditability(fields, isPrimary);
        }
        this.removeField(fields, 'creationtime');
        this.removeField(fields, 'modifiedtime');
        this.removeField(fields, 'displayCondition');
        this.removeField(fields, 'restrictions');

        return {
            attributes: this.getOrderedFields(fields),
            category: StructureTypeCategory.PAGE
        };
    }

    /**
     * Return the CMS page structure with some modifications for the context of editing the info of an existing page.
     * The field order is modified, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the page to be edited
     * @param pageId The ID of the existing page to be modified
     *
     * @returns A modified page structure
     */
    public async getPageStructureForPageEditing(
        pageTypeCode: string,
        pageId: string
    ): Promise<GenericEditorStructure> {
        const fields = await this.getFields(pageTypeCode);
        const readOnlyFieldNames = ['uid', 'creationtime', 'modifiedtime'];
        fields
            .filter((field) => readOnlyFieldNames.indexOf(field.qualifier) >= 0)
            .forEach((field) => {
                field.editable = false;
            });

        if (pageTypeCode === 'ContentPage') {
            const isPrimary = await this.pageService.isPagePrimary(pageId);
            this.setLabelEditability(fields, isPrimary);
            return {
                attributes: this.getOrderedFields(fields),
                category: StructureTypeCategory.PAGE
            };
        }
        return {
            attributes: this.getOrderedFields(fields),
            category: StructureTypeCategory.PAGE
        };
    }

    /**
     * Return the CMS page structure with some modifications for the context of viewing the info of an existing page.
     * The field order is modified, and the label field for variation content pages is disabled.
     *
     * @param pageTypeCode The page type of the existing page
     *
     * @returns A modified page structure
     */
    public async getPageStructureForViewing(pageTypeCode: string): Promise<GenericEditorStructure> {
        const fields = await this.getFields(pageTypeCode);
        fields.forEach((field) => {
            field.editable = false;
        });
        this.removeField(fields, 'typeCode');
        this.removeField(fields, 'template');
        this.removeField(fields, 'displayCondition');
        this.removeField(fields, 'restrictions');
        return {
            attributes: this.getOrderedFields(fields),
            category: StructureTypeCategory.PAGE
        };
    }

    private moveElement(
        array: CMSItemStructureField[],
        oldPosition: number,
        newPosition: number
    ): CMSItemStructureField[] {
        if (
            oldPosition < 0 ||
            oldPosition >= array.length ||
            newPosition < 0 ||
            newPosition >= array.length
        ) {
            return array;
        }
        array.splice(newPosition, 0, array.splice(oldPosition, 1)[0]);
        return array;
    }

    private getOrderedFields(unorderedFields: CMSItemStructureField[]): CMSItemStructureField[] {
        let index: number;

        for (let i = PAGE_STRUCTURE_PRE_ORDER.length - 1; i >= 0; i--) {
            index = unorderedFields.findIndex(
                (field) => field.qualifier === PAGE_STRUCTURE_PRE_ORDER[i]
            );
            this.moveElement(unorderedFields, index, 0);
        }
        for (let i = 0; i < PAGE_STRUCTURE_POST_ORDER.length; i++) {
            index = unorderedFields.findIndex(
                (field) => field.qualifier === PAGE_STRUCTURE_POST_ORDER[i]
            );
            this.moveElement(unorderedFields, index, unorderedFields.length - 1);
        }
        return unorderedFields;
    }

    private setLabelEditability(fields: CMSItemStructureField[], isPrimary: boolean): void {
        const labelFieldIndex = fields.findIndex((field) => field.qualifier === 'label');

        // Leave the attribute uneditable if user does not have "change" attribute permission
        // NOTE: editable field might be undefined, undefined !== false => true
        if (labelFieldIndex !== -1 && fields[labelFieldIndex].editable !== false) {
            fields[labelFieldIndex].editable = isPrimary;
        }
    }

    private async getFields(pageTypeCode: string): Promise<CMSItemStructureField[]> {
        const structure = await this.typeStructureRestService.getStructureByType(pageTypeCode);
        structure.push(
            {
                cmsStructureType: 'DisplayConditionEditor',
                i18nKey: 'type.abstractpage.displayCondition.name',
                qualifier: 'displayCondition',
                editable: false
            },
            {
                cmsStructureType: 'ShortString',
                i18nKey: 'se.cms.pageinfo.page.type',
                qualifier: 'typeCode',
                editable: false
            }
        );
        return structure;
    }

    private removeField(fields: CMSItemStructureField[], fieldQualifier: string): void {
        const index = fields.findIndex((field) => field.qualifier === fieldQualifier);
        if (index !== -1) {
            fields.splice(index, 1);
        }
    }
}
