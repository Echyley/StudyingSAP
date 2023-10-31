/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { isString, isObject, isArray, isUndefined, map } from 'lodash';
import {
    IPersonalizationsmarteditContextMenuServiceProxy,
    PersonalizationsmarteditUtils,
    ContextMenuDto,
    Personalization,
    Customize,
    CombinedView,
    SeData,
    CustomizationVariation
} from 'personalizationcommons';
import { PersonalizationsmarteditComponentHandlerService } from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import { PersonalizationsmarteditContextService } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInner';
import { CrossFrameEventService, EVENTS } from 'smarteditcommons';

@Injectable()
export class PersonalizationsmarteditContextualMenuService {
    public static readonly EDIT_PERSONALIZATION_IN_WORKFLOW =
        'personalizationsmartedit.editPersonalizationInWorkflow.enabled';

    public contextPersonalization: Personalization;
    public contextCustomize: Customize;
    public contextCombinedView: CombinedView;
    public contextSeData: SeData;
    private isWorkflowRunningBoolean = false;

    constructor(
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        protected personalizationsmarteditContextMenuServiceProxy: IPersonalizationsmarteditContextMenuServiceProxy,
        protected crossFrameEventService: CrossFrameEventService
    ) {
        this.init();
    }

    public async updateWorkflowStatus(): Promise<void> {
        this.isWorkflowRunningBoolean =
            await this.personalizationsmarteditContextService.isCurrentPageActiveWorkflowRunning();
    }

    public openDeleteAction(config: ContextMenuDto): void {
        const configProperties = isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {} as ContextMenuDto;
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode =
            configProperties.smarteditPersonalizationVariationId || null;
        configurationToPass.selectedCustomizationCode =
            configProperties.smarteditPersonalizationCustomizationId || null;
        const componentCatalog = configProperties.smarteditCatalogVersionUuid.split('/');
        configurationToPass.componentCatalog = componentCatalog[0];
        configurationToPass.componentCatalogVersion = componentCatalog[1];
        const contextCustomization = this.getSelectedCustomization(
            configurationToPass.selectedCustomizationCode
        );
        configurationToPass.catalog = contextCustomization.catalog;
        configurationToPass.catalogVersion = contextCustomization.catalogVersion;
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(
            configProperties.smarteditContainerSourceId
        );

        this.personalizationsmarteditContextMenuServiceProxy.openDeleteAction(configurationToPass);
    }

    public openAddAction(config: ContextMenuDto): void {
        const configProperties = isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {} as ContextMenuDto;
        configurationToPass.componentType = config.componentType;
        configurationToPass.componentId = config.componentId;
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode = this.getSelectedVariationCode();
        const componentCatalog = configProperties.smarteditCatalogVersionUuid.split('/');
        configurationToPass.componentCatalog = componentCatalog[0];
        const contextCustomization = this.getSelectedCustomization();
        configurationToPass.catalog = contextCustomization.catalog;
        configurationToPass.selectedCustomizationCode = contextCustomization.code;
        const slot = this.personalizationsmarteditComponentHandlerService.getParentSlotForComponent(
            config.element
        );
        const slotCatalog = this.personalizationsmarteditComponentHandlerService
            .getCatalogVersionUuid(slot)
            .split('/');
        configurationToPass.slotCatalog = slotCatalog[0];
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(
            configProperties.smarteditContainerSourceId
        );
        configurationToPass.slotsToRefresh.push(config.slotId);

        return this.personalizationsmarteditContextMenuServiceProxy.openAddAction(
            configurationToPass
        );
    }

    public openEditAction(config: ContextMenuDto): void {
        const configProperties = isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {} as ContextMenuDto;
        configurationToPass.componentType = config.componentType;
        configurationToPass.componentId = config.componentId;
        configurationToPass.containerId = config.containerId;
        configurationToPass.containerSourceId = configProperties.smarteditContainerSourceId;
        configurationToPass.slotId = config.slotId;
        configurationToPass.actionId = configProperties.smarteditPersonalizationActionId || null;
        configurationToPass.selectedVariationCode =
            configProperties.smarteditPersonalizationVariationId || null;
        configurationToPass.selectedCustomizationCode =
            configProperties.smarteditPersonalizationCustomizationId || null;
        configurationToPass.componentUuid = configProperties.smarteditComponentUuid || null;
        configurationToPass.slotsToRefresh = this.getSlotsToRefresh(
            configProperties.smarteditContainerSourceId
        );

        return this.personalizationsmarteditContextMenuServiceProxy.openEditAction(
            configurationToPass
        );
    }

