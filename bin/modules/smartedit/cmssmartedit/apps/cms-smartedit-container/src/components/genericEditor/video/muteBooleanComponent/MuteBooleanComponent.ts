/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import {
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    SystemEventService,
    CLICK_DROPDOWN
} from 'smarteditcommons';

/**
 * this component is for video mute;
 */
@Component({
    selector: 'se-mute-boolean',
    templateUrl: './MuteBooleanComponent.html'
})
export class MuteBooleanComponent {
    private unRegClickValueChanged: () => void;
    constructor(
        private systemEventService: SystemEventService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>
    ) {
        if (this.widget.field.dependsOnField) {
            this.widget.field.hideFieldWidget = true;
            const onClickEventName = `${this.widget.id}${this.widget.field.dependsOnField}${CLICK_DROPDOWN}`;
            this.unRegClickValueChanged = this.systemEventService.subscribe(
                onClickEventName,
                (eventId, data) => this.onDependencyValueChangedEvent(data)
            );
        }
    }

    onDependencyValueChangedEvent(checked: string): void {
        this.widget.field.hideFieldWidget = checked !== this.widget.field.dependsOnValue;
        if (this.widget.field.hideFieldWidget) {
            this.widget.model[this.widget.qualifier] = false;
        }
    }
}
