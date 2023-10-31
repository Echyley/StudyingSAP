/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import {
    DRAG_AND_DROP_EVENTS,
    IComponentEditingFacade,
    CloneComponentInfo,
    DragInfo,
    SlotInfo,
    CmsDragAndDropDragInfo,
    IPageContentSlotsComponentsRestService,
    ICMSComponent
} from 'cmscommons';
import {
    DragAndDropService,
    GatewayFactory,
    ID_ATTRIBUTE,
    ISharedDataService,
    MessageGateway,
    SystemEventService,
    TYPE_ATTRIBUTE,
    UUID_ATTRIBUTE,
    YJQUERY_TOKEN,
    COMPONENT_IN_SLOT_STATUS,
    ISlotRestrictionsService,
    IWaitDialogService,
    windowUtils,
    PAGE_TREE_SLOT_EXPANDED_EVENT,
    CrossFrameEventService
} from 'smarteditcommons';

export const ENABLE_CLONE_ON_DROP = 'enableCloneComponentOnDrop';

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

@Injectable()
export class CmsDragAndDropService {
    private static readonly CMS_DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';
    private static readonly TARGET_SELECTOR = '.se-page-tree .se-page-tree-drop-area';
    private static readonly SOURCE_SELECTOR =
        ".smartEditComponent[data-smartedit-component-type!='ContentSlot']";
    private static readonly COMPONENT_SELECTOR = '.smartEditComponent';

    private static readonly DROP_EFFECT_COPY = 'copy';
    private static readonly DROP_EFFECT_NONE = 'none';
    private static readonly SCROLL_AREA_SELECTOR = '.se-page-tree-scroll-area';
    private static readonly ALLOWED_DROP_CLASS = 'se-page-tree-slot-allowed-drop';
    private static readonly DRAGGED_COMPONENT_CLASS = 'se-page-tree-component-dragged';
    private static readonly NODE_SMARTEDIT_ELEMENT_UUID = 'node-smartedit-element-uuid';
    private static readonly SLOT_ID = 'smartedit-slot-id';
    private static readonly SLOT_UUID = 'smartedit-slot-uuid';
    private static readonly SLOT_CATALOG_VERSION_UUID = 'smartedit-slot-catalog-version-uuid';
    private static readonly COMPONENT_ID = 'smartedit-component-id';
    private static readonly COMPONENT_UUID = 'smartedit-component-uuid';
    private static readonly COMPONENT_TYPE = 'smartedit-component-type';
    private static readonly PAGE_TREE_DRAG_AND_DROP_ID = 'se.cms.page.tree.dragAndDrop';
    private static readonly PAGE_TREE_SOURCE_SELECTOR = '.se-page-tree-component-node';
    private static readonly SE_PAGE_TREE_SLOT = 'SE-PAGE-TREE-SLOT';
    private static readonly SE_PAGE_TREE_COMPONENT = 'SE-PAGE-TREE-COMPONENT';
    private static readonly TAG_NAME = 'tagName';
    private static readonly EXPAND_SLOT_TIME_OUT = 400;
    private static readonly SCROLLING_AREA_HEIGHT = 60;
    private static readonly SCROLLING_STEP = 10;

    private readonly _window: Window;
    private dragInfo: CmsDragAndDropDragInfo = null;
    private highlightedElement: CmsPageTreeDropElement = null;
    private scrollable: JQuery;
    private isMouseInTopScrollArea = false;
    private isMouseInBottomScrollArea = false;
    private animationFrameId: number;

    private gateway: MessageGateway;

    constructor(
        private readonly dragAndDropService: DragAndDropService,
        private readonly gatewayFactory: GatewayFactory,
        private readonly sharedDataService: ISharedDataService,
        private readonly systemEventService: SystemEventService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private readonly slotRestrictionsService: ISlotRestrictionsService,
        private readonly waitDialogService: IWaitDialogService,
        private readonly componentEditingFacade: IComponentEditingFacade,
        private readonly pageContentSlotsComponentsRestService: IPageContentSlotsComponentsRestService,
        private readonly crossFrameEventService: CrossFrameEventService
    ) {
        this._window = windowUtils.getWindow();
        this.gateway = this.gatewayFactory.createGateway('cmsDragAndDrop');
    }

