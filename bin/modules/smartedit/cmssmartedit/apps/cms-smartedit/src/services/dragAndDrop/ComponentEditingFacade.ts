/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    ComponentService,
    IComponentVisibilityAlertService,
    PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI,
    ICMSComponent,
    ISlotVisibilityService,
    COMPONENT_CREATED_EVENT,
    COMPONENT_UPDATED_EVENT,
    SlotInfo,
    CloneComponentInfo,
    DragInfo,
    IComponentEditingFacade
} from 'cmscommons';
import {
    CrossFrameEventService,
    EVENT_SMARTEDIT_COMPONENT_UPDATED,
    IAlertConfig,
    IAlertService,
    IExperience,
    IPageInfoService,
    IRenderService,
    IRestService,
    IRestServiceFactory,
    ISharedDataService,
    LogService,
    objectUtils,
    SystemEventService,
    EXPERIENCE_STORAGE_KEY,
    CMSItem,
    ErrorResponse,
    IEditorModalService,
    GatewayProxied
} from 'smarteditcommons';

const contentSlotComponentsResourceLocation = `${PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI}/pages/:pageId/contentslots/:currentSlotId/components/:componentId`;

/**
 * This service provides methods that allow adding or removing components in the page.
 */
@GatewayProxied(
    'addNewComponentToSlot',
    'addExistingComponentToSlot',
    'cloneExistingComponentToSlot',
    'moveComponent'
)
@Injectable()
export class ComponentEditingFacade extends IComponentEditingFacade {
    private contentSlotComponentsRestService: IRestService<void>;

    constructor(
        private alertService: IAlertService,
        private componentService: ComponentService,
        private componentVisibilityAlertService: IComponentVisibilityAlertService,
        private crossFrameEventService: CrossFrameEventService,
        private editorModalService: IEditorModalService,
        private logService: LogService,
        private pageInfoService: IPageInfoService,
        private renderService: IRenderService,
        private restServiceFactory: IRestServiceFactory,
        private slotVisibilityService: ISlotVisibilityService,
        private sharedDataService: ISharedDataService,
        private systemEventService: SystemEventService,
        private translateService: TranslateService
    ) {
        super();
    }

