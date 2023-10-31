/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { IComponentMenuConditionAndCallbackService } from 'cmscommons';
import {
    IPageTreeNodeService,
    LogService,
    IContextualMenuConfiguration,
    IPermissionService
} from 'smarteditcommons';
import {
    NodeInfoService,
    ComponentNode,
    INTERVAL_RETRIES,
    INTERVAL_MILLISEC
} from '../../../services/pageTree/NodeInfoService';

@Component({
    selector: 'se-page-tree-component',
    templateUrl: './PageTreeComponent.html',
    styleUrls: ['./PageTreeComponent.scss', '../PageTreeSlot/PageTreeSlot.scss']
})
export class PageTreeComponent implements OnInit {
    @Input() component: ComponentNode;
    @Input() slotId: string;
    @Input() slotUuid: string;
    @Input() slotElementUuid: string;
    @Input() slotCatalogVersionUuid: string;
    @Output() onComponentExpanded: EventEmitter<ComponentNode> = new EventEmitter();
    public draggable: boolean;
    public menuConfiguration: IContextualMenuConfiguration;

    private publishComponentInterval: any = null;

    constructor(
        private readonly pageTreeNodeService: IPageTreeNodeService,
        private readonly nodeInfoService: NodeInfoService,
        private readonly logService: LogService,
        private readonly componentMenuConditionAndCallbackService: IComponentMenuConditionAndCallbackService,
        private readonly permissionService: IPermissionService
    ) {}

    async ngOnInit(): Promise<void> {
        const componentAttributes = this.nodeInfoService.getComponentAttributes(this.component);
        this.menuConfiguration = this.nodeInfoService.getContextualMenuConfiguration(
            this.component,
            componentAttributes,
            this.slotId,
            this.slotUuid
        );

        if (this.component.isHidden) {
            this.draggable = false;
        } else {
            const allowed = await this.permissionService.isPermitted([
                {
                    names: ['se.context.menu.drag.and.drop.component']
                }
            ]);

            this.draggable =
                allowed &&
                (await this.componentMenuConditionAndCallbackService.dragCondition(
                    this.menuConfiguration
                ));
        }
    }

    async onClickComponentNode($event: Event): Promise<void> {
        clearInterval(this.publishComponentInterval);
        this.component.isExpanded = !this.component.isExpanded;

        if (this.component.isExpanded) {
            this.component.componentNodes = await this.nodeInfoService.getChildComponents(
                this.component
            );
            this.onComponentExpanded.emit(this.component);

            // In case the active component is not in viewport
            await this.pageTreeNodeService.scrollToElement(this.component.elementUuid);

            if (!(await this.checkComponentAndPublishSelected())) {
                let retries = 0;
                this.publishComponentInterval = setInterval(async () => {
                    if (await this.checkComponentAndPublishSelected()) {
                        clearInterval(this.publishComponentInterval);
                    }
                    if (retries > INTERVAL_RETRIES) {
                        this.logService.info(
                            `PageTreeComponent:: onClickComponentNode error: smartedit-element ${this.component.elementUuid} is not existed`
                        );
                        clearInterval(this.publishComponentInterval);
                    }
                    retries++;
                }, INTERVAL_MILLISEC);
            }
        } else {
            this.nodeInfoService.publishComponentSelected(
                this.component,
                false,
                this.slotElementUuid
            );
        }
    }

    getComponentNavigationIconTitle(): string {
        return this.component.isExpanded
            ? 'se.cms.pagetree.slot.node.navigation.icon.collapse.title'
            : 'se.cms.pagetree.slot.node.navigation.icon.expand.title';
    }

    private async checkComponentAndPublishSelected(): Promise<boolean> {
        const existedSmartEditElement = await this.pageTreeNodeService.existedSmartEditElement(
            this.component.elementUuid
        );
        if (existedSmartEditElement) {
            this.nodeInfoService.publishComponentSelected(
                this.component,
                true,
                this.slotElementUuid
            );
            return true;
        }
        return false;
    }
}
