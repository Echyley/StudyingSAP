/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    OnInit,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';

import { WORKFLOW_FINISHED_EVENT } from 'cmscommons';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
    CrossFrameEventService,
    EVENTS,
    EVENT_PERSPECTIVE_CHANGED,
    IDropdownMenuItem,
    IIframeClickDetectionService,
    IWaitDialogService,
    LogService,
    SmarteditRoutingService,
    TypedMap,
    DropdownMenuItemDefaultComponent,
    IPageService,
    CmsApprovalStatus,
    WorkflowService,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

/**
 * Represents one of the approval status options available to a super user.
 */
interface PageApprovalOption extends IDropdownMenuItem {
    status: CmsApprovalStatus;
    selected?: boolean;
}
type PageApprovalOptionsByStatus = TypedMap<PageApprovalOption>;

const PAGE_APPROVAL_SELECTOR_CLOSE_CALLBACK_ID = 'pageApprovalSelectorClose';

/**
 * This component displays a dropdown in the page to allow super users force a page into approved or checked approval status.
 * This is to give them the possibility of skipping a workflow and be able to sync the page.
 *
 * It is only displayed when there is no workflow in progress.
 * But if the currentPage's original page never sync to online, then the currentPage can not be update to 'Ready to Sync'
 */
@Component({
    selector: 'se-page-approval-selector',
    templateUrl: './PageApprovalSelectorComponent.html',
    styleUrls: ['./PageApprovalSelectorComponent.scss'],
    host: {
        '[class.se-page-approval-selector]': 'true'
    },
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageApprovalSelectorComponent implements OnInit {
    public approvalInfo: string;
    public isOpen = false;
    public showApprovalInfo = false;
    public showDropdown = true;
    /**
     * Options displayed in the dropdown.
     * All options except the selected one.
     */
    public pageApprovalOptions$: Observable<PageApprovalOption[]>;
    private pageApprovalOptionsSubject = new BehaviorSubject<PageApprovalOptionsByStatus>({
        CHECK: {
            // Draft
            status: CmsApprovalStatus.CHECK,
            key: 'se.cms.page.approval.check',
            icon: 'se-page-approval-selector__icon--draft',
            component: DropdownMenuItemDefaultComponent,
            condition: (): boolean => true,
            callback: (_selectedItem, clickedItem: PageApprovalOption): Promise<void> =>
                this.selectApprovalStatus(clickedItem)
        },
        APPROVED: {
            // Ready to Sync
            status: CmsApprovalStatus.APPROVED,
            key: 'se.cms.page.approval.approved',
            icon: 'se-page-approval-selector__icon--ready-to-sync',
            component: DropdownMenuItemDefaultComponent,
            condition: (): boolean => true,
            callback: (_selectedItem, clickedItem: PageApprovalOption): Promise<void> =>
                this.selectApprovalStatus(clickedItem)
        }
    });

    private unRegWfFinishedHandler: () => void;
    private unRegPerspectiveChangedHandler: () => void;

    constructor(
        private iframeClickDetectionService: IIframeClickDetectionService,
        private pageService: IPageService,
        private waitDialogService: IWaitDialogService,
        private workflowService: WorkflowService,
        private crossFrameEventService: CrossFrameEventService,
        private routingService: SmarteditRoutingService,
        private logService: LogService,
        private cdr: ChangeDetectorRef,
        private userTrackingService: IUserTrackingService
    ) {}

    async ngOnInit(): Promise<void> {
        this.unRegWfFinishedHandler = this.crossFrameEventService.subscribe(
            WORKFLOW_FINISHED_EVENT,
            () => this.hideComponentIfWorkflowInProgress()
        );
        this.unRegPerspectiveChangedHandler = this.crossFrameEventService.subscribe(
            EVENT_PERSPECTIVE_CHANGED,
            () => this.hideComponentIfWorkflowInProgress()
        );

        this.iframeClickDetectionService.registerCallback(
            PAGE_APPROVAL_SELECTOR_CLOSE_CALLBACK_ID,
            () => this.closeDropdown()
        );

        this.pageApprovalOptions$ = this.pageApprovalOptionsSubject.pipe(
            map((options) => Object.values(options).filter((option) => !option.selected))
        );

        await this.hideComponentIfWorkflowInProgress();
    }

    ngOnDestroy(): void {
        this.unRegWfFinishedHandler();
        this.unRegPerspectiveChangedHandler();
        this.iframeClickDetectionService.removeCallback(PAGE_APPROVAL_SELECTOR_CLOSE_CALLBACK_ID);
    }

    public async onClickDropdown(): Promise<void> {
        if (!this.isOpen) {
            const approvalStatus = await this.getCurrentPageApprovalStatus();
            const pageHasUnavailableDependencies =
                await this.workflowService.pageHasUnavailableDependencies();

            if (approvalStatus !== CmsApprovalStatus.APPROVED && pageHasUnavailableDependencies) {
                this.approvalInfo = await this.workflowService.fetchPageTranslatedApprovalInfo();

                this.showApprovalInfo = true;
            } else {
                this.showApprovalInfo = false;
            }

            this.selectOption(approvalStatus);
        }
        this.isOpen = !this.isOpen;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    /**
     * Callback used to manually force the change of the approval status of a page.
     * The page will be updated with the selected status and reloaded.
     */
    private async selectApprovalStatus({ status }: PageApprovalOption): Promise<void> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.TOOL_BAR,
            'Ready To Sync'
        );
        this.waitDialogService.showWaitModal(null);
        try {
            await this.pageService.forcePageApprovalStatus(status);

            this.crossFrameEventService.publish(EVENTS.PAGE_UPDATED);

            this.routingService.reload();
        } catch (error) {
            this.logService.warn("[PageApprovalSelector] - Can't change page status.", error);
        } finally {
            this.waitDialogService.hideWaitModal();
            this.unselectOptions();
        }
    }

    private async getCurrentPageApprovalStatus(): Promise<CmsApprovalStatus> {
        const { approvalStatus } = await this.pageService.getCurrentPageInfo();
        return approvalStatus;
    }

    private async hideComponentIfWorkflowInProgress(): Promise<void> {
        const { uuid } = await this.pageService.getCurrentPageInfo();
        const workflow = await this.workflowService.getActiveWorkflowForPageUuid(uuid);

        this.showDropdown = !workflow;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    /**
     * If the page is currently in checked or approved status, then this method will mark the corresponding status as the 'selected' option.
     */
    private selectOption(option: CmsApprovalStatus): void {
        this.unselectOptions();

        const options = this.getOptions();
        if (options[option]) {
            options[option].selected = true;
        }
        this.pageApprovalOptionsSubject.next(options);
    }

    private unselectOptions(): void {
        const options = this.getOptions();
        Object.keys(options).forEach((key) => {
            options[key].selected = false;
        });

        this.pageApprovalOptionsSubject.next(options);
    }

    private getOptions(): PageApprovalOptionsByStatus {
        return { ...this.pageApprovalOptionsSubject.getValue() };
    }

    private closeDropdown(): void {
        this.isOpen = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
