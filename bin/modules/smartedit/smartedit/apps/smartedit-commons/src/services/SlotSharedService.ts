/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ComponentAttributes } from '../di';
import { EXPERIENCE_STORAGE_KEY } from '../utils/smarteditconstants';
import { IComponentHandlerService } from './IComponentHandlerService';
import {
    ICatalogService,
    IExperience,
    IPageInfoService,
    ISharedDataService,
    CMSItem,
    IEditorModalService
} from './interfaces';
import { PageContentSlotsService } from './PageContentSlotsService';
import { cmsitemsUri } from './workflow';

/**
 * Provides methods to interact with the backend for shared slot information.
 */
@Injectable()
export class SlotSharedService {
    private disableShareSlotStatus: boolean;

    constructor(
        private readonly pageContentSlotsService: PageContentSlotsService,
        private readonly pageInfoService: IPageInfoService,
        private readonly translateService: TranslateService,
        private readonly editorModalService: IEditorModalService,
        private readonly componentHandlerService: IComponentHandlerService,
        private readonly catalogService: ICatalogService,
        private readonly sharedDataService: ISharedDataService
    ) {}

    /**
     * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not.
     * Based on this service method the slot shared button is shown or hidden for a particular slotId
     */
    public isSlotShared(slotId: string): Promise<boolean> {
        return this.pageContentSlotsService.isSlotShared(slotId);
    }

    /**
     * Checks whether the given slot is global icon slot or not
     * Returns true if either of the below conditions are true.
     * If the current catalog is multicountry related and if the slot is external slot.
     * If the current catalog is multicountry related and the slot is not external slot but the current page is from parent catalog.
     */
    public async isGlobalSlot(slotId: string, slotType: string): Promise<boolean> {
        const isExternalSlot = await this.componentHandlerService.isExternalComponent(
            slotId,
            slotType
        );

        const [isCurrentCatalogMultiCountry, experience] = await Promise.all([
            this.catalogService.isCurrentCatalogMultiCountry(),
            this.sharedDataService.get(EXPERIENCE_STORAGE_KEY) as unknown as IExperience
        ]);
        // isMultiCountry -> Has the current site with current catalog have any parent catalog ?
        const isMultiCountry = isCurrentCatalogMultiCountry || false;

        // isCurrentPageFromParent -> Is the current page from the parent catalog ?
        const isCurrentPageFromParent = this.isCurrentPageFromParentCatalog(experience);

        return isMultiCountry && (isExternalSlot || (isCurrentPageFromParent && !isExternalSlot));
    }

    /**
     * Sets the status of the disableSharedSlot feature
     */
    public setSharedSlotEnablementStatus(status: boolean): void {
        this.disableShareSlotStatus = status;
    }

    /**
     * Checks the status of the disableSharedSlot feature
     *
     */
    public areSharedSlotsDisabled(): boolean {
        return this.disableShareSlotStatus;
    }

    /**
     * Replaces the global slot (multicountry related) based on the options selected in the "Replace Slot" generic editor.
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    public async replaceGlobalSlot(componentAttributes: ComponentAttributes): Promise<any> {
        this.validateComponentAttributes(componentAttributes);

        const cmsItem = await this.constructCmsItemParameter(componentAttributes);

        const componentData = {
            title: 'se.cms.slot.shared.replace.editor.title',
            structure: {
                attributes: [
                    {
                        cmsStructureType: 'SlotSharedSlotTypeField',
                        qualifier: 'isSlotCustom',
                        required: true
                    },
                    {
                        cmsStructureType: 'SlotSharedCloneActionField',
                        qualifier: 'cloneAction',
                        required: true
                    }
                ]
            },
            contentApi: cmsitemsUri,
            saveLabel: 'se.cms.slot.shared.replace.editor.save',
            content: cmsItem,
            initialDirty: true
        };

        return this.editorModalService.openGenericEditor(componentData);
    }

    /**
     * Replaces the shared slot (non-multicountry related) based on the options selected in the "Replace Slot" generic editor
     *
     * @returns A promise that resolves when replace slot operation is completed.
     */
    public async replaceSharedSlot(componentAttributes: ComponentAttributes): Promise<any> {
        this.validateComponentAttributes(componentAttributes);

        const cmsItem = await this.constructCmsItemParameter(componentAttributes);
        cmsItem.isSlotCustom = true;

        const componentData = {
            title: 'se.cms.slot.shared.replace.editor.title',
            structure: {
                attributes: [
                    {
                        cmsStructureType: 'SlotSharedCloneActionField',
                        qualifier: 'cloneAction',
                        required: true
                    }
                ]
            },
            contentApi: cmsitemsUri,
            saveLabel: 'se.cms.slot.shared.replace.editor.save',
            content: cmsItem,
            initialDirty: true
        };

        return this.editorModalService.openGenericEditor(componentData);
    }

    private async constructCmsItemParameter(
        componentAttributes: ComponentAttributes
    ): Promise<Partial<CMSItem>> {
        const cloneText = this.translateService.instant('se.cms.slot.shared.clone');

        const pageUid = await this.pageInfoService.getPageUID();
        const targetCatalogVersionUuid = await this.pageInfoService.getCatalogVersionUUIDFromPage();
        const componentName = `${pageUid}-${componentAttributes.smarteditComponentId}-${cloneText}`;
        const cmsItem = {
            name: componentName,
            smarteditComponentId: componentAttributes.smarteditComponentId,
            contentSlotUuid: componentAttributes.smarteditComponentUuid,
            itemtype: componentAttributes.smarteditComponentType,
            catalogVersion: targetCatalogVersionUuid,
            pageUuid: pageUid,
            onlyOneRestrictionMustApply: false
        };
        return cmsItem;
    }

    private validateComponentAttributes(componentAttributes: ComponentAttributes): void | never {
        if (!componentAttributes) {
            throw new Error('Parameter: componentAttributes needs to be supplied!');
        }

        const validationAttributes = [
            'smarteditComponentId',
            'smarteditComponentUuid',
            'smarteditComponentType'
        ];
        const invalidAttr = validationAttributes.find((attr) => !componentAttributes[attr]);
        if (!!invalidAttr) {
            throw new Error(`Parameter: componentAttributes.${invalidAttr} needs to be supplied!`);
        }
    }

    private isCurrentPageFromParentCatalog(experience: IExperience): boolean {
        const pageContextCatalogVersionUuid = experience?.pageContext?.catalogVersionUuid || '';
        const catalogDescriptorCatalogVersionUuid =
            experience?.catalogDescriptor?.catalogVersionUuid || '';
        const isCurrentPageFromParent =
            catalogDescriptorCatalogVersionUuid !== pageContextCatalogVersionUuid;

        return isCurrentPageFromParent;
    }
}
