import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import { CrossFrameEventService, IUriContext, Nullable, SystemEventService, ToolbarItemInternal, IPageService, ICMSPage, IUserTrackingService } from 'smarteditcommons';
import { PageFacade } from '../../../facades';
export declare class DeletePageToolbarItemComponent implements OnInit, OnDestroy {
    private pageService;
    private pageFacade;
    private managePageService;
    private systemEventService;
    private crossFrameEventService;
    private cdr;
    private userTrackingService;
    toolbarItem: ToolbarItemInternal;
    isReady: boolean;
    isDeletePageEnabled: boolean;
    tooltipMessage: Nullable<string>;
    uriContext: IUriContext;
    pageInfo: ICMSPage;
    private unRegPageChange;
    constructor(pageService: IPageService, pageFacade: PageFacade, managePageService: ManagePageService, systemEventService: SystemEventService, crossFrameEventService: CrossFrameEventService, cdr: ChangeDetectorRef, userTrackingService: IUserTrackingService, toolbarItem: ToolbarItemInternal);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    get isDeletePageDisabled(): boolean;
    deletePage(): Promise<void>;
    private updateToolbar;
    private markAsNotReady;
    private markAsReady;
    private resolveTooltipMessage;
}