    /**
     * Adds a new component to the slot and opens a component modal to edit its properties.
     *
     * @param slotInfo The target slot for the new component.
     * @param catalogVersionUuid The catalog version on which to create the new component
     * @param componentType The type of the new component to add.
     * @param position The position in the slot where to add the new component.
     *
     */
    public async addNewComponentToSlot(
        slotInfo: SlotInfo,
        catalogVersionUuid: string,
        componentType: string,
        position: number
    ): Promise<void> {
        const componentAttributes = {
            smarteditComponentType: componentType,
            catalogVersionUuid
        };
        const editedComponent = await this.editorModalService.open<ICMSComponent>(
            componentAttributes,
            slotInfo.targetSlotUUId,
            position
        );

        this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
            itemId: editedComponent.uuid,
            itemType: editedComponent.itemtype,
            catalogVersion: editedComponent.catalogVersion,
            restricted: editedComponent.restricted,
            slotId: slotInfo.targetSlotId,
            visible: editedComponent.visible
        });

        this.crossFrameEventService.publish(COMPONENT_CREATED_EVENT, editedComponent);

        return this.renderSlots(
            [slotInfo.targetSlotId],
            editedComponent.uid,
            slotInfo.targetSlotId,
            true
        );
    }

    /**
     * Adds an existing component to the slot and display an Alert whenever the component is either hidden or restricted.
     *
     * @param targetSlotId The ID of the slot where to drop the component.
     * @param dragInfo The dragInfo object containing the componentId, componentUuid and componentType.
     * @param position The position in the slot where to add the component.
     */
    public async addExistingComponentToSlot(
        targetSlotId: string,
        dragInfo: DragInfo,
        position: number
    ): Promise<void> {
        const pageId = await this.pageInfoService.getPageUID();

        let item: CMSItem;
        try {
            await this.componentService.addExistingComponent(
                pageId,
                dragInfo.componentId,
                targetSlotId,
                position
            );
            item = await this.componentService.loadComponentItem(dragInfo.componentUuid);
        } catch (error) {
            this.generateAndAlertErrorMessage(dragInfo.componentId, targetSlotId, error);
            return Promise.reject();
        }

        this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
            itemId: dragInfo.componentUuid,
            itemType: dragInfo.componentType,
            catalogVersion: item.catalogVersion,
            restricted: item.restricted as boolean,
            slotId: targetSlotId,
            visible: item.visible as boolean
        });

        // 1. First update the cache.
        this.systemEventService.publish(COMPONENT_UPDATED_EVENT, item);

        // 2. Then replay decorators (via EVENT_SMARTEDIT_COMPONENT_UPDATED).
        // This is important because there might be existing instances of the component in the page that need to
        // be updated. For example, if the component was not shared, it would not show the SharedComponent contextual button.
        // However, if a user adds another instance into the page then the component becomes shared. Both instances of the
        // component must show that the component is shared now. Thus, the first instance needs to be updated too.
        this.crossFrameEventService.publish(EVENT_SMARTEDIT_COMPONENT_UPDATED, {
            componentId: dragInfo.componentId,
            componentType: dragInfo.componentType,
            componentUuid: dragInfo.componentUuid,
            requiresReplayingDecorators: true
        });

        return this.renderSlots(targetSlotId, dragInfo.componentId, targetSlotId, true);
    }

    /**
     * This methods clones an existing component to the slot by opening a component modal to edit its properties.
     */
    public async cloneExistingComponentToSlot(componentInfo: CloneComponentInfo): Promise<void> {
        const componentItem = await this.componentService
            .loadComponentItem(componentInfo.dragInfo.componentUuid)
            .catch((error) => {
                this.generateAndAlertErrorMessage(
                    componentInfo.dragInfo.componentId,
                    componentInfo.targetSlotId,
                    error
                );
                return Promise.reject();
            });

        const experience = (await this.sharedDataService.get(
            EXPERIENCE_STORAGE_KEY
        )) as IExperience;
        const component = objectUtils.copy(componentItem);
        // While cloning an existing components, remove some parameters, reset catalogVersion to the version of the page.
        // If cloning an existing component, prefix name and drop restrictions - doing this here will make generic editor dirty and enable save by default.
        component.componentUuid = component.uuid;
        component.cloneComponent = true;
        component.catalogVersion = experience.pageContext.catalogVersionUuid;
        component.name = `${this.translateService.instant(
            'se.cms.component.name.clone.of.prefix'
        )} ${component.name}`;

        delete component.uuid;
        delete component.uid;
        delete component.slots;
        delete component.restrictions;
        delete component.creationtime;
        delete component.modifiedtime;

        const componentAttributes = {
            smarteditComponentType: componentInfo.dragInfo.componentType,
            catalogVersionUuid: experience.pageContext.catalogVersionUuid,
            content: objectUtils.copy(component),
            initialDirty: true
        };
        const updatedComponent = await this.editorModalService.open(
            componentAttributes,
            componentInfo.targetSlotId,
            componentInfo.position
        );

        this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
            itemId: updatedComponent.uuid,
            itemType: updatedComponent.itemtype,
            catalogVersion: updatedComponent.catalogVersion,
            restricted: updatedComponent.restricted,
            slotId: componentInfo.targetSlotId,
            visible: updatedComponent.visible
        });

        this.crossFrameEventService.publish(COMPONENT_CREATED_EVENT, updatedComponent);

        return this.renderSlots(
            componentInfo.targetSlotId,
            updatedComponent.uid,
            componentInfo.targetSlotId,
            true
        );
    }

    /**
     * This methods moves a component from two slots in a page.
     *
     * @param sourceSlotId The ID of the slot where the component is initially located.
     * @param targetSlotId The ID of the slot where to drop the component.
     * @param componentId The ID of the component to add into the slot.
     * @param position The position in the slot where to add the component.
     */
    public async moveComponent(
        sourceSlotId: string,
        targetSlotId: string,
        componentId: string,
        position: number
    ): Promise<void> {
        this.contentSlotComponentsRestService =
            this.contentSlotComponentsRestService ||
            this.restServiceFactory.get(contentSlotComponentsResourceLocation, 'componentId');

        const pageId = await this.pageInfoService.getPageUID();

        try {
            await this.contentSlotComponentsRestService.update({
                pageId,
                currentSlotId: sourceSlotId,
                componentId,
                slotId: targetSlotId,
                position
            });
        } catch (error) {
            this.generateAndAlertErrorMessage(componentId, targetSlotId, error, {
                message: 'se.cms.draganddrop.move.failed',
                messagePlaceholders: {
                    slotID: targetSlotId,
                    componentID: componentId
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
            return Promise.reject();
        }
        return this.renderSlots([sourceSlotId, targetSlotId], componentId, targetSlotId);
    }

    private generateAndAlertSuccessMessage(sourceComponentId: string, targetSlotId: string): void {
        this.alertService.showSuccess({
            message: 'se.cms.draganddrop.success',
            messagePlaceholders: {
                sourceComponentId,
                targetSlotId
            },
            minWidth: '',
            mousePersist: true,
            duration: 1000,
            dismissible: true,
            width: '300px'
        });
    }

    private generateAndAlertErrorMessage(
        sourceComponentId: string,
        targetSlotId: string,
        requestResponse: ErrorResponse,
        alertConf?: IAlertConfig
    ): void {
        if (this.hasErrorResponseErrors(requestResponse)) {
            this.alertService.showDanger({
                message: 'se.cms.draganddrop.error',
                messagePlaceholders: {
                    sourceComponentId,
                    targetSlotId,
                    detailedError: requestResponse.error.errors[0].message
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        } else if (alertConf) {
            this.alertService.showDanger(alertConf);
        }
    }

    private hasErrorResponseErrors(response: ErrorResponse): boolean {
        return !!(response?.error?.errors?.length > 0);
    }

    private async renderSlots(
        slots: string | string[],
        sourceComponentId: string,
        targetSlotId: string,
        showSuccess?: boolean
    ): Promise<void> {
        try {
            await this.renderService.renderSlots(slots);
        } catch (error) {
            this.logService.error(
                `${this.constructor.name}.renderSlots::renderService.renderSlots - targetSlotId:`,
                targetSlotId
            );
            this.logService.error(error);

            this.generateAndAlertErrorMessage(sourceComponentId, targetSlotId, error);
            return Promise.reject(error);
        }

        try {
            await this.slotVisibilityService.reloadSlotsInfo();

            if (showSuccess) {
                this.generateAndAlertSuccessMessage(sourceComponentId, targetSlotId);
            }
        } catch (error) {
            this.logService.error(
                `${this.constructor.name}.renderSlots::slotVisibilityService.reloadSlotsInfo`
            );
            this.logService.error(error);
            return Promise.reject(error);
        }
        return Promise.resolve();
    }
}
