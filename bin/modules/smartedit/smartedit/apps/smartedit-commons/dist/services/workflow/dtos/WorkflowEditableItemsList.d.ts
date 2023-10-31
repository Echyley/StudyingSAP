import { WorkflowEditableItem } from './WorkflowEditableItem';
/**
 * Interface used by {@link WorkflowService}  to represent a list of editable items from some workflow.
 */
export interface WorkflowEditableItemsList {
    editableItems: WorkflowEditableItem[];
}
