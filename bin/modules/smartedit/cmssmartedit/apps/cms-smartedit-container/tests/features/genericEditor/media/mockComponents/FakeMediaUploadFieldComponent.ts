/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MediaUploadFieldComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';

@Component({
    selector: 'se-media-upload-field',
    template: ''
})
export class FakeMediaUploadFieldComponent implements Partial<MediaUploadFieldComponent> {
    @Input() fieldValue: string;
    @Input() fieldName: string;
    @Input() fieldErrors: string[];
    @Input() isRequired? = false;
    @Input() labelI18nKey: string;

    @Output() fieldValueChange = new EventEmitter<string>();
}
