import { OnInit } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import { IAnnouncementService, Nullable, AnnouncementData, WorkflowService, WorkflowTask } from 'smarteditcommons';
interface WorkflowAnnouncementData {
    task: WorkflowTask;
}
export declare class WorkflowInboxSingleTaskAnnouncementComponent implements OnInit {
    private workflowService;
    private cmsTimeService;
    private announcementService;
    task: WorkflowTask;
    startedAgo: Nullable<string>;
    private announcementId;
    constructor(workflowService: WorkflowService, cmsTimeService: CMSTimeService, announcementService: IAnnouncementService, data: AnnouncementData<WorkflowAnnouncementData>);
    ngOnInit(): void;
    onClick(event: Event): void;
    private getStartedAgo;
}
export {};
