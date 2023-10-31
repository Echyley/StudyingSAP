/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import { MediaFormatUploadedComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';
import { Media } from 'smarteditcommons';

@Component({
    selector: 'se-media-format-uploaded',
    template: `<div class="se-media--present"></div>`
})
export class FakeMediaFormatUploadedComponent implements Partial<MediaFormatUploadedComponent> {
    @Input() media: Media;
    @Input() replaceLabelI18nKey: string;
    @Input() acceptedFileTypes: string[];
    @Input() isFieldDisabled: boolean;
}
