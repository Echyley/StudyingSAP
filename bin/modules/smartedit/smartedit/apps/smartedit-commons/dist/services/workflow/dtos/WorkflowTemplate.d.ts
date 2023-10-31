import { LocalizedMap } from 'smarteditcommons';
/**
 * Interface used by {@link WorkflowService} to represent a workflow template.
 */
export interface WorkflowTemplate {
    code: string;
    name: LocalizedMap;
}
