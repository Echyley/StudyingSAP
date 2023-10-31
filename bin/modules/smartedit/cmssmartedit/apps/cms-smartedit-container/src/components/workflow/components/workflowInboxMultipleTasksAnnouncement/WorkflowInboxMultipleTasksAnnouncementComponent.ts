/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import {
    AnnouncementData,
    ANNOUNCEMENT_DATA,
    IAnnouncementService,
    SystemEventService,
    TranslationMap,
    WorkflowTask
} from 'smarteditcommons';

import { CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN } from '../../constants';

interface WorkflowAnnouncementData {
    tasks: WorkflowTask[];
}

@Component({
    selector: 'se-workflow-inbox-multiple-tasks-announcement',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './WorkflowInboxMultipleTasksAnnouncementComponent.html'
})
export class WorkflowInboxMultipleTasksAnnouncementComponent implements OnInit {
    public tasks: WorkflowTask[];
    public count: TranslationMap = {
        announcementCount: null
    };
    private announcementId: string;

    constructor(
        private systemEventService: SystemEventService,
        private announcementService: IAnnouncementService,
        @Inject(ANNOUNCEMENT_DATA) data: AnnouncementData<WorkflowAnnouncementData>
    ) {
        ({ id: this.announcementId, tasks: this.tasks } = data);
    }

    ngOnInit(): void {
        this.count.announcementCount = String(this.tasks.length);
    }

    public onClick($event: Event): void {
        $event.stopPropagation();
        this.systemEventService.publish(CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN);
        this.announcementService.closeAnnouncement(this.announcementId);
    }
}