    public register(): void {
        this.dragAndDropService.register({
            id: CmsDragAndDropService.CMS_DRAG_AND_DROP_ID,
            sourceSelector: CmsDragAndDropService.SOURCE_SELECTOR,
            targetSelector: CmsDragAndDropService.TARGET_SELECTOR,
            startCallback: (event: DragEvent) => this.onStart(event),
            dragEnterCallback: (event: DragEvent) => this.onDragEnter(event),
            dragOverCallback: (event: DragEvent) => this.onDragOver(event),
            dropCallback: (event: DragEvent) => this.onDrop(event),
            outCallback: (event: DragEvent) => this.onDragLeave(event),
            stopCallback: () => this.onStop(),
            enableScrolling: false
        });

        this.dragAndDropService.register({
            id: CmsDragAndDropService.PAGE_TREE_DRAG_AND_DROP_ID,
            sourceSelector: CmsDragAndDropService.PAGE_TREE_SOURCE_SELECTOR,
            targetSelector: CmsDragAndDropService.TARGET_SELECTOR,
            startCallback: (event: DragEvent) => this.onStartInPageTree(event),
            dragEnterCallback: (event: DragEvent) => this.onDragEnter(event),
            dragOverCallback: (event: DragEvent) => this.onDragOver(event),
            dropCallback: (event: DragEvent) => this.onDrop(event),
            outCallback: (event: DragEvent) => this.onDragLeave(event),
            stopCallback: (event: DragEvent) => this.onStopInPageTree(event),
            enableScrolling: false
        });
    }

    public unregister(): void {
        this.dragAndDropService.unregister([
            CmsDragAndDropService.CMS_DRAG_AND_DROP_ID,
            CmsDragAndDropService.PAGE_TREE_DRAG_AND_DROP_ID
        ]);
    }

    public apply(): void {
        this.dragAndDropService.apply(null);
    }

    public update(): void {
        this.dragAndDropService.update(CmsDragAndDropService.CMS_DRAG_AND_DROP_ID);
    }

    public updateInPageTree(): void {
        this.dragAndDropService.update(CmsDragAndDropService.PAGE_TREE_DRAG_AND_DROP_ID);
    }

    private async onStart(event: DragEvent): Promise<void> {
        event.dataTransfer.effectAllowed = CmsDragAndDropService.DROP_EFFECT_COPY;
        this.scrollable = this.yjQuery(CmsDragAndDropService.SCROLL_AREA_SELECTOR);
        const cloneOnDrop = await this.sharedDataService.get(ENABLE_CLONE_ON_DROP);
        const component = this.getSelector(event.target).closest(
            CmsDragAndDropService.COMPONENT_SELECTOR
        );

        const dragInfo: CmsDragAndDropDragInfo = {
            componentId: component.attr(ID_ATTRIBUTE),
            componentUuid: component.attr(UUID_ATTRIBUTE),
            componentType: component.attr(TYPE_ATTRIBUTE),
            slotUuid: null,
            slotId: null,

            cloneOnDrop: cloneOnDrop ? true : false
        };
        this.dragInfo = dragInfo;
        this.gateway.publish(DRAG_AND_DROP_EVENTS.DRAG_STARTED, dragInfo);
        this.systemEventService.publishAsync(DRAG_AND_DROP_EVENTS.DRAG_STARTED);
        this._window.requestAnimationFrame(this.scroll.bind(this));
    }

    private onStop(): void {
        this.gateway.publish(DRAG_AND_DROP_EVENTS.DRAG_STOPPED, null);
        this._window.cancelAnimationFrame(this.animationFrameId);
        this.isMouseInBottomScrollArea = false;
        this.isMouseInTopScrollArea = false;
        this.clearHighlightedElement();
    }

