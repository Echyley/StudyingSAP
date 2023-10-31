/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    DEFAULT_SYNCHRONIZATION_POLLING as SYNCHRONIZATION_POLLING,
    WORKFLOW_CREATED_EVENT,
    WORKFLOW_FINISHED_EVENT
} from 'cmscommons';
import { WorkflowFacade } from 'cmssmarteditcontainer/components/workflow/services';
import { GenericEditorModalService } from 'cmssmarteditcontainer/services';
import { GenericEditorUnrelatedErrorEvent } from 'cmssmarteditcontainer/services/components/GenericEditorModalComponent';
import { GenericEditorModalComponentSaveCallback } from 'cmssmarteditcontainer/services/genericEditorModalTypes';
import { Observable } from 'rxjs';
import {
    IAlertService,
    IConfirmationModalService,
    IPageInfoService,
    SystemEventService,
    L10nPipe,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    EventHandler,
    functionsUtils,
    WorkflowService,
    Workflow,
    WorkflowAction,
    WorkflowActionStatus,
    WorkflowDecision,
    WorkflowOperations,
    WorkflowStatus,
    IGenericEditorModalServiceComponent
} from 'smarteditcommons';

describe('WorkflowFacade - ', () => {
    let alertService: jasmine.SpyObj<IAlertService>;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let genericEditorModalService: jasmine.SpyObj<GenericEditorModalService>;
    let confirmationModalService: jasmine.SpyObj<IConfirmationModalService>;
    let unregisterErrorListener: jasmine.Spy;
    let workflowFacade: WorkflowFacade;

    const PAGE_UUID = 'some page UUID';
    const SYNC_POLLING_SPEED_PREFIX = 'workflow-';
    const WORKFLOW_CODE = 'workflowCode';
    const WORKFLOW_ACTION_CODE = 'workflowActionCode';
    const WORKFLOW_DECISION: WorkflowDecision = {
        code: 'some code',
        name: { en: 'some name' },
        description: { en: 'some description' }
    };
    // TODO change workflowAction structure
    const WORKFLOW_ACTION: WorkflowAction = {
        code: 'some code',
        name: { en: 'some name' },
        description: { en: 'some description' },
        actionType: 'some type',
        status: WorkflowActionStatus.IN_PROGRESS,
        startedAgoInMillis: 5435564,
        isCurrentUserParticipant: false,
        decisions: [WORKFLOW_DECISION]
    };
    const WORKFLOW_TEMPLATE_CODE1 = 'WorkflowTemplateCode1';
    const WORKFLOW_TEMPLATE_NAME1 = 'WorkflowTemplateName1';
    const WORKFLOW_TEMPLATE_CODE2 = 'WorkflowTemplateCode2';
    const WORKFLOW_TEMPLATE_NAME2 = 'WorkflowTemplateName2';

    let testWorkflow: Workflow;

    beforeEach(() => {
        alertService = jasmine.createSpyObj<IAlertService>('alertService', [
            'showSuccess',
            'showDanger'
        ]);
        genericEditorModalService = jasmine.createSpyObj<GenericEditorModalService>(
            'genericEditorModalService',
            ['open']
        );
        confirmationModalService = jasmine.createSpyObj<IConfirmationModalService>(
            'confirmationModalService',
            ['confirm']
        );
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
            'getPageUUID'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publishAsync',
            'publish'
        ]);
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);
        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'getWorkflowTemplates',
            'cancelWorflow',
            'getResourceWorkflowURI',
            'getAllActionsForWorkflowCode',
            'getActiveActionsForWorkflowCode',
            'getCommentsForWorkflowAction',
            'getResourceWorkflowOperationsURI',
            'updateWorkflowTasksCount',
            'getWorkflowInboxTasks'
        ]);
        unregisterErrorListener = jasmine.createSpy('unregisterErrorListener');

        workflowFacade = new WorkflowFacade(
            alertService,
            workflowService,
            confirmationModalService,
            genericEditorModalService,
            pageInfoService,
            systemEventService,
            l10nPipe
        );

        testWorkflow = {
            workflowCode: WORKFLOW_CODE,
            templateCode: WORKFLOW_TEMPLATE_CODE1,
            isAvailableForCurrentPrincipal: true,
            status: WorkflowStatus.RUNNING
        };

        genericEditorModalService.open.and.callFake(((
            componentData: IGenericEditorModalServiceComponent,
            saveCallback: GenericEditorModalComponentSaveCallback<Workflow>
        ) => Promise.resolve(saveCallback(testWorkflow))) as any);

        workflowService.getWorkflowTemplates.and.returnValue(
            Promise.resolve<any>({
                templates: [
                    {
                        code: WORKFLOW_TEMPLATE_CODE1,
                        name: WORKFLOW_TEMPLATE_NAME1
                    },
                    {
                        code: WORKFLOW_TEMPLATE_CODE2,
                        name: WORKFLOW_TEMPLATE_NAME2
                    }
                ]
            })
        );

        l10nPipe.transform.and.callFake(
            (localiezdMap: any) =>
                new Observable((subscriver) => {
                    subscriver.next(localiezdMap.en);
                })
        );
    });

    beforeEach(() => {
        workflowService.getWorkflowInboxTasks.and.returnValue(
            Promise.resolve<any>({ tasks: [], pagination: { totalCount: 3 } })
        );
    });

    describe('start workflow ', () => {
        const QUALIFIER_1 = 'some qualifier';
        const ERROR_MSG_1 = 'some unrelated error 1';

        const VERSION_LABEL_QUALIFIER = 'versionLabel';
        const CREATE_VERSION_QUALIFIER = 'createVersion';

        let eventHandler: EventHandler;
        let eventData: GenericEditorUnrelatedErrorEvent;

        beforeEach(() => {
            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));

            eventData = {
                sourceGenericEditorId: 'some editor',
                messages: [
                    {
                        subject: QUALIFIER_1,
                        message: ERROR_MSG_1
                    }
                ]
            };
            systemEventService.subscribe.and.callFake(
                (eventName: string, handler: EventHandler) => {
                    eventHandler = handler;
                    return unregisterErrorListener;
                }
            );
        });

        it('WHEN start workflow is called THEN the generic editor is opened with the current page already as an attachment', async () => {
            // WHEN
            await workflowFacade.startWorkflow();

            // THEN
            expect(genericEditorModalService.open).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    content: {
                        attachments: [PAGE_UUID]
                    }
                }),
                jasmine.any(Function)
            );
        });

        it('WHEN workflow is started THEN a success message is shown AND the editor is properly cleaned', async () => {
            // WHEN
            await workflowFacade.startWorkflow();

            // THEN
            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                jasmine.any(Function)
            );
            expect(alertService.showSuccess).toHaveBeenCalled();
            expect(unregisterErrorListener).toHaveBeenCalled();
        });

        it('WHEN workflow is started THEN it publishes an event that the workflow has been created', async () => {
            await workflowFacade.startWorkflow();

            expect(systemEventService.publish).toHaveBeenCalledWith(WORKFLOW_CREATED_EVENT);
        });

        it('WHEN workflow is started THEN it updates workflow tasks count', async () => {
            const updateWorkflowTasksCountSpy = spyOn(workflowFacade, 'updateWorkflowTasksCount');

            await workflowFacade.startWorkflow();

            expect(updateWorkflowTasksCountSpy).toHaveBeenCalledWith();
        });

        it('WHEN start workflow modal is opened AND cancelled THEN the editor is properly cleaned', async () => {
            genericEditorModalService.open.and.returnValue(Promise.reject());

            try {
                // WHEN
                await workflowFacade.startWorkflow();

                functionsUtils.assertFail();
            } catch {
                // THEN
                expect(systemEventService.subscribe).toHaveBeenCalledWith(
                    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                    jasmine.any(Function)
                );
                expect(unregisterErrorListener).toHaveBeenCalled();
            }
        });

        it('GIVEN a workflow with a validation error is not handled by the generic editor WHEN start workflow modal is saved THEN an alert is shown', async () => {
            // GIVEN
            await workflowFacade.startWorkflow();

            // WHEN
            eventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, eventData);

            // THEN
            expect(alertService.showDanger).toHaveBeenCalledWith(`${ERROR_MSG_1} `);
            expect(systemEventService.publishAsync).not.toHaveBeenCalled();
        });

        it('GIVEN a workflow with a version label validation error WHEN start workflow modal is saved THEN it republishes the error to createVersion field', async () => {
            // GIVEN
            eventData.messages = [
                {
                    subject: VERSION_LABEL_QUALIFIER,
                    message: ERROR_MSG_1
                }
            ];
            const expectedEventData = {
                messages: [
                    {
                        subject: CREATE_VERSION_QUALIFIER,
                        message: ERROR_MSG_1
                    }
                ]
            };

            await workflowFacade.startWorkflow();

            // WHEN
            eventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, eventData);

            // THEN
            expect(alertService.showDanger).not.toHaveBeenCalled();
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                jasmine.objectContaining(expectedEventData)
            );
        });
    });

    describe('cancel workflow', () => {
        let workflow: Workflow;
        beforeEach(() => {
            workflow = {} as Workflow;
            confirmationModalService.confirm.and.returnValue(Promise.resolve<any>({}));
            workflowService.cancelWorflow.and.returnValue(Promise.resolve<any>({}));
            workflowService.getWorkflowInboxTasks.and.returnValue(
                Promise.resolve<any>({ tasks: [], pagination: { totalCount: 1 } })
            );
        });

        it('WHEN workflow is canceled THEN confirmation message is displayed AND success alert is shown', async () => {
            await workflowFacade.cancelWorflow(workflow);

            expect(confirmationModalService.confirm).toHaveBeenCalled();
            expect(alertService.showSuccess).toHaveBeenCalled();
        });

        it('WHEN workflow is canceled THEN an event that the workflow has been finished is published ', async () => {
            await workflowFacade.cancelWorflow(workflow);

            expect(systemEventService.publish).toHaveBeenCalledWith(WORKFLOW_FINISHED_EVENT);
        });

        it('WHEN workflow is canceled THEN an event regarding workflow task count update is published', async () => {
            const updateWorkflowTasksCountSpy = spyOn(workflowFacade, 'updateWorkflowTasksCount');

            await workflowFacade.cancelWorflow(workflow);

            expect(updateWorkflowTasksCountSpy).toHaveBeenCalledWith();
        });
    });

    it('WHEN getAllActionsForWorkflow is called THEN it calls the getAllActionsForWorkflowCode of workflowService', async () => {
        workflowService.getAllActionsForWorkflowCode.and.returnValue(Promise.resolve<any>({}));

        await workflowFacade.getAllActionsForWorkflow(WORKFLOW_CODE);

        expect(workflowService.getAllActionsForWorkflowCode).toHaveBeenCalledWith(WORKFLOW_CODE);
    });

    it('WHEN getActiveActionsForWorkflow is called THEN it calls the getActiveActionsForWorkflowCode of workflowService', async () => {
        workflowService.getActiveActionsForWorkflowCode.and.returnValue(Promise.resolve<any>({}));

        await workflowFacade.getActiveActionsForWorkflow(WORKFLOW_CODE);

        expect(workflowService.getActiveActionsForWorkflowCode).toHaveBeenCalledWith(WORKFLOW_CODE);
    });

    it('WHEN getCommentsForWorkflowAction is called THEN it calls the getCommentsForworkflowAction of workflowService and copies the comments object to results', async () => {
        workflowService.getCommentsForWorkflowAction.and.returnValue(
            Promise.resolve<any>({
                comments: ['comment1', 'comment2'],
                pagination: {
                    someKey: 'someValue'
                }
            })
        );

        const workflowActionCommentsPage = await workflowFacade.getCommentsForWorkflowAction(
            WORKFLOW_CODE,
            WORKFLOW_ACTION_CODE,
            { currentPage: 0 }
        );

        expect(workflowService.getCommentsForWorkflowAction).toHaveBeenCalledWith(
            WORKFLOW_CODE,
            WORKFLOW_ACTION_CODE,
            { currentPage: 0 }
        );
        expect(workflowActionCommentsPage.results.length).toBe(2);
    });

    describe('make decision', () => {
        const expectedConfig = {
            title: WORKFLOW_DECISION.name.en,
            content: {
                operation: WorkflowOperations.MAKE_DECISION,
                workflowCode: WORKFLOW_CODE,
                actionCode: WORKFLOW_ACTION.code,
                decisionCode: WORKFLOW_DECISION.code
            }
        };

        beforeEach(() => {
            workflowService.getCommentsForWorkflowAction.and.returnValue(
                Promise.resolve<any>({
                    comments: ['comment1', 'comment2'],
                    pagination: {
                        someKey: 'someValue'
                    }
                })
            );

            pageInfoService.getPageUUID.and.returnValue(Promise.resolve(PAGE_UUID));
            systemEventService.publish.and.returnValue(Promise.resolve());
        });

        it('GIVEN normal action WHEN makeDecision is called THEN it opens the generic editor', async () => {
            // WHEN
            const workflowReturned = await workflowFacade.makeDecision(
                WORKFLOW_CODE,
                WORKFLOW_ACTION,
                WORKFLOW_DECISION
            );

            // THEN
            expect(genericEditorModalService.open).toHaveBeenCalledWith(
                jasmine.objectContaining(expectedConfig),
                jasmine.any(Function),
                null
            );

            expect(alertService.showSuccess).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    message: 'se.cms.workflow.make.decision.success',
                    messagePlaceholders: {
                        workflowDecisionName: WORKFLOW_DECISION.name.en,
                        workflowActionName: WORKFLOW_ACTION.name.en
                    }
                })
            );

            expect(workflowReturned).toBe(testWorkflow);
        });

        it('GIVEN end action WHEN makeDecision is called THEN workflow completed message is displayed', async () => {
            // GIVEN
            testWorkflow.status = WorkflowStatus.FINISHED;

            // WHEN
            const workflowReturned = await workflowFacade.makeDecision(
                WORKFLOW_CODE,
                WORKFLOW_ACTION,
                WORKFLOW_DECISION
            );

            // THEN
            expect(genericEditorModalService.open).toHaveBeenCalledWith(
                jasmine.objectContaining(expectedConfig),
                jasmine.any(Function),
                null
            );
            expect(alertService.showSuccess).toHaveBeenCalledWith(
                'se.cms.workflow.completed.alert.success'
            );

            expect(workflowReturned).toBe(testWorkflow);
        });

        it('GIVEN normal action WHEN makeDecision is called AND user cancels the editor THEN no action takes place', async () => {
            // GIVEN
            genericEditorModalService.open.and.returnValue(Promise.reject());

            // WHEN
            try {
                await workflowFacade.makeDecision(
                    WORKFLOW_CODE,
                    WORKFLOW_ACTION,
                    WORKFLOW_DECISION
                );

                functionsUtils.assertFail();
            } catch {
                // THEN
                expect(genericEditorModalService.open).toHaveBeenCalledWith(
                    jasmine.objectContaining(expectedConfig),
                    jasmine.any(Function),
                    null
                );
                expect(alertService.showSuccess).not.toHaveBeenCalled();
            }
        });

        it('GIVEN any action WHEN makeDecision is called THEN the synchronization polling speed event is triggered to force a sync job to start', async () => {
            // WHEN
            await workflowFacade.makeDecision(WORKFLOW_CODE, WORKFLOW_ACTION, WORKFLOW_DECISION);

            // THEN
            expect(systemEventService.publish).toHaveBeenCalledWith(
                SYNCHRONIZATION_POLLING.SPEED_UP,
                SYNC_POLLING_SPEED_PREFIX + PAGE_UUID
            );
        });

        it('GIVEN any action WHEN makeDecision is called AND user cancels the editor THEN no synchronization polling speed even is triggered', async () => {
            // GIVEN
            genericEditorModalService.open.and.returnValue(Promise.reject());

            try {
                // WHEN
                await workflowFacade.makeDecision(
                    WORKFLOW_CODE,
                    WORKFLOW_ACTION,
                    WORKFLOW_DECISION
                );

                functionsUtils.assertFail();
            } catch {
                // THEN
                expect(systemEventService.publish).not.toHaveBeenCalled();
            }
        });
    });

    describe('updateWorkflowTasksCount', () => {
        it('WHEN count is provided THEN it delegates to workflowService', async () => {
            const count = 100;

            await workflowFacade.updateWorkflowTasksCount(count);

            expect(workflowService.updateWorkflowTasksCount).toHaveBeenCalledWith(count);
        });

        it('WHEN count is not provided THEN it fetches then it gets the count AND delegates to workfloService', async () => {
            await workflowFacade.updateWorkflowTasksCount();

            expect(workflowService.updateWorkflowTasksCount).toHaveBeenCalledWith(3);
        });
    });
});
