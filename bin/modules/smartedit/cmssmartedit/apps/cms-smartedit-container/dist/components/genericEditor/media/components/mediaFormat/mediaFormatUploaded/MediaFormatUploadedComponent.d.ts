import { EventEmitter } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Media } from 'smarteditcommons';
export declare class MediaFormatUploadedComponent {
    private readonly sanitizer;
    media: Media;
    replaceLabelI18nKey: string;
    acceptedFileTypes: string[];
    isFieldDisabled: boolean;
    onFileSelect: EventEmitter<FileList | string>;
    onDelete: EventEmitter<void>;
    constructor(sanitizer: DomSanitizer);
    onFileSelectorFileSelect(file: FileList | string): void;
    onRemoveButtonClick(): void;
    isImage(): boolean;
    isVideo(): boolean;
    isPdf(): boolean;
    getSafeUrl(): SafeResourceUrl;
}
