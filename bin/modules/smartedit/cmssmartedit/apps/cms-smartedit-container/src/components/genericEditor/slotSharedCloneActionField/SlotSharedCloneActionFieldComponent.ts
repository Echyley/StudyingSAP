/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, ChangeDetectionStrategy } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA, GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';

enum CloneAction {
    'clone' = 'clone',
    'useExisting' = 'reference',
    'remove' = 'remove'
}

@Component({
    selector: 'se-component-slot-shared-clone-action-field',
    templateUrl: './SlotSharedCloneActionFieldComponent.html',
    styleUrls: ['./SlotSharedCloneActionFieldComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotSharedCloneActionFieldComponent {
    public page: ICMSPage;
    public cloneAction = CloneAction;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<any>
    ) {
        ({ model: this.page } = data);
    }
}
