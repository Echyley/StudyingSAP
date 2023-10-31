import { ChangeDetectorRef, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CrossFrameEventService, IDropdownMenuItem, IIframeClickDetectionService, IWaitDialogService, LogService, SmarteditRoutingService, IPageService, CmsApprovalStatus, WorkflowService, IUserTrackingService } from 'smarteditcommons';
interface PageApprovalOption extends IDropdownMenuItem {
    status: CmsApprovalStatus;
    selected?: boolean;
}
export declare class PageApprovalSelectorComponent implements OnInit {
    private iframeClickDetectionService;
    private pageService;
    private waitDialogService;
    private workflowService;
    private crossFrameEventService;
    private routingService;
    private logService;
    private cdr;
    private userTrackingService;
    approvalInfo: string;
    isOpen: boolean;
    showApprovalInfo: boolean;
    showDropdown: boolean;
    pageApprovalOptions$: Observable<PageApprovalOption[]>;
    private pageApprovalOptionsSubject;
    private unRegWfFinishedHandler;
    private unRegPerspectiveChangedHandler;
    constructor(iframeClickDetectionService: IIframeClickDetectionService, pageService: IPageService, waitDialogService: IWaitDialogService, workflowService: WorkflowService, crossFrameEventService: CrossFrameEventService, routingService: SmarteditRoutingService, logService: LogService, cdr: ChangeDetectorRef, userTrackingService: IUserTrackingService);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    onClickDropdown(): Promise<void>;
    private selectApprovalStatus;
    private getCurrentPageApprovalStatus;
    private hideComponentIfWorkflowInProgress;
    private selectOption;
    private unselectOptions;
    private getOptions;
    private closeDropdown;
}
export {};