    private async onStartInPageTree(event: DragEvent): Promise<void> {
        event.dataTransfer.effectAllowed = CmsDragAndDropService.DROP_EFFECT_COPY;
        this.scrollable = this.yjQuery(CmsDragAndDropService.SCROLL_AREA_SELECTOR);
        const cloneOnDrop = await this.sharedDataService.get(ENABLE_CLONE_ON_DROP);
        const component = this.yjQuery(event.target).closest(
            CmsDragAndDropService.PAGE_TREE_SOURCE_SELECTOR
        );
        component.addClass(CmsDragAndDropService.DRAGGED_COMPONENT_CLASS);

        const dragInfo: CmsDragAndDropDragInfo = {
            componentId: component.attr(CmsDragAndDropService.COMPONENT_ID),
            componentUuid: component.attr(CmsDragAndDropService.COMPONENT_UUID),
            componentType: component.attr(CmsDragAndDropService.COMPONENT_TYPE),
            slotUuid: component.attr(CmsDragAndDropService.SLOT_UUID),
            slotId: component.attr(CmsDragAndDropService.SLOT_ID),

            slotOperationRelatedId: component.attr('smartedit-container-id'),
            slotOperationRelatedType: component.attr('smartedit-container-type'),
            cloneOnDrop: cloneOnDrop ? true : false
        };
        this.dragInfo = dragInfo;
        this._window.requestAnimationFrame(this.scroll.bind(this));
    }

    private onStopInPageTree(event: DragEvent): void {
        const element = this.getSelector(event.target).closest(
            CmsDragAndDropService.PAGE_TREE_SOURCE_SELECTOR
        );
        element.removeClass(CmsDragAndDropService.DRAGGED_COMPONENT_CLASS);
        this.onStop();
    }

    private onDragEnter(event: DragEvent): void {
        this.setMouseArea(event);
        this.highlightElement(event);
    }

    private onDragOver(event: DragEvent): void {
        this.setMouseArea(event);
        this.highlightElement(event);
    }

    private async onDrop(event: DragEvent): Promise<void> {
        if (this.highlightedElement && this.highlightedElement.isAllowed) {
            let positionInSlot = 0;
            // Return from asynchronous functions, highlightedElement may be cleared.
            const { isSlot, slotId, isBottom, componentUuid, slotCatalogVersion, slotUuid } =
                this.highlightedElement;
            if (!isSlot) {
                positionInSlot = await this.getComponentIndexInSlot(slotId, componentUuid);

                if (isBottom) {
                    positionInSlot++;
                }
            }
            await this.dropElementToSlot(slotId, slotUuid, positionInSlot, slotCatalogVersion);
        }

        this.onStop();
    }

    private onDragLeave(event: DragEvent): void {
        if (this.highlightedElement && this.highlightedElement.original) {
            const targetRect = this.highlightedElement.original.get(0).getBoundingClientRect();
            /*
             * To stop jitter of highlight element
             * Sometimes the dragleave will be triggered by the child elements of the data-droppale element
             * sometimes mouse are not leave but the dragleave also be triggered
             */
            if (!this.dragLeaveEventInRegion(event, targetRect)) {
                this.clearHighlightedElement();
            }
        }
    }

    // The coordinates of events are integers, For example: when drag leave triggered at pageX 352 but element.right is 352.3984375
    private dragLeaveEventInRegion(event: DragEvent, element: Region): boolean {
        return (
            event.pageX > Math.ceil(element.left) &&
            event.pageX < Math.floor(element.right) &&
            event.pageY > Math.ceil(element.top) &&
            event.pageY < Math.floor(element.bottom)
        );
    }

