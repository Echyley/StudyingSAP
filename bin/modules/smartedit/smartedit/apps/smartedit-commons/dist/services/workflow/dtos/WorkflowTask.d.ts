import { WorkflowAction } from './WorkflowAction';
import { WorkflowTasksPage } from './WorkflowTasksPage';
/**
 * Represents a workflow task.
 */
export interface WorkflowTask {
    action: WorkflowAction;
    attachments: WorkflowTasksPage[];
}
