/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnInit,
    ViewRef
} from '@angular/core';
import { MAX_UPLOAD_FILE_SIZE } from 'cmssmarteditcontainer/constants';
import {
    ErrorContext,
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    LogService,
    TypedMap,
    GenericEditorMediaType,
    ISharedDataService,
    FILE_VALIDATION_CONFIG,
    IFileValidation,
    IMediaService
} from 'smarteditcommons';
import { MediaUtilService } from '../../services';

const VIDEO_MIME_PREFIX = 'video';
const MP4_MIME_PREFIX = 'mp4';

/** Represents a container for Simple Banner Component media. */
@Component({
    selector: 'se-media',
    templateUrl: './MediaComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaComponent implements OnInit {
    public acceptedFileTypes: string[];
    public fileErrors: ErrorContext[];
    public image: File | string | null;
    public isFieldDisabled: boolean;
    public mediaSelectorId: string;
    public previewMessage = false;
    public maxUploadFileSize: number;
    private readonly mediaIdByLang: TypedMap<string>;
    private readonly lang: string;
    private field: GenericEditorField;

    constructor(
        private cdr: ChangeDetectorRef,
        private fileValidationService: IFileValidation,
        private logService: LogService,
        private mediaUtilService: MediaUtilService,
        private mediaService: IMediaService,
        private sharedDataService: ISharedDataService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<TypedMap<string>>
    ) {
        ({ field: this.field, model: this.mediaIdByLang, qualifier: this.lang } = data);

        this.acceptedFileTypes = this.mediaUtilService.getAcceptedFileTypes(
            this.field.allowMediaType
        );

        this.fileErrors = [];
        this.image = null;
        this.isFieldDisabled = data.isFieldDisabled();
        this.mediaSelectorId = this.field.qualifier;
        this.field.allowMediaType = this.field.allowMediaType || GenericEditorMediaType.DEFAULT;
    }

    async ngOnInit(): Promise<void> {
        if (this.mediaIdByLang[this.lang]) {
            await this.checkMediaPreview(this.mediaIdByLang[this.lang]);
        }
        const maxUploadFileSizeConfiguration = await this.sharedDataService.get(
            MAX_UPLOAD_FILE_SIZE
        );
        this.maxUploadFileSize =
            maxUploadFileSizeConfiguration && typeof maxUploadFileSizeConfiguration === 'number'
                ? maxUploadFileSizeConfiguration
                : FILE_VALIDATION_CONFIG.DEFAULT_MAX_UPLOAD_FILE_SIZE;
    }

    public async onMediaIdChange(id: string): Promise<void> {
        this.fileErrors = [];
        this.previewMessage = false;
        this.mediaIdByLang[this.lang] = id;
        if (id) {
            await this.checkMediaPreview(id);
        }
    }

    public async onFileSelect(file: FileList | string): Promise<void> {
        this.mediaIdByLang[this.lang] = undefined;
        this.resetImage();
        try {
            // [CXEC-21683]Enable file url uploading
            if (typeof file === 'string') {
                this.image = file;
            } else {
                const singleFile = file[0];
                await this.fileValidationService.validate(
                    singleFile,
                    this.maxUploadFileSize,
                    this.fileErrors
                );
                this.image = singleFile;
            }
        } catch {
            this.logService.warn('Invalid file');
        }
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public async onMediaUploaded(id: string): Promise<void> {
        this.resetImage();
        await this.onMediaIdChange(id);
        if (this.field.initiated) {
            this.field.initiated.length = 0;
        }
    }

    public resetImage(): void {
        this.fileErrors = [];
        this.image = null;
    }

    /** File Selector can be shown only if model exists but field is not set. */
    public canShowFileSelector(): boolean {
        return this.mediaIdByLang && !this.mediaIdByLang[this.lang] && !this.image;
    }

    private canVideoPreview(mime: string): void {
        if (mime && mime.includes(VIDEO_MIME_PREFIX) && mime.indexOf(MP4_MIME_PREFIX) === -1) {
            this.previewMessage = true;
        }
    }

    private async checkMediaPreview(id: string): Promise<void> {
        const media = await this.mediaService.getMedia(id);
        this.canVideoPreview(media?.mime);
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
