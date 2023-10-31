/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import { Workflow, WorkflowAction } from 'smarteditcommons';

@Component({
    selector: 'se-page-workflow-menu-tasks-tab',
    template: `
        <div class="se-page-workflow-tasks-wrapper">
            <div *ngFor="let action of actions">
                <div class="se-page-workflow-task fd-panel">
                    <div>
                        <se-workflow-action-item
                            [workflow]="workflow"
                            [workflowAction]="action"
                            [canMakeDecisions]="canMakeDecisions"></se-workflow-action-item>
                    </div>
                </div>
                <br />
            </div>
        </div>
    `,
    styleUrls: ['./PageWorkflowMenuTasksTabComponent.scss']
})
export class PageWorkflowMenuTasksTabComponent {
    @Input() actions: WorkflowAction[];
    @Input() workflow: Workflow;
    @Input() canMakeDecisions: boolean;
}
