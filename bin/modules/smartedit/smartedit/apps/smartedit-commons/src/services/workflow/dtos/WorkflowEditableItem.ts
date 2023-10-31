/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Interface used by {@link WorkflowService} to represent an item that is editable in some workflow.
 */
export interface WorkflowEditableItem {
    uid: string;
    uuid: string;
    editableByUser: boolean;
    editableInWorkflow: string;
}
