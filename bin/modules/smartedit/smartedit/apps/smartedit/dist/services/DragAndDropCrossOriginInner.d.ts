/// <reference types="jquery" />
import { CrossFrameEventService, DragAndDropScrollingService, InViewElementObserver, IDragAndDropCrossOrigin, PolyfillService } from 'smarteditcommons';
/** @internal */
export declare class DragAndDropCrossOrigin extends IDragAndDropCrossOrigin {
    private document;
    private yjQuery;
    private crossFrameEventService;
    private inViewElementObserver;
    private dragAndDropScrollingService;
    private polyfillService;
    private currentElementHovered;
    private lastElementHovered;
    private isSearchingElement;
    constructor(document: Document, yjQuery: JQueryStatic, crossFrameEventService: CrossFrameEventService, inViewElementObserver: InViewElementObserver, dragAndDropScrollingService: DragAndDropScrollingService, polyfillService: PolyfillService);
    initialize(): void;
    private onDnDCrossOriginStart;
    private onTrackMouseInner;
    private onDropElementInner;
    private dispatchDragEvent;
    private getMousePositionInPage;
}
