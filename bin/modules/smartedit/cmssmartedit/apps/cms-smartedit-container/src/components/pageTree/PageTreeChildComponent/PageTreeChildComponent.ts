/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import { IContextualMenuConfiguration } from 'smarteditcommons';
import { NodeInfoService, ComponentNode } from '../../../services/pageTree/NodeInfoService';

@Component({
    selector: 'se-page-tree-child-component',
    templateUrl: './PageTreeChildComponent.html',
    styleUrls: ['../PageTreeComponent/PageTreeComponent.scss', '../PageTreeSlot/PageTreeSlot.scss']
})
export class PageTreeChildComponent {
    @Input() component: ComponentNode;
    @Input() parentMenuConfiguration: IContextualMenuConfiguration;
    @Input() slotId: string;
    @Input() slotUuid: string;

    constructor(private readonly nodeInfoService: NodeInfoService) {}

    async onClickComponentNode($event: Event, component: ComponentNode): Promise<void> {
        component.isExpanded = !component.isExpanded;

        if (component.isExpanded) {
            component.componentNodes = await this.nodeInfoService.getChildComponents(component);
        }
    }
}
