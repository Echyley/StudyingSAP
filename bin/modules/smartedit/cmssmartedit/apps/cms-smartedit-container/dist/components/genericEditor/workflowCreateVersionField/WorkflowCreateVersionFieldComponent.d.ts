import { ChangeDetectorRef, OnDestroy, OnInit } from '@angular/core';
import { L10nPipe, GenericEditorField, GenericEditorWidgetData, Nullable, WorkflowService } from 'smarteditcommons';
interface WorkflowCreateVersionFieldData {
    workflowCode: string;
    actionCode: string;
    decisionCode: string;
    templateCode: string;
    createVersion: boolean;
    versionLabel: Nullable<string>;
}
export declare class WorkflowCreateVersionFieldComponent implements OnInit, OnDestroy {
    private workflowService;
    data: GenericEditorWidgetData<WorkflowCreateVersionFieldData>;
    private cdr;
    private l10nPipe;
    field: GenericEditorField;
    model: WorkflowCreateVersionFieldData;
    private editor;
    private unregisterOnChangeEvent;
    constructor(workflowService: WorkflowService, data: GenericEditorWidgetData<WorkflowCreateVersionFieldData>, cdr: ChangeDetectorRef, l10nPipe: L10nPipe);
    ngOnInit(): void;
    ngOnDestroy(): void;
    onCreateVersionChange(): Promise<void>;
}
export {};
