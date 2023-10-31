/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSTimeService } from 'cmscommons';
import { WorkflowActionCommentComponent } from 'cmssmarteditcontainer/components/workflow/components/workflowMenu/workflowActionComment/WorkflowActionCommentComponent';
import { WorkflowAction, WorkflowActionComment } from 'smarteditcommons';

describe('WorkflowActionCommentComponent', () => {
    let component: WorkflowActionCommentComponent;
    let cMSTimeService: jasmine.SpyObj<CMSTimeService>;

    beforeEach(() => {
        cMSTimeService = jasmine.createSpyObj<CMSTimeService>('cMSTimeService', ['getTimeAgo']);
        cMSTimeService.getTimeAgo.and.returnValue('22 Hour(s) Ago');

        component = new WorkflowActionCommentComponent(cMSTimeService);
    });

    describe('initialize', () => {
        it('comment is decision comment when there is decision name', () => {
            component.actionComment = {
                decisionName: 'Approve'
            } as WorkflowActionComment;
            component.ngOnInit();

            expect(component.isDecisionComment).toBe(true);
        });

        it('comment is general comment when there is no decision name', () => {
            component.actionComment = {} as WorkflowActionComment;
            component.ngOnInit();

            expect(component.isDecisionComment).toBe(false);
        });
    });

    describe('createdAgo', () => {
        it('should return string that shows how much time ago the comment was created', () => {
            component.actionComment = {
                createdAgoInMillis: 1000
            } as WorkflowActionComment;

            expect(component.createdAgo).toBeDefined();
        });

        it('should return null if WorkflowActionComment has no specified createdAgoInMillis property', () => {
            component.actionComment = {} as WorkflowActionComment;

            expect(component.createdAgo).toBe(null);
        });
    });

    describe('isIncomingDecision', () => {
        it('comment is incoming decision (returns true) when WorkflowAction is different from WorkflowAction code', () => {
            component.actionComment = {
                originalActionCode: '000001KE'
            } as WorkflowActionComment;
            component.workflowAction = {
                code: '000001KF'
            } as WorkflowAction;
            expect(component.isIncomingDecision()).toBe(true);
        });

        it('comment is outgoing decision (returns false) when WorkflowAction is the same as WorkflowAction code', () => {
            component.actionComment = {
                originalActionCode: '000001KE'
            } as WorkflowActionComment;
            component.workflowAction = {
                code: '000001KE'
            } as WorkflowAction;
            expect(component.isIncomingDecision()).toBe(false);
        });
    });
});
