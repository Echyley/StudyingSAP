/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICMSComponent, ISlotVisibilityService } from 'cmscommons';
import {
    LogService,
    PageTreeNode,
    CmsitemsRestService,
    stringUtils,
    CrossFrameEventService,
    IPageTreeNodeService,
    Payload,
    EVENT_PAGE_TREE_SLOT_SELECTED,
    EVENT_PAGE_TREE_COMPONENT_SELECTED,
    ComponentAttributes,
    IContextualMenuConfiguration
} from 'smarteditcommons';

export const INTERVAL_RETRIES = 20;
export const INTERVAL_MILLISEC = 300;
export interface SlotNode extends PageTreeNode {
    componentNodes: ComponentNode[];
    hiddenComponentCount: number;
}

export interface ComponentNode extends ICMSComponent {
    isHidden: boolean;
    isExpanded: boolean;
    componentNodes?: ComponentNode[];
    componentId?: string;
    componentUuid?: string;
    componentTypeFromPage?: string;
    containerId?: string;
    containerType?: string;
    catalogVersionUuid?: string;
    elementUuid?: string;
}

/**
 * Used to build extra information for page tree nodes such as get slot node's hidden components,
 *  get component node's information from backend by uuid
 * */

@Injectable()
export class NodeInfoService {
    protected treeNodes: SlotNode[];
    constructor(
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly cmsitemsRestService: CmsitemsRestService,
        private readonly logService: LogService,
        private readonly slotVisibilityService: ISlotVisibilityService,
        private readonly pageTreeNodeService: IPageTreeNodeService
    ) {
        this.treeNodes = [];
    }

    public async buildNodesInfo(): Promise<SlotNode[]> {
        const nodes = await this.pageTreeNodeService.getSlotNodes();
        this.treeNodes = [];
        this.treeNodes = await Promise.all(
            nodes.map(async (node) => this.addMoreInfoToOneNode(node))
        );

        return this.treeNodes;
    }

    public async updatePartTreeNodesInfo(updatedNodes: Payload): Promise<SlotNode[]> {
        await Promise.all(
            Object.keys(updatedNodes).map(async (key) => {
                const index = this.treeNodes.findIndex((node) => node.elementUuid === key);
                if (index > -1) {
                    const node = this.treeNodes[index];
                    node.childrenNode = updatedNodes[key] as PageTreeNode[];
                    this.treeNodes[index] = await this.addMoreInfoToOneNode(node);
                }
            })
        );
        return this.treeNodes;
    }

    public async updatePartTreeNodesInfoBySlotUuid(slotId: string): Promise<SlotNode[]> {
        const index = this.treeNodes.findIndex((node) => node.componentId === slotId);
        if (index > -1) {
            const node = this.treeNodes[index];
            this.treeNodes[index] = await this.addMoreInfoToOneNode(node);
        }
        return this.treeNodes;
    }

    public publishComponentSelected(
        component: ComponentNode,
        activeSlot: boolean,
        slotElementUuid: string
    ): void {
        // Should active the slot of this component
        if (activeSlot) {
            this.crossFrameEventService.publish(EVENT_PAGE_TREE_SLOT_SELECTED, {
                elementUuid: slotElementUuid,
                active: true
            });
        }

        // Active the component itself
        this.crossFrameEventService.publish(EVENT_PAGE_TREE_COMPONENT_SELECTED, {
            elementUuid: component.elementUuid,
            active: component.isExpanded
        });
    }

    public publishSlotSelected(slot: SlotNode): void {
        // Active the slot itself
        this.crossFrameEventService.publish(EVENT_PAGE_TREE_SLOT_SELECTED, {
            elementUuid: slot.elementUuid,
            active: slot.isExpanded
        });

        // To make other active component become inactive
        this.crossFrameEventService.publish(EVENT_PAGE_TREE_COMPONENT_SELECTED, {
            elementUuid: slot.elementUuid,
            active: slot.isExpanded
        });
    }

    public async getChildComponents(component: ComponentNode): Promise<ComponentNode[]> {
        const childUuids = component.children.map((child) =>
            typeof child === 'string' ? child : child.uuid
        );
        const displayComponents = await this.getComponentsDataByUUIDs(childUuids);
        return displayComponents.map((displayComponent) =>
            this.buildComponentNode(
                displayComponent,
                !displayComponent.visible || displayComponent.restricted
            )
        );
    }

    public getComponentAttributes(component: ComponentNode): ComponentAttributes {
        return {
            smarteditCatalogVersionUuid: component.catalogVersion,
            smarteditComponentId: component.uid,
            smarteditComponentType: component.typeCode,
            smarteditComponentUuid: component.uuid,
            smarteditElementUuid: component.elementUuid
        };
    }

    public getContextualMenuConfiguration(
        component: ComponentNode,
        componentAttributes: ComponentAttributes,
        slotId: string,
        slotUuid: string
    ): IContextualMenuConfiguration {
        return {
            componentType: component.typeCode,
            componentId: component.uid,
            componentUuid: component.uuid,
            containerType: component.containerType,
            containerId: component.containerId,
            isComponentHidden: component.isHidden,
            componentAttributes,
            slotId,
            slotUuid
        };
    }

    private async addMoreInfoToOneNode(node: PageTreeNode): Promise<SlotNode> {
        const hiddenComponents = await this.slotVisibilityService.getHiddenComponents(
            node.componentId
        );
        const hiddenComponentCount = hiddenComponents.length;

        const childUuids = node.childrenNode.map((child) => child.componentUuid);
        const displayComponents = await this.getComponentsDataByUUIDs(childUuids);
        let componentNodes = displayComponents.map((component) =>
            this.buildDisplayComponentNode(component, node)
        );
        componentNodes = componentNodes.concat(
            hiddenComponents.map((component) => this.buildComponentNode(component, true))
        );

        return { ...node, componentNodes, hiddenComponentCount };
    }

    private async getComponentsDataByUUIDs(uuids: string[]): Promise<ICMSComponent[]> {
        try {
            const data = await this.cmsitemsRestService.getByIds<ICMSComponent>(uuids, 'DEFAULT');
            return Promise.resolve((data.response ? data.response : [data]) as ICMSComponent[]);
        } catch (error) {
            this.logService.error('SlotInfoService:: getSlotsDataByUUIDs error:', error.message);
            return Promise.reject();
        }
    }

    private buildComponentNode(component: ICMSComponent, isHidden: boolean): ComponentNode {
        const elementUuid = stringUtils.generateIdentifier();
        return {
            isExpanded: false,
            componentNodes: [],
            isHidden,
            elementUuid,
            ...component
        };
    }

    private buildDisplayComponentNode(component: ICMSComponent, slot: PageTreeNode): ComponentNode {
        const node = slot.childrenNode.find((child) => child.componentUuid === component.uuid);
        const {
            componentId,
            componentUuid,
            componentTypeFromPage,
            containerId,
            containerType,
            catalogVersionUuid,
            elementUuid
        } = node;
        return {
            isHidden: false,
            isExpanded: false,
            componentId,
            componentUuid,
            componentTypeFromPage,
            containerId,
            containerType,
            catalogVersionUuid,
            elementUuid,
            componentNodes: [],
            ...component
        };
    }
}
