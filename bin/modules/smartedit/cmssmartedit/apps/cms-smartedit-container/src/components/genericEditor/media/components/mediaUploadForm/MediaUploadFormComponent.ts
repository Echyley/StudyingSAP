/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ComponentFactoryResolver,
    EventEmitter,
    Inject,
    Input,
    OnChanges,
    OnDestroy,
    Optional,
    Output,
    SimpleChanges,
    ViewChild,
    ViewRef
} from '@angular/core';
import { Subscription } from 'rxjs';
import {
    ErrorContext,
    FILE_VALIDATION_CONFIG,
    FileValidator,
    FileValidatorFactory,
    stringUtils,
    ErrorResponse,
    GenericEditorMediaType,
    ICMSMedia,
    IMediaToUpload,
    MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN,
    MediaToUpload,
    MediaUploadFieldsCustom,
    IMediaUploadFieldsCustomComponent,
    TypedMap
} from 'smarteditcommons';
import { MediaUploadFieldsCustomDirective } from '../../directives/MediaUploadFieldsCustomDirective';
import {
    MediaBackendValidationHandler,
    MediaUtilService,
    MediaFolderService,
    MediaFolderFetchStrategy
} from '../../services';

interface ImageEditableParams {
    code: string;
    description: string;
    altText: string;
}

