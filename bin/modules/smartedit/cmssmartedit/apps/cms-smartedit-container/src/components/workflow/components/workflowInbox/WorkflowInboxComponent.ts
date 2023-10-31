/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import { WORKFLOW_CREATED_EVENT } from 'cmscommons';
import {
    FetchPageStrategy,
    Page,
    SystemEventService,
    ToolbarItemInternal,
    TOOLBAR_ITEM,
    WorkflowTasksPollingService,
    WorkflowTask,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import {
    CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN,
    WORKFLOW_TASKS_MENU_OPENED_EVENT
} from '../../constants';
import { WorkflowFacade } from '../../services';

@Component({
    selector: 'se-workflow-inbox',
    templateUrl: './WorkflowInboxComponent.html',
    styleUrls: ['./WorkflowInboxComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkflowInboxComponent implements OnInit, OnDestroy {
    public pageSize = 10;
    public tasksNotReady: boolean;
    public totalNumberOfTasks: number;
    public workflowTasks: WorkflowTask[] = [];

    private unRegisterOpenDropdownEvent: () => void;
    private unRegisterWorkflowCreatedEvent: () => void;
    private unRegWorkflowTasksMenuOpenedEvent: () => void;

    constructor(
        private workflowFacade: WorkflowFacade,
        private systemEventService: SystemEventService,
        private workflowTasksPollingService: WorkflowTasksPollingService,
        private userTrackingService: IUserTrackingService,
        @Inject(TOOLBAR_ITEM) public actionItem: ToolbarItemInternal,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.tasksNotReady = true;
        this.unRegisterOpenDropdownEvent = this.systemEventService.subscribe(
            CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN,
            () => this.openDropdown()
        );

        this.unRegisterWorkflowCreatedEvent = this.systemEventService.subscribe(
            WORKFLOW_CREATED_EVENT,
            () => this.loadInboxTasks('', this.pageSize, 0)
        );

        this.unRegWorkflowTasksMenuOpenedEvent = this.systemEventService.subscribe(
            WORKFLOW_TASKS_MENU_OPENED_EVENT,
            () => this.hideDropdown()
        );

        this.workflowTasksPollingService.startPolling();
    }

    ngOnDestroy(): void {
        this.workflowTasksPollingService.stopPolling();
        this.unRegisterOpenDropdownEvent();
        this.unRegisterWorkflowCreatedEvent();
        this.unRegWorkflowTasksMenuOpenedEvent();
    }

    public fetchPageOfInboxTasks: FetchPageStrategy<WorkflowTask> = (
        search: string,
        pageSize: number,
        currentPage: number
    ) => this.loadInboxTasks(search, pageSize, currentPage);

    public async loadInboxTasks(
        mask: string,
        pageSize: number,
        currentPage: number
    ): Promise<Page<WorkflowTask>> {
        const page = await this.workflowFacade.getWorkflowInboxTasks({
            currentPage,
            mask,
            pageSize
        });
        this.tasksNotReady = false;
        this.totalNumberOfTasks = page.pagination.totalCount;
        this.workflowFacade.updateWorkflowTasksCount(this.totalNumberOfTasks);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
        return page;
    }

    public onInboxTasksLoaded(workflowTasks: WorkflowTask[]): void {
        this.workflowTasks = workflowTasks;
    }

    public onDropdownToggle(isOpen: boolean): void {
        if (isOpen) {
            this.workflowTasksPollingService.stopPolling();
            return;
        }

        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.HEADER_TOOL,
            'Inbox'
        );

        this.workflowTasksPollingService.startPolling();
        this.tasksNotReady = true;
    }

    public trackByIndex(index: number): number {
        return index;
    }

    private openDropdown(): void {
        this.actionItem.isOpen = true;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private hideDropdown(): void {
        this.actionItem.isOpen = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
