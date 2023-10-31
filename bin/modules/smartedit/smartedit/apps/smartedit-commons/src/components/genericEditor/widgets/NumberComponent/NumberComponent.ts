/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';

import { SystemEventService } from '../../../../services';
import { CLICK_DROPDOWN } from '../../../../utils';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';

@Component({
    selector: 'se-number',
    templateUrl: './NumberComponent.html'
})
export class NumberComponent {
    private readonly unRegClickValueChanged: () => void;

    constructor(
        private readonly systemEventService: SystemEventService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>
    ) {
        const onClickEventName = `${this.widget.id}${this.widget.field.dependsOnField}${CLICK_DROPDOWN}`;
        this.unRegClickValueChanged = this.systemEventService.subscribe(
            onClickEventName,
            (_eventId, data) => this.onClickEvent(data)
        );
    }

    onClickEvent(data: string): void {
        this.widget.field.hideFieldWidget = data !== this.widget.field.dependsOnValue;
        if (this.widget.field.hideFieldWidget) {
            this.widget.model[this.widget.qualifier] = null;
        }
    }
}
