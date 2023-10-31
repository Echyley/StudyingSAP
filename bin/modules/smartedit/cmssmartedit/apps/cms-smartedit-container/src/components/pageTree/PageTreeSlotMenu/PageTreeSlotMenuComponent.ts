/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnInit,
    ViewRef
} from '@angular/core';
import {
    ComponentAttributes,
    IContextualMenuButton,
    IContextualMenuConfiguration
} from 'smarteditcommons';
import { SlotNode, PageTreeSlotMenuService } from '../../../services/pageTree';

@Component({
    selector: 'se-page-tree-slot-menu',
    templateUrl: './PageTreeSlotMenuComponent.html',
    styleUrls: ['./PageTreeSlotMenuComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageTreeSlotMenuComponent implements OnInit {
    @Input() node: SlotNode;
    public items: IContextualMenuButton[];
    public componentAttributes: ComponentAttributes;

    constructor(
        private readonly pageTreeSlotMenuService: PageTreeSlotMenuService,
        private readonly cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        this.componentAttributes = {
            smarteditCatalogVersionUuid: this.node.catalogVersionUuid,
            smarteditComponentId: this.node.componentId,
            smarteditComponentType: this.node.componentTypeFromPage,
            smarteditComponentUuid: this.node.componentUuid,
            smarteditElementUuid: this.node.elementUuid
        };
        const menuConfiguration: IContextualMenuConfiguration = {
            componentType: this.node.componentTypeFromPage,
            componentId: this.node.componentId,
            componentUuid: this.node.componentUuid,
            containerType: this.node.containerType,
            containerId: this.node.containerId,
            componentAttributes: this.componentAttributes,
            slotId: this.node.containerId,
            slotUuid: this.node.componentUuid
        };
        const newItems = await this.pageTreeSlotMenuService.getPageTreeComponentMenus(
            menuConfiguration
        );
        this.items = newItems.filter((item) => !!item);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
