/// <reference types="jquery" />
import { OnDestroy, OnInit } from '@angular/core';
import { CrossFrameEventService, IIframeClickDetectionService, IPerspective, IPerspectiveService, LogService, SystemEventService, IUserTrackingService } from 'smarteditcommons';
export declare class PerspectiveSelectorComponent implements OnInit, OnDestroy {
    private logService;
    private yjQuery;
    private perspectiveService;
    private iframeClickDetectionService;
    private systemEventService;
    private crossFrameEventService;
    private readonly userTrackingService;
    isOpen: boolean;
    popperOptions: {
        placement: string;
        modifiers: {
            preventOverflow: {
                enabled: boolean;
                escapeWithReference: boolean;
                boundariesElement: string;
            };
            applyStyle: {
                gpuAcceleration: boolean;
            };
        };
    };
    isDisabled: boolean;
    private perspectives;
    private displayedPerspectives;
    private unRegOverlayDisabledFn;
    private unRegPerspectiveAddedFn;
    private unRegPerspectiveChgFn;
    private unRegUserHasChanged;
    private unRegPerspectiveRefreshFn;
    private unRegStrictPreviewModeToggleFn;
    private activePerspective;
    constructor(logService: LogService, yjQuery: JQueryStatic, perspectiveService: IPerspectiveService, iframeClickDetectionService: IIframeClickDetectionService, systemEventService: SystemEventService, crossFrameEventService: CrossFrameEventService, userTrackingService: IUserTrackingService);
    onDocumentClick(event: Event): void;
    ngOnInit(): void;
    ngOnDestroy(): void;
    selectPerspective(event: Event, choice: string): void;
    getDisplayedPerspectives(): IPerspective[];
    getActivePerspectiveName(): string;
    hasActivePerspective(): boolean;
    isTooltipVisible(): boolean;
    getNavigationArrowIconIsCollapse(): string;
    private checkTooltipVisibilityCondition;
    private filterPerspectives;
    private closeDropdown;
    private onPerspectiveAdded;
    private refreshPerspectives;
    private _refreshActivePerspective;
    private onClickHandler;
    private togglePerspectiveSelector;
}
