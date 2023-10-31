import { Pagination } from '@smart/utils';
import { CrossFrameEventService } from '../crossFrame/CrossFrameEventService';
import { IRestServiceFactory } from '../interfaces';
import { TimerService } from '../timer';
import { WorkflowTask } from './dtos';
declare type WorkflowTaskSubscriber = (tasks: WorkflowTask[], pagination: Pagination) => any;
/**
 * Used to retrieve inbox tasks.
 */
export declare class WorkflowTasksPollingService {
    private readonly timerService;
    private readonly restServiceFactory;
    private readonly crossFrameEventService;
    private readonly resourceInboxURI;
    private readonly subscribers;
    private syncPollingTimer;
    private readonly savedHashedTasks;
    private readonly inboxRESTService;
    constructor(timerService: TimerService, restServiceFactory: IRestServiceFactory, crossFrameEventService: CrossFrameEventService);
    /**
     * Stops a polling timer.
     */
    stopPolling(): void;
    /**
     * Starts a polling timer.
     */
    startPolling(): void;
    /**
     * Adds a new subscriber to the polling service. The subscriber is called with a list of new tasks and a pagination information.
     *
     * @param subscriber The subscriber.
     * @param callOnInit Default is true, when set to false, will not call the subscriber on initialization of the polling.
     *
     * @returns The method that can be used to unsubscribe.
     */
    addSubscriber(subscriber: WorkflowTaskSubscriber, callOnInit: boolean): () => void;
    /**
     * Initializes a polling process.
     */
    private initPolling;
    /**
     * Unsubscribes a subscriber.
     * @param subscriber The subscriber that will be unsubscribed.
     */
    private unsubscribe;
    /**
     * Returns tasks that have not been yet delivered to subscribers.
     * @param tasks The list of retrieved tasks from the backend.
     * @return The list of new tasks.
     */
    private getNewTasks;
    /**
     * New tasks are added at the end of the array. If the array is bigger than INBOX_POLLING_PAGESIZE
     * it shrinks from the beginning to the INBOX_POLLING_PAGESIZE size.
     * @param newTasks The list of new tasks that will be stored in cache. Each task is encoded as base-64 string.
     */
    private saveNewHashedTasks;
    /**
     * Encodes a task.
     * @param task the task that will be encoded to a base-64 string.
     * @return The encoded string
     */
    private encodeTask;
    /**
     * Retrieves the list if tasks from the backend in paginated view.
     * It calls each subscriber with a list of new tasks and pagination information.
     */
    private fetchInboxTasks;
    private fetchTaskList;
}
export {};
