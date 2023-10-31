/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import {
    IPageTreeNodeService,
    LogService,
    SystemEventService,
    PAGE_TREE_SLOT_EXPANDED_EVENT,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import {
    SlotNode,
    INTERVAL_RETRIES,
    INTERVAL_MILLISEC,
    ComponentNode,
    NodeInfoService
} from '../../../services/pageTree/NodeInfoService';

@Component({
    selector: 'se-page-tree-slot',
    templateUrl: './PageTreeSlot.html',
    styleUrls: ['./PageTreeSlot.scss']
})
export class PageTreeSlot implements OnDestroy {
    @Input() node: SlotNode;
    @Output() onSlotExpanded: EventEmitter<SlotNode> = new EventEmitter();
    private publishSlotInterval: any = null;
    private readonly unregisterPageTreeSlotExpanded: () => void;

    constructor(
        private readonly pageTreeNodeService: IPageTreeNodeService,
        private readonly nodeInfoService: NodeInfoService,
        private readonly systemEventService: SystemEventService,
        private readonly logService: LogService,
        private readonly userTrackingService: IUserTrackingService
    ) {
        this.unregisterPageTreeSlotExpanded = this.systemEventService.subscribe(
            PAGE_TREE_SLOT_EXPANDED_EVENT,
            (eventId: string, elementUuid: string) => {
                if (elementUuid === this.node.elementUuid) {
                    this.node.isExpanded = true;
                }
            }
        );
    }

    ngOnDestroy(): void {
        if (this.unregisterPageTreeSlotExpanded) {
            this.unregisterPageTreeSlotExpanded();
        }
    }

    async onClickSlotNode($event: Event): Promise<void> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.PAGE_STRUCTURE,
            'Slot'
        );

        clearInterval(this.publishSlotInterval);
        this.node.isExpanded = !this.node.isExpanded;

        if (!this.node.isExpanded) {
            this.node.componentNodes.forEach((component) => (component.isExpanded = false));
            this.nodeInfoService.publishSlotSelected(this.node);
        } else {
            this.onSlotExpanded.emit(this.node);
            // In case the active slot is not in viewport
            await this.pageTreeNodeService.scrollToElement(this.node.elementUuid);

            if (!(await this.checkSlotAndPublishSelected())) {
                let retries = 0;
                this.publishSlotInterval = setInterval(async () => {
                    if (await this.checkSlotAndPublishSelected()) {
                        clearInterval(this.publishSlotInterval);
                    }
                    if (retries > INTERVAL_RETRIES) {
                        this.logService.info(
                            `PageTreeSlot:: onClickSlotNode error: smartedit-element ${this.node.elementUuid} is not existed`
                        );
                        clearInterval(this.publishSlotInterval);
                    }
                    retries++;
                }, INTERVAL_MILLISEC);
            }
        }
    }

    onComponentExpanded(component: ComponentNode): void {
        this.node.componentNodes
            .filter((node) => node.elementUuid !== component.elementUuid)
            .forEach((node) => {
                if (node.isExpanded) {
                    node.isExpanded = false;
                    this.nodeInfoService.publishComponentSelected(
                        node,
                        false,
                        this.node.elementUuid
                    );
                }
            });
    }

    getNodeNavigationIconTitle(): string {
        return this.node.isExpanded
            ? 'se.cms.pagetree.slot.node.navigation.icon.collapse.title'
            : 'se.cms.pagetree.slot.node.navigation.icon.expand.title';
    }

    private async checkSlotAndPublishSelected(): Promise<boolean> {
        const existedSmartEditElement = await this.pageTreeNodeService.existedSmartEditElement(
            this.node.elementUuid
        );
        if (existedSmartEditElement) {
            this.nodeInfoService.publishSlotSelected(this.node);
            return true;
        }
        return false;
    }
}
