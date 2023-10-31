/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit, OnDestroy, ViewEncapsulation, DoCheck } from '@angular/core';
import {
    TOOLBAR_ITEM,
    ToolbarItemInternal,
    CrossFrameEventService,
    EVENTS,
    SMARTEDIT_DRAG_AND_DROP_EVENTS,
    DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME,
    Tab,
    OVERLAY_DISABLED_EVENT,
    ComponentMenuService
} from 'smarteditcommons';
import { RESET_COMPONENT_MENU_EVENT, OPEN_COMPONENT_EVENT } from './constants';
import { ComponentsTabComponent } from './tabs/ComponentsTabComponent';
import { ComponentTypesTabComponent } from './tabs/ComponentTypesTabComponent';
import { ComponentMenuTabModel } from './types';

const TAB_IDS = {
    COMPONENT_TYPES_TAB_ID: 'componentTypesTab',
    COMPONENTS_TAB_ID: 'componentsTab'
};

@Component({
    selector: 'se-component-menu',
    templateUrl: './ComponentMenuComponent.html',
    styleUrls: ['./ComponentMenuComponent.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ComponentMenuComponent implements OnInit, OnDestroy, DoCheck {
    public hasMultipleContentCatalogs: boolean;
    public isDragging: boolean;
    public model: ComponentMenuTabModel;
    public tabsList: Tab[];

    private isOpenPreviousValue: boolean;
    private unregisterDragEndEvent: () => void;
    private unregisterDragStartEvent: () => void;
    private unregisterOpenComponentEvent: () => void;
    private unregisterOverlapEvent: () => void;
    private unregisterPageChangeEvent: () => void;

    constructor(
        @Inject(TOOLBAR_ITEM) public toolbarItem: ToolbarItemInternal,
        private crossFrameEventService: CrossFrameEventService,
        private componentMenuService: ComponentMenuService
    ) {}

    async ngOnInit(): Promise<void> {
        await this.initializeComponentMenu();
        this.unregisterPageChangeEvent = this.crossFrameEventService.subscribe(
            EVENTS.PAGE_CHANGE,
            () => this.initializeComponentMenu()
        );
        this.unregisterOpenComponentEvent = this.crossFrameEventService.subscribe(
            OPEN_COMPONENT_EVENT,
            () => {
                this.resetComponentMenu(true);
            }
        );
        this.unregisterOverlapEvent = this.crossFrameEventService.subscribe(
            OVERLAY_DISABLED_EVENT,
            () => this.closeMenu()
        );
        this.unregisterDragStartEvent = this.crossFrameEventService.subscribe(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START,
            () => {
                this.isDragging = true;
                this.closeMenu();
            }
        );
        this.unregisterDragEndEvent = this.crossFrameEventService.subscribe(
            SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END,
            () => {
                this.isDragging = false;
                this.crossFrameEventService.publish(DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.END);
            }
        );
    }

    ngDoCheck(): void {
        if (this.isOpenPreviousValue !== this.toolbarItem.isOpen) {
            this.isOpenPreviousValue = this.toolbarItem.isOpen;
            if (this.toolbarItem.isOpen) {
                this.crossFrameEventService.publish(DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.START);
            } else if (!this.toolbarItem.isOpen && !this.isDragging) {
                this.crossFrameEventService.publish(DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME.END);
            }
        }
    }

    ngOnDestroy(): void {
        this.unregisterPageChangeEvent?.();
        this.unregisterOpenComponentEvent?.();
        this.unregisterDragStartEvent?.();
        this.unregisterOverlapEvent?.();
        this.unregisterDragEndEvent?.();
    }

    private async initializeComponentMenu(): Promise<void> {
        // This is to ensure that the component menu DOM is completely clean, even after a page change.
        this.tabsList = null;

        this.hasMultipleContentCatalogs =
            await this.componentMenuService.hasMultipleContentCatalogs();

        this.tabsList = [
            {
                id: TAB_IDS.COMPONENT_TYPES_TAB_ID,
                title: 'se.cms.compomentmenu.tabs.componenttypes',
                component: ComponentTypesTabComponent,
                hasErrors: false
            },
            {
                id: TAB_IDS.COMPONENTS_TAB_ID,
                title: 'se.cms.compomentmenu.tabs.customizedcomp',
                component: ComponentsTabComponent,
                hasErrors: false
            }
        ];

        this.model = {
            componentsTab: { hasMultipleContentCatalogs: this.hasMultipleContentCatalogs },
            isOpen: this.toolbarItem.isOpen
        };
        // Initialize isOpenPreviousValue.
        this.isOpenPreviousValue = this.toolbarItem.isOpen;

        // This variable is assigned in the tabsList to make sure there's no
        // tight coupling with the view (instead of relying on a position in the array).
        this.resetComponentMenu(this.toolbarItem.isOpen);
    }

    private resetComponentMenu(isToolbarOpened: boolean): void {
        if (this.tabsList) {
            // Reset component menu only when it is opened. It is required to set the specific tab as an active.
            // OPEN_COMPONENT_EVENT is called before toolbar updates item.isOpen property.
            if (!this.toolbarItem.isOpen) {
                this.tabsList = this.tabsList.map((tab) => ({
                    ...tab,
                    active: tab.id === TAB_IDS.COMPONENT_TYPES_TAB_ID
                }));
            }

            // Model is passed to <se-tabs> component with default change detection strategy via @Input binding.
            // Then the value is passed down to <se-tab> component, which ngOnChanges checks if model has changed by using "isEqual" that does deep comparison -> that is the reason why you need to pass a new reference.
            this.model = {
                ...this.model,
                isOpen: isToolbarOpened
            };

            this.crossFrameEventService.publish(RESET_COMPONENT_MENU_EVENT);
        }
    }

    private closeMenu(): void {
        if (this.toolbarItem) {
            this.toolbarItem.isOpen = false;
        }
    }
}
