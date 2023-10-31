/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import {
    TabData,
    TAB_DATA,
    Workflow,
    WorkflowActionStatus,
    WorkflowAction
} from 'smarteditcommons';
import { PageWorkflowMenuTabsData } from '../types';

@Component({
    selector: 'se-page-workflow-menu-current-tasks-tab',
    template: `
        <se-page-workflow-menu-tasks-tab
            [actions]="currentActions"
            [workflow]="workflow"
            [canMakeDecisions]="true">
        </se-page-workflow-menu-tasks-tab>
    `
})
export class PageWorkflowMenuCurrentTasksTabComponent implements OnInit {
    public currentActions: WorkflowAction[];
    public workflow: Workflow;
    private actions: WorkflowAction[];

    constructor(@Inject(TAB_DATA) tabData: TabData<PageWorkflowMenuTabsData>) {
        ({ actions: this.actions, workflow: this.workflow } = tabData.model);
    }

    ngOnInit(): void {
        this.currentActions = this.actions.filter(
            (action) => action.status.toLowerCase() === WorkflowActionStatus.IN_PROGRESS
        );
    }
}
