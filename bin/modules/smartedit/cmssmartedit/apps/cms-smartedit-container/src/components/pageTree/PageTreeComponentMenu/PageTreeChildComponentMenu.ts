/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectorRef,
    Component,
    forwardRef,
    Input,
    OnDestroy,
    OnInit,
    ViewRef
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
import {
    ComponentNode,
    NodeInfoService,
    PageTreeChildComponentMenuService
} from '../../../services/pageTree';
import { PageTreeMenuItemOverlayComponent } from './PageTreeMenuItemOverlayComponent';
import { ParentMenu } from './ParentMenu';
@Component({
    template: `
        <div class="se-page-tree-component-menu">
            <span
                *ngIf="component.isHidden"
                class="sap-icon--hide se-page-tree-component-menu--hide"
                title="{{ 'se.cms.pagetree.component.menu.icon.hide.title' | translate }}"></span>
            <se-popup-overlay
                *ngFor="let item of menuItems"
                [popupOverlay]="itemTemplateOverlayWrapper"
                [popupOverlayTrigger]="canShowTemplate(item) && templateOverlayIsOpen"
                [popupOverlayData]="{ item: item }"
                (popupOverlayOnHide)="onHideItemPopup()">
                <span
                    class="se-page-tree-component-menu--icon"
                    [ngClass]="item.displayIconClass"
                    (click)="triggerMenuItemAction(item, $event)"
                    title="{{ item.i18nKey | translate }}"></span>
            </se-popup-overlay>
        </div>
    `,
    styleUrls: ['./PageTreeComponentMenuComponent.scss'],
    selector: 'se-pagetree-child-component-menu',
    providers: [{ provide: ParentMenu, useExisting: forwardRef(() => PageTreeChildComponentMenu) }]
})
export class PageTreeChildComponentMenu implements ParentMenu, OnInit, OnDestroy {
    @Input() component: ComponentNode;
    @Input() parentMenuConfiguration: IContextualMenuConfiguration;
    @Input() slotId: string;
    @Input() slotUuid: string;
    public menuItems: IContextualMenuButton[];
    public componentAttributes: ComponentAttributes;
    public menuConfiguration: IContextualMenuConfiguration;
    public remainOpenMap: TypedMap<boolean> = {};
    public itemTemplateOverlayWrapper: PopupOverlayConfig = {
        component: PageTreeMenuItemOverlayComponent
    };
    public templateOverlayIsOpen = false;
    private displayedItem: IContextualMenuButton;
    private unregisterInnerFrameClicked: () => void;

    constructor(
        private readonly pageTreeChildComponentMenuService: PageTreeChildComponentMenuService,
        private readonly nodeInfoService: NodeInfoService,
        private readonly cdr: ChangeDetectorRef,
        private crossFrameEventService: CrossFrameEventService
    ) {}

    async ngOnInit(): Promise<void> {
        this.componentAttributes = this.nodeInfoService.getComponentAttributes(this.component);
        this.menuConfiguration = this.nodeInfoService.getContextualMenuConfiguration(
            this.component,
            this.componentAttributes,
            this.slotId,
            this.slotUuid
        );

        const newItems =
            await this.pageTreeChildComponentMenuService.getPageTreeChildComponentMenus(
                this.parentMenuConfiguration,
                this.menuConfiguration
            );

        this.menuItems = newItems.filter((item) => !!item);

        this.unregisterInnerFrameClicked = this.crossFrameEventService.subscribe(
            EVENT_INNER_FRAME_CLICKED,
            () => {
                if (!this.crossFrameEventService.isIframe()) {
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

    canShowTemplate(menuItem: IContextualMenuButton): boolean {
        return this.displayedItem === menuItem;
    }

    onHideItemPopup(): void {
        this.displayedItem = null;
    }

    triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void {
        if (item.action.component) {
            if (this.displayedItem === item) {
                this.displayedItem = null;
            } else {
                this.displayedItem = item;
            }
        } else if (item.action.callback) {
            item.action.callback(this.menuConfiguration, $event);
        }
        this.templateOverlayIsOpen = !this.templateOverlayIsOpen;
    }

    setRemainOpen(key: string, remainOpen: boolean): void {
        this.remainOpenMap[key] = remainOpen;
    }

    getItems(): IContextualMenuButton[] {
        return this.menuItems;
    }
}