    private async dropElementToSlot(
        targetSlotId: string,
        targetSlotUUId: string,
        position: number,
        catalogVersionUuid: string
    ): Promise<void> {
        const sourceSlotId = this.dragInfo.slotId;
        const sourceComponentUuid = this.dragInfo.componentUuid;

        // if component is dragged from component-menu, there is no slotOperationRelated(Id/Type) available.
        const sourceSlotOperationRelatedId =
            this.dragInfo.slotOperationRelatedId || this.dragInfo.componentId;
        let performAction: Promise<void>;
        this.waitDialogService.showWaitModal();
        if (!sourceSlotId) {
            performAction = this.addComponentToSlot(
                targetSlotId,
                targetSlotUUId,
                position,
                catalogVersionUuid
            );
        } else {
            if (sourceSlotId === targetSlotId) {
                const currentComponentPos = await this.getComponentIndexInSlot(
                    sourceSlotId,
                    sourceComponentUuid
                );

                if (currentComponentPos < position) {
                    // The current component will be removed from its current position, thus the target
                    // position needs to take this into account.
                    position--;
                }
                if (currentComponentPos === position) {
                    // Do not perform update if position and slot has not changed.
                    this.waitDialogService.hideWaitModal();
                    return;
                }
            }
            performAction = this.componentEditingFacade.moveComponent(
                sourceSlotId,
                targetSlotId,
                sourceSlotOperationRelatedId,
                position
            );
        }

        try {
            await performAction;
        } catch (e) {
            this.waitDialogService.hideWaitModal();
        } finally {
            this.waitDialogService.hideWaitModal();
            this.gateway.publish(DRAG_AND_DROP_EVENTS.SCROLL_TO_MODIFIED_SLOT, targetSlotId);
            this.crossFrameEventService.publish(
                DRAG_AND_DROP_EVENTS.SCROLL_TO_MODIFIED_SLOT,
                targetSlotId
            );
        }
    }

    private addComponentToSlot(
        targetSlotId: string,
        targetSlotUUId: string,
        position: number,
        catalogVersionUuid: string
    ): Promise<void> {
        const sourceComponentId = this.dragInfo.componentId;
        const componentType = this.dragInfo.slotOperationRelatedType || this.dragInfo.componentType;
        let performAction: Promise<void>;
        if (!sourceComponentId) {
            const slotInfo: SlotInfo = {
                targetSlotId,
                targetSlotUUId
            };

            performAction = this.componentEditingFacade.addNewComponentToSlot(
                slotInfo,
                catalogVersionUuid,
                componentType,
                position
            );
        } else {
            const dragInfo: DragInfo = {
                componentId: sourceComponentId,
                componentUuid: this.dragInfo.componentUuid,
                componentType
            };
            const componentProperties: CloneComponentInfo = {
                targetSlotId,
                dragInfo,
                position
            };

            performAction = this.dragInfo.cloneOnDrop
                ? this.componentEditingFacade.cloneExistingComponentToSlot(componentProperties)
                : this.componentEditingFacade.addExistingComponentToSlot(
                      targetSlotId,
                      dragInfo,
                      position
                  );
        }

        return performAction;
    }

    private clearHighlightedElement(): void {
        if (this.highlightedElement && this.highlightedElement.original) {
            this.highlightedElement.original.removeClass(CmsDragAndDropService.ALLOWED_DROP_CLASS);
            if (!this.highlightedElement.isSlot) {
                const dropArea = this.highlightedElement.original.children(
                    '.se-page-tree-component-drop-area'
                );
                dropArea.css('display', 'none');
                dropArea.css('top', '');
                dropArea.css('bottom', '');
            }
        }

        this.highlightedElement = null;
    }

    private highlightElement(event: DragEvent): void {
        event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_NONE;
        const element = this.yjQuery(event.target).closest(
            CmsDragAndDropService.TARGET_SELECTOR
        ) as JQuery;
        if (
            element.parent().prop(CmsDragAndDropService.TAG_NAME) ===
            CmsDragAndDropService.SE_PAGE_TREE_SLOT
        ) {
            this.highlightSlot(event, element);
        } else if (
            element.parent().prop(CmsDragAndDropService.TAG_NAME) ===
            CmsDragAndDropService.SE_PAGE_TREE_COMPONENT
        ) {
            const firstLevelNode = element.children('.se-page-tree-node').first();
            this.highlightComponent(event, firstLevelNode);
        }
    }

