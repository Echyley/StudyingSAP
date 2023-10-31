import { CMSTimeService } from 'cmscommons';
import { L10nPipe, WorkflowService, WorkflowTask } from 'smarteditcommons';
export declare class WorkflowInboxTaskComponent {
    private cMSTimeService;
    private workflowService;
    private l10nPipe;
    task: WorkflowTask;
    taskName: Promise<string>;
    taskDescription: Promise<string>;
    constructor(cMSTimeService: CMSTimeService, workflowService: WorkflowService, l10nPipe: L10nPipe);
    ngOnInit(): void;
    getTaskName(): Promise<string>;
    getTaskDescription(): Promise<string>;
    getTaskCreatedAgo(): string;
    onClick($event: Event): void;
}
