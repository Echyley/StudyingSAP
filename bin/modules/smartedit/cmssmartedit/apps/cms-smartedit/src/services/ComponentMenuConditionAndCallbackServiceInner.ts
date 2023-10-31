/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import {
    IComponentMenuConditionAndCallbackService,
    IComponentSharedService,
    IComponentVisibilityAlertService,
    IRemoveComponentService,
    ISlotVisibilityService,
    IComponentEditingFacade,
    TypePermissionsRestService
} from 'cmscommons';
import {
    CmsitemsRestService,
    IComponentHandlerService,
    ComponentUpdatedEventInfo,
    CrossFrameEventService,
    EVENT_PAGE_TREE_SLOT_NEED_UPDATE,
    EVENT_SMARTEDIT_COMPONENT_UPDATED,
    GatewayProxied,
    IAlertService,
    ICatalogService,
    ICatalogVersionPermissionService,
    IConfirmationModalService,
    IContextualMenuConfiguration,
    IEditorModalService,
    IFeatureService,
    IPageInfoService,
    ISlotRestrictionsService,
    LogService
} from 'smarteditcommons';
import { ComponentInfoService } from './ComponentInfoService';

@GatewayProxied(
    'externalCondition',
    'removeCondition',
    'removeCallback',
    'cloneCondition',
    'cloneCallback',
    'sharedCondition',
    'editConditionForHiddenComponent',
    'dragCondition'
)
export class ComponentMenuConditionAndCallbackService extends IComponentMenuConditionAndCallbackService {
    constructor(
        private readonly componentHandlerService: IComponentHandlerService,
        private readonly typePermissionsRestService: TypePermissionsRestService,
        private readonly componentVisibilityAlertService: IComponentVisibilityAlertService,
        private readonly editorModalService: IEditorModalService,
        private readonly featureService: IFeatureService,
        private readonly slotRestrictionsService: ISlotRestrictionsService,
        private readonly confirmationModalService: IConfirmationModalService,
        private readonly logService: LogService,
        private readonly alertService: IAlertService,
        private readonly removeComponentService: IRemoveComponentService,
        private readonly slotVisibilityService: ISlotVisibilityService,
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly translateService: TranslateService,
        private readonly componentInfoService: ComponentInfoService,
        private readonly cmsitemsRestService: CmsitemsRestService,
        private readonly componentEditingFacade: IComponentEditingFacade,
        private readonly componentSharedService: IComponentSharedService,
        private readonly pageInfoService: IPageInfoService,
        private readonly catalogService: ICatalogService,
        private readonly catalogVersionPermissionService: ICatalogVersionPermissionService
    ) {
        super();
    }

    public async editConditionForHiddenComponent(
        configuration: IContextualMenuConfiguration
    ): Promise<boolean> {
        const isSlotEditable = await this.slotRestrictionsService.isSlotEditable(
            configuration.slotId
        );
        if (!isSlotEditable) {
            return false;
        }
        const smarteditCatalogVersionUuid =
            configuration.componentAttributes &&
            configuration.componentAttributes.smarteditCatalogVersionUuid;
        if (smarteditCatalogVersionUuid) {
            const uuid = await this.pageInfoService.getCatalogVersionUUIDFromPage();
            const isExternal = smarteditCatalogVersionUuid !== uuid;
            const { catalogId, version } = await this.catalogService.getCatalogVersionByUuid(
                smarteditCatalogVersionUuid
            );

            const isWritable = await this.catalogVersionPermissionService.hasWritePermission(
                catalogId,
                version
            );
            return isWritable && !isExternal;
        }
        return false;
    }

    public async sharedCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        let slotId = null;
        if (configuration.element) {
            slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
        } else {
            slotId = configuration.slotId;
        }

        if (configuration.isComponentHidden) {
            const pageUuid = await this.pageInfoService.getCatalogVersionUUIDFromPage();
            if (configuration.componentAttributes.smarteditCatalogVersionUuid !== pageUuid) {
                return false;
            }
        } else {
            const [isExternalComponent, isSlotEditable] = await Promise.all([
                this.componentHandlerService.isExternalComponent(
                    configuration.componentId,
                    configuration.componentType
                ),
                this.slotRestrictionsService.isSlotEditable(slotId)
            ]);
            if (isExternalComponent || !isSlotEditable) {
                return false;
            }
        }

