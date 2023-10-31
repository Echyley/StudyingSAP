/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import {
    ItemComponentData,
    ITEM_COMPONENT_DATA_TOKEN,
    SelectComponent,
    GenericEditorMediaType,
    Media
} from 'smarteditcommons';

/** Represents Media Selector Dropdown option.  */
@Component({
    selector: 'se-media-printer',
    templateUrl: './MediaPrinterComponent.html',
    styleUrls: ['./MediaPrinterComponent.scss', '../../../mediaPreviewContainer.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaPrinterComponent {
    public media: Media;
    public isSelected: boolean;
    public select: SelectComponent<Media>;

    constructor(
        @Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData<Media>,
        private readonly sanitizer: DomSanitizer
    ) {
        ({ item: this.media, selected: this.isSelected, select: this.select } = data);
    }

    public isDisabled(): boolean {
        return this.select.isReadOnly;
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
