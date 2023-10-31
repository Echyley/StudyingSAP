/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'se-media-upload-field',
    templateUrl: './MediaUploadFieldComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaUploadFieldComponent {
    @Input() fieldValue: string;
    @Input() fieldName: string;
    @Input() fieldErrors: string[];
    @Input() isRequired? = false;
    @Input() labelI18nKey: string;

    @Output() fieldValueChange = new EventEmitter<string>();

    public onChangeValue(value: string): void {
        this.fieldValueChange.emit(value);
    }
    public hasError(): boolean {
        return this.fieldErrors?.length > 0;
    }

    public getContainerClassName(): string {
        return `se-media-upload__file-info-field--${this.fieldName}`;
    }

    public getErrorClassName(): string {
        return `upload-field-error--${this.fieldName}`;
    }
}
