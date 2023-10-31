/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    Input,
    forwardRef,
    OnInit,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef,
    OnDestroy
} from '@angular/core';
import {
    ComponentAttributes,
    CrossFrameEventService,
    EVENT_INNER_FRAME_CLICKED,
    IContextualMenuButton,
    IContextualMenuConfiguration,
    PopupOverlayConfig,
    TypedMap
} from 'smarteditcommons';
import { ComponentNode, NodeInfoService } from '../../../services/pageTree/NodeInfoService';
import { PageTreeComponentMenuService } from '../../../services/pageTree/PageTreeComponentMenuService';
import { PageTreeMenuItemOverlayComponent } from './PageTreeMenuItemOverlayComponent';
import { PageTreeMoreItemsComponent } from './PageTreeMoreItemsComponent';
import { ParentMenu } from './ParentMenu';

@Component({
    selector: 'se-page-tree-component-menu',
    templateUrl: './PageTreeComponentMenuComponent.html',
    styleUrls: ['./PageTreeComponentMenuComponent.scss'],
    providers: [
        { provide: ParentMenu, useExisting: forwardRef(() => PageTreeComponentMenuComponent) }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageTreeComponentMenuComponent implements ParentMenu, OnInit, OnDestroy {
    @Input() component: ComponentNode;
    @Input() slotId: string;
    @Input() slotUuid: string;
    public componentAttributes: ComponentAttributes;
    public remainOpenMap: TypedMap<boolean> = {};
    public items: IContextualMenuButton[];
    public leftButton: IContextualMenuButton;
    public moreMenuIsOpen = false;
    public templateOverlayIsOpen = false;

    public moreMenuPopupConfig: PopupOverlayConfig = {
        component: PageTreeMoreItemsComponent,
        halign: 'left'
    };
    public itemTemplateOverlayWrapper: PopupOverlayConfig = {
        component: PageTreeMenuItemOverlayComponent
    };
    public menuConfiguration: IContextualMenuConfiguration;

    private displayedItem: IContextualMenuButton;
    private unregisterInnerFrameClicked: () => void;
    constructor(
        private readonly pageTreeComponentMenuService: PageTreeComponentMenuService,
        private readonly nodeInfoService: NodeInfoService,
        private readonly cdr: ChangeDetectorRef,
        private crossFrameEventService: CrossFrameEventService
    ) {}

    getItems(): IContextualMenuButton[] {
        return this.items;
    }

    async ngOnInit(): Promise<void> {
        this.componentAttributes = this.nodeInfoService.getComponentAttributes(this.component);
        this.menuConfiguration = this.nodeInfoService.getContextualMenuConfiguration(
            this.component,
            this.componentAttributes,
            this.slotId,
            this.slotUuid
        );

        const newItems = await this.pageTreeComponentMenuService.getPageTreeComponentMenus(
            this.menuConfiguration
        );

        if (newItems && newItems.length > 0) {
            this.items = newItems.filter((item) => !!item);
            this.leftButton = this.items.shift();
        }

        this.unregisterInnerFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_INNER_FRAME_CLICKED,
            () => {
                if (!this.crossFrameEventService.isIframe()) {
                    this.moreMenuIsOpen = false;
                    this.templateOverlayIsOpen = false;
                    if (!(this.cdr as ViewRef).destroyed) {
                        this.cdr.detectChanges();
                    }
                }
            }
        );

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        if (this.unregisterInnerFrameClicked) {
            this.unregisterInnerFrameClicked();
        }
    }

    setRemainOpen(key: string, remainOpen: boolean): void {
        this.remainOpenMap[key] = remainOpen;
    }

    showOverlay(active: boolean): boolean {
        if (active) {
            return true;
        }

        return Object.keys(this.remainOpenMap).reduce(
            (isOpen: boolean, key: string) => isOpen || this.remainOpenMap[key],
            false
        );
    }

    canShowTemplate(menuItem: IContextualMenuButton): boolean {
        return this.displayedItem === menuItem;
    }

    onHideItemPopup(hideMoreMenu = false): void {
        this.displayedItem = null;
        if (hideMoreMenu) {
            this.moreMenuIsOpen = false;
        }
    }

    // When action.component is on more menu, here may have an issue.
    triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void {
        this.moreMenuIsOpen = false;
        this.templateOverlayIsOpen = !this.templateOverlayIsOpen;
        if (item.action.component) {
            if (this.displayedItem === item) {
                this.displayedItem = null;
            } else {
                this.displayedItem = item;
            }
        } else if (item.action.callback) {
            item.action.callback(this.menuConfiguration, $event);
        }
    }

    toggleMoreMenu(): void {
        this.moreMenuIsOpen = !this.moreMenuIsOpen;
    }
}
