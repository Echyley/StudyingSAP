/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';
import { isEmpty } from 'lodash';
import { take } from 'rxjs/operators';
import {
    L10nPipe,
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IGenericEditor,
    Nullable,
    WorkflowService,
    WorkflowAction,
    WorkflowDecision
} from 'smarteditcommons';

interface WorkflowCreateVersionFieldData {
    workflowCode: string;
    actionCode: string;
    decisionCode: string;
    templateCode: string;
    createVersion: boolean;
    versionLabel: Nullable<string>;
}

@Component({
    selector: 'se-workflow-create-version-field',
    templateUrl: './WorkflowCreateVersionFieldComponent.html',
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkflowCreateVersionFieldComponent implements OnInit, OnDestroy {
    public field: GenericEditorField;
    public model: WorkflowCreateVersionFieldData;
    private editor: IGenericEditor;
    private unregisterOnChangeEvent: () => void;

    constructor(
        private workflowService: WorkflowService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<WorkflowCreateVersionFieldData>,
        private cdr: ChangeDetectorRef,
        private l10nPipe: L10nPipe
    ) {
        ({ field: this.field, model: this.model, editor: this.editor } = data);
    }

    ngOnInit(): void {
        this.unregisterOnChangeEvent = this.editor.api.addContentChangeEvent(() =>
            this.onCreateVersionChange()
        );
    }

    ngOnDestroy(): void {
        this.unregisterOnChangeEvent();
    }

    public async onCreateVersionChange(): Promise<void> {
        if (this.model.createVersion === false) {
            this.model.versionLabel = null;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
            return;
        }

        if (!isEmpty(this.model.versionLabel)) {
            return;
        }

        if (this.model.decisionCode) {
            // Create version label generation while making decisions
            const workflowActions = await this.workflowService.getAllActionsForWorkflowCode(
                this.model.workflowCode
            );
            const workflowAction = workflowActions.find(
                (action: WorkflowAction) => action.code === this.model.actionCode
            );
            const workflowDecision = workflowAction.decisions.find(
                (decision: WorkflowDecision) => decision.code === this.model.decisionCode
            );
            const [workflowDecisionName, workflowActionName] = await Promise.all([
                this.l10nPipe.transform(workflowDecision.name).pipe(take(1)).toPromise(),
                this.l10nPipe.transform(workflowAction.name).pipe(take(1)).toPromise()
            ]);
            this.model.versionLabel = `${workflowDecisionName} for ${workflowActionName}`;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
            return;
        }

        // Create version label generation while creating workflows
        const workflow = await this.workflowService.getWorkflowTemplateByCode(
            this.model.templateCode
        );
        if (workflow) {
            const workflowName = await this.l10nPipe
                .transform(workflow.name)
                .pipe(take(1))
                .toPromise();
            this.model.versionLabel = `${workflowName} workflow started`;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        }
    }
}