    public openEditComponentAction(config: ContextMenuDto): void {
        const configProperties = isString(config.componentAttributes)
            ? JSON.parse(config.componentAttributes)
            : config.componentAttributes;
        const configurationToPass = {} as ContextMenuDto;
        configurationToPass.smarteditComponentType = configProperties.smarteditComponentType;
        configurationToPass.smarteditComponentUuid = configProperties.smarteditComponentUuid;
        configurationToPass.smarteditCatalogVersionUuid =
            configProperties.smarteditCatalogVersionUuid;

        return this.personalizationsmarteditContextMenuServiceProxy.openEditComponentAction(
            configurationToPass
        );
    }

    public isCustomizationFromCurrentCatalog(config: ContextMenuDto): boolean {
        const items = this.contextCombinedView.selectedItems || [];
        const foundItems = items.filter(
            (item) =>
                item.customization.code ===
                    config.componentAttributes.smarteditPersonalizationCustomizationId &&
                item.variation.code ===
                    config.componentAttributes.smarteditPersonalizationVariationId
        );
        const foundItem = foundItems.shift();
        if (foundItem) {
            return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
                foundItem.customization,
                this.personalizationsmarteditContextService.getSeData()
            );
        }
        return false;
    }

    public isPersonalizationAllowedInWorkflow(): boolean {
        this.refreshContext();
        return this.isEditPersonalizationInWorkflowAllowed(this.contextPersonalization.enabled);
    }

    public isContextualMenuAddItemEnabled(config: ContextMenuDto): boolean {
        this.refreshContext();
        let isEnabled = this.isContextualMenuEnabled();
        isEnabled = isEnabled && !this.isElementHighlighted(config);
        isEnabled = isEnabled && this.isSlotInCurrentCatalog(config);
        isEnabled = isEnabled && this.isSelectedCustomizationFromCurrentCatalog();
        return this.isEditPersonalizationInWorkflowAllowed(isEnabled);
    }

    public isContextualMenuEditItemEnabled(config: ContextMenuDto): boolean {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled =
            isEnabled && !isUndefined(config.componentAttributes.smarteditPersonalizationActionId);
        isEnabled = isEnabled && this.isSlotInCurrentCatalog(config);
        isEnabled =
            isEnabled &&
            (this.isSelectedCustomizationFromCurrentCatalog() ||
                this.isCustomizationFromCurrentCatalog(config));
        return this.isEditPersonalizationInWorkflowAllowed(isEnabled);
    }

    public isContextualMenuDeleteItemEnabled(config: ContextMenuDto): boolean {
        return this.isContextualMenuEditItemEnabled(config);
    }

    public isContextualMenuShowActionListEnabled(config: any): boolean {
        this.refreshContext();
        let isEnabled = !isUndefined(config.componentAttributes.smarteditPersonalizationActionId);
        isEnabled = isEnabled && this.contextCombinedView.enabled;
        isEnabled = isEnabled && !this.contextCombinedView.customize.selectedCustomization;
        return isEnabled;
    }

    public isContextualMenuInfoEnabled(): boolean {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled = isEnabled && !isObject(this.contextCustomize.selectedVariations);
        isEnabled = isEnabled || isArray(this.contextCustomize.selectedVariations);
        isEnabled = isEnabled && !this.contextCombinedView.enabled;

        return isEnabled;
    }

    public isContextualMenuInfoItemEnabled(): boolean {
        const isEnabled = this.isContextualMenuInfoEnabled();
        return (
            isEnabled ||
            !this.isEditPersonalizationInWorkflowAllowed(this.contextPersonalization.enabled)
        );
    }

    public isContextualMenuEditComponentItemEnabled(config: ContextMenuDto): boolean {
        this.refreshContext();
        let isEnabled = this.contextPersonalization.enabled;
        isEnabled =
            isEnabled &&
            !this.contextCombinedView.enabled &&
            this.isComponentInCurrentCatalog(config);
        return isEnabled;
    }

    private isEditPersonalizationInWorkflowAllowed(condition: boolean): boolean {
        const seConfigurationData =
            this.personalizationsmarteditContextService.getSeData().seConfigurationData || [];
        const isEditPersonalizationInWorkflowPropertyEnabled =
            seConfigurationData[
                PersonalizationsmarteditContextualMenuService.EDIT_PERSONALIZATION_IN_WORKFLOW
            ] === true;
        if (isEditPersonalizationInWorkflowPropertyEnabled) {
            return condition;
        } else {
            return condition && !this.isWorkflowRunningBoolean;
        }
    }

    private isCustomizeObjectValid(customize: Customize): boolean {
        return (
            isObject(customize.selectedCustomization) &&
            isObject(customize.selectedVariations) &&
            !isArray(customize.selectedVariations)
        );
    }

    private isContextualMenuEnabled(): boolean {
        return (
            this.isCustomizeObjectValid(this.contextCustomize) ||
            (this.contextCombinedView.enabled &&
                this.isCustomizeObjectValid(this.contextCombinedView.customize))
        );
    }

    private isElementHighlighted(config: ContextMenuDto): boolean {
        if (this.contextCombinedView.enabled) {
            return this.contextCombinedView.customize.selectedComponents.includes(
                config.componentAttributes.smarteditContainerSourceId
            );
        } else {
            return this.contextCustomize.selectedComponents.includes(
                config.componentAttributes.smarteditContainerSourceId
            );
        }
    }

    private isSlotInCurrentCatalog(config: ContextMenuDto): boolean {
        const slot = this.personalizationsmarteditComponentHandlerService.getParentSlotForComponent(
            config.element
        );
        const catalogUuid =
            this.personalizationsmarteditComponentHandlerService.getCatalogVersionUuid(slot);
        const experienceCV =
            this.contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
        return experienceCV === catalogUuid;
    }

    private isComponentInCurrentCatalog(config: ContextMenuDto): boolean {
        const experienceCV =
            this.contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
        const componentCV = config.componentAttributes.smarteditCatalogVersionUuid;
        return experienceCV === componentCV;
    }

    private isSelectedCustomizationFromCurrentCatalog(): boolean {
        const customization =
            this.contextCustomize.selectedCustomization ||
            this.contextCombinedView.customize.selectedCustomization;
        if (customization) {
            return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
                customization,
                this.personalizationsmarteditContextService.getSeData()
            );
        }
        return false;
    }

    private init(): void {
        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
            this.updateWorkflowStatus();
        });
        this.crossFrameEventService.subscribe(EVENTS.PAGE_UPDATED, () => {
            this.updateWorkflowStatus();
        });
    }

    private refreshContext(): void {
        this.contextPersonalization =
            this.personalizationsmarteditContextService.getPersonalization();
        this.contextCustomize = this.personalizationsmarteditContextService.getCustomize();
        this.contextCombinedView = this.personalizationsmarteditContextService.getCombinedView();
        this.contextSeData = this.personalizationsmarteditContextService.getSeData();
    }

    private getSelectedVariationCode(): string {
        if (this.personalizationsmarteditContextService.getCombinedView().enabled) {
            return (
                this.personalizationsmarteditContextService.getCombinedView().customize
                    .selectedVariations as CustomizationVariation
            ).code;
        }
        return (
            this.personalizationsmarteditContextService.getCustomize()
                .selectedVariations as CustomizationVariation
        ).code;
    }

    // return type should be Customization but it's in container
    private getSelectedCustomization(customizationCode?: string): any {
        if (this.personalizationsmarteditContextService.getCombinedView().enabled) {
            let customization =
                this.personalizationsmarteditContextService.getCombinedView().customize
                    .selectedCustomization;
            if (!customization && customizationCode) {
                customization = this.personalizationsmarteditContextService
                    .getCombinedView()
                    .selectedItems.filter(
                        (elem) => elem.customization.code === customizationCode
                    )[0].customization;
            }
            return customization;
        }
        return this.personalizationsmarteditContextService.getCustomize().selectedCustomization;
    }

    private getSlotsToRefresh(containerSourceId: string): string[] {
        let slotsSelector =
            this.personalizationsmarteditComponentHandlerService.getAllSlotsSelector();
        slotsSelector += ' [data-smartedit-container-source-id="' + containerSourceId + '"]'; // space at beginning is important
        const slots =
            this.personalizationsmarteditComponentHandlerService.getFromSelector(slotsSelector);
        const slotIds = Array.prototype.slice.call(
            map(slots, (el) =>
                this.personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(
                    this.personalizationsmarteditComponentHandlerService.getFromSelector(el)
                )
            )
        );
        return slotIds;
    }
}
