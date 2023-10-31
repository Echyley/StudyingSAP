import { EventEmitter } from '@angular/core';
export declare class MediaUploadFieldComponent {
    fieldValue: string;
    fieldName: string;
    fieldErrors: string[];
    isRequired?: boolean;
    labelI18nKey: string;
    fieldValueChange: EventEmitter<string>;
    onChangeValue(value: string): void;
    hasError(): boolean;
    getContainerClassName(): string;
    getErrorClassName(): string;
}
