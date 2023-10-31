import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { FetchPageStrategy, Page, SystemEventService, ToolbarItemInternal, WorkflowTasksPollingService, WorkflowTask, IUserTrackingService } from 'smarteditcommons';
import { WorkflowFacade } from '../../services';
export declare class WorkflowInboxComponent implements OnInit, OnDestroy {
    private workflowFacade;
    private systemEventService;
    private workflowTasksPollingService;
    private userTrackingService;
    actionItem: ToolbarItemInternal;
    private cdr;
    pageSize: number;
    tasksNotReady: boolean;
    totalNumberOfTasks: number;
    workflowTasks: WorkflowTask[];
    private unRegisterOpenDropdownEvent;
    private unRegisterWorkflowCreatedEvent;
    private unRegWorkflowTasksMenuOpenedEvent;
    constructor(workflowFacade: WorkflowFacade, systemEventService: SystemEventService, workflowTasksPollingService: WorkflowTasksPollingService, userTrackingService: IUserTrackingService, actionItem: ToolbarItemInternal, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
    fetchPageOfInboxTasks: FetchPageStrategy<WorkflowTask>;
    loadInboxTasks(mask: string, pageSize: number, currentPage: number): Promise<Page<WorkflowTask>>;
    onInboxTasksLoaded(workflowTasks: WorkflowTask[]): void;
    onDropdownToggle(isOpen: boolean): void;
    trackByIndex(index: number): number;
    private openDropdown;
    private hideDropdown;
}
