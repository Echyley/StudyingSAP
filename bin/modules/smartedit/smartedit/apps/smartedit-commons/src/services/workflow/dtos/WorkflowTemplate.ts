/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { LocalizedMap } from 'smarteditcommons';

/**
 * Interface used by {@link WorkflowService} to represent a workflow template.
 */
export interface WorkflowTemplate {
    code: string;
    name: LocalizedMap;
}
