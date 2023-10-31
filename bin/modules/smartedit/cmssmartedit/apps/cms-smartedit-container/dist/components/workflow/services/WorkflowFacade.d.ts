import { IAlertService, IConfirmationModalService, IPageInfoService, Page, Pageable, SystemEventService, L10nPipe, WorkflowService, WorkflowTaskPage, Workflow, WorkflowAction, WorkflowActionComment, WorkflowDecision } from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services/GenericEditorModalService';
/**
 * Used to manage workflows.
 */
export declare class WorkflowFacade {
    private alertService;
    private workflowService;
    private confirmationModalService;
    private genericEditorModalService;
    private pageInfoService;
    private systemEventService;
    private l10nPipe;
    private VERSION_LABEL_QUALIFIER;
    private CREATE_VERSION_QUALIFIER;
    private SYNC_POLLING_SPEED_PREFIX;
    private startWorkflowStructureAttributes;
    private editWorkflowStructureAttributes;
    private makeDecisionStructureAttributes;
    constructor(alertService: IAlertService, workflowService: WorkflowService, confirmationModalService: IConfirmationModalService, genericEditorModalService: GenericEditorModalService, pageInfoService: IPageInfoService, systemEventService: SystemEventService, l10nPipe: L10nPipe);
    /**
     * Opens the generic editor form that is used to start a workflow.
     *
     * @returns A promise that resolves to a workflow instance.
     */
    startWorkflow(): Promise<Workflow>;
    /**
     * Cancels the workflow. Shows the confirmation message before sending the request.
     */
    cancelWorflow(workflow: Workflow): Promise<void>;
    /**
     * Opens modal to edit workflow.
     *
     * Returns edited workflow.
     */
    editWorkflow(workflow: Workflow): Promise<Workflow>;
    updateWorkflowTasksCount(count?: number): Promise<void>;
    /**
     * Returns all actions for a given workflow code.
     */
    getAllActionsForWorkflow(workflowCode: string): Promise<WorkflowAction[]>;
    /**
     * Returns all active actions for a given workflow code and for the current user.
     */
    getActiveActionsForWorkflow(workflowCode: string): Promise<WorkflowAction[]>;
    /**
     * Returns a paged list of comments for a given workflow and workflow action.
     *
     * Since the result is paginated and used by the InfiniteScrollingComponent, the list of comments is populated in the results property.
     */
    getCommentsForWorkflowAction(workflowCode: string, workflowActionCode: string, payload: Pageable): Promise<Page<WorkflowActionComment>>;
    /**
     * Returns a paged list of active workflow inbox tasks for a given user.
     */
    getWorkflowInboxTasks(payload: Pageable): Promise<WorkflowTaskPage>;
    getTotalNumberOfTasks(): Promise<number>;
    /**
     * Makes a decision for a given workflow action and workflow decision.
     */
    makeDecision(workflowCode: string, workflowAction: WorkflowAction, decision: WorkflowDecision): Promise<Workflow>;
    private confirmDecision;
    /**
     * Returns the data for GenericEditorModalComponent.
     *
     * @param workflow the data object to populate generic editor.
     * If workflow is null the generic editor displays fields to start a workflow,
     * Otherwise, the generic editor displays fields to edit a workflow.
     */
    private getWorkflowDataForEditor;
    private getComponentDataForStartingWorkflow;
    private getComponentDataForNonStartingWorkflow;
    /**
     * Handles errors that the generic editor didn't handle directly (for example, due to unknown qualifiers) in two
     * ways:
     * 1. Even though there are different attributes for createVersion and versionLabel, in the front-end they are handled
     *    in the same widget, under the createVersion qualifier. However, when validating, the backend sends the error
     *    directly to versionLabel. Thus, any time there's an error directed to versionLabel it needs to be assigned to
     *    createVersion and republished for it to become visible.
     * 2. Any other error will be displayed in an alert.
     */
    private handleUnrelatedValidationErrors;
}
