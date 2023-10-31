/**
 * Represents possible workflow action statuses.
 */
export declare enum WorkflowActionStatus {
    PENDING = "pending",
    IN_PROGRESS = "in_progress",
    PAUSED = "paused",
    COMPLETED = "completed",
    DISABLED = "disabled",
    ENDED_THROUGH_END_OF_WORKFLOW = "ended_through_end_of_workflow",
    TERMINATED = "terminated"
}
