/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    WORKFLOW_CREATED_EVENT,
    WORKFLOW_FINISHED_EVENT,
    DEFAULT_SYNCHRONIZATION_POLLING as SYNCHRONIZATION_POLLING
} from 'cmscommons';
import { take } from 'rxjs/operators';
import {
    ConfirmationModalConfig,
    GenericEditorField,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    IAlertService,
    IConfirmationModalService,
    IPageInfoService,
    Page,
    Pageable,
    SystemEventService,
    L10nPipe,
    WorkflowService,
    WorkflowTaskPage,
    Workflow,
    WorkflowAction,
    WorkflowActionComment,
    WorkflowDecision,
    WorkflowOperations,
    WorkflowStatus,
    IGenericEditorModalServiceComponent
} from 'smarteditcommons';
import {
    GenericEditorUnrelatedErrorEvent,
    GenericEditorUnrelatedErrorMessage
} from '../../../services/components/GenericEditorModalComponent';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';

/**
 * Used to manage workflows.
 */
@Injectable()
export class WorkflowFacade {
    private VERSION_LABEL_QUALIFIER = 'versionLabel';
    private CREATE_VERSION_QUALIFIER = 'createVersion';
    private SYNC_POLLING_SPEED_PREFIX = 'workflow-';

    private startWorkflowStructureAttributes: GenericEditorField[];
    private editWorkflowStructureAttributes: GenericEditorField[];
    private makeDecisionStructureAttributes: GenericEditorField[];

    constructor(
        private alertService: IAlertService,
        private workflowService: WorkflowService,
        private confirmationModalService: IConfirmationModalService,
        private genericEditorModalService: GenericEditorModalService,
        private pageInfoService: IPageInfoService,
        private systemEventService: SystemEventService,
        private l10nPipe: L10nPipe
    ) {
        this.startWorkflowStructureAttributes = [
            {
                cmsStructureType: 'EditableDropdown',
                qualifier: 'templateCode',
                i18nKey: 'se.cms.workflow.editor.template',
                localized: false,
                required: true,
                idAttribute: 'code',
                labelAttributes: ['name']
            },
            {
                cmsStructureType: 'LongString',
                qualifier: 'description',
                i18nKey: 'se.cms.workflow.editor.description',
                required: false
            },
            {
                cmsStructureType: 'WorkflowCreateVersionField',
                qualifier: this.CREATE_VERSION_QUALIFIER,
                required: false
            }
        ];

        this.editWorkflowStructureAttributes = [
            {
                cmsStructureType: 'LongString',
                qualifier: 'description',
                i18nKey: 'se.cms.workflow.editor.description',
                required: false
            }
        ];

        this.makeDecisionStructureAttributes = [
            {
                cmsStructureType: 'LongString',
                qualifier: 'comment',
                i18nKey: 'se.cms.workflow.editor.comment',
                required: false
            },
            {
                cmsStructureType: 'WorkflowCreateVersionField',
                qualifier: this.CREATE_VERSION_QUALIFIER,
                required: false
            }
        ];
    }

    /**
     * Opens the generic editor form that is used to start a workflow.
     *
     * @returns A promise that resolves to a workflow instance.
     */
    public async startWorkflow(): Promise<Workflow> {
        const componentData = await this.getWorkflowDataForEditor();

        const unregisterErrorListener = this.systemEventService.subscribe(
            GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
            (eventId: string, eventData: GenericEditorUnrelatedErrorEvent) =>
                this.handleUnrelatedValidationErrors(eventId, eventData)
        );

        try {
            return await this.genericEditorModalService.open<Workflow>(componentData, (result) => {
                this.alertService.showSuccess('se.cms.workflow.create.alert.success');
                this.systemEventService.publish(WORKFLOW_CREATED_EVENT);
                this.updateWorkflowTasksCount();
                return result;
            });
        } finally {
            unregisterErrorListener();
        }
    }

    /**
     * Cancels the workflow. Shows the confirmation message before sending the request.
     */
    public async cancelWorflow(workflow: Workflow): Promise<void> {
        await this.confirmationModalService.confirm({
            title: 'se.cms.workflow.cancel.confirmation.title',
            description: 'se.cms.workflow.cancel.confirmation.description'
        } as ConfirmationModalConfig);

        try {
            await this.workflowService.cancelWorflow(workflow);
            this.alertService.showSuccess('se.cms.workflow.cancel.alert.success');
        } finally {
            this.systemEventService.publish(WORKFLOW_FINISHED_EVENT);
            this.updateWorkflowTasksCount();
        }
    }

