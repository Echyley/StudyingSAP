/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EvictionTag } from '@smart/utils';

export const WORKFLOW_CREATED_EVENT = 'WORKFLOW_CREATED_EVENT';

export const WORKFLOW_FINISHED_EVENT = 'WORKFLOW_FINISHED_EVENT';

export const workflowCreatedEvictionTag = new EvictionTag({ event: WORKFLOW_CREATED_EVENT });

export const workflowCompletedEvictionTag = new EvictionTag({ event: WORKFLOW_FINISHED_EVENT });

export const workflowTasksMenuOpenedEvictionTag = new EvictionTag({
    event: 'WORKFLOW_TASKS_MENU_OPENED_EVENT'
});
