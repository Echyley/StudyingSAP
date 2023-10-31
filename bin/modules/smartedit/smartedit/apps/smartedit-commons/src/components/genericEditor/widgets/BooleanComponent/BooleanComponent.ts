/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';

import { SystemEventService } from 'smarteditcommons/services';
import { CLICK_DROPDOWN, stringUtils } from 'smarteditcommons/utils';
import { GenericEditorWidgetData } from '../../../genericEditor/types';
import { GENERIC_EDITOR_WIDGET_DATA } from '../../components/tokens';

/**
 * Component responsible for generating custom toggle for the Generic Editor.
 *
 * The following is an example of a possible field structures that can be returned by the Structure API for seBoolean to work:
 * {
 *   cmsStructureType: "Boolean",
 *   qualifier: "someQualifier",
 *   i18nKey: 'i18nkeyForSomeQualifier',
 *   localized: false,
 *   defaultValue: true
 * }
 *
 * There is an optional property called defaultValue (which can be set to TRUE to enable the toggle by default)
 */

@Component({
    selector: 'se-boolean',
    templateUrl: './BooleanComponent.html'
})
export class BooleanComponent implements OnInit {
    public qualifierId: string;
    private eventId: string;
    constructor(
        private readonly systemEventService: SystemEventService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public widget: GenericEditorWidgetData<any>
    ) {}

    ngOnInit(): void {
        this.eventId = `${this.widget.id}${this.widget.qualifier}${CLICK_DROPDOWN}`;
        this.qualifierId = this.widget.qualifier + stringUtils.generateIdentifier();
        if (this.widget.model[this.widget.qualifier] === undefined) {
            const defaultValue =
                this.widget.field.defaultValue !== undefined
                    ? this.widget.field.defaultValue
                    : false;
            this.widget.model[this.widget.qualifier] = defaultValue;
            this.widget.editor.pristine[this.widget.qualifier] = defaultValue;
        }
        this.systemEventService.publishAsync(
            this.eventId,
            `${this.widget.model[this.widget.qualifier]}`
        );
    }

    checkboxOnClick(event: HTMLInputElement): void {
        this.systemEventService.publishAsync(this.eventId, `${event.checked}`);
    }
}