    /**
     * Opens modal to edit workflow.
     *
     * Returns edited workflow.
     */
    public async editWorkflow(workflow: Workflow): Promise<Workflow> {
        const componentData = await this.getWorkflowDataForEditor(workflow);
        return this.genericEditorModalService.open<Workflow>(componentData, (result) => result);
    }

    public async updateWorkflowTasksCount(count?: number): Promise<void> {
        const tasksCount = await (typeof count === 'undefined'
            ? this.getTotalNumberOfTasks()
            : count);
        this.workflowService.updateWorkflowTasksCount(tasksCount);
    }

    /**
     * Returns all actions for a given workflow code.
     */
    public getAllActionsForWorkflow(workflowCode: string): Promise<WorkflowAction[]> {
        return this.workflowService.getAllActionsForWorkflowCode(workflowCode);
    }

    /**
     * Returns all active actions for a given workflow code and for the current user.
     */
    public getActiveActionsForWorkflow(workflowCode: string): Promise<WorkflowAction[]> {
        return this.workflowService.getActiveActionsForWorkflowCode(workflowCode);
    }

    /**
     * Returns a paged list of comments for a given workflow and workflow action.
     *
     * Since the result is paginated and used by the InfiniteScrollingComponent, the list of comments is populated in the results property.
     */
    public async getCommentsForWorkflowAction(
        workflowCode: string,
        workflowActionCode: string,
        payload: Pageable
    ): Promise<Page<WorkflowActionComment>> {
        const page = await this.workflowService.getCommentsForWorkflowAction(
            workflowCode,
            workflowActionCode,
            payload
        );
        page.results = page.comments;
        delete page.comments;
        return page;
    }

    /**
     * Returns a paged list of active workflow inbox tasks for a given user.
     */
    public async getWorkflowInboxTasks(payload: Pageable): Promise<WorkflowTaskPage> {
        const page = await this.workflowService.getWorkflowInboxTasks(payload);
        page.results = page.tasks;
        delete page.tasks;
        return page;
    }

    public async getTotalNumberOfTasks(): Promise<number> {
        const page = await this.getWorkflowInboxTasks({ pageSize: 1, currentPage: 0 });

        return page.pagination.totalCount;
    }

    /**
     * Makes a decision for a given workflow action and workflow decision.
     */
    public async makeDecision(
        workflowCode: string,
        workflowAction: WorkflowAction,
        decision: WorkflowDecision
    ): Promise<Workflow> {
        const componentData = {
            title: await this.l10nPipe.transform(decision.name).pipe(take(1)).toPromise(),
            structure: {
                attributes: this.makeDecisionStructureAttributes
            },
            contentApi: this.workflowService.getResourceWorkflowOperationsURI(),
            saveLabel: 'se.cms.workflow.editor.button.submit',
            content: {
                operation: WorkflowOperations.MAKE_DECISION,
                workflowCode,
                actionCode: workflowAction.code,
                decisionCode: decision.code
            },
            initialDirty: true
        };
        return this.genericEditorModalService.open<Workflow>(
            componentData,
            async (result) => {
                await this.confirmDecision(result.status, workflowAction, decision);
                return result;
            },
            null
        );
    }

