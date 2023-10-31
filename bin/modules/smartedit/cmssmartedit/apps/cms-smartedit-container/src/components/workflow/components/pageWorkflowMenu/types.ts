/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Workflow, WorkflowAction } from 'smarteditcommons';

export interface PageWorkflowMenuTabsData {
    workflow: Workflow;
    actions: WorkflowAction[];
}
