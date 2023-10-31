import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { CrossFrameEventService, SystemEventService, IPageService, ICMSPage } from 'smarteditcommons';
export declare class PageDisplayStatusComponent implements OnInit, OnDestroy {
    private pageService;
    private crossFrameEventService;
    private systemEventService;
    private cdr;
    cmsPage?: ICMSPage;
    showLastSyncTime: boolean;
    page: ICMSPage;
    lastSynchedDate: number;
    private unRegPageSyncEvent;
    private unRegPageUpdatedEvent;
    constructor(pageService: IPageService, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    hasBeenSynchedBefore(): boolean;
    private initPageInfo;
    private updateLastSynchedDate;
    private updatePageInfo;
    private canUpdateLastSynchedDate;
    private canUpdatePageInfo;
}
