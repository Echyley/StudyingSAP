/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewEncapsulation
} from '@angular/core';
import { FetchStrategy, Page, Media, IMediaService } from 'smarteditcommons';
import { MediaPrinterComponent } from './mediaPrinter';

/** Represents a dropdown from which a media image can be selected. */
@Component({
    selector: 'se-media-selector',
    templateUrl: './MediaSelectorComponent.html',
    styleUrls: ['./MediaSelectorComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaSelectorComponent implements OnInit {
    @Input() id: string;
    @Input() mediaId: string;
    @Input() mimeType: string;
    @Output() mediaIdChange: EventEmitter<string>;
    @Input() isDisabled: boolean;

    public fetchStrategy: FetchStrategy<Media>;
    public mediaPrinterComponent = MediaPrinterComponent;

    constructor(private mediaService: IMediaService) {
        this.mediaIdChange = new EventEmitter();
    }

    ngOnInit(): void {
        this.fetchStrategy = {
            fetchEntity: (uuid: string): Promise<Media> => this.mediaService.getMedia(uuid),
            fetchPage: (
                search: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<Media>> =>
                this.mediaService.getPage(search, pageSize, currentPage, this.mimeType)
        };
    }

    public onMediaIdChange(id: string): void {
        this.mediaId = id;
        this.mediaIdChange.emit(id);
    }
}
