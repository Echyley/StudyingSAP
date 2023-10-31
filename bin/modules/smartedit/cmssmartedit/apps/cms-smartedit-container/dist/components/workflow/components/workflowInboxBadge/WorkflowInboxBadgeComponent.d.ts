import { Observable } from 'rxjs';
import { WorkflowService } from 'smarteditcommons';
export declare class WorkflowInboxBadgeComponent {
    private workflowService;
    inboxCount$: Observable<number>;
    constructor(workflowService: WorkflowService);
    ngOnInit(): void;
    stringifyCount(count: number): string;
}
