/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnDestroy,
    OnInit,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import {
    CollapsibleContainerApi,
    FetchPageStrategy,
    Nullable,
    Page,
    SmarteditRoutingService,
    SystemEventService,
    WorkflowService,
    Workflow,
    WorkflowAction,
    WorkflowActionComment,
    WorkflowActionStatus,
    WorkflowDecision
} from 'smarteditcommons';
import {
    WORKFLOW_DECISION_SELECTED_EVENT,
    WORKFLOW_ITEM_MENU_OPENED_EVENT
} from '../../../constants';
import { WorkflowFacade } from '../../../services';
import { WorkflowItemMenuOpenedEventData } from '../../workflowItemMenu/WorkflowItemMenuComponent';

export const PAGE_APPROVAL_PAGE_APPROVED_ID = 'PageApprovalPageApproved';

@Component({
    selector: 'se-workflow-action-item',
    templateUrl: './WorkflowActionItemComponent.html',
    styleUrls: ['./WorkflowActionItemComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkflowActionItemComponent implements OnInit, OnDestroy {
    @Input() workflow: Workflow;
    @Input() workflowAction: WorkflowAction;
    @Input() canMakeDecisions: boolean;

    public isMenuOpen = false;
    public hasComments = true;
    public pageSize = 10;
    public workflowActionComments: WorkflowActionComment[];
    public showApprovalInfo = false;
    public approvalInfo: string;

    private collapsibleContainerApi: CollapsibleContainerApi;
    private unRegWorkflowMenuOpenedEvent: () => void;

    constructor(
        private readonly workflowFacade: WorkflowFacade,
        private readonly cMSTimeService: CMSTimeService,
        private readonly systemEventService: SystemEventService,
        private readonly routingService: SmarteditRoutingService,
        private readonly cdr: ChangeDetectorRef,
        private readonly workflowService: WorkflowService
    ) {}

    ngOnInit(): void {
        this.unRegWorkflowMenuOpenedEvent = this.systemEventService.subscribe(
            WORKFLOW_ITEM_MENU_OPENED_EVENT,
            (eventId: string, eventData: WorkflowItemMenuOpenedEventData) =>
                this.onOtherMenuOpening(eventId, eventData)
        );
    }

    ngOnDestroy(): void {
        this.unRegWorkflowMenuOpenedEvent();
    }

    public fetchPageOfComments: FetchPageStrategy<WorkflowActionComment> = (
        mask: string,
        pageSize: number,
        currentPage: number
    ) => this.loadCommentsAndSetHasCommentsFlag(mask, pageSize, currentPage);

    public onCommentsLoaded(comments: WorkflowActionComment[]): void {
        this.workflowActionComments = comments;
    }

    public setCollapsibleContainerApi($api: CollapsibleContainerApi): void {
        this.collapsibleContainerApi = $api;
    }

    public getWorkflowActionStatusClass(): Nullable<string> {
        switch (this.workflowAction.status.toLowerCase()) {
            case WorkflowActionStatus.IN_PROGRESS:
                return 'se-workflow-action-item--started';
            case WorkflowActionStatus.COMPLETED:
                return 'se-workflow-action-item--completed';
            default:
                return null;
        }
    }

    public getReadableStatus(): string {
        switch (this.workflowAction.status.toLowerCase()) {
            case WorkflowActionStatus.IN_PROGRESS:
                return 'se.cms.actionitem.page.workflow.action.status.started';
            case WorkflowActionStatus.PENDING:
                return 'se.cms.actionitem.page.workflow.action.status.not.started';
            default:
                return this.workflowAction.status;
        }
    }

    public getActiveSince(): Nullable<string> {
        if (!!this.workflowAction.startedAgoInMillis) {
            return this.cMSTimeService.getTimeAgo(this.workflowAction.startedAgoInMillis);
        }
        return null;
    }

    /** Whether to show the main action and decisions menu buttons. */
    public canShowDecisionButtons(): boolean {
        return (
            this.canMakeDecisions &&
            this.workflowAction.isCurrentUserParticipant &&
            this.workflowAction.decisions.length > 0
        );
    }

    /**
     * This method is necessary to make sure that comments are loaded only on-demand.
     * Without this method the infinite scroll used inside the container would load the comments as soon as the workflow menu is opened.
     * Instead, this method only shows the infinite scrolling (and thus loads the comments) only the first time the collapsible container
     * is expanded.
     */
    public canShowComments(): boolean {
        const isContainerExpanded =
            this.collapsibleContainerApi && this.collapsibleContainerApi.isExpanded();

        return (
            isContainerExpanded ||
            (this.workflowActionComments && this.workflowActionComments.length > 0)
        );
    }

    /**
     * Performs main action such as: Approve, Reject, Send for Review.
     */
    public async onMainButtonClick($event: MouseEvent, decision: WorkflowDecision): Promise<void> {
        // This is necessary to stop the action history from opening when the main button is clicked.
        $event.preventDefault();
        $event.stopPropagation();

        this.isMenuOpen = false;

        if (decision.code === PAGE_APPROVAL_PAGE_APPROVED_ID) {
            const pageHasUnavailableDependencies =
                await this.workflowService.pageHasUnavailableDependencies();
            if (pageHasUnavailableDependencies) {
                this.showApprovalInfo = !this.showApprovalInfo;
                this.approvalInfo = await this.workflowService.fetchPageTranslatedApprovalInfo();
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
                return;
            }
        }

        this.makeDecision(decision).then(() => {
            this.routingService.reload();
        });
    }

    /** Opens decisions menu. */
    public onSplitButtonClick($event: MouseEvent): void {
        $event.preventDefault();
        $event.stopPropagation();

        this.isMenuOpen = !this.isMenuOpen;
        if (this.isMenuOpen) {
            this.systemEventService.publishAsync(WORKFLOW_ITEM_MENU_OPENED_EVENT, {
                code: this.workflowAction.code
            });
        }
    }

    public onMenuHide(): void {
        this.isMenuOpen = false;
    }

    public trackByIndex(index: number): number {
        return index;
    }

    private async loadCommentsAndSetHasCommentsFlag(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<WorkflowActionComment>> {
        const page = await this.loadComments(mask, pageSize, currentPage);

        this.hasComments = page.pagination.totalCount > 0;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }

        return page;
    }

    private loadComments(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<WorkflowActionComment>> {
        return this.workflowFacade.getCommentsForWorkflowAction(
            this.workflow.workflowCode,
            this.workflowAction.code,
            {
                mask,
                pageSize,
                currentPage
            }
        );
    }

    private makeDecision(decision: WorkflowDecision): Promise<Workflow> {
        this.systemEventService.publish(WORKFLOW_DECISION_SELECTED_EVENT);

        return this.workflowFacade.makeDecision(
            this.workflow.workflowCode,
            this.workflowAction,
            decision
        );
    }

    /** Closes the opened menu when other menu has been clicked. */
    private onOtherMenuOpening(_eventId: string, eventData: WorkflowItemMenuOpenedEventData): void {
        if (this.workflowAction.code !== eventData.code) {
            this.isMenuOpen = false;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }
}
