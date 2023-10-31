import { TranslateService } from '@ngx-translate/core';
import { IStorageService, Page, Pageable, SearchParams } from '@smart/utils';
import { Observable } from 'rxjs';
import { CrossFrameEventService } from '../crossFrame/CrossFrameEventService';
import { ICMSPage, ICatalogService, IExperienceService, IRestServiceFactory, ISharedDataService, IPageService, ISyncPollingService } from '../interfaces';
import { IPerspectiveService } from '../perspectives/IPerspectiveService';
import { SystemEventService } from '../SystemEventService';
import { CmsitemsRestService } from './CmsitemsRestService';
import { Workflow, WorkflowAction, WorkflowActionComment, WorkflowEditableItem, WorkflowTask, WorkflowTemplate } from './dtos';
import { WorkflowTasksPollingService } from './WorkflowTasksPollingService';
export declare enum OpenPageWorkflowMenu {
    Default = "Default",
    SwitchPerspective = "SwitchPerspective"
}
export interface WorkflowActionCommentPage extends Page<WorkflowActionComment> {
    comments: WorkflowActionComment[];
}
export interface WorkflowTaskPage extends Page<WorkflowTask> {
    tasks: WorkflowTask[];
}
/**
 * This service is used to manage workflows.
 */
export declare class WorkflowService {
    private readonly restServiceFactory;
    private readonly crossFrameEventService;
    private readonly systemEventService;
    private readonly sharedDataService;
    private readonly perspectiveService;
    private readonly catalogService;
    private readonly experienceService;
    private readonly workflowTasksPollingService;
    private readonly pageService;
    private readonly syncPollingService;
    private readonly cmsitemsRestService;
    private readonly translateService;
    private readonly storageService;
    private readonly resourceWorkflowURI;
    private readonly resourceWorkflowOperationsURI;
    private readonly resourceWorkflowTemplateURI;
    private readonly resourceWorkflowActionsURI;
    private readonly resourceWorkflowActionCommentsURI;
    private readonly resourceWorkflowEditableItemsURI;
    private readonly resourceWorkflowInboxTasksURI;
    private readonly workflowRESTService;
    private readonly workflowActionsRESTService;
    private workflowOperationsRESTService;
    private readonly workflowTemplateRESTService;
    private workflowActionCommentsRESTService;
    private readonly workflowEditableItemsRESTService;
    private readonly workflowInboxTasksRESTService;
    private readonly workflowTasksCountSubject;
    constructor(restServiceFactory: IRestServiceFactory, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService, sharedDataService: ISharedDataService, perspectiveService: IPerspectiveService, catalogService: ICatalogService, experienceService: IExperienceService, workflowTasksPollingService: WorkflowTasksPollingService, pageService: IPageService, syncPollingService: ISyncPollingService, cmsitemsRestService: CmsitemsRestService, translateService: TranslateService, storageService: IStorageService);
    /**
     * Fetch workflow search result by making a REST call to the workflow API.
     *
     * @param queryParams The object representing the query params
     * @param queryParams.pageSize number of items in the page
     * @param queryParams.currentPage current page number
     * @param queryParams.attachments comma separated list of attachment id
     * @param queryParams.status comma separated list of workflow status
     * @param queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns If request is successful, it returns a promise that resolves with the workflow search result. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflows(queryParams: SearchParams): Promise<Workflow[]>;
    /**
     * Fetch workflow templates search result by making a REST call to the workflow API.
     *
     * @param queryParams The object representing the query params.
     * @param queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
     * @param queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
     *
     * @returns If request is successful, it returns a promise that resolves with the workflow template search result. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowTemplates(queryParams: SearchParams): Promise<WorkflowTemplate[]>;
    /**
     * Fetch all actions for a given workflow code.
     *
     * @returns If request is successful, it returns a promise that resolves to list of available actions. If the
     * request fails, it resolves with errors from the backend.
     */
    getAllActionsForWorkflowCode(workflowCode: string): Promise<WorkflowAction[]>;
    /**
     * Cancels the workflow. Shows the confirmation message before sending the request.
     */
    cancelWorflow(workflow: Workflow): Promise<Workflow>;
    /**
     * Returns a workflow template using its code.
     *
     * @returns A promise that resolves with the workflow template result, if the request is successful. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowTemplateByCode(code: string): Promise<WorkflowTemplate>;
    /**
     * This method determines whether the current catalog version (the one in the current experience) has workflows
     * enabled. A catalog version has workflows enabled if it has at least one workflow template assigned to it.
     *
     * @returns A promise that resolves to a boolean. It will be true, if the workflow is
     * enabled for the current catalog version. False, otherwise.
     */
    areWorkflowsEnabledOnCurrentCatalogVersion(): Promise<boolean>;
    /**
     * Fetch an active workflow for a page uuid.
     *
     * @returns A promise that resolves with the workflow object
     * or null, if the request is sucessful and there is no active workflow for provided page uuid.
     * If the request fails, it resolves with errors from the backend.
     */
    getActiveWorkflowForPageUuid(pageUuid: string): Promise<Workflow>;
    /**
     * Verifies whether the page is in a workflow or not.
     *
     * @returns If request is successful, it returns a promise that resolves with boolean value.
     * If the request fails, it resolves with errors from the backend.
     */
    isPageInWorkflow(page: ICMSPage): Promise<boolean>;
    /**
     * Verifies whether the use is a participant of a current active action.
     *
     * @returns If request is successful, it returns a promise that resolves to a boolean. If the
     * request fails, it resolves with errors from the backend.
     */
    isUserParticipanInActiveAction(workflowCode: string): Promise<boolean>;
    /**
     * Fetch all active actions for a given workflow code and for the current user.
     *
     * @returns If request is successful, it returns a promise that resolves to list of active actions. If the
     * request fails, it resolves with errors from the backend.
     */
    getActiveActionsForWorkflowCode(workflowCode: string): Promise<WorkflowAction[]>;
    /**
     * Fetch a page of comments for a given workflow action and some pageable data.
     *
     * @returns If request is successful, it returns a promise that resolves to list of available comments for a given workflow and workflow action. If the
     * request fails, it resolves with errors from the backend.
     */
    getCommentsForWorkflowAction(workflowCode: string, workflowActionCode: string, payload: Pageable): Promise<WorkflowActionCommentPage>;
    /**
     * Fetches a page of workflow inbox tasks active for a given user.
     *
     * @returns If request is successful, it returns a promise that resolves to a page of workflow inbox tasks for a given user. If the
     * request fails, it resolves with errors from the backend.
     */
    getWorkflowInboxTasks(payload: Pageable): Promise<WorkflowTaskPage>;
    /** The total number of active workflow tasks. */
    getTotalNumberOfActiveWorkflowTasks(): Observable<number>;
    updateWorkflowTasksCount(count: number): void;
    /**
     * Returns information about whether each item is editable or not. It also returns a workflow code where item is editable.
     *
     * @returns If request is successful, it returns a promise that resolves to a list of objects where each object
     * contains information about whether each item is editable or not. If the request fails, it resolves with errors from the backend.
     */
    getWorkflowEditableItems(itemUids: string[]): Promise<WorkflowEditableItem[]>;
    /**
     * Returns a resource uri for workflows.
     */
    getResourceWorkflowURI(): string;
    /**
     * Returns a resource uri for workflow operations.
     */
    getResourceWorkflowOperationsURI(): string;
    /**
     * Opens the page workflow menu. If the current perspective is not basic or advanced, it will switch to advanced perspective and then opens the menu.
     */
    openPageWorkflowMenu(): Promise<void>;
    /**
     * Loads the experience by building experience params from the given Workflow Task and then opens the page workflow menu.
     * If the current experience is same as the experience params from the given workflow task, it just opens the page workflow menu.
     * Otherwise, it loads the experience and then opens the page workflow menu.
     */
    loadExperienceAndOpenPageWorkflowMenu(task: WorkflowTask): Promise<void>;
    pageHasUnavailableDependencies(): Promise<boolean>;
    fetchPageTranslatedApprovalInfo(): Promise<string>;
    private _loadExperience;
    private getPageSyncStatus;
}
