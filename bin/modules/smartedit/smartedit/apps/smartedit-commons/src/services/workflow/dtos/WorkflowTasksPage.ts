/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TypedMap } from '@smart/utils';

/**
 * Interface used by WorkflowTask as a page related attachment.
 */
export interface WorkflowTasksPage {
    pageName: string;
    pageUid: string;
    catalogId: string;
    catalogName: TypedMap<string>;
    catalogVersion: string;
}
