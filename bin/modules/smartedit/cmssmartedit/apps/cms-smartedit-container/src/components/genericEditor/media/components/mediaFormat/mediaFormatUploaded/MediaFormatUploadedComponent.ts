/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { GenericEditorMediaType, Media } from 'smarteditcommons';

@Component({
    selector: 'se-media-format-uploaded',
    templateUrl: './MediaFormatUploadedComponent.html',
    styleUrls: ['../../../mediaPreviewContainer.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaFormatUploadedComponent {
    @Input() media: Media;
    @Input() replaceLabelI18nKey: string;
    @Input() acceptedFileTypes: string[];
    @Input() isFieldDisabled: boolean;

    @Output() onFileSelect: EventEmitter<FileList | string>;
    @Output() onDelete: EventEmitter<void>;

    constructor(private readonly sanitizer: DomSanitizer) {
        this.onFileSelect = new EventEmitter();
        this.onDelete = new EventEmitter();
    }

    public onFileSelectorFileSelect(file: FileList | string): void {
        this.onFileSelect.emit(file);
    }

    public onRemoveButtonClick(): void {
        this.onDelete.emit();
    }

    public isImage(): boolean {
        return !this.isVideo() && !this.isPdf();
    }

    public isVideo(): boolean {
        return this.media.mime && this.media.mime.includes(GenericEditorMediaType.VIDEO);
    }

    public isPdf(): boolean {
        return this.media.mime && this.media.mime.includes(GenericEditorMediaType.PDF_DOCUMENT);
    }

    public getSafeUrl(): SafeResourceUrl {
        return this.sanitizer.bypassSecurityTrustResourceUrl(this.media.url);
    }
}