        return this.componentSharedService.isComponentShared(
            configuration.componentAttributes.smarteditComponentUuid
        );
    }

    public async externalCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        let slotId = null;
        if (!!configuration.element) {
            slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
        } else {
            slotId = configuration.slotId;
        }

        const isSlotEditable = await this.slotRestrictionsService.isSlotEditable(slotId);
        if (!isSlotEditable) {
            return false;
        }

        const smarteditCatalogVersionUuid =
            configuration.componentAttributes &&
            configuration.componentAttributes.smarteditCatalogVersionUuid;
        if (smarteditCatalogVersionUuid) {
            const uuid = await this.pageInfoService.getCatalogVersionUUIDFromPage();
            return smarteditCatalogVersionUuid !== uuid;
        }

        return this.componentHandlerService.isExternalComponent(
            configuration.componentId,
            configuration.componentType
        );
    }

    public async removeCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        if (!configuration.isComponentHidden) {
            let slotId = null;
            if (!!configuration.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(
                    configuration.element
                );
            } else {
                slotId = configuration.slotId;
            }

            const slotEditable = await this.slotRestrictionsService.isSlotEditable(slotId);
            if (slotEditable) {
                const hasDeletePermission =
                    await this.typePermissionsRestService.hasDeletePermissionForTypes([
                        configuration.componentType
                    ]);
                return hasDeletePermission[configuration.componentType];
            }
            return false;
        }

        const hasDeletePermissionOfHidden =
            await this.typePermissionsRestService.hasDeletePermissionForTypes([
                configuration.componentType
            ]);
        return hasDeletePermissionOfHidden[configuration.componentType];
    }
    public async removeCallback(
        configuration: IContextualMenuConfiguration,
        $event: Event
    ): Promise<void> {
        let slotOperationRelatedId: string;
        let slotOperationRelatedType: string;

        if (configuration.element) {
            slotOperationRelatedId = this.componentHandlerService.getSlotOperationRelatedId(
                configuration.element
            );
            slotOperationRelatedType = this.componentHandlerService.getSlotOperationRelatedType(
                configuration.element
            );
        } else {
            slotOperationRelatedId = configuration.containerId
                ? configuration.containerId
                : configuration.componentId;
            slotOperationRelatedType =
                configuration.containerId && configuration.containerType
                    ? configuration.containerType
                    : configuration.componentType;
        }

        const confirmed = await this.confirmationModalService
            .confirm({
                description: 'se.cms.contextmenu.removecomponent.confirmation.message',
                title: 'se.cms.contextmenu.removecomponent.confirmation.title'
            })
            .catch(() => this.logService.log('Confirmation cancelled'));
        if (!confirmed) {
            return;
        }

        await this.removeComponentService.removeComponent({
            slotId: configuration.slotId,
            slotUuid: configuration.slotUuid,
            componentId: configuration.componentId,
            componentType: configuration.componentType,
            componentUuid: configuration.componentAttributes.smarteditComponentUuid,
            slotOperationRelatedId,
            slotOperationRelatedType
        });
        this.slotVisibilityService.reloadSlotsInfo();

        // This is necessary in case the component was used more than once in the page. If so, those instances need to be updated.
        this.crossFrameEventService.publish(EVENT_SMARTEDIT_COMPONENT_UPDATED, {
            componentId: configuration.componentId,
            componentType: configuration.componentType,
            requiresReplayingDecorators: true
        } as ComponentUpdatedEventInfo);

        const translation = this.translateService.instant(
            'se.cms.alert.component.removed.from.slot',
            {
                componentID: slotOperationRelatedId,
                slotID: configuration.slotId
            }
        );
        this.alertService.showSuccess({
            message: translation,
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });

        if (configuration.isComponentHidden) {
            await this.crossFrameEventService.publish(
                EVENT_PAGE_TREE_SLOT_NEED_UPDATE,
                configuration.slotId
            );
        }

        $event.preventDefault();
        $event.stopPropagation();
    }
    public async cloneCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        const componentUuid = configuration.componentAttributes.smarteditComponentUuid;

        if (!configuration.isComponentHidden) {
            let slotId = null;
            if (!!configuration.element) {
                slotId = this.componentHandlerService.getParentSlotForComponent(
                    configuration.element
                );
            } else {
                slotId = configuration.slotId;
            }

            const isSlotEditable = await this.slotRestrictionsService.isSlotEditable(slotId);
            if (isSlotEditable) {
                const hasCreatePermission =
                    await this.typePermissionsRestService.hasCreatePermissionForTypes([
                        configuration.componentType
                    ]);
                if (hasCreatePermission[configuration.componentType]) {
                    const component = await this.componentInfoService.getById(componentUuid, true);
                    return component.cloneable;
                } else {
                    return false;
                }
            }
            return false;
        }
        const hiddenComponent = await this.cmsitemsRestService.getById(componentUuid);
        return hiddenComponent.cloneable as boolean;
    }
    public async cloneCallback(configuration: IContextualMenuConfiguration): Promise<void> {
        const sourcePosition = this.componentHandlerService.getComponentPositionInSlot(
            configuration.slotId,
            configuration.componentAttributes.smarteditComponentId
        );
        await this.componentEditingFacade.cloneExistingComponentToSlot({
            targetSlotId: configuration.slotId,
            dragInfo: {
                componentId: configuration.componentAttributes.smarteditComponentId,
                componentType: configuration.componentType,
                componentUuid: configuration.componentAttributes.smarteditComponentUuid
            },
            position: sourcePosition + 1
        });
        return null;
    }

    public async dragCondition(configuration: IContextualMenuConfiguration): Promise<boolean> {
        let slotId = null;
        if (!!configuration.element) {
            slotId = this.componentHandlerService.getParentSlotForComponent(configuration.element);
        } else {
            slotId = configuration.slotId;
        }

        const slotEditable = await this.slotRestrictionsService.isSlotEditable(slotId);
        if (slotEditable) {
            const hasUpdatePermission =
                await this.typePermissionsRestService.hasUpdatePermissionForTypes([
                    configuration.componentType
                ]);
            return hasUpdatePermission[configuration.componentType];
        }
        return false;
    }
}
