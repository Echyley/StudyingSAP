/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { CMSTimeService } from 'cmscommons';
import { Nullable, WorkflowAction, WorkflowActionComment } from 'smarteditcommons';

@Component({
    selector: 'se-workflow-action-comment',
    templateUrl: './WorkflowActionCommentComponent.html',
    styleUrls: ['./WorkflowActionCommentComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkflowActionCommentComponent implements OnInit {
    @Input() actionComment: WorkflowActionComment;
    @Input() workflowAction: WorkflowAction;
    public isDecisionComment: boolean;

    constructor(private cMSTimeService: CMSTimeService) {}

    ngOnInit(): void {
        this.isDecisionComment = !!this.actionComment.decisionName;
    }

    get createdAgo(): Nullable<string> {
        if (!!this.actionComment.createdAgoInMillis) {
            return this.cMSTimeService.getTimeAgo(this.actionComment.createdAgoInMillis);
        }
        return null;
    }

    public isIncomingDecision(): boolean {
        return !(this.actionComment.originalActionCode === this.workflowAction.code);
    }
}
