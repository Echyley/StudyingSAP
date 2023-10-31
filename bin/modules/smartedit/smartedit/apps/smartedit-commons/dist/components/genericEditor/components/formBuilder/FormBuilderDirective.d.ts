import { EventEmitter, TemplateRef, ViewContainerRef } from '@angular/core';
import { Payload } from '@smart/utils';
import { Observable } from 'rxjs';
import { GenericEditorState } from '../../models';
import { GenericEditorStateBuilderService } from '../../services/GenericEditorStateBuilderService';
import { GenericEditorSchema } from '../../types';
export declare class FormBuilderDirective {
    private templateRef;
    private viewContainer;
    private stateBuilderService;
    set formBuilder(input: {
        data$: Observable<Payload>;
        schema$: Observable<GenericEditorSchema>;
    });
    stateCreated: EventEmitter<GenericEditorState>;
    private _subscription;
    constructor(templateRef: TemplateRef<any>, viewContainer: ViewContainerRef, stateBuilderService: GenericEditorStateBuilderService);
    ngOnDestroy(): void;
    private _onDataStream;
    /**
     * @internal
     * Removes subscription and destroyes all views.
     */
    private _dispose;
}
