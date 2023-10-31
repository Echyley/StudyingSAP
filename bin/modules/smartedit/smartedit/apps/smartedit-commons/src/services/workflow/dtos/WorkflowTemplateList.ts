/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { WorkflowTemplate } from './WorkflowTemplate';

/**
 * Interface used by {@link WorkflowService} to query workflow templates.
 */
export interface WorkflowTemplateList {
    templates: WorkflowTemplate[];
}
