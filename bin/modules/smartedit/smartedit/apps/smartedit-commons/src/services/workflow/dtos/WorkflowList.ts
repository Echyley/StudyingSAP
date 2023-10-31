/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Workflow } from './Workflow';

/**
 * Interface used by {@link WorkflowService} to query workflows.
 */
export interface WorkflowList {
    workflows: Workflow[];
}
