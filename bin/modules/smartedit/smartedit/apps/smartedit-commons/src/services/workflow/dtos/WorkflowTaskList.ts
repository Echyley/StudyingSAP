/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pagination } from '@smart/utils';
import { WorkflowTask } from './WorkflowTask';

/**
 * Represent a Workflow Task List.
 */
export interface WorkflowTaskList {
    tasks: WorkflowTask[];
    pagination: Pagination;
}
