/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import {
    CrossFrameEventService,
    IRestService,
    IRestServiceFactory,
    Timer,
    TimerService,
    WorkflowTasksPollingService,
    WorkflowTask,
    WorkflowTaskList
} from 'smarteditcommons';

describe('WorkflowTasksPollingService - ', () => {
    let workflowTasksPollingService: WorkflowTasksPollingService;
    let timerService: jasmine.SpyObj<TimerService>;
    let timer: jasmine.SpyObj<Timer>;
    let inboxRESTService: jasmine.SpyObj<IRestService<WorkflowTaskList>>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

    const initialTasks = getListOfTasks(0);
    const initialPagination = {
        totalCount: 10
    };

    beforeEach(() => {
        timer = jasmine.createSpyObj<Timer>('Timer', ['start', 'restart', 'stop', 'isActive']);

        timerService = jasmine.createSpyObj<TimerService>('timerService', ['createTimer']);
        timerService.createTimer.and.returnValue(timer);

        inboxRESTService = jasmine.createSpyObj<IRestService<WorkflowTaskList>>(
            'inboxRESTService',
            ['get']
        );
        inboxRESTService.get.and.returnValue(
            Promise.resolve<any>({
                tasks: initialTasks,
                pagination: initialPagination
            })
        );

        const restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceFactory.get.and.returnValue(inboxRESTService);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe', 'publish']
        );

        workflowTasksPollingService = new WorkflowTasksPollingService(
            timerService,
            restServiceFactory,
            crossFrameEventService
        );
    });

    it('Should initialize the polling', () => {
        // THEN
        expect(timerService.createTimer).toHaveBeenCalled();
    });

    it("Should restart polling if it's not active", () => {
        // GIVEN
        timer.isActive.and.returnValue(false);
        expect(timer.restart.calls.count()).toBe(1);

        // WHEN
        workflowTasksPollingService.startPolling();

        // THEN
        expect(timer.restart.calls.count()).toBe(2);
    });

    it("Should not restart polling if it's active", () => {
        // GIVEN
        timer.isActive.and.returnValue(true);
        expect(timer.restart.calls.count()).toBe(1);

        // WHEN
        workflowTasksPollingService.startPolling();

        // THEN
        expect(timer.restart.calls.count()).toBe(1);
    });

    it("Should stop polling if it's active", () => {
        // GIVEN
        timer.isActive.and.returnValue(true);

        // WHEN
        workflowTasksPollingService.stopPolling();

        // THEN
        expect(timer.stop).toHaveBeenCalled();
    });

    it("Should not stop polling if it's not active", () => {
        // GIVEN
        timer.isActive.and.returnValue(false);

        // WHEN
        workflowTasksPollingService.stopPolling();

        // THEN
        expect(timer.stop).not.toHaveBeenCalled();
    });

    it('Should add a subscriber to a list of subscribers', () => {
        // GIVEN
        const fn = () => null;

        // WHEN
        workflowTasksPollingService.addSubscriber(fn, true);

        // THEN
        expect((workflowTasksPollingService as any).subscribers).toEqual([
            {
                subscriber: fn,
                callOnInit: true
            }
        ]);
    });

    it('Should unsubscribe a subscriber', () => {
        // GIVEN
        const subscriber1 = () => null;

        const subscriber2 = () => null;

        const subscriber3 = () => null;

        // WHEN
        workflowTasksPollingService.addSubscriber(subscriber1, true);
        const unsubscribe2 = workflowTasksPollingService.addSubscriber(subscriber2, true);
        workflowTasksPollingService.addSubscriber(subscriber3, true);
        expect((workflowTasksPollingService as any).subscribers).toEqual([
            {
                subscriber: subscriber1,
                callOnInit: true
            },
            {
                subscriber: subscriber2,
                callOnInit: true
            },
            {
                subscriber: subscriber3,
                callOnInit: true
            }
        ]);

        // THEN
        unsubscribe2();
        expect((workflowTasksPollingService as any).subscribers).toEqual([
            {
                subscriber: subscriber1,
                callOnInit: true
            },
            {
                subscriber: subscriber3,
                callOnInit: true
            }
        ]);
    });

    it('Should fetch the whole list of new tasks if retrieved first time and call subscribers that only need to be called on init', async () => {
        // GIVEN
        const subscriber1Spy = jasmine.createSpy();
        const subscriber2Spy = jasmine.createSpy();

        const tasks = getListOfTasks(2);
        const pagination = {
            totalCount: 10
        };

        inboxRESTService.get.and.returnValue(
            Promise.resolve({
                tasks,
                pagination
            } as WorkflowTaskList)
        );

        // WHEN
        workflowTasksPollingService.addSubscriber(subscriber1Spy, true);
        workflowTasksPollingService.addSubscriber(subscriber2Spy, false);
        await (workflowTasksPollingService as any).fetchInboxTasks(true);

        // THEN
        expect(subscriber1Spy).toHaveBeenCalledWith(tasks, pagination);
        expect(subscriber2Spy).not.toHaveBeenCalled();
    });

    it('Should fetch the whole list of new tasks and call all subscribers when called with isInit false', async () => {
        // GIVEN
        const subscriber1Spy = jasmine.createSpy();
        const subscriber2Spy = jasmine.createSpy();

        const tasks = getListOfTasks(2);
        const pagination = {
            totalCount: 10
        };

        inboxRESTService.get.and.returnValue(
            Promise.resolve({
                tasks,
                pagination
            } as WorkflowTaskList)
        );

        // WHEN
        workflowTasksPollingService.addSubscriber(subscriber1Spy, true);
        workflowTasksPollingService.addSubscriber(subscriber2Spy, false);
        await (workflowTasksPollingService as any).fetchInboxTasks(false);

        // THEN
        expect(subscriber1Spy).toHaveBeenCalledWith(tasks, pagination);
        expect(subscriber2Spy).toHaveBeenCalledWith(tasks, pagination);
    });

    it('Should retrive only new tasks and call subscriber for them', async () => {
        // GIVEN
        const subscriberSpy = jasmine.createSpy();
        workflowTasksPollingService.addSubscriber(subscriberSpy, true);

        // first call with 10 tasks (index from 1 to 10)
        const firstTasks = getListOfTasks(10, 1);
        const pagination = {
            totalCount: 10
        };

        inboxRESTService.get.and.returnValue(
            Promise.resolve({
                tasks: firstTasks,
                pagination
            } as WorkflowTaskList)
        );
        await (workflowTasksPollingService as any).fetchInboxTasks();
        expect(subscriberSpy).toHaveBeenCalledWith(firstTasks, pagination);

        // WHEN
        // second call with 2 more new tasks (index from 3 to 12)
        const secondTasks = getListOfTasks(10, 3);
        inboxRESTService.get.and.returnValue(
            Promise.resolve({
                tasks: secondTasks,
                pagination
            } as WorkflowTaskList)
        );
        await (workflowTasksPollingService as any).fetchInboxTasks();

        // THEN
        // calls subscriberSpy only with new tasks with index starting 11 to 12
        const resultTasks = getListOfTasks(2, 11);
        expect(subscriberSpy).toHaveBeenCalledWith(resultTasks, pagination);
    });

    it('Should fetch inbox tasks on AUTHORIZATION_SUCCESS', () => {
        spyOn(workflowTasksPollingService as any, 'initPolling');
        // WHEN
        const callback = crossFrameEventService.subscribe.calls.argsFor(0)[1];
        callback(null);
        // THEN
        expect((workflowTasksPollingService as any).initPolling).toHaveBeenCalled();
    });

    function getListOfTasks(numberOfTasks: number, startingTaskIndex = 1): WorkflowTask[] {
        const result: WorkflowTask[] = [];
        for (let i = 1; i <= numberOfTasks; i++) {
            result.push({
                action: null,
                attachments: [
                    {
                        pageName: 'page' + startingTaskIndex,
                        pageUid: 'pageUid' + startingTaskIndex,
                        catalogId: 'catalogId' + startingTaskIndex,
                        catalogVersion: 'catalogVersion' + startingTaskIndex,
                        catalogName: { en: 'catalogName' + startingTaskIndex }
                    }
                ]
            });
            startingTaskIndex++;
        }
        return result;
    }
});
