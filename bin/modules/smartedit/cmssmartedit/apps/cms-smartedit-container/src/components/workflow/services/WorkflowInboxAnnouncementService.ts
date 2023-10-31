/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable, OnDestroy, Type } from '@angular/core';
import {
    IAnnouncementService,
    Pagination,
    WorkflowTasksPollingService,
    WorkflowTask
} from 'smarteditcommons';

import { WorkflowInboxMultipleTasksAnnouncementComponent } from '../components/workflowInboxMultipleTasksAnnouncement/WorkflowInboxMultipleTasksAnnouncementComponent';
import { WorkflowInboxSingleTaskAnnouncementComponent } from '../components/workflowInboxSingleTaskAnnouncement/WorkflowInboxSingleTaskAnnouncementComponent';

/**
 * This service is used to show announcements for workflow inbox tasks.
 */
@Injectable()
export class WorkflowInboxAnnouncementService implements OnDestroy {
    private unsubscribePollingService: () => void;

    constructor(
        private workflowTasksPollingService: WorkflowTasksPollingService,
        private announcementService: IAnnouncementService
    ) {
        this.unsubscribePollingService = this.workflowTasksPollingService.addSubscriber(
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            (tasks: WorkflowTask[], pagination: Pagination) => {
                if (tasks) {
                    this.displayAnnouncement(tasks);
                }
            },
            false
        );
    }

    ngOnDestroy(): void {
        this.unsubscribePollingService();
    }

    private displayAnnouncement(tasks: WorkflowTask[]): void {
        if (tasks.length === 1) {
            this.showSingleTaskAnnouncement(tasks[0]);
        } else if (tasks.length > 1) {
            this.showMultipleTasksAnnouncement(tasks);
        }
    }

    private async showSingleTaskAnnouncement(task: WorkflowTask): Promise<void> {
        await this.showAnnouncement(WorkflowInboxSingleTaskAnnouncementComponent, {
            task
        });
    }

    private async showMultipleTasksAnnouncement(tasks: WorkflowTask[]): Promise<void> {
        await this.showAnnouncement(WorkflowInboxMultipleTasksAnnouncementComponent, {
            tasks
        });
    }

    private showAnnouncement(component: Type<any>, data: any): PromiseLike<string> {
        return this.announcementService.showAnnouncement({
            component,
            data
        });
    }
}
