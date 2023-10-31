/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import { take } from 'rxjs/operators';
import { L10nPipe, WorkflowService, WorkflowTask } from 'smarteditcommons';

@Component({
    selector: 'se-workflow-inbox-task',
    templateUrl: './WorkflowInboxTaskComponent.html',
    styleUrls: ['./WorkflowInboxTaskComponent.scss'],
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkflowInboxTaskComponent {
    @Input() task: WorkflowTask;
    taskName: Promise<string>;
    taskDescription: Promise<string>;

    constructor(
        private cMSTimeService: CMSTimeService,
        private workflowService: WorkflowService,
        private l10nPipe: L10nPipe
    ) {}

    ngOnInit(): void {
        this.taskName = this.getTaskName();
        this.taskDescription = this.getTaskDescription();
    }

    public async getTaskName(): Promise<string> {
        return this.l10nPipe.transform(this.task.action.name).pipe(take(1)).toPromise();
    }

    public async getTaskDescription(): Promise<string> {
        const catalogName = await this.l10nPipe
            .transform(this.task.attachments[0].catalogName)
            .pipe(take(1))
            .toPromise();
        return `${catalogName} ${this.task.attachments[0].catalogVersion} | ${this.task.attachments[0].pageName}`;
    }

    public getTaskCreatedAgo(): string {
        return this.cMSTimeService.getTimeAgo(this.task.action.startedAgoInMillis);
    }

    public onClick($event: Event): void {
        $event.preventDefault();

        this.workflowService.loadExperienceAndOpenPageWorkflowMenu(this.task);
    }
}
