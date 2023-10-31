/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, ChangeDetectionStrategy } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA, GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';

@Component({
    selector: 'se-component-slot-shared-slot-type-field',
    templateUrl: './SlotSharedSlotTypeFieldComponent.html',
    styleUrls: ['./SlotSharedSlotTypeFieldComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotSharedSlotTypeFieldComponent {
    public page: ICMSPage;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<ICMSPage>
    ) {
        ({ model: this.page } = data);
    }
}
