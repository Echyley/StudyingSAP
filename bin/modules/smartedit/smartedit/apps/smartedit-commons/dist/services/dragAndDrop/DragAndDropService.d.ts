/// <reference types="jquery" />
import { IDragAndDropCrossOrigin } from '../interfaces/IDragAndDropCrossOrigin';
import { SystemEventService } from '../SystemEventService';
import { DragAndDropScrollingService } from './DragAndDropScrollingService';
import { InViewElementObserver } from './InViewElementObserver';
export interface DragAndDropConfiguration {
    id: string;
    sourceSelector: string | string[];
    targetSelector: string;
    startCallback: (event: Event) => void;
    dragEnterCallback?: (event: Event) => void;
    dragOverCallback?: (event: Event) => void;
    dropCallback?: (event: Event) => void;
    outCallback?: (event: Event) => void;
    stopCallback: (event: Event) => void;
    helper?: () => string;
    enableScrolling: boolean;
    /** @internal */
    _cachedDragImage?: HTMLImageElement;
}
export declare class DragAndDropService {
    private yjQuery;
    private dragAndDropScrollingService;
    private inViewElementObserver;
    private systemEventService;
    private dragAndDropCrossOrigin;
    private static readonly DRAGGABLE_ATTR;
    private static readonly DROPPABLE_ATTR;
    private configurations;
    private isDragAndDropExecuting;
    constructor(yjQuery: JQueryStatic, dragAndDropScrollingService: DragAndDropScrollingService, inViewElementObserver: InViewElementObserver, systemEventService: SystemEventService, dragAndDropCrossOrigin: IDragAndDropCrossOrigin);
    /**
     * This method registers a new instance of the drag and drop service.
     * Note: Registering doesn't start the service. It just provides the configuration, which later must be applied with the apply method.
     *
     */
    register(configuration: DragAndDropConfiguration): void;
    /**
     * This method removes the drag and drop instances specified by the provided IDs.
     *
     */
    unregister(configurationsIDList: string[]): void;
    /**
     * This method applies all drag and drop configurations registered.
     *
     */
    applyAll(): void;
    /**
     * This method apply the configuration specified by the provided ID in the current page. After this method is executed drag and drop can be started by the user.
     *
     */
    apply(configurationID: string): void;
    /**
     * This method updates the drag and drop instance specified by the provided ID in the current page. It is important to execute this method every time a draggable or droppable element
     * is added or removed from the page DOM.
     *
     */
    update(configurationID: string): void;
    /**
     * This method forces the page to prepare for a drag and drop operation. This method is necessary when the drag and drop operation is started somewhere else,
     * like on a different iFrame.
     *
     */
    markDragStarted(): void;
    /**
     * This method forces the page to clean after a drag and drop operation. This method is necessary when the drag and drop operation is stopped somewhere else,
     * like on a different iFrame.
     *
     */
    markDragStopped(): void;
    private _update;
    private deactivateConfiguration;
    private onDragStart;
    private onDragEnd;
    private onDragEnter;
    private onDragOver;
    private onDrop;
    private onDragLeave;
    private cacheDragImages;
    private setDragTransferData;
    private getSelector;
    private setDragAndDropExecutionStatus;
    private initializeScrolling;
    private deactivateScrolling;
    private browserRequiresCustomScrolling;
}
