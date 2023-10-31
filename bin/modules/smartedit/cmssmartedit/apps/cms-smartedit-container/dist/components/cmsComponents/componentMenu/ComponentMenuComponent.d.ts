import { OnInit, OnDestroy, DoCheck } from '@angular/core';
import { ToolbarItemInternal, CrossFrameEventService, Tab, ComponentMenuService } from 'smarteditcommons';
import { ComponentMenuTabModel } from './types';
export declare class ComponentMenuComponent implements OnInit, OnDestroy, DoCheck {
    toolbarItem: ToolbarItemInternal;
    private crossFrameEventService;
    private componentMenuService;
    hasMultipleContentCatalogs: boolean;
    isDragging: boolean;
    model: ComponentMenuTabModel;
    tabsList: Tab[];
    private isOpenPreviousValue;
    private unregisterDragEndEvent;
    private unregisterDragStartEvent;
    private unregisterOpenComponentEvent;
    private unregisterOverlapEvent;
    private unregisterPageChangeEvent;
    constructor(toolbarItem: ToolbarItemInternal, crossFrameEventService: CrossFrameEventService, componentMenuService: ComponentMenuService);
    ngOnInit(): Promise<void>;
    ngDoCheck(): void;
    ngOnDestroy(): void;
    private initializeComponentMenu;
    private resetComponentMenu;
    private closeMenu;
}
