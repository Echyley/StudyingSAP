/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { DialogConfig } from '@fundamental-ngx/core';
import { TranslateService } from '@ngx-translate/core';
import { ComponentService, IContextAwareEditableItemService } from 'cmscommons';
import { isNil } from 'lodash';
import {
    stringUtils,
    GatewayProxied,
    GenericEditorStackService,
    IRenderService,
    Payload,
    CmsitemsRestService,
    CMSItem,
    IGenericEditorModalServiceComponent,
    IEditorModalService,
    IComponent
} from 'smarteditcommons';
import { GenericEditorModalService } from './GenericEditorModalService';

@GatewayProxied('open', 'openAndRerenderSlot', 'openGenericEditor')
@Injectable()
export class EditorModalService extends IEditorModalService {
    constructor(
        private genericEditorModalService: GenericEditorModalService,
        private componentService: ComponentService,
        private renderService: IRenderService,
        private contextAwareEditableItemService: IContextAwareEditableItemService,
        private cmsitemsRestService: CmsitemsRestService,
        private translateService: TranslateService,
        private genericEditorStackService: GenericEditorStackService
    ) {
        super();
    }

    public async openAndRerenderSlot(
        componentType: string,
        componentUuid: string,
        targetedQualifier: string,
        config?: DialogConfig,
        saveCallback?: (item: any) => void,
        editorStackId?: string
    ): Promise<any> {
        const componentAttributes: IComponent = {
            smarteditComponentType: componentType,
            smarteditComponentUuid: componentUuid
        };
        const attributes = await this.preloadContent(componentUuid, componentAttributes);

        const componentData = this.createComponentData(
            attributes,
            {
                targetedQualifier
            },
            editorStackId || stringUtils.generateIdentifier()
        );
        return this.markAndOpenGenericEditor(componentData, saveCallback, config);
    }

    public async open(
        componentAttributes: IComponent,
        targetSlotId?: string,
        position?: number,
        targetedQualifier?: string,
        saveCallback?: (item: any) => void,
        editorStackId?: string,
        config?: DialogConfig
    ): Promise<any> {
        const attributes = await this.preloadContent(
            componentAttributes.smarteditComponentUuid,
            componentAttributes
        );

        const componentData = this.createComponentData(
            attributes,
            {
                slotId: targetSlotId,
                position,
                targetedQualifier
            },
            editorStackId || stringUtils.generateIdentifier()
        );
        return this.markAndOpenGenericEditor(componentData, saveCallback, config);
    }

    public openGenericEditor(
        data: IGenericEditorModalServiceComponent,
        saveCallback?: () => void,
        errorCallback?: () => void,
        config?: DialogConfig
    ): Promise<any> {
        return this.genericEditorModalService.open(data, saveCallback, errorCallback, config);
    }

    /**
     * Loads content of the item by uuid and populates the content attribute of componentAttributes object only if it's not already provided.
     */
    private async preloadContent(
        uuid: string,
        componentAttributes: IComponent
    ): Promise<IComponent> {
        const component = await this.getContentByUuid(uuid);

        if (component !== null && isNil(componentAttributes.content)) {
            componentAttributes.content = component;
        }

        return componentAttributes;
    }

    /**
     * Opens generic editor.
     *
     * @param componentData Object that contains all parameters for generic editor.
     * Note: if the componentUuid is not provided the generic editor will be opened for creation.
     *
     * @param saveCallback the save callback that is triggered after submit.
     */
    private async markAndOpenGenericEditor(
        componentData: IGenericEditorModalServiceComponent,
        saveCallback?: (item: any) => void,
        config?: DialogConfig
    ): Promise<any> {
        await this.markComponentAsShared(componentData.content, componentData);

        return this.genericEditorModalService.open(
            componentData,
            (item: any) => {
                if (saveCallback) {
                    saveCallback(item);
                }

                if (componentData.editorStackId) {
                    const [topEditor] = this.genericEditorStackService.getEditorsStack(
                        componentData.editorStackId
                    );
                    const {
                        component: { uuid }
                    } = topEditor;

                    this.componentService
                        .getSlotsForComponent(uuid as string)
                        .then((slotIds: string[]) => {
                            this.renderService.renderSlots(slotIds);
                        });
                }
            },
            null,
            config
        );
    }

