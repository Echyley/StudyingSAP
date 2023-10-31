/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { WorkflowEditableItem } from './WorkflowEditableItem';

/**
 * Interface used by {@link WorkflowService}  to represent a list of editable items from some workflow.
 */
export interface WorkflowEditableItemsList {
    editableItems: WorkflowEditableItem[];
}
