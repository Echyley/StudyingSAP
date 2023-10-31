/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ContextAwareEditableItemService } from 'cmssmarteditcontainer/services/contextAwareEditableItem/ContextAwareEditableItemServiceOuter';
import { IPageService, WorkflowService } from 'smarteditcommons';

describe('ContextAwareEditableItemServiceTest', () => {
    let service: ContextAwareEditableItemService;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let pageService: jasmine.SpyObj<IPageService>;

    const ITEM_UID = 'itemUid';
    const EDITABLE_IN_WORFKLOW_CODE = 'editableInWorkflow';
    const ANOTHER_WORKFLOW_CODE = 'anotherWorkflowCode';
    const PREVIEWED_PAGE_UID = 'previewedPageUid';

    beforeEach(() => {
        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'getWorkflowEditableItems',
            'getActiveWorkflowForPageUuid'
        ]);
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);
        service = new ContextAwareEditableItemService(workflowService, pageService);

        pageService.getCurrentPageInfo.and.returnValue(
            Promise.resolve<any>({
                uid: PREVIEWED_PAGE_UID
            })
        );

        workflowService.getActiveWorkflowForPageUuid.and.returnValue(
            Promise.resolve<any>({
                workflowCode: EDITABLE_IN_WORFKLOW_CODE
            })
        );
    });

    it('should return true if the item is editable and active workflow same as the previewed page workflow', async () => {
        // GIVEN
        workflowService.getWorkflowEditableItems.and.returnValue(
            Promise.resolve([
                {
                    uid: ITEM_UID,
                    uuid: ITEM_UID,
                    editableByUser: true,
                    editableInWorkflow: EDITABLE_IN_WORFKLOW_CODE
                }
            ])
        );

        // WHEN
        const editable = await service.isItemEditable(ITEM_UID);

        // THEN
        expect(editable).toBe(true);
    });

    it('should return false if the item is editable and active workflow not the same as the previewed page workflow', async () => {
        // GIVEN
        workflowService.getWorkflowEditableItems.and.returnValue(
            Promise.resolve([
                {
                    uid: ITEM_UID,
                    uuid: ITEM_UID,
                    editableByUser: true,
                    editableInWorkflow: EDITABLE_IN_WORFKLOW_CODE
                }
            ])
        );
        workflowService.getActiveWorkflowForPageUuid.and.returnValue(
            Promise.resolve<any>({
                workflowCode: ANOTHER_WORKFLOW_CODE
            })
        );

        // WHEN
        const editable = await service.isItemEditable(ITEM_UID);

        // THEN
        expect(editable).toBe(false);
    });

    it('should return false if the item is not editable', async () => {
        // GIVEN
        workflowService.getWorkflowEditableItems.and.returnValue(
            Promise.resolve<any>([
                {
                    uid: ITEM_UID,
                    uuid: ITEM_UID,
                    editableByUser: false
                }
            ])
        );

        // WHEN
        const editable = await service.isItemEditable(ITEM_UID);

        // THEN
        expect(editable).toBe(false);
    });

    it('should return true if the item is editable and the item is in workflow and there is no context', async () => {
        // GIVEN
        workflowService.getWorkflowEditableItems.and.returnValue(
            Promise.resolve([
                {
                    uid: ITEM_UID,
                    uuid: ITEM_UID,
                    editableByUser: true,
                    editableInWorkflow: EDITABLE_IN_WORFKLOW_CODE
                }
            ])
        );
        pageService.getCurrentPageInfo.and.returnValue(Promise.reject());

        // WHEN
        const editable = await service.isItemEditable(ITEM_UID);

        // THEN
        expect(editable).toBe(true);
    });
});