    private async highlightSlot(event: DragEvent, element: JQuery): Promise<void> {
        const elementUuid = element.attr(CmsDragAndDropService.NODE_SMARTEDIT_ELEMENT_UUID);
        const slotId = element.attr(CmsDragAndDropService.SLOT_ID);
        const slotUuid = element.attr(CmsDragAndDropService.SLOT_UUID);
        const slotCatalogVersion = element.attr(CmsDragAndDropService.SLOT_CATALOG_VERSION_UUID);
        const expanded = element.attr('expanded') === 'true';

        if (this.highlightedElement) {
            if (this.highlightedElement.id !== elementUuid) {
                this.clearHighlightedElement();
            } else {
                if (this.highlightedElement.isAllowed) {
                    event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_COPY;
                } else {
                    event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_NONE;
                }
                return;
            }
        }

        this.highlightedElement = {
            original: element,
            id: elementUuid,
            isSlot: true,
            slotId,
            slotUuid,
            slotCatalogVersion
        };

        // Call asynchronous functions as little as possible in dragover event.
        const isAllowed = await this.isAllowedSlot(slotId);

        // The highlighted slot might have changed while waiting for the promise to be resolved.
        if (this.highlightedElement && this.highlightedElement.id === elementUuid) {
            this.highlightedElement.isAllowed = isAllowed;
            if (this.highlightedElement.isAllowed) {
                element.addClass(CmsDragAndDropService.ALLOWED_DROP_CLASS);
                event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_COPY;
            } else {
                event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_NONE;
            }
            this.expandedSlotInPageTree(expanded, elementUuid);
        }
    }

    private expandedSlotInPageTree(expanded: boolean, slotElementUuid: string): void {
        if (!expanded && this.highlightedElement.isAllowed) {
            setTimeout(() => {
                if (this.highlightedElement && this.highlightedElement.id === slotElementUuid) {
                    this.systemEventService.publish(PAGE_TREE_SLOT_EXPANDED_EVENT, slotElementUuid);
                }
            }, CmsDragAndDropService.EXPAND_SLOT_TIME_OUT);
        }
    }

    private async highlightComponent(event: DragEvent, element: JQuery): Promise<void> {
        const elementUuid = element.attr(CmsDragAndDropService.NODE_SMARTEDIT_ELEMENT_UUID);
        const slotId = element.attr(CmsDragAndDropService.SLOT_ID);
        const slotUuid = element.attr(CmsDragAndDropService.SLOT_UUID);
        const slotCatalogVersion = element.attr(CmsDragAndDropService.SLOT_CATALOG_VERSION_UUID);
        const componentId = element.attr(CmsDragAndDropService.COMPONENT_ID);
        const componentUuid = element.attr(CmsDragAndDropService.COMPONENT_UUID);

        if (this.highlightedElement) {
            if (this.highlightedElement.id !== elementUuid) {
                this.clearHighlightedElement();
            } else {
                // Change the highlight class by mouse position in same component
                this.modifyComponentClass(event, element);
                return;
            }
        }

        this.highlightedElement = {
            original: element,
            id: elementUuid,
            isSlot: false,
            slotId,
            slotUuid,
            slotCatalogVersion,
            componentId,
            componentUuid
        };

        // Call asynchronous functions as little as possible in dragover event.
        const isAllowed = await this.isAllowedSlot(slotId);

        // The highlighted component might have changed while waiting for the promise to be resolved.
        if (this.highlightedElement && this.highlightedElement.id === elementUuid) {
            this.highlightedElement.isAllowed = isAllowed;
            this.modifyComponentClass(event, element);
        }
    }

    private modifyComponentClass(event: DragEvent, element: JQuery): void {
        const dropArea = element.children('.se-page-tree-component-drop-area');
        if (this.highlightedElement.isAllowed && !!dropArea) {
            event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_COPY;
            if (this.isMouseInTopHalfRegion(event, element)) {
                dropArea.css('top', '-5px');
                dropArea.css('bottom', '');
                this.highlightedElement.isBottom = false;
                dropArea.css('display', 'flex');
            } else if (this.isMouseInBottomHalfRegion(event, element)) {
                dropArea.css('top', '');
                dropArea.css('bottom', '-5px');
                this.highlightedElement.isBottom = true;
                dropArea.css('display', 'flex');
            }
        } else {
            event.dataTransfer.dropEffect = CmsDragAndDropService.DROP_EFFECT_NONE;
        }
    }

    private async isAllowedSlot(slotId: string): Promise<boolean> {
        const isSlotEditable = await this.slotRestrictionsService.isSlotEditable(slotId);
        const isComponentAllowed =
            await this.slotRestrictionsService.determineComponentStatusInSlot(
                slotId,
                this.dragInfo
            );

        const isAllowed = isComponentAllowed === COMPONENT_IN_SLOT_STATUS.ALLOWED && isSlotEditable;
        const mayBeAllowed =
            isComponentAllowed === COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED && isSlotEditable;
        return isAllowed || mayBeAllowed;
    }

