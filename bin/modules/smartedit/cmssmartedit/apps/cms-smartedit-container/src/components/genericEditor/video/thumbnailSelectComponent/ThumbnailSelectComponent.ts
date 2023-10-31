/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnDestroy } from '@angular/core';
import {
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    SystemEventService,
    CLICK_DROPDOWN,
    LINKED_DROPDOWN
} from 'smarteditcommons';
export enum ThumbnailSelectOption {
    uploadThumbnail = 'UPLOAD_THUMBNAIL',
    noThumbnail = 'NO_THUMBNAIL'
}

@Component({
    selector: 'se-thumbnail-select',
    templateUrl: './ThumbnailSelectComponent.html'
})
export class ThumbnailSelectComponent implements OnDestroy {
    public id: string;
    public field: GenericEditorField;
    public model: string;
    public qualifier: string;

    private readonly unRegSelectValueChanged: () => void;
    private readonly unRegDependsOnValueChanged: () => void;
    private readonly optionEventId: string;

    constructor(
        private systemEventService: SystemEventService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public widget: GenericEditorWidgetData<string>
    ) {
        ({ id: this.id, field: this.field, model: this.model, qualifier: this.qualifier } = widget);
        if (this.field.dependsOnField) {
            const onDependsOnValueChangedEventName = `${this.id}${this.field.dependsOnField}${CLICK_DROPDOWN}`;
            this.unRegDependsOnValueChanged = this.systemEventService.subscribe(
                onDependsOnValueChangedEventName,
                (_eventId, data) => this.onDependsOnValueChanged(data)
            );
        }

        this.optionEventId = `${this.id}${this.qualifier}${CLICK_DROPDOWN}`;
        const onSelectValueChangedEventName = `${this.id}${LINKED_DROPDOWN}`;
        this.unRegSelectValueChanged = this.systemEventService.subscribe(
            onSelectValueChangedEventName,
            (_eventId, data) => this.onThumbnailSelectValueChanged(data)
        );
    }

    ngOnDestroy(): void {
        this.unRegSelectValueChanged();
        this.unRegDependsOnValueChanged();
    }

    /**
     * Called when select value changes in the following order:
     * - select
     * - sub-select
     * - select (reinitialize)
     */
    private onThumbnailSelectValueChanged({ optionObject: option, qualifier }): void {
        // only handle change for the select (not dependant select)
        if (this.qualifier !== qualifier) {
            return;
        }

        if (!option) {
            return;
        }

        const optionValue = option.id;
        const selectedOption = Object.values(ThumbnailSelectOption).includes(optionValue);
        if (!selectedOption) {
            throw new Error('Selected option is not supported');
        }
        if (!this.field.hideFieldWidget) {
            this.systemEventService.publishAsync(this.optionEventId, optionValue);
        }
    }

    private onDependsOnValueChanged(data: string): void {
        this.field.hideFieldWidget = data !== this.field.dependsOnValue;

        if (this.field.hideFieldWidget) {
            // to disable the child field if dependent field hide.
            this.systemEventService.publishAsync(this.optionEventId, null);
        } else {
            // to enable the child field if dependent field enable and it has value.
            this.systemEventService.publishAsync(this.optionEventId, this.model[this.qualifier]);
        }
    }
}
