/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject } from '@angular/core';
import {
    PageTreeNode,
    IPageTreeNodeService,
    YJQUERY_TOKEN,
    ID_ATTRIBUTE,
    TYPE_ATTRIBUTE,
    CONTAINER_ID_ATTRIBUTE,
    CATALOG_VERSION_UUID_ATTRIBUTE,
    UUID_ATTRIBUTE,
    CONTAINER_TYPE_ATTRIBUTE,
    GatewayProxied,
    IComponentHandlerService,
    stringUtils,
    ELEMENT_UUID_ATTRIBUTE,
    LogService,
    EVENT_PART_REFRESH_TREE_NODE,
    CrossFrameEventService,
    AggregatedNode,
    EVENT_OVERALL_REFRESH_TREE_NODE,
    CONTENT_SLOT_TYPE,
    WindowUtils,
    Payload
} from 'smarteditcommons';

@GatewayProxied('getSlotNodes', 'scrollToElement', 'existedSmartEditElement')
export class PageTreeNodeService extends IPageTreeNodeService {
    constructor(
        private readonly componentHandlerService: IComponentHandlerService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly windowUtils: WindowUtils,
        private readonly logService: LogService
    ) {
        super();
    }

    buildSlotNodes(): void {
        this.slotNodes = this.buildSlotNodesByElement(this.yjQuery('body'));
        this.crossFrameEventService.publish(EVENT_OVERALL_REFRESH_TREE_NODE);
    }

    updateSlotNodes(nodes: AggregatedNode[]): void {
        if (this.slotNodes && nodes.length > 0) {
            const parentSet = new Set();
            const updatedSlotNodes: Payload = {};

            // If there is slot add or remove, it is necessary to rebuild the entire page
            if (this.hasSlotNode(nodes)) {
                this.buildSlotNodes();
                return;
            }

            // Process components update
            nodes.forEach((node) => {
                if (parentSet.has(node.parent) || !node.parent) {
                    return;
                }
                parentSet.add(node.parent);
                const element: JQuery = this.yjQuery(node.parent);
                const parentUUid = element.attr(ELEMENT_UUID_ATTRIBUTE);

                if (!parentUUid) {
                    return;
                }
                const childrenNode = this.buildSlotNodesByElement(element);
                updatedSlotNodes[parentUUid] = childrenNode as Payload[];
                this.slotNodes.forEach((item) => {
                    if (item.elementUuid === parentUUid) {
                        item.childrenNode = childrenNode;
                    }
                });
            });
            this.crossFrameEventService.publish(EVENT_PART_REFRESH_TREE_NODE, updatedSlotNodes);
        }
    }

    getSlotNodes(): Promise<PageTreeNode[]> {
        return Promise.resolve(this.slotNodes);
    }

