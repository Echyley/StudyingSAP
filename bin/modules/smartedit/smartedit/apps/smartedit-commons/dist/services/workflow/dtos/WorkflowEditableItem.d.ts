/**
 * Interface used by {@link WorkflowService} to represent an item that is editable in some workflow.
 */
export interface WorkflowEditableItem {
    uid: string;
    uuid: string;
    editableByUser: boolean;
    editableInWorkflow: string;
}