    private async confirmDecision(
        status: WorkflowStatus,
        workflowAction: WorkflowAction,
        decision: WorkflowDecision
    ): Promise<void> {
        const currentPageUuid = await this.pageInfoService.getPageUUID();
        await this.systemEventService.publish(
            SYNCHRONIZATION_POLLING.SPEED_UP,
            this.SYNC_POLLING_SPEED_PREFIX + currentPageUuid
        );

        if (status.toLowerCase() === WorkflowStatus.FINISHED) {
            this.alertService.showSuccess('se.cms.workflow.completed.alert.success');
            this.systemEventService.publishAsync(WORKFLOW_FINISHED_EVENT);
        } else {
            this.alertService.showSuccess({
                message: 'se.cms.workflow.make.decision.success',
                messagePlaceholders: {
                    workflowDecisionName: await this.l10nPipe
                        .transform(decision.name)
                        .pipe(take(1))
                        .toPromise(),
                    workflowActionName: await this.l10nPipe
                        .transform(workflowAction.name)
                        .pipe(take(1))
                        .toPromise()
                },
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        }

        // resets back to slow polling
        this.systemEventService.publish(
            SYNCHRONIZATION_POLLING.SLOW_DOWN,
            this.SYNC_POLLING_SPEED_PREFIX + currentPageUuid
        );

        const tasksCount = await this.getTotalNumberOfTasks();
        this.updateWorkflowTasksCount(tasksCount);
    }

    /**
     * Returns the data for GenericEditorModalComponent.
     *
     * @param workflow the data object to populate generic editor.
     * If workflow is null the generic editor displays fields to start a workflow,
     * Otherwise, the generic editor displays fields to edit a workflow.
     */
    private async getWorkflowDataForEditor(
        workflow: Workflow = null
    ): Promise<IGenericEditorModalServiceComponent> {
        const isStartingWorkflow = workflow === null;
        const componentData: IGenericEditorModalServiceComponent = {
            title: null,
            contentApi: this.workflowService.getResourceWorkflowURI()
        };

        return isStartingWorkflow
            ? this.getComponentDataForStartingWorkflow(componentData)
            : this.getComponentDataForNonStartingWorkflow(componentData, workflow.workflowCode);
    }

    private async getComponentDataForStartingWorkflow(
        componentData: IGenericEditorModalServiceComponent
    ): Promise<IGenericEditorModalServiceComponent> {
        const templateCodeField = this.startWorkflowStructureAttributes.find(
            (structureAttribute: GenericEditorField) =>
                structureAttribute.qualifier === 'templateCode'
        );
        const workflowTemplates = await this.workflowService.getWorkflowTemplates({});
        templateCodeField.options = workflowTemplates;

        componentData.title = 'se.cms.workflow.editor.start.workflow.title';
        componentData.saveLabel = 'se.cms.workflow.editor.button.start';
        componentData.structure = {
            attributes: this.startWorkflowStructureAttributes
        };

        const pageUuid = await this.pageInfoService.getPageUUID();
        componentData.content = {
            attachments: [pageUuid]
        };
        return componentData;
    }

    private getComponentDataForNonStartingWorkflow(
        componentData: IGenericEditorModalServiceComponent,
        workflowCode: string
    ): IGenericEditorModalServiceComponent {
        componentData.title = 'se.cms.workflow.editor.edit.workflow.title';
        componentData.saveLabel = 'se.cms.workflow.editor.button.save';
        componentData.structure = {
            attributes: this.editWorkflowStructureAttributes
        };
        componentData.componentUuid = workflowCode;
        componentData.componentType = 'workflow';
        return componentData;
    }

    /**
     * Handles errors that the generic editor didn't handle directly (for example, due to unknown qualifiers) in two
     * ways:
     * 1. Even though there are different attributes for createVersion and versionLabel, in the front-end they are handled
     *    in the same widget, under the createVersion qualifier. However, when validating, the backend sends the error
     *    directly to versionLabel. Thus, any time there's an error directed to versionLabel it needs to be assigned to
     *    createVersion and republished for it to become visible.
     * 2. Any other error will be displayed in an alert.
     */
    private handleUnrelatedValidationErrors(
        key: string,
        eventData: GenericEditorUnrelatedErrorEvent
    ): void {
        if (!eventData.sourceGenericEditorId) {
            return;
        }

        let alertMessage = '';
        const errorsToRepublish: GenericEditorUnrelatedErrorMessage[] = [];
        eventData.messages.forEach((error) => {
            if (error.subject === this.VERSION_LABEL_QUALIFIER) {
                error.subject = this.CREATE_VERSION_QUALIFIER;
                errorsToRepublish.push(error);
            } else {
                alertMessage += error.message + ' ';
            }
        });

        if (alertMessage) {
            this.alertService.showDanger(alertMessage);
        }

        if (errorsToRepublish.length > 0) {
            this.systemEventService.publishAsync(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                {
                    messages: errorsToRepublish
                }
            );
        }
    }
}