    /*
     * If element is not in viewport or element bottom is in viewport scroll element into view
     */
    scrollToElement(elementUuid: string): Promise<void> {
        const element = this.yjQuery(`[data-smartedit-element-uuid="${elementUuid}"]`)
            .get()
            .shift();
        if (element) {
            if (this.elementTopInViewport(element) || this.elementCenterInViewport(element)) {
                return;
            }
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else {
            this.logService.error(`data-smartedit-element-uuid="${elementUuid}" is not existed!`);
        }
    }

    existedSmartEditElement(elementUuid: string): Promise<boolean> {
        const element = this.yjQuery(
            `smartedit-element[data-smartedit-element-uuid="${elementUuid}"]`
        )
            .get()
            .shift();
        return Promise.resolve(!!element);
    }

    handleBodyWidthChange(): void {
        const slotNodes = this.buildSlotNodesByElement(this.yjQuery('body'));
        let changed = false;
        if (this.slotNodes.length !== slotNodes.length) {
            changed = true;
        } else {
            for (let i = 0; i < this.slotNodes.length; i++) {
                changed = !this.areSameNodes(this.slotNodes[i], slotNodes[i]);
                if (changed) {
                    break;
                }
            }
        }

        if (changed) {
            this.slotNodes = slotNodes;
            this.crossFrameEventService.publish(EVENT_OVERALL_REFRESH_TREE_NODE);
        }
    }

    protected _buildSlotNode(node: HTMLElement): PageTreeNode {
        const element: JQuery = this.yjQuery(node);
        const childrenNode: PageTreeNode[] = Array.from(
            this.componentHandlerService.getFirstSmartEditComponentChildren(element) as any
        )
            .filter((child: HTMLElement) => this._isValidElement(child))
            .map((firstLevelComponent: HTMLElement) => this._buildSlotNode(firstLevelComponent));

        if (!element.attr(ELEMENT_UUID_ATTRIBUTE)) {
            element.attr(ELEMENT_UUID_ATTRIBUTE, stringUtils.generateIdentifier());
        }

        return {
            componentId: element.attr(ID_ATTRIBUTE),
            componentUuid: element.attr(UUID_ATTRIBUTE),
            componentTypeFromPage: element.attr(TYPE_ATTRIBUTE),
            catalogVersionUuid: element.attr(CATALOG_VERSION_UUID_ATTRIBUTE),
            containerId: element.attr(CONTAINER_ID_ATTRIBUTE),
            containerType: element.attr(CONTAINER_TYPE_ATTRIBUTE),
            elementUuid: element.attr(ELEMENT_UUID_ATTRIBUTE),
            isExpanded: false,
            childrenNode
        };
    }

    protected _isValidElement(ele: HTMLElement): boolean {
        const element: JQuery = this.yjQuery(ele);

        if (!element || !element.is(':visible')) {
            return false;
        }

        return !(
            !element.attr(ID_ATTRIBUTE) ||
            !element.attr(UUID_ATTRIBUTE) ||
            !element.attr(TYPE_ATTRIBUTE) ||
            !element.attr(CATALOG_VERSION_UUID_ATTRIBUTE)
        );
    }

    private areSameNodes(slotNodeA: PageTreeNode, slotNodeB: PageTreeNode): boolean {
        let same = true;
        if (
            slotNodeA.elementUuid !== slotNodeB.elementUuid ||
            slotNodeA.childrenNode.length !== slotNodeB.childrenNode.length
        ) {
            same = false;
        } else {
            for (let j = 0; j < slotNodeA.childrenNode.length; j++) {
                if (
                    slotNodeA.childrenNode[j].elementUuid !== slotNodeB.childrenNode[j].elementUuid
                ) {
                    same = false;
                    break;
                }
            }
        }

        return same;
    }

    private hasSlotNode(nodes: AggregatedNode[]): boolean {
        const slotNode = nodes.find((node) => {
            if (node.node) {
                const element: JQuery = this.yjQuery(node.node);
                return element.attr(TYPE_ATTRIBUTE) === CONTENT_SLOT_TYPE;
            } else {
                return false;
            }
        });
        return !!slotNode;
    }

    private elementTopInViewport(element): boolean {
        const rect = element.getBoundingClientRect();
        return (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.top <
                (this.windowUtils.getWindow().innerHeight ||
                    document.documentElement.clientHeight) &&
            rect.left <
                (this.windowUtils.getWindow().innerWidth || document.documentElement.clientWidth)
        );
    }

    private elementCenterInViewport(element): boolean {
        const rect = element.getBoundingClientRect();
        return (
            rect.top < 0 &&
            rect.left < 0 &&
            rect.bottom >
                (this.windowUtils.getWindow().innerHeight ||
                    document.documentElement.clientHeight) &&
            rect.right >
                (this.windowUtils.getWindow().innerWidth || document.documentElement.clientWidth)
        );
    }

    private buildSlotNodesByElement(element): PageTreeNode[] {
        return Array.from(
            this.componentHandlerService.getFirstSmartEditComponentChildren(element) as any
        )
            .filter((child: HTMLElement) => this._isValidElement(child))
            .map((firstLevelComponent: HTMLElement) => this._buildSlotNode(firstLevelComponent));
    }
}
