/// <reference types="jquery" />
import { TranslateService } from '@ngx-translate/core';
import { WindowUtils } from '@smart/utils';
import { InViewElementObserver } from './InViewElementObserver';
/**
 * @internal
 * @ignore
 */
export declare class DragAndDropScrollingService {
    private windowUtils;
    private translate;
    private inViewElementObserver;
    private yjQuery;
    private static readonly TOP_SCROLL_AREA_ID;
    private static readonly BOTTOM_SCROLL_AREA_ID;
    private SCROLLING_AREA_HEIGHT;
    private FAST_SCROLLING_AREA_HEIGHT;
    private SCROLLING_STEP;
    private FAST_SCROLLING_STEP;
    private topScrollArea;
    private bottomScrollArea;
    private throttleScrollingEnabled;
    private scrollLimitY;
    private scrollDelta;
    private initialized;
    private scrollable;
    private throttledScrollPage;
    private animationFrameId;
    constructor(windowUtils: WindowUtils, translate: TranslateService, inViewElementObserver: InViewElementObserver, yjQuery: JQueryStatic);
    initialize(): void;
    deactivate(): void;
    enable(): void;
    disable(): void;
    toggleThrottling(isEnabled: boolean): void;
    private addScrollAreas;
    private addEventListeners;
    private removeEventListeners;
    private onDragEnter;
    private onDragOver;
    private onDragLeave;
    private scrollPage;
    private getFromSelector;
    private getFromHTMLSelector;
    private getFromElementSelector;
    private getScrollAreas;
    private showScrollAreas;
}