@Component({
    selector: 'se-media-upload-form',
    templateUrl: './MediaUploadFormComponent.html',
    styleUrls: ['./MediaUploadFormComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaUploadFormComponent implements OnChanges, OnDestroy {
    @Input() image: any;
    @Input() allowMediaType: GenericEditorMediaType;
    @Input() maxUploadFileSize: number;
    @Output() onCancel: EventEmitter<void>;
    @Output() onSelect: EventEmitter<FileList | string>;
    @Output() onUploadSuccess: EventEmitter<string>;

    @ViewChild(MediaUploadFieldsCustomDirective, { static: true })
    mediaUploadFieldsCustomDirective: MediaUploadFieldsCustomDirective;

    public acceptedFileTypes: string[];
    public isUploading: boolean;
    public fieldErrors: ErrorContext[];
    public imageParams: ImageEditableParams | null;
    public folderSelected: string;
    public folderFetchStrategy: MediaFolderFetchStrategy;
    public folderErrors: string[];

    private fileValidator: FileValidator;
    private subscriptions: Subscription[] = [];
    private customFields: TypedMap<string> = null;

    constructor(
        private cdr: ChangeDetectorRef,
        private fileValidatorFactory: FileValidatorFactory,
        private mediaBackendValidationHandler: MediaBackendValidationHandler,
        private mediaUploaderService: IMediaToUpload,
        private mediaFolderService: MediaFolderService,
        private mediaUtilService: MediaUtilService,
        private resolver: ComponentFactoryResolver,
        @Optional()
        @Inject(MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN)
        private mediaUploadFieldsCustom: MediaUploadFieldsCustom
    ) {
        this.onCancel = new EventEmitter();
        this.onSelect = new EventEmitter();
        this.onUploadSuccess = new EventEmitter();

        this.fieldErrors = [];
        this.imageParams = null;
        this.fileValidator = this.fileValidatorFactory.build([
            {
                subject: 'code',
                message: 'se.uploaded.image.code.is.required',
                validate: (code: string): boolean => !!code
            }
        ]);
    }

    async ngOnInit(): Promise<void> {
        this.acceptedFileTypes = this.mediaUtilService.getAcceptedFileTypes(this.allowMediaType);
        this.folderFetchStrategy = this.mediaFolderService.mediaFoldersFetchStrategy;
        this.folderSelected = await this.mediaFolderService.getDefaultFolder();
        this.loadMediaUploadFieldsCustom();
        this.cdr.markForCheck();
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    }

    ngOnChanges(changes: SimpleChanges): void {
        const imageChange = changes.image;
        if (imageChange) {
            // [CXEC-21683]Enable file url uploading
            let imageName;
            if (typeof this.image === 'string') {
                imageName = this.image;
            } else {
                imageName = this.image.name;
            }
            this.imageParams = {
                code: imageName,
                description: imageName,
                altText: imageName
            };
        }
    }

    public getErrorsForFieldByCode(code: keyof ImageEditableParams): string[] {
        return this.fieldErrors
            .filter((error) => error.subject === code)
            .map((error) => error.message);
    }

    public async uploadMedia(): Promise<void> {
        this.fieldErrors = [];
        if (
            !this.fileValidator.validate(this.imageParams, this.maxUploadFileSize, this.fieldErrors)
        ) {
            return;
        }

        // [CXEC-21683]Enable file url uploading
        if (typeof this.image === 'object') {
            const validate = this.acceptedFileTypes.filter(
                (type: string) => this.image && this.image.type && this.image.type.includes(type)
            );
            if (!validate || (validate && validate.length < 1)) {
                this.fieldErrors.push({
                    subject: 'code',
                    message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_TYPE_INVALID
                });
                return;
            }
        }

        this.isUploading = true;
        // [CXEC-21683]Enable file url uploading
        try {
            const uploadedMedia: MediaToUpload = {
                code: stringUtils.escapeHtml(this.imageParams.code) as string,
                description: stringUtils.escapeHtml(this.imageParams.description) as string,
                altText: stringUtils.escapeHtml(this.imageParams.altText) as string,
                folder: this.folderSelected ? this.folderSelected : ''
            };
            // [CXEC-24222]Enable custom fields
            if (this.customFields) {
                Object.getOwnPropertyNames(this.customFields).forEach((propertyName: string) => {
                    uploadedMedia[propertyName] = stringUtils.escapeHtml(
                        this.customFields[propertyName]
                    ) as string;
                });
            }

            if (typeof this.image === 'string') {
                uploadedMedia.url = this.image;
            } else {
                uploadedMedia.file = this.image;
            }
            this.onMediaUploadSuccess(await this.mediaUploaderService.uploadMedia(uploadedMedia));
        } catch (error) {
            this.onMediaUploadFail(error as ErrorResponse);
        } finally {
            this.isUploading = false;
        }
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public cancel(): void {
        this.reset();

        this.onCancel.emit();
    }

    public onChangeFieldValue(value: string, paramName: keyof ImageEditableParams): void {
        this.imageParams[paramName] = value;
    }

    public onFileSelect(file: FileList | string): void {
        this.onSelect.emit(file);
    }

    public hasError(): boolean {
        return this.folderErrors?.length > 0;
    }

    public folderSelectedChanged(folder: string): void {
        this.folderErrors = []; // clean the error
    }

    private onMediaUploadSuccess({ uuid }: ICMSMedia): void {
        this.reset();

        this.onUploadSuccess.emit(uuid);
    }

    private onMediaUploadFail(response: ErrorResponse): void {
        this.mediaBackendValidationHandler.handleResponse(response, this.fieldErrors);
        this.folderErrors = this.fieldErrors
            .filter((error) => error.subject === 'folder')
            .map((error) => error.message);
    }

    private reset(): void {
        this.imageParams = null;
        this.fieldErrors = [];
        this.isUploading = false;
    }

    private loadMediaUploadFieldsCustom(): void {
        if (!this.mediaUploadFieldsCustom) {
            return;
        }
        const componentFactory =
            this.resolver.resolveComponentFactory<IMediaUploadFieldsCustomComponent>(
                this.mediaUploadFieldsCustom.component
            );

        const viewContainerRef = this.mediaUploadFieldsCustomDirective.viewContainerRef;
        viewContainerRef.clear();

        const componentRef =
            viewContainerRef.createComponent<IMediaUploadFieldsCustomComponent>(componentFactory);
        componentRef.instance.image = this.image;
        this.subscriptions.push(
            componentRef.instance.customFieldsChange.subscribe((customFields) => {
                this.customFields = customFields;
            })
        );
    }
}
