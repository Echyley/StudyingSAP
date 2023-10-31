/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewRef } from '@angular/core';
import { DRAG_AND_DROP_EVENTS } from 'cmscommons';
import {
    CrossFrameEventService,
    EVENT_OPEN_IN_PAGE_TREE,
    YJQUERY_TOKEN,
    LogService,
    EVENT_OVERALL_REFRESH_TREE_NODE,
    EVENT_PART_REFRESH_TREE_NODE,
    Payload,
    EVENT_PAGE_TREE_SLOT_NEED_UPDATE
} from 'smarteditcommons';
import {
    SlotNode,
    NodeInfoService,
    INTERVAL_RETRIES,
    INTERVAL_MILLISEC
} from '../../../services/pageTree/NodeInfoService';

@Component({
    selector: 'se-page-tree',
    templateUrl: './PageTreePanel.html',
    styleUrls: ['./PageTreePanel.scss']
})
export class PageTreePanel implements OnInit, OnDestroy {
    private slotNodes: SlotNode[] = null;
    private scrollToNodeInterval: any = null;
    private expandComponentNodeInterval: any = null;
    private readonly unregisterOpenInPageTree: () => void;
    private readonly unregisterPartRefreshTreeNode: () => void;
    private readonly unregisterOverallRefreshTree: () => void;
    private readonly unregisterPageTreeSlotUpdate: () => void;
    private readonly unregisterScrollToModifiedSlot: () => void;

    constructor(
        private readonly crossFrameEventService: CrossFrameEventService,
        private readonly nodeInfoService: NodeInfoService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly cdr: ChangeDetectorRef,
        private readonly logService: LogService
    ) {
        this.unregisterOpenInPageTree = this.crossFrameEventService.subscribe(
            EVENT_OPEN_IN_PAGE_TREE,
            this.handleEventOpenInPageTree.bind(this)
        );

        this.unregisterPartRefreshTreeNode = this.crossFrameEventService.subscribe(
            EVENT_PART_REFRESH_TREE_NODE,
            async (_eventId: string, eventData?: Payload) => {
                this.slotNodes = await this.nodeInfoService.updatePartTreeNodesInfo(eventData);
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            }
        );

        this.unregisterOverallRefreshTree = this.crossFrameEventService.subscribe(
            EVENT_OVERALL_REFRESH_TREE_NODE,
            this.updateSlotNode.bind(this)
        );

        this.unregisterPageTreeSlotUpdate = this.crossFrameEventService.subscribe(
            EVENT_PAGE_TREE_SLOT_NEED_UPDATE,
            async (_eventId: string, eventData?: string) => {
                this.slotNodes = await this.nodeInfoService.updatePartTreeNodesInfoBySlotUuid(
                    eventData
                );
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            }
        );

        this.unregisterScrollToModifiedSlot = this.crossFrameEventService.subscribe(
            DRAG_AND_DROP_EVENTS.SCROLL_TO_MODIFIED_SLOT,
            (eventId: string, slotId: string) => {
                this.slotNodes &&
                    this.slotNodes.forEach((node) => {
                        if (node.componentId === slotId) {
                            node.isExpanded = true;
                            this.scrollToNode(node.elementUuid);
                        } else {
                            node.isExpanded = false;
                            node.componentNodes.forEach(
                                (component) => (component.isExpanded = false)
                            );
                        }
                    });
            }
        );
    }

    ngOnDestroy(): void {
        if (this.unregisterOpenInPageTree) {
            this.unregisterOpenInPageTree();
        }

        if (this.unregisterPartRefreshTreeNode) {
            this.unregisterPartRefreshTreeNode();
        }

        if (this.unregisterOverallRefreshTree) {
            this.unregisterOverallRefreshTree();
        }

        if (this.unregisterPageTreeSlotUpdate) {
            this.unregisterPageTreeSlotUpdate();
        }

        if (this.unregisterScrollToModifiedSlot) {
            this.unregisterScrollToModifiedSlot();
        }
    }

    async ngOnInit(): Promise<void> {
        await this.updateSlotNode();
    }

    onSlotExpanded(slot: SlotNode): void {
        this.slotNodes
            .filter((node) => slot.elementUuid !== node.elementUuid)
            .forEach((node) => {
                node.isExpanded = false;
                node.componentNodes.forEach((component) => (component.isExpanded = false));
            });
    }

    handleEventOpenInPageTree(eventId: string, elementUuid: string): void {
        clearInterval(this.expandComponentNodeInterval);
        if (!this.expandComponentNode(elementUuid)) {
            let retries = 0;
            this.expandComponentNodeInterval = setInterval(() => {
                if (this.expandComponentNode(elementUuid)) {
                    clearInterval(this.expandComponentNodeInterval);
                }

                if (retries > INTERVAL_RETRIES) {
                    this.logService.error(
                        `PageTreeComponent:: handleEventOpenInPageTree error: expand component node ${elementUuid} failed`
                    );
                    clearInterval(this.expandComponentNodeInterval);
                }
                retries++;
            }, INTERVAL_MILLISEC);
        }
    }

    private async updateSlotNode(): Promise<void> {
        this.slotNodes = await this.nodeInfoService.buildNodesInfo();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private expandComponentNode(elementUuid: string): boolean {
        if (this.slotNodes) {
            this.slotNodes.forEach((slot) => {
                let componentNode = null;

                slot.componentNodes.forEach((component) => {
                    if (component.elementUuid === elementUuid) {
                        component.isExpanded = true;
                        componentNode = component;
                    } else {
                        component.isExpanded = false;
                    }
                });

                if (componentNode) {
                    slot.isExpanded = true;
                    this.scrollToNodeByInterval(componentNode.elementUuid);
                } else {
                    slot.isExpanded = false;
                }
            });
            return true;
        }
        return false;
    }

    private scrollToNode(elementUuid: string): boolean {
        const element = this.yjQuery(`div[node-smartedit-element-uuid="${elementUuid}"]`)
            .get()
            .shift();
        if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            return true;
        }
        return false;
    }
    /*
     * Try to check if element is in viewport
     */
    private scrollToNodeByInterval(elementUuid: string): void {
        clearInterval(this.scrollToNodeInterval);
        if (!this.scrollToNode(elementUuid)) {
            let retries = 0;
            this.scrollToNodeInterval = setInterval(() => {
                if (this.scrollToNode(elementUuid)) {
                    clearInterval(this.scrollToNodeInterval);
                }
                if (retries > INTERVAL_RETRIES) {
                    this.logService.error(
                        `PageTreeComponent:: scrollToNodeByInterval error: scroll to tree node ${elementUuid} failed!`
                    );
                    clearInterval(this.scrollToNodeInterval);
                }
                retries++;
            }, INTERVAL_MILLISEC);
        }
    }
}
