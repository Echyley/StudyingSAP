/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import {
    WorkflowActionItemComponent,
    PAGE_APPROVAL_PAGE_APPROVED_ID
} from 'cmssmarteditcontainer/components/workflow/components/workflowMenu/workflowActionItem/WorkflowActionItemComponent';
import {
    WORKFLOW_DECISION_SELECTED_EVENT,
    WORKFLOW_ITEM_MENU_OPENED_EVENT
} from 'cmssmarteditcontainer/components/workflow/constants';
import { WorkflowFacade } from 'cmssmarteditcontainer/components/workflow/services';
import {
    SystemEventService,
    CollapsibleContainerApi,
    SmarteditRoutingService,
    WorkflowService,
    Workflow,
    WorkflowAction,
    WorkflowActionComment,
    WorkflowDecision,
    WorkflowActionStatus
} from 'smarteditcommons';

describe('WorkflowActionItemComponent', () => {
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);
    let workflowFacade: jasmine.SpyObj<WorkflowFacade>;
    let cMSTimeService: jasmine.SpyObj<CMSTimeService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let component: WorkflowActionItemComponent;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let routingService: jasmine.SpyObj<SmarteditRoutingService>;

    const SE_CMS_WORKFLOW_APPROVAL_INFO =
        'The status of current page can’t be changed to Ready to Synch. Please synchronize original-page first and try again.';

    beforeEach(() => {
        workflowFacade = jasmine.createSpyObj<WorkflowFacade>('workflowFacade', [
            'getCommentsForWorkflowAction',
            'makeDecision'
        ]);

        workflowFacade.makeDecision.and.returnValue(Promise.resolve(null));

        cMSTimeService = jasmine.createSpyObj<CMSTimeService>('cMSTimeService', ['getTimeAgo']);

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publish',
            'publishAsync',
            'subscribe'
        ]);

        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'pageHasUnavailableDependencies',
            'fetchPageTranslatedApprovalInfo'
        ]);

        routingService = jasmine.createSpyObj<SmarteditRoutingService>('routingService', [
            'reload'
        ]);

        component = new WorkflowActionItemComponent(
            workflowFacade,
            cMSTimeService,
            systemEventService,
            routingService,
            cdr,
            workflowService
        );
    });

    it('WHEN component is initialized THEN it should subscribe to WORKFLOW_ITEM_MENU_OPENED_EVENT event', () => {
        component.ngOnInit();

        expect(systemEventService.subscribe).toHaveBeenCalledWith(
            WORKFLOW_ITEM_MENU_OPENED_EVENT,
            jasmine.any(Function)
        );
    });

    it('WHEN component is destroyed THEN it should unregister from WORKFLOW_ITEM_MENU_OPENED_EVENT event', () => {
        const mockUnRegWorkflowMenuOpenedEvent = jasmine.createSpy(
            'mockUnRegWorkflowMenuOpenedEvent'
        );
        systemEventService.subscribe.and.returnValue(mockUnRegWorkflowMenuOpenedEvent);
        component.ngOnInit();

        component.ngOnDestroy();

        expect(mockUnRegWorkflowMenuOpenedEvent).toHaveBeenCalled();
    });

    describe('fetchPageOfComments', () => {
        const mockPayload = {
            mask: null,
            pageSize: 10,
            currentPage: 0
        };
        beforeEach(() => {
            component.workflow = {
                workflowCode: '000001J'
            } as Workflow;
            component.workflowAction = {
                code: '000001J'
            } as WorkflowAction;
        });

        it('GIVEN there are comments to be loaded WHEN called THEN it should delegate to Workflow Facade AND set hasComments flag to true', async () => {
            workflowFacade.getCommentsForWorkflowAction.and.returnValue({
                pagination: {
                    totalCount: 10
                }
            } as any);

            await component.fetchPageOfComments(
                mockPayload.mask,
                mockPayload.pageSize,
                mockPayload.currentPage
            );

            expect(workflowFacade.getCommentsForWorkflowAction).toHaveBeenCalledWith(
                jasmine.any(String),
                jasmine.any(String),
                jasmine.objectContaining(mockPayload)
            );
            expect(component.hasComments).toBe(true);
        });

        it('GIVEN there are no comments to be loaded WHEN called THEN it should delegate to Workflow Facade AND set hasComments flag to false', async () => {
            workflowFacade.getCommentsForWorkflowAction.and.returnValue({
                pagination: {
                    totalCount: 0
                }
            } as any);

            await component.fetchPageOfComments(
                mockPayload.mask,
                mockPayload.pageSize,
                mockPayload.currentPage
            );

            expect(workflowFacade.getCommentsForWorkflowAction).toHaveBeenCalledWith(
                jasmine.any(String),
                jasmine.any(String),
                jasmine.objectContaining(mockPayload)
            );
            expect(component.hasComments).toBe(false);
        });
    });

    describe('getWorkflowActionStatusClass', () => {
        it('WHEN status is In Progress THEN it should return correct class', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.IN_PROGRESS
            } as WorkflowAction;

            expect(component.getWorkflowActionStatusClass()).toBe(
                'se-workflow-action-item--started'
            );
        });

        it('WHEN status is Completed THEN it should return correct class', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.COMPLETED
            } as WorkflowAction;

            expect(component.getWorkflowActionStatusClass()).toBe(
                'se-workflow-action-item--completed'
            );
        });

        it('WHEN status is neither In Progress nor Completed THEN it should return null', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.PENDING
            } as WorkflowAction;

            expect(component.getWorkflowActionStatusClass()).toBeNull();
        });
    });

    describe('getReadableStatus', () => {
        it('WHEN status is In Progress THEN it should return correct I18n key', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.IN_PROGRESS
            } as WorkflowAction;

            expect(component.getReadableStatus()).toBe(
                'se.cms.actionitem.page.workflow.action.status.started'
            );
        });

        it('WHEN status is Pending THEN it should return correct I18n key', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.PENDING
            } as WorkflowAction;

            expect(component.getReadableStatus()).toBe(
                'se.cms.actionitem.page.workflow.action.status.not.started'
            );
        });

        it('WHEN status is neither In Progress nor Completed THEN it should return the status', () => {
            component.workflowAction = {
                status: WorkflowActionStatus.COMPLETED
            } as WorkflowAction;

            expect(component.getReadableStatus()).toBe('completed');
        });
    });

    it('should return string that shows how much time ago the action was started', () => {
        component.workflowAction = {
            startedAgoInMillis: 2000
        } as WorkflowAction;
        cMSTimeService.getTimeAgo.and.returnValue('22 Hour(s) Ago');
        const actual1 = component.getActiveSince();
        expect(cMSTimeService.getTimeAgo).toHaveBeenCalledWith(
            component.workflowAction.startedAgoInMillis
        );
        expect(actual1).toBe('22 Hour(s) Ago');

        component.workflowAction = {} as WorkflowAction;
        const actual2 = component.getActiveSince();
        expect(actual2).toBeNull();
    });

    describe('Decision Buttons for a particular action', () => {
        it(
            'GIVEN the user is participant of particular action ' +
                'AND user can make decisions AND there is at least one decision THEN the buttons should be shown',
            () => {
                component.canMakeDecisions = true;
                component.workflowAction = {
                    decisions: [{}],
                    isCurrentUserParticipant: true
                } as WorkflowAction;

                expect(component.canShowDecisionButtons()).toBe(true);
            }
        );

        it('GIVEN conditions are not met THEN it should not show the buttons', () => {
            component.canMakeDecisions = false;

            expect(component.canShowDecisionButtons()).toBe(false);
        });
    });

    describe('canShowComments', () => {
        it('should show comments WHEN show comments chevron is opened OR action comments are loaded', () => {
            const $api = jasmine.createSpyObj<CollapsibleContainerApi>('collapsibleContainerApi', [
                'isExpanded'
            ]);
            $api.isExpanded.and.returnValue(true);
            component.setCollapsibleContainerApi($api);

            expect(component.canShowComments()).toBe(true);

            // chevron is closed but actions are loaded
            component.workflowActionComments = [{}] as WorkflowActionComment[];
            $api.isExpanded.and.returnValue(false);

            expect(component.canShowComments()).toBe(true);
        });

        it('should not show comments WHEN show comments chevron is closed AND action comments are not loaded', () => {
            component.workflowActionComments = [];
            const $api = jasmine.createSpyObj<CollapsibleContainerApi>('collapsibleContainerApi', [
                'isExpanded'
            ]);
            $api.isExpanded.and.returnValue(false);
            component.setCollapsibleContainerApi($api);

            expect(component.canShowComments()).toBe(false);
        });
    });

    it('WHEN main button is clicked THEN it should close menu AND make a decision AND publish event', () => {
        component.workflow = {
            workflowCode: '000001J'
        } as Workflow;
        component.workflowAction = {} as WorkflowAction;

        const decision = {} as WorkflowDecision;
        component.onMainButtonClick(new MouseEvent('click'), decision);

        expect(component.isMenuOpen).toBe(false);

        expect(systemEventService.publish).toHaveBeenCalledWith(WORKFLOW_DECISION_SELECTED_EVENT);

        expect(workflowFacade.makeDecision).toHaveBeenCalledWith(
            component.workflow.workflowCode,
            component.workflowAction,
            decision
        );
    });

    it('WHEN main button is clicked, the decision is approved and the current page has unavailable dependencies Then should show approval information', () => {
        workflowService.pageHasUnavailableDependencies.and.returnValue(Promise.resolve(true));

        workflowService.fetchPageTranslatedApprovalInfo.and.returnValue(
            Promise.resolve(SE_CMS_WORKFLOW_APPROVAL_INFO)
        );

        component.workflow = {
            workflowCode: '000001J'
        } as Workflow;
        component.workflowAction = {} as WorkflowAction;

        const decision = {
            code: PAGE_APPROVAL_PAGE_APPROVED_ID
        } as WorkflowDecision;
        component.onMainButtonClick(new MouseEvent('click'), decision).then(() => {
            expect(component.showApprovalInfo).toBe(true);
            expect(component.approvalInfo).toBe(SE_CMS_WORKFLOW_APPROVAL_INFO);
        });
    });

    describe('WHEN Split Button is clicked ', () => {
        beforeEach(() => {
            component.workflowAction = {
                code: '000001J'
            } as WorkflowAction;
        });

        it('THEN it should toggle Decisions Menu', () => {
            component.onSplitButtonClick(new MouseEvent('click'));

            expect(component.isMenuOpen).toBe(true);

            component.onSplitButtonClick(new MouseEvent('click'));

            expect(component.isMenuOpen).toBe(false);
        });

        it('THEN should publish event WHEN Decisions Menu is open', () => {
            component.onSplitButtonClick(new MouseEvent('click'));
            expect(component.isMenuOpen).toBe(true);
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                WORKFLOW_ITEM_MENU_OPENED_EVENT,
                jasmine.objectContaining({
                    code: '000001J'
                })
            );
        });
    });

    it('should close Decision Menu', () => {
        component.isMenuOpen = true;

        component.onMenuHide();

        expect(component.isMenuOpen).toBe(false);
    });
});
