import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { IPageInfoService, WorkflowService } from 'smarteditcommons';
export declare class PersonalizationsmarteditContextServiceReverseProxy {
    protected personalizationsmarteditContextService: PersonalizationsmarteditContextService;
    protected workflowService: WorkflowService;
    protected pageInfoService: IPageInfoService;
    static readonly WORKFLOW_RUNNING_STATUS = "RUNNING";
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, workflowService: WorkflowService, pageInfoService: IPageInfoService);
    applySynchronization(): void;
    isCurrentPageActiveWorkflowRunning(): Promise<boolean>;
}
