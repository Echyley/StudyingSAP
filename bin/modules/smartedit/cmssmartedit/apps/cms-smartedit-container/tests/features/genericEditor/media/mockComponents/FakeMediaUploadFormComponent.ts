/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';
import { MediaUploadFormComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';
import { GenericEditorMediaType } from 'smarteditcommons';

@Component({
    selector: 'se-media-upload-form',
    template: `<div class="se-media--present"></div>`
})
export class FakeMediaUploadFormComponent implements Partial<MediaUploadFormComponent> {
    @Input() image: File;
    @Input() allowMediaType: GenericEditorMediaType;
    @Input() maxUploadFileSize: number;
}
