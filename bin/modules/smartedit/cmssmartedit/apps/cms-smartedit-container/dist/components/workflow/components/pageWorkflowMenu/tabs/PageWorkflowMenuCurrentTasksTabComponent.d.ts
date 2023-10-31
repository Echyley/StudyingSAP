import { OnInit } from '@angular/core';
import { TabData, Workflow, WorkflowAction } from 'smarteditcommons';
import { PageWorkflowMenuTabsData } from '../types';
export declare class PageWorkflowMenuCurrentTasksTabComponent implements OnInit {
    currentActions: WorkflowAction[];
    workflow: Workflow;
    private actions;
    constructor(tabData: TabData<PageWorkflowMenuTabsData>);
    ngOnInit(): void;
}
