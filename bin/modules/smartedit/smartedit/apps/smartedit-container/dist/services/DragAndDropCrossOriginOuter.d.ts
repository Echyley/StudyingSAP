/// <reference types="jquery" />
import { CrossFrameEventService, IDragAndDropCrossOrigin } from 'smarteditcommons';
import { IframeManagerService } from './iframe/IframeManagerService';
/**
 * Polyfill for HTML5 Drag and Drop in a cross-origin setup.
 * Most browsers (except Firefox) do not allow on-page drag-and-drop from non-same-origin frames.
 * This service is a polyfill to allow it, by listening the 'dragover' event over a sibling <div> of the iframe and sending the mouse position to the inner frame.
 * The inner frame 'DragAndDropCrossOriginInner' will use document.elementFromPoint (or isPointOverElement helper function for IE only) to determine the current hovered element and then dispatch drag events onto elligible droppable elements.
 *
 * More information about security restrictions:
 * https://bugs.chromium.org/p/chromium/issues/detail?id=251718
 * https://bugs.chromium.org/p/chromium/issues/detail?id=59081
 * https://www.infosecurity-magazine.com/news/new-google-chrome-clickjacking-vulnerability/
 * https://bugzilla.mozilla.org/show_bug.cgi?id=605991
 */
/** @internal */
export declare class DragAndDropCrossOrigin extends IDragAndDropCrossOrigin {
    private yjQuery;
    private crossFrameEventService;
    private iframeManagerService;
    private throttledSendMousePosition;
    constructor(yjQuery: JQueryStatic, crossFrameEventService: CrossFrameEventService, iframeManagerService: IframeManagerService);
    initialize(): void;
    private isEnabled;
    private onDragStart;
    private onDragEnd;
    private sendMousePosition;
    private getIframeDragArea;
    private getPositionRelativeToIframe;
    private syncIframeDragArea;
}
