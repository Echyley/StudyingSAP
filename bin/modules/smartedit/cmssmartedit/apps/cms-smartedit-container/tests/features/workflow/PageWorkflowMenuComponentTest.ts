/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { WORKFLOW_FINISHED_EVENT } from 'cmscommons';
import { PageWorkflowMenuComponent } from 'cmssmarteditcontainer/components/workflow/components/pageWorkflowMenu/PageWorkflowMenuComponent';
import {
    CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU,
    WORKFLOW_DECISION_SELECTED_EVENT,
    WORKFLOW_TASKS_MENU_OPENED_EVENT
} from 'cmssmarteditcontainer/components/workflow/constants';
import { WorkflowFacade } from 'cmssmarteditcontainer/components/workflow/services';
import {
    EVENT_PERSPECTIVE_CHANGED,
    SmarteditRoutingService,
    SystemEventService,
    ToolbarItemInternal,
    IPageService,
    ICMSPage,
    WorkflowService,
    Workflow,
    WorkflowAction
} from 'smarteditcommons';

describe('PageWorkflowMenuComponent', () => {
    let pageService: jasmine.SpyObj<IPageService>;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let workflowFacade: jasmine.SpyObj<WorkflowFacade>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let routingService: jasmine.SpyObj<SmarteditRoutingService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    const mockWorkflow = {
        workflowCode: '000001J'
    } as Workflow;
    const mockPageInfo = {
        displayStatus: 'DRAFT'
    } as ICMSPage;
    let mockActionItem: ToolbarItemInternal;
    let mockUnRegOpenMenuEvent: jasmine.Spy;
    let mockUnRegPerspectiveChangedEvent: jasmine.Spy;
    let mockUnRegWorkflowFinishedEvent: jasmine.Spy;
    let mockUnRegDecisionSelectedEvent: jasmine.Spy;
    let mockUnRegWorkflowRefreshEvent: jasmine.Spy;

    let component: PageWorkflowMenuComponent;
    beforeEach(() => {
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);
        pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockPageInfo));

        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'getActiveWorkflowForPageUuid'
        ]);
        workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(mockWorkflow));

        workflowFacade = jasmine.createSpyObj<WorkflowFacade>('workflowFacade', [
            'startWorkflow',
            'getAllActionsForWorkflow'
        ]);
        workflowFacade.getAllActionsForWorkflow.and.returnValue(
            Promise.resolve<any>([
                {
                    status: 'COMPLETED',
                    startedAgoInMillis: 10
                },
                {
                    status: 'IN_PROGRESS',
                    startedAgoInMillis: 2
                },
                {
                    status: 'IN_PROGRESS',
                    startedAgoInMillis: 1
                },

                {
                    status: 'PENDING',
                    startedAgoInMillis: 3
                }
            ])
        );

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish',
            'subscribe'
        ]);
        mockUnRegOpenMenuEvent = jasmine.createSpy();
        mockUnRegPerspectiveChangedEvent = jasmine.createSpy();
        mockUnRegWorkflowFinishedEvent = jasmine.createSpy();
        mockUnRegDecisionSelectedEvent = jasmine.createSpy();
        mockUnRegWorkflowRefreshEvent = jasmine.createSpy();
        systemEventService.subscribe.and.returnValues(
            mockUnRegOpenMenuEvent,
            mockUnRegPerspectiveChangedEvent,
            mockUnRegWorkflowFinishedEvent,
            mockUnRegDecisionSelectedEvent,
            mockUnRegWorkflowRefreshEvent
        );

        routingService = jasmine.createSpyObj<SmarteditRoutingService>('smarteditRoutingService', [
            'reload'
        ]);

        mockActionItem = {
            isOpen: false
        } as ToolbarItemInternal;

        component = new PageWorkflowMenuComponent(
            workflowService,
            workflowFacade,
            systemEventService,
            routingService,
            pageService,
            cdr,
            mockActionItem
        );
    });

    describe('initialize', () => {
        it('THEN it should subscribe to Open Page Workflow Menu event', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU,
                jasmine.any(Function)
            );
        });

        it('THEN it should subscribe to Perspective Changed event', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                EVENT_PERSPECTIVE_CHANGED,
                jasmine.any(Function)
            );
        });

        it('THEN it should subscribe to Workflow Finished event', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                WORKFLOW_FINISHED_EVENT,
                jasmine.any(Function)
            );
        });

        it('THEN it should subscribe to Decision Selected event', async () => {
            await component.ngOnInit();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                WORKFLOW_DECISION_SELECTED_EVENT,
                jasmine.any(Function)
            );
        });

        it('GIVEN workflow exists THEN it should load the workflow data AND display "Page Tasks" button ', async () => {
            await component.ngOnInit();

            expect(component.pageHasWorkflow).toBe(true);
            expect(component.workflow).toEqual(mockWorkflow);
        });

        it('GIVEN workflow does not exist THEN it should display "Start Workflow" button', async () => {
            workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(null));

            await component.ngOnInit();

            expect(component.pageHasWorkflow).toBe(false);
            expect(component.workflow).toBe(null);
        });

        it('GIVEN workflow does not exist AND page display status is draft THEN "Start workflow" button should be enabled', async () => {
            workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(null));

            await component.ngOnInit();

            expect(component.isWorkflowEnabled).toEqual(true);
        });

        it('GIVEN workflow does not exist AND page display status is other than draft THEN "Start workflow" button should be disabled', async () => {
            pageService.getCurrentPageInfo.and.returnValue(
                Promise.resolve({
                    ...mockPageInfo,
                    displayStatus: 'SYNCED'
                })
            );
            workflowService.getActiveWorkflowForPageUuid.and.returnValue(Promise.resolve(null));

            await component.ngOnInit();

            expect(component.isWorkflowEnabled).toEqual(false);
        });
    });

    describe('destroy', () => {
        beforeEach(async () => {
            await component.ngOnInit();
            await component.ngOnDestroy();
        });

        it('THEN it should unregister from  Open Page Workflow Menu event', () => {
            expect(mockUnRegOpenMenuEvent).toHaveBeenCalled();
        });

        it('THEN it should unregister from Perspective Changed event', () => {
            expect(mockUnRegPerspectiveChangedEvent).toHaveBeenCalled();
        });

        it('THEN it should unregister from Workflow Finished event', () => {
            expect(mockUnRegWorkflowFinishedEvent).toHaveBeenCalled();
        });

        it('THEN it should unregister from Decision Selected event', () => {
            expect(mockUnRegDecisionSelectedEvent).toHaveBeenCalled();
        });

        it('THEN it should unregister from Workflow Refresh event', () => {
            expect(mockUnRegWorkflowRefreshEvent).toHaveBeenCalled();
        });
    });

    it('WHEN dropdown is open THEN it sets the flag to true AND loads the data properly', async () => {
        await component.onDropdownToggle(true);

        expect(systemEventService.publish).toHaveBeenCalledWith(WORKFLOW_TASKS_MENU_OPENED_EVENT);
        expect(workflowService.getActiveWorkflowForPageUuid).toHaveBeenCalled();
        expect(component.tabsData.actions).toEqual([
            {
                status: 'IN_PROGRESS',
                startedAgoInMillis: 1
            },
            {
                status: 'IN_PROGRESS',
                startedAgoInMillis: 2
            },
            {
                status: 'PENDING',
                startedAgoInMillis: 3
            },
            {
                status: 'COMPLETED',
                startedAgoInMillis: 10
            }
        ] as unknown as WorkflowAction[]);
    });

    it('WHEN dropdown is closed THEN it sets the flag to false', async () => {
        await component.onDropdownToggle(false);

        expect(component.actionItem.isOpen).toBe(false);
        expect(systemEventService.publish).not.toHaveBeenCalled();
    });
});