    /**
     * Retrieves a content object by its uuid. If the uuid is undefined, null is returned.
     */
    private async getContentByUuid(uuid: string): Promise<CMSItem> {
        if (!isNil(uuid)) {
            return this.cmsitemsRestService.getById(uuid);
        }
        return null;
    }

    /**
     * Verifies whether the component is shared in workflow context. If yes, then makes the component readonly and adds a message that the component
     * is used in different workflow. If no, checks whether the component is shared in page context and adds a message about it.
     */
    private async markComponentAsShared(
        componentContent: Payload,
        modelServiceParameters: IGenericEditorModalServiceComponent
    ): Promise<void> {
        if (!componentContent?.uid) {
            return;
        }

        const componentIsEditable = await this.contextAwareEditableItemService.isItemEditable(
            componentContent.uid as string
        );
        modelServiceParameters.readOnlyMode = !componentIsEditable;
        if (modelServiceParameters.readOnlyMode && this.componentIsShared(componentContent)) {
            this.addComponentInfoMessage(
                'se.cms.component.workflow.shared.component',
                modelServiceParameters
            );
        } else if (
            this.componentIsShared(componentContent) &&
            !modelServiceParameters.readOnlyMode
        ) {
            this.addComponentInfoMessage(
                'se.cms.component.shared.component',
                modelServiceParameters
            );
        } else if (
            !this.componentIsShared(componentContent) &&
            modelServiceParameters.readOnlyMode
        ) {
            this.addComponentInfoMessage(
                'se.cms.component.workflow.component',
                modelServiceParameters
            );
        }
    }

    /**
     * Verifies whether the component is shared or not by checking the slots attribute of the component payload.
     */
    private componentIsShared(componentContent: Payload): boolean {
        return (componentContent.slots as Payload[])?.length > 1;
    }

    /**
     * Adds a message to a messages attribute of IGenericEditorModalServiceComponent. If the attribute is undefined then the new one is created.
     */
    private addComponentInfoMessage(
        message: string,
        modelServiceParameters: IGenericEditorModalServiceComponent
    ): void {
        modelServiceParameters.messages = modelServiceParameters.messages || [];
        modelServiceParameters.messages.push({
            type: 'info',
            message: this.translateService.instant(message)
        });
    }

    private prepareContentForCreate(
        content: Payload,
        componentType: string,
        catalogVersionUuid: string,
        slotId: string,
        position: number
    ): Payload {
        const preparedContent = content ? Object.assign({}, content) : {};
        preparedContent.position = !stringUtils.isBlank(preparedContent.position)
            ? preparedContent.position
            : position;
        preparedContent.slotId = preparedContent.slotId || slotId;
        preparedContent.typeCode = preparedContent.typeCode || componentType;
        preparedContent.itemtype = preparedContent.itemtype || componentType;
        preparedContent.catalogVersion = preparedContent.catalogVersion || catalogVersionUuid;
        preparedContent.visible = !stringUtils.isBlank(preparedContent.visible)
            ? preparedContent.visible
            : true;
        return preparedContent;
    }

    private createComponentData(
        componentAttributes: IComponent,
        params: IComponentAttributes,
        editorStackId?: string
    ): IGenericEditorModalServiceComponent {
        let type: string;
        try {
            type = componentAttributes.smarteditComponentType.toLowerCase();
        } catch (error) {
            throw new Error(
                `editorModalService.createComponentData - invalid component type in componentAttributes. ${error}`
            );
        }

        const isCreateOperation = isNil(componentAttributes.smarteditComponentUuid);
        let content: Payload;
        if (isCreateOperation) {
            content = this.prepareContentForCreate(
                componentAttributes.content,
                componentAttributes.smarteditComponentType,
                componentAttributes.catalogVersionUuid,
                params.slotId,
                params.position
            );
        } else {
            content = componentAttributes.content;
        }
        return {
            componentUuid: componentAttributes.smarteditComponentUuid,
            componentType: componentAttributes.smarteditComponentType,
            title: `type.${type}.name`,
            targetedQualifier: params.targetedQualifier,
            initialDirty: componentAttributes.initialDirty,
            content,
            editorStackId
        };
    }
}

interface IComponentAttributes {
    position?: number;
    slotId?: string;
    targetedQualifier?: string;
}
