import { OnInit } from '@angular/core';
import { AnnouncementData, IAnnouncementService, SystemEventService, TranslationMap, WorkflowTask } from 'smarteditcommons';
interface WorkflowAnnouncementData {
    tasks: WorkflowTask[];
}
export declare class WorkflowInboxMultipleTasksAnnouncementComponent implements OnInit {
    private systemEventService;
    private announcementService;
    tasks: WorkflowTask[];
    count: TranslationMap;
    private announcementId;
    constructor(systemEventService: SystemEventService, announcementService: IAnnouncementService, data: AnnouncementData<WorkflowAnnouncementData>);
    ngOnInit(): void;
    onClick($event: Event): void;
}
export {};
