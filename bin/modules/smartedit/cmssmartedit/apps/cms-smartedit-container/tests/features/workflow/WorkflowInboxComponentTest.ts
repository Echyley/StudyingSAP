/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { WORKFLOW_CREATED_EVENT } from 'cmscommons';
import { WorkflowInboxComponent } from 'cmssmarteditcontainer/components/workflow/components/workflowInbox/WorkflowInboxComponent';
import {
    CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN,
    WORKFLOW_TASKS_MENU_OPENED_EVENT
} from 'cmssmarteditcontainer/components/workflow/constants';
import { WorkflowFacade } from 'cmssmarteditcontainer/components/workflow/services';
import {
    Page,
    Pagination,
    SystemEventService,
    ToolbarItemInternal,
    WorkflowTasksPollingService,
    WorkflowTask,
    IUserTrackingService
} from 'smarteditcommons';

describe('WorkflowInboxComponent', () => {
    let workflowFacade: jasmine.SpyObj<WorkflowFacade>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let workflowTasksPollingService: jasmine.SpyObj<WorkflowTasksPollingService>;
    let mockUnsubscribeOpenDropdownEvent: jasmine.Spy;
    let mockUnsubscribeWorkflowCreatedEvent: jasmine.Spy;
    let mockUnsubscribeWorkflowTasksMenuOpenedEvent: jasmine.Spy;
    let loadInboxTasksSpy: jasmine.Spy;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    const mockWorkflowTask: WorkflowTask = {
        action: null,
        attachments: []
    };
    const mockWorkflowInboxTasksPage: Page<WorkflowTask> = {
        pagination: {
            totalCount: 3
        } as Pagination,
        results: [mockWorkflowTask]
    };

    let component: WorkflowInboxComponent;
    beforeEach(() => {
        workflowFacade = jasmine.createSpyObj<WorkflowFacade>('workflowFacade', [
            'getWorkflowInboxTasks',
            'updateWorkflowTasksCount'
        ]);
        workflowFacade.getWorkflowInboxTasks.and.returnValue(
            Promise.resolve<any>(mockWorkflowInboxTasksPage)
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe',
            'publish'
        ]);
        mockUnsubscribeOpenDropdownEvent = jasmine.createSpy();
        mockUnsubscribeWorkflowCreatedEvent = jasmine.createSpy();
        mockUnsubscribeWorkflowTasksMenuOpenedEvent = jasmine.createSpy();
        systemEventService.subscribe.and.returnValues(
            mockUnsubscribeOpenDropdownEvent,
            mockUnsubscribeWorkflowCreatedEvent,
            mockUnsubscribeWorkflowTasksMenuOpenedEvent
        );

        workflowTasksPollingService = jasmine.createSpyObj<WorkflowTasksPollingService>(
            'workflowTasksPollingService',
            ['startPolling', 'stopPolling']
        );

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        const actionItem = {
            isOpen: false
        } as ToolbarItemInternal;

        component = new WorkflowInboxComponent(
            workflowFacade,
            systemEventService,
            workflowTasksPollingService,
            userTrackingService,
            actionItem,
            cdr
        );

        loadInboxTasksSpy = spyOn(component, 'loadInboxTasks').and.callThrough();
    });

    describe('WHEN initialized THEN', () => {
        it('it should set tasksNotReady flag to true', () => {
            component.ngOnInit();

            expect(component.tasksNotReady).toBe(true);
        });

        it('it should subscribe to CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN event', () => {
            component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN,
                jasmine.any(Function)
            );
        });

        it('it should subscribe to WORKFLOW_CREATED_EVENT event', () => {
            component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                WORKFLOW_CREATED_EVENT,
                jasmine.any(Function)
            );
        });

        it('THEN it should subscribe to Workflow Tasks Menu Opened event', () => {
            component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                WORKFLOW_TASKS_MENU_OPENED_EVENT,
                jasmine.any(Function)
            );
        });

        it('THEN it should start polling', () => {
            component.ngOnInit();

            expect(workflowTasksPollingService.startPolling).toHaveBeenCalled();
        });
    });

    describe('WHEN destroyed THEN', () => {
        it('it should stop polling', () => {
            component.ngOnInit();
            component.ngOnDestroy();

            expect(workflowTasksPollingService.stopPolling).toHaveBeenCalled();
        });

        it('it should unsubscribe from CMS_EVENT_OPEN_WORKFLOW_INBOX_DROPDOWN event', () => {
            component.ngOnInit();
            component.ngOnDestroy();

            expect(mockUnsubscribeOpenDropdownEvent).toHaveBeenCalled();
        });

        it('it should unsubscribe from WORKFLOW_CREATED_EVENT event', () => {
            component.ngOnInit();
            component.ngOnDestroy();

            expect(mockUnsubscribeWorkflowCreatedEvent).toHaveBeenCalled();
        });

        it('THEN it should unsubscribe from Workflow Tasks Menu Opened event', () => {
            component.ngOnInit();
            component.ngOnDestroy();

            expect(mockUnsubscribeWorkflowTasksMenuOpenedEvent).toHaveBeenCalled();
        });
    });

    it('should open inbox on event', () => {
        systemEventService.subscribe.and.callThrough();
        component.ngOnInit();

        const cb = systemEventService.subscribe.calls.argsFor(0)[1];

        expect(component.actionItem.isOpen).toBe(false);
        cb(null);

        expect(component.actionItem.isOpen).toBe(true);
    });

    it('WHEN fetchPageOfInboxTasks is called THEN it should delegate to loadInboxTasks', () => {
        component.fetchPageOfInboxTasks('', 10, 10);

        expect(loadInboxTasksSpy).toHaveBeenCalledWith('', 10, 10);
    });

    describe('loadInboxTasks', () => {
        describe('loads inbox taks properly', () => {
            let actualPage: Page<WorkflowTask>;
            beforeEach(async () => {
                actualPage = await component.loadInboxTasks('', 10, 10);
            });

            it('should set tasksNotReady to false', () => {
                expect(component.tasksNotReady).toBe(false);
            });

            it('should set totalNumberOfTasks properly', () => {
                expect(component.totalNumberOfTasks).toEqual(3);
            });

            it('should call workflow tasks count with a total number of tasks', () => {
                expect(workflowFacade.updateWorkflowTasksCount).toHaveBeenCalledWith(3);
            });

            it('should return page of workflow inbox tasks', () => {
                expect(actualPage.results.length).toEqual(1);
            });
        });

        it('should load inbox tasks when workflow has been created', () => {
            systemEventService.subscribe.and.callThrough();
            component.ngOnInit();

            const cb = systemEventService.subscribe.calls.argsFor(1)[1];
            cb(null);

            expect(component.loadInboxTasks).toHaveBeenCalled();
        });
    });

    it('should set workflowTasks properly when the tasks has been loaded', () => {
        component.onInboxTasksLoaded([mockWorkflowTask]);

        expect(component.workflowTasks.length).toEqual(1);
    });

    describe('GIVEN inbox has been toggled', () => {
        it('WHEN opened THEN it should stop polling', () => {
            component.onDropdownToggle(true);

            expect(workflowTasksPollingService.stopPolling).toHaveBeenCalled();
        });

        it('WHEN closed THEN it should start polling AND set tasksNotReady flag to false AND track user action', () => {
            component.onDropdownToggle(false);

            expect(workflowTasksPollingService.startPolling).toHaveBeenCalled();
            expect(component.tasksNotReady).toBe(true);
            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });
    });
});
