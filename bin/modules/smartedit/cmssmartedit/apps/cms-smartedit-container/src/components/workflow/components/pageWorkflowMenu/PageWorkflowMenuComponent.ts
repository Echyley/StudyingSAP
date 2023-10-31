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
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { WORKFLOW_FINISHED_EVENT } from 'cmscommons';
import {
    EVENT_PERSPECTIVE_CHANGED,
    SmarteditRoutingService,
    SystemEventService,
    Tab,
    ToolbarItemInternal,
    TOOLBAR_ITEM,
    IPageService,
    WorkflowService,
    Workflow,
    WorkflowAction,
    WorkflowActionStatus
} from 'smarteditcommons';
import {
    CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU,
    WORKFLOW_DECISION_SELECTED_EVENT,
    WORKFLOW_REFRESH_EVENT,
    WORKFLOW_TASKS_MENU_OPENED_EVENT
} from '../../constants';
import { WorkflowFacade } from '../../services/WorkflowFacade';
import { PageWorkflowMenuAllTasksTabComponent } from './tabs/PageWorkflowMenuAllTasksTabComponent';
import { PageWorkflowMenuCurrentTasksTabComponent } from './tabs/PageWorkflowMenuCurrentTasksTabComponent';
import { PageWorkflowMenuTabsData } from './types';

@Component({
    selector: 'se-page-workflow-menu',
    templateUrl: './PageWorkflowMenuComponent.html',
    styleUrls: ['./PageWorkflowMenuComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageWorkflowMenuComponent implements OnInit, OnDestroy {
    /** Whether the component can be displayed. */
    public isReady = false;
    /** Whether "Start workflow" button is disabled */
    public isWorkflowEnabled = false;
    /** Whether the "Current" and "All" tabs has been fetched. */
    public areTabsReady = false;
    /** When true "Page Tasks" button is displayed. Otherwise "Start Workflow" button. */
    public pageHasWorkflow = false;
    public tabsList: Tab[] = [
        {
            id: 'currentTasksTab',
            title: 'se.cms.page.workflow.tabs.currenttasks',
            hasErrors: false,
            component: PageWorkflowMenuCurrentTasksTabComponent
        },
        {
            id: 'allTasksTab',
            title: 'se.cms.page.workflow.tabs.alltasks',
            hasErrors: false,
            component: PageWorkflowMenuAllTasksTabComponent
        }
    ];
    /** Data passed down to se-tabs. */
    public tabsData: PageWorkflowMenuTabsData = {
        workflow: null,
        actions: []
    };

    private _workflow: Workflow;
    private unRegOpenMenuEvent: () => void;
    private unRegPerspectiveChangedEvent: () => void;
    private unRegWorkflowFinishedEvent: () => void;
    private unRegDecisionSelectedEvent: () => void;
    private unRegWorkflowRefreshEvent: () => void;

    constructor(
        private workflowService: WorkflowService,
        private workflowFacade: WorkflowFacade,
        private systemEventService: SystemEventService,
        private routingService: SmarteditRoutingService,
        private pageService: IPageService,
        private cdr: ChangeDetectorRef,
        @Inject(TOOLBAR_ITEM) public actionItem: ToolbarItemInternal
    ) {}

    async ngOnInit(): Promise<void> {
        // user selects a task from the Workflow Inbox
        this.unRegOpenMenuEvent = this.systemEventService.subscribe(
            CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU,
            (_eventId: string, open: boolean) => this.onDropdownToggle(open)
        );

        this.unRegPerspectiveChangedEvent = this.systemEventService.subscribe(
            EVENT_PERSPECTIVE_CHANGED,
            () => this.loadWorkflow()
        );

        this.unRegWorkflowFinishedEvent = this.systemEventService.subscribe(
            WORKFLOW_FINISHED_EVENT,
            () => this.loadWorkflow()
        );

        this.unRegDecisionSelectedEvent = this.systemEventService.subscribe(
            WORKFLOW_DECISION_SELECTED_EVENT,
            () => this.closeDropdown()
        );

        this.unRegWorkflowRefreshEvent = this.systemEventService.subscribe(
            WORKFLOW_REFRESH_EVENT,
            () => this.loadWorkflow()
        );

        await this.loadWorkflow();
        this.isReady = true;
        this.actionItem.isOpen = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        this.unRegOpenMenuEvent();
        this.unRegPerspectiveChangedEvent();
        this.unRegWorkflowFinishedEvent();
        this.unRegDecisionSelectedEvent();
        this.unRegWorkflowRefreshEvent();
    }

    set workflow(value: Workflow) {
        this._workflow = value;

        this.tabsData.workflow = value;
    }

    get workflow(): Workflow {
        return this._workflow;
    }

    public async onDropdownToggle(open: boolean): Promise<void> {
        this.actionItem.isOpen = open;
        if (!open) {
            return;
        }
        this.systemEventService.publish(WORKFLOW_TASKS_MENU_OPENED_EVENT);

        await this.loadWorkflowAndActions();
    }

    public async startWorkflow(): Promise<void> {
        await this.workflowFacade.startWorkflow();

        this.routingService.reload();
    }

    private async loadWorkflowAndActions(): Promise<void> {
        this.areTabsReady = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }

        await this.loadWorkflow(false);
        await this.loadActions();

        this.areTabsReady = true;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private async loadWorkflow(detectChanges = true): Promise<void> {
        const page = await this.pageService.getCurrentPageInfo();
        const workflow = await this.workflowService.getActiveWorkflowForPageUuid(page.uuid);

        this.workflow = workflow;
        this.pageHasWorkflow = !!workflow;
        this.isWorkflowEnabled = page.displayStatus === 'DRAFT';

        if (detectChanges) {
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }

    private async loadActions(): Promise<void> {
        const actions = await this.workflowFacade.getAllActionsForWorkflow(
            this.workflow.workflowCode
        );
        this.setActions(actions);
    }

    private setActions(actions: WorkflowAction[]): void {
        actions.sort((a, b) => this.actionComparator(a, b));
        this.tabsData.actions = actions;
    }

    private closeDropdown(): void {
        this.actionItem.isOpen = false;
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private actionComparator(a: WorkflowAction, b: WorkflowAction): number {
        const priorityA = this.getActionPriority(a);
        const priorityB = this.getActionPriority(b);

        return priorityA !== priorityB
            ? priorityA - priorityB
            : a.startedAgoInMillis - b.startedAgoInMillis;
    }

    private getActionPriority(action: WorkflowAction): number {
        switch (action.status.toLowerCase()) {
            case WorkflowActionStatus.IN_PROGRESS:
                return 1;
            case WorkflowActionStatus.PENDING:
                return 2;
            case WorkflowActionStatus.COMPLETED:
                return 3;
            default:
                return 4;
        }
    }
}
