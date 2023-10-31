/// <reference types="jquery" />
import { IComponentEditingFacade, IPageContentSlotsComponentsRestService } from 'cmscommons';
import { DragAndDropService, GatewayFactory, ISharedDataService, SystemEventService, ISlotRestrictionsService, IWaitDialogService, CrossFrameEventService } from 'smarteditcommons';
export declare const ENABLE_CLONE_ON_DROP = "enableCloneComponentOnDrop";
export interface CmsPageTreeDropElement {
    id: string;
    slotId: string;
    slotUuid: string;
    slotCatalogVersion: string;
    original: JQuery;
    isSlot: boolean;
    componentId?: string;
    componentUuid?: string;
    isAllowed?: boolean;
    isBottom?: boolean;
}
export interface Region {
    left: number;
    right: number;
    bottom: number;
    top: number;
}
export declare class CmsDragAndDropService {
    private readonly dragAndDropService;
    private readonly gatewayFactory;
    private readonly sharedDataService;
    private readonly systemEventService;
    private readonly yjQuery;
    private readonly slotRestrictionsService;
    private readonly waitDialogService;
    private readonly componentEditingFacade;
    private readonly pageContentSlotsComponentsRestService;
    private readonly crossFrameEventService;
    private static readonly CMS_DRAG_AND_DROP_ID;
    private static readonly TARGET_SELECTOR;
    private static readonly SOURCE_SELECTOR;
    private static readonly COMPONENT_SELECTOR;
    private static readonly DROP_EFFECT_COPY;
    private static readonly DROP_EFFECT_NONE;
    private static readonly SCROLL_AREA_SELECTOR;
    private static readonly ALLOWED_DROP_CLASS;
    private static readonly DRAGGED_COMPONENT_CLASS;
    private static readonly NODE_SMARTEDIT_ELEMENT_UUID;
    private static readonly SLOT_ID;
    private static readonly SLOT_UUID;
    private static readonly SLOT_CATALOG_VERSION_UUID;
    private static readonly COMPONENT_ID;
    private static readonly COMPONENT_UUID;
    private static readonly COMPONENT_TYPE;
    private static readonly PAGE_TREE_DRAG_AND_DROP_ID;
    private static readonly PAGE_TREE_SOURCE_SELECTOR;
    private static readonly SE_PAGE_TREE_SLOT;
    private static readonly SE_PAGE_TREE_COMPONENT;
    private static readonly TAG_NAME;
    private static readonly EXPAND_SLOT_TIME_OUT;
    private static readonly SCROLLING_AREA_HEIGHT;
    private static readonly SCROLLING_STEP;
    private readonly _window;
    private dragInfo;
    private highlightedElement;
    private scrollable;
    private isMouseInTopScrollArea;
    private isMouseInBottomScrollArea;
    private animationFrameId;
    private gateway;
    constructor(dragAndDropService: DragAndDropService, gatewayFactory: GatewayFactory, sharedDataService: ISharedDataService, systemEventService: SystemEventService, yjQuery: JQueryStatic, slotRestrictionsService: ISlotRestrictionsService, waitDialogService: IWaitDialogService, componentEditingFacade: IComponentEditingFacade, pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService, crossFrameEventService: CrossFrameEventService);
    register(): void;
    unregister(): void;
    apply(): void;
    update(): void;
    updateInPageTree(): void;
    private onStart;
    private onStop;
    private onStartInPageTree;
    private onStopInPageTree;
    private onDragEnter;
    private onDragOver;
    private onDrop;
    private onDragLeave;
    private dragLeaveEventInRegion;
    private dropElementToSlot;
    private addComponentToSlot;
    private clearHighlightedElement;
    private highlightElement;
    private highlightSlot;
    private expandedSlotInPageTree;
    private highlightComponent;
    private modifyComponentClass;
    private isAllowedSlot;
    private isMouseInTopHalfRegion;
    private isMouseInBottomHalfRegion;
    private getTopHalfRect;
    private getBottomHalfRect;
    private isMouseInRegion;
    private setMouseArea;
    private scroll;
    private getComponentIndexInSlot;
    private getSelector;
}