    private isMouseInTopHalfRegion(event: DragEvent, element: JQuery): boolean {
        const scrollY = this._window.pageYOffset;
        const upperHalfRegion = this.getTopHalfRect(element, scrollY);
        return this.isMouseInRegion(event, upperHalfRegion);
    }

    private isMouseInBottomHalfRegion(event: DragEvent, element: JQuery): boolean {
        const scrollY = this._window.pageYOffset;
        const upperBottomRegion = this.getBottomHalfRect(element, scrollY);
        return this.isMouseInRegion(event, upperBottomRegion);
    }

    private getTopHalfRect(element: JQuery, scrollY: number): Region {
        const baseRect = element.get(0).getBoundingClientRect();
        return {
            left: baseRect.left,
            right: baseRect.right,
            bottom: baseRect.top + scrollY + (baseRect.bottom - baseRect.top) / 2,
            top: baseRect.top + scrollY
        };
    }

    private getBottomHalfRect(element: JQuery, scrollY: number): Region {
        const baseRect = element.get(0).getBoundingClientRect();
        return {
            left: baseRect.left,
            right: baseRect.right,
            bottom: baseRect.bottom + scrollY,
            top: baseRect.top + scrollY + (baseRect.bottom - baseRect.top) / 2
        };
    }

    private isMouseInRegion(event: DragEvent, element: Region): boolean {
        return (
            event.pageX >= element.left &&
            event.pageX <= element.right &&
            event.pageY >= element.top &&
            event.pageY <= element.bottom
        );
    }

    private setMouseArea(event: DragEvent): void {
        const scrollAreaRect = this.scrollable.get(0).getBoundingClientRect();
        const scrollTopRegion: Region = {
            left: scrollAreaRect.left,
            right: scrollAreaRect.right,
            bottom:
                scrollAreaRect.top +
                this._window.pageYOffset +
                CmsDragAndDropService.SCROLLING_AREA_HEIGHT,
            top: scrollAreaRect.top + this._window.pageYOffset
        };
        const scrollBottomRegion: Region = {
            left: scrollAreaRect.left,
            right: scrollAreaRect.right,
            bottom: scrollAreaRect.bottom + this._window.pageYOffset,
            top:
                scrollAreaRect.bottom +
                this._window.pageYOffset -
                CmsDragAndDropService.SCROLLING_AREA_HEIGHT
        };
        if (this.isMouseInRegion(event, scrollTopRegion)) {
            this.isMouseInTopScrollArea = true;
            this.isMouseInBottomScrollArea = false;
        } else if (this.isMouseInRegion(event, scrollBottomRegion)) {
            this.isMouseInTopScrollArea = false;
            this.isMouseInBottomScrollArea = true;
        } else {
            this.isMouseInTopScrollArea = false;
            this.isMouseInBottomScrollArea = false;
        }
    }

    private scroll(): void {
        const scrollTop = this.scrollable.scrollTop();
        if (this.isMouseInTopScrollArea) {
            if (scrollTop > 0) {
                const next = scrollTop - CmsDragAndDropService.SCROLLING_STEP;
                this.scrollable.scrollTop(next);
            }
        } else if (this.isMouseInBottomScrollArea) {
            const scrollHeight = this.scrollable.get(0).scrollHeight;
            const height = this.scrollable.get(0).offsetHeight;
            if (scrollTop + height < scrollHeight) {
                const next = scrollTop + CmsDragAndDropService.SCROLLING_STEP;
                this.scrollable.scrollTop(next);
            }
        }
        this.animationFrameId = this._window.requestAnimationFrame(this.scroll.bind(this));
    }

    private async getComponentIndexInSlot(slotId, componentUuid): Promise<number> {
        const componentsForSlot =
            await this.pageContentSlotsComponentsRestService.getComponentsForSlot(slotId);
        let index = componentsForSlot.findIndex(
            (componentInSlot: ICMSComponent) => componentInSlot.uuid === componentUuid
        );
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    private getSelector(selector: EventTarget): JQuery<EventTarget> {
        return this.yjQuery(selector);
    }
}
