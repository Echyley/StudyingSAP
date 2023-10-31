import { EventEmitter, OnInit } from '@angular/core';
import { FetchStrategy, Media, IMediaService } from 'smarteditcommons';
import { MediaPrinterComponent } from './mediaPrinter';
export declare class MediaSelectorComponent implements OnInit {
    private mediaService;
    id: string;
    mediaId: string;
    mimeType: string;
    mediaIdChange: EventEmitter<string>;
    isDisabled: boolean;
    fetchStrategy: FetchStrategy<Media>;
    mediaPrinterComponent: typeof MediaPrinterComponent;
    constructor(mediaService: IMediaService);
    ngOnInit(): void;
    onMediaIdChange(id: string): void;
}
