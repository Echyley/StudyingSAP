/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import { GatewayProxied, IPageService, WorkflowService } from 'smarteditcommons';

@GatewayProxied()
@Injectable()
export class ContextAwareEditableItemService extends IContextAwareEditableItemService {
    constructor(private workflowService: WorkflowService, private pageService: IPageService) {
        super();
    }

    public async isItemEditable(itemUid: string): Promise<boolean> {
        const workflowEditableItems = await this.workflowService.getWorkflowEditableItems([
            itemUid
        ]);
        const item = workflowEditableItems.find((data) => data.uid === itemUid);

        if (item.editableInWorkflow) {
            const editable = await this.editableInCurrentPageContext(item.editableInWorkflow);
            return item.editableByUser && editable;
        }

        return item.editableByUser;
    }

    /**
     * Verifies whether the item's active workflow equals to the workflow of page currently in preview.
     */
    private async editableInCurrentPageContext(editableInWorkflow: string): Promise<boolean> {
        try {
            const pageInfo = await this.pageService.getCurrentPageInfo();
            const activeWorkflow = await this.workflowService.getActiveWorkflowForPageUuid(
                pageInfo.uid
            );
            return activeWorkflow !== null && activeWorkflow.workflowCode === editableInWorkflow;
        } catch {
            return true;
        }
    }
}
