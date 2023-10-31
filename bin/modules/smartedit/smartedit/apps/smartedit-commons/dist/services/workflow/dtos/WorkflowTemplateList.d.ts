import { WorkflowTemplate } from './WorkflowTemplate';
/**
 * Interface used by {@link WorkflowService} to query workflow templates.
 */
export interface WorkflowTemplateList {
    templates: WorkflowTemplate[];
}
