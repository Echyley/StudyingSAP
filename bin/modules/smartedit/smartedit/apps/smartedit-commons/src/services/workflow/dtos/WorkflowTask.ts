/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { WorkflowAction } from './WorkflowAction';
import { WorkflowTasksPage } from './WorkflowTasksPage';

/**
 * Represents a workflow task.
 */
export interface WorkflowTask {
    action: WorkflowAction;
    attachments: WorkflowTasksPage[];
}
