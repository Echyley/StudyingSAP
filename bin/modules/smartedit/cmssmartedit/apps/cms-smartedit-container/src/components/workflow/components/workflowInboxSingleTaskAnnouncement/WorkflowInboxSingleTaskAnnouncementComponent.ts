/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import {
    IAnnouncementService,
    Nullable,
    ANNOUNCEMENT_DATA,
    AnnouncementData,
    WorkflowService,
    WorkflowTask
} from 'smarteditcommons';

interface WorkflowAnnouncementData {
    task: WorkflowTask;
}
@Component({
    selector: 'se-workflow-inbox-single-task-announcement',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './WorkflowInboxSingleTaskAnnouncementComponent.html'
})
export class WorkflowInboxSingleTaskAnnouncementComponent implements OnInit {
    public task: WorkflowTask;
    public startedAgo: Nullable<string>;
    private announcementId: string;

    constructor(
        private workflowService: WorkflowService,
        private cmsTimeService: CMSTimeService,
        private announcementService: IAnnouncementService,
        @Inject(ANNOUNCEMENT_DATA) data: AnnouncementData<WorkflowAnnouncementData>
    ) {
        ({ id: this.announcementId, task: this.task } = data);
    }

    ngOnInit(): void {
        this.startedAgo = this.getStartedAgo();
    }

    public onClick(event: Event): void {
        event.stopPropagation();
        this.workflowService.loadExperienceAndOpenPageWorkflowMenu(this.task);
        this.announcementService.closeAnnouncement(this.announcementId);
    }

    private getStartedAgo(): Nullable<string> {
        if (!!this.task.action.startedAgoInMillis) {
            return this.cmsTimeService.getTimeAgo(this.task.action.startedAgoInMillis);
        }
        return null;
    }
}
