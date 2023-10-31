/// <reference types="jquery" />
import { DoCheck, ElementRef, OnDestroy, OnInit, Injector } from '@angular/core';
import { ComponentAttributes, IContextualMenuButton, IContextualMenuService, NodeUtils, PopupOverlayConfig, SystemEventService, IComponentHandlerService, CrossFrameEventService, IUserTrackingService } from 'smarteditcommons';
import { ContextualMenu } from '../../../../services';
import { BaseContextualMenuComponent } from './BaseContextualMenuComponent';
export declare class MoreItemsComponent {
    parent: ContextualMenuDecoratorComponent;
    constructor(parent: ContextualMenuDecoratorComponent);
}
export declare class ContextualMenuItemOverlayComponent {
    private readonly data;
    private readonly parent;
    private readonly injector;
    componentInjector: Injector;
    constructor(data: {
        item: IContextualMenuButton;
    }, parent: ContextualMenuDecoratorComponent, injector: Injector);
    ngOnInit(): void;
    get item(): IContextualMenuButton;
    private createComponentInjector;
}
export declare class ContextualMenuDecoratorComponent extends BaseContextualMenuComponent implements OnInit, DoCheck, OnDestroy {
    private readonly yjQuery;
    private readonly element;
    private readonly contextualMenuService;
    private readonly systemEventService;
    private readonly componentHandlerService;
    private readonly nodeUtils;
    private readonly crossFrameEventService;
    private readonly userTrackingService;
    smarteditComponentType: string;
    smarteditComponentId: string;
    smarteditContainerType: string;
    smarteditContainerId: string;
    smarteditCatalogVersionUuid: string;
    smarteditElementUuid: string;
    componentAttributes: ComponentAttributes;
    set active(_active: string | boolean);
    get active(): boolean | string;
    items: ContextualMenu;
    openItem: IContextualMenuButton;
    moreMenuIsOpen: boolean;
    slotAttributes: {
        smarteditSlotId: string;
        smarteditSlotUuid: string;
    };
    itemTemplateOverlayWrapper: PopupOverlayConfig;
    moreMenuPopupConfig: PopupOverlayConfig;
    moreButton: {
        displayClass: string;
        i18nKey: string;
    };
    private displayedItem;
    private oldWidth;
    private dndUnRegFn;
    private unregisterRefreshItems;
    private _active;
    private _pageTreeActive;
    constructor(yjQuery: JQueryStatic, element: ElementRef, contextualMenuService: IContextualMenuService, systemEventService: SystemEventService, componentHandlerService: IComponentHandlerService, nodeUtils: NodeUtils, crossFrameEventService: CrossFrameEventService, userTrackingService: IUserTrackingService);
    ngDoCheck(): void;
    ngOnDestroy(): void;
    ngOnInit(): void;
    get smarteditSlotId(): string;
    get smarteditSlotUuid(): string;
    onInit(): void;
    toggleMoreMenu(): void;
    shouldShowTemplate(menuItem: IContextualMenuButton): boolean;
    onShowItemPopup(item: IContextualMenuButton): void;
    onHideItemPopup(hideMoreMenu?: boolean): void;
    onShowMoreMenuPopup(): void;
    onHideMoreMenuPopup(): void;
    hideAllPopups(): void;
    getItems(): ContextualMenu;
    showContextualMenuBorders(): boolean;
    triggerMenuItemAction(item: IContextualMenuButton, $event: Event): void;
    private maxContextualMenuItems;
    private updateItems;
}
