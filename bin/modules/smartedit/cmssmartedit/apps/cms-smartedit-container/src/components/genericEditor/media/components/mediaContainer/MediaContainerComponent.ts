/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import { TypePermissionsRestService } from 'cmscommons';
import { isEmpty } from 'lodash';
import {
    CLICK_DROPDOWN,
    ErrorContext,
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IGenericEditor,
    LogService,
    SystemEventService,
    TypedMap,
    ISharedDataService,
    FILE_VALIDATION_CONFIG,
    IFileValidation
} from 'smarteditcommons';
import { LoadConfigManagerService } from 'smarteditcontainer';
import {
    CMS_CLEAR_MEDIA_CONTAINER_SELECTED_OPTION,
    MAX_UPLOAD_FILE_SIZE
} from '../../../../../constants';

export const MediaContainerClonePrefix = 'clone_';
export interface MediaContainer {
    catalogVersion: string;
    /** Map of Media Format with its uploaded file uuid. */
    medias: { [index in MediaFormatType]: string } | unknown;
    /** Responsive Media Name. */
    qualifier: string;
    mediaContainerUuid: string;
}

export enum MediaFormatType {
    widescreen = 'widescreen',
    desktop = 'desktop',
    tablet = 'tablet',
    mobile = 'mobile'
}

/** Represents a container of pre-defined screen types for which Media can be uploaded.  */
@Component({
    selector: 'se-media-container',
    templateUrl: './MediaContainerComponent.html'
})
export class MediaContainerComponent implements OnInit {
    /** Selected Image to be uploaded or replaced. */
    public image: {
        file: File | string;
        format: MediaFormatType;
    };
    /** Advanced Media Container Management enables the user to create a pre-defined Media Container that can be later selected from the dropdown. */
    public advancedMediaContainerManagementEnabled: boolean;
    public hasReadPermissionOnMediaRelatedTypes: boolean;
    public mediaContainerCreationInProgress: boolean;
    /** Used by MediaContainerSelector as unique id for system event.  */
    public selectorEventNameAffix: string;
    public fileValidationErrors: ErrorContext[];
    public initialMediaContainerName: string;

    public field: GenericEditorField;
    public model: TypedMap<MediaContainer | undefined>;
    public lang: string;
    public isFieldDisabled: () => boolean;
    public maxUploadFileSize: number;

    private initialMediaContainerNameKey: string;
    private readonly editor: IGenericEditor;
    private readonly unRegDependsOnValueChanged: () => void;
    private readonly sessionStorage: Storage = window.sessionStorage;

    constructor(
        private readonly systemEventService: SystemEventService,
        private readonly logService: LogService,
        private readonly typePermissionsRestService: TypePermissionsRestService,
        private readonly loadConfigManagerService: LoadConfigManagerService,
        private readonly fileValidationService: IFileValidation,
        private readonly sharedDataService: ISharedDataService,
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<TypedMap<MediaContainer>>
    ) {
        ({
            field: this.field,
            model: this.model,
            editor: this.editor,
            qualifier: this.lang,
            isFieldDisabled: this.isFieldDisabled
        } = data);

        this.selectorEventNameAffix = `${this.field.qualifier}_${this.lang}`;
        if (this.field.dependsOnField) {
            this.field.hideFieldWidget = true;
            const onDependsOnValueChangedEventName = `${this.editor.id}${this.field.dependsOnField}${CLICK_DROPDOWN}`;
            this.unRegDependsOnValueChanged = this.systemEventService.subscribe(
                onDependsOnValueChangedEventName,
                (_eventId, value) => this.onDependsOnValueChanged(value)
            );
        }
    }

    async ngOnInit(): Promise<void> {
        if (!this.model[this.lang]) {
            this.setMediaContainer(this.constructEmptyMediaContainer());
            this.resetPristineMediaContainer();
        }

        const maxUploadFileSizeConfiguration = await this.sharedDataService.get(
            MAX_UPLOAD_FILE_SIZE
        );
        this.maxUploadFileSize =
            maxUploadFileSizeConfiguration && typeof maxUploadFileSizeConfiguration === 'number'
                ? maxUploadFileSizeConfiguration
                : FILE_VALIDATION_CONFIG.DEFAULT_MAX_UPLOAD_FILE_SIZE;

        await this.initHasReadPermissionOnMediaRelatedTypes();

        await this.initAdvancedMediaContainerManagementEnabled();

        this.initialMediaContainerNameKey = `${this.editor.id}_InitialMediaContainerName_${this.lang}`;
        this.resetModelForClone();
        this.getInitialMediaContainerName();
    }

    public getMediaContainerName(): string {
        return this.model[this.lang]?.qualifier || '';
    }

    public getInitialMediaContainerName(): void {
        // Set default value for Responsive Media Name when cloning a Page in Advanced Edit
        if (!!this.editor.initialContent.cloneComponent) {
            const initialCloneMediaContainerName = this.sessionStorage.getItem(
                this.initialMediaContainerNameKey
            );
            this.initialMediaContainerName =
                initialCloneMediaContainerName || this.model[this.lang]?.qualifier || '';
        } else {
            this.initialMediaContainerName = this.model[this.lang]?.qualifier || '';
        }
    }

    public getMediaContainerCellClassName(format: MediaFormatType): string {
        return `se-media-container-cell--${format}`;
    }

    public setMediaContainer(mediaContainer: MediaContainer): void {
        this.model[this.lang] = mediaContainer;
        // when mediaContainer change the related error should be change, reset errors for next update.
        this.resetErrors();
    }

    public canShowMediaFormatWithUploadForm(): boolean {
        return (
            this.hasReadPermissionOnMediaRelatedTypes &&
            (this.isMediaContainerSelected() ||
                this.mediaContainerCreationInProgress ||
                !this.advancedMediaContainerManagementEnabled)
        );
    }

    public isMediaFormatUnderEdit(format: MediaFormatType): boolean {
        // optional because image is not always selected hence it might be null
        return format === this.image?.format;
    }

    /**
     * Format is set when you select a file by "Replace" button.
     * Format is not set when you select a file in Upload Form.
     */
    public async onFileSelect(file: FileList | string, format?: MediaFormatType): Promise<void> {
        const imageFormat = format || this.image?.format;
        this.resetImage();

        try {
            // [CXEC-21683]Enable file url uploading
            if (typeof file === 'string') {
                this.image = {
                    file,
                    format: imageFormat
                };
            } else {
                const singleFile: File = file[0];
                await this.fileValidationService.validate(
                    singleFile,
                    this.maxUploadFileSize,
                    this.fileValidationErrors
                );
                this.image = {
                    file: singleFile,
                    format: imageFormat
                };
            }
        } catch {
            this.logService.warn('Invalid file');
        }
    }

    public onFileUploadSuccess(uuid: string, format: MediaFormatType): void {
        this.setMediaUuidForFormat(uuid, format);
        this.resetErrors();
        this.resetImage();
    }

    public resetErrors(): void {
        if (this.field.hasErrors) {
            this.field.hasErrors = false;
            this.field.messages = [];
            const controls = this.editor.form.group.controls;
            for (const tabName of Object.keys(controls)) {
                controls[tabName].updateValueAndValidity();
            }
        }
    }

    public onMediaContainerCreate(name: string): void {
        this.clearModel();

        this.onMediaContainerNameChange(name);

        this.model[this.lang].medias = {};
    }

    public onMediaContainerRemove(): void {
        this.clearModel();
        this.model[this.lang].medias = {};
    }

    public onMediaContainerNameChange(name: string): void {
        this.model[this.lang].qualifier = name;
    }

    public onMediaContainerCreationInProgressChange(inProgress: boolean): void {
        this.mediaContainerCreationInProgress = inProgress;
    }

    public removeMediaByFormat(format: MediaFormatType): void {
        delete this.model[this.lang].medias[format];
    }

    /**
     * Returns whether if cloning is in progress in advanced media management mode.
     */
    public isAdvancedCloning(): boolean {
        return !!this.editor.initialContent.cloneComponent;
    }

    private constructEmptyMediaContainer(): MediaContainer {
        return {
            catalogVersion: undefined,
            medias: {},
            qualifier: undefined,
            mediaContainerUuid: undefined
        };
    }

    /*  without this function, each time when open dialog to edit media component,
        save button will be highlight.
    */
    private resetPristineMediaContainer(): void {
        const target = this.editor.form.fields.find((element: GenericEditorField) => {
            let hasMediaContainerType = false;
            if (Array.isArray(element.containedTypes)) {
                hasMediaContainerType = element.containedTypes.some(
                    (type) => type === 'MediaContainer'
                );
            }
            return hasMediaContainerType;
        });
        if (!!this.editor.form.pristine[target.qualifier]) {
            this.editor.form.pristine[target.qualifier][this.lang] =
                this.constructEmptyMediaContainer();
        }
    }

    private onDependsOnValueChanged(value: string): void {
        this.field.hideFieldWidget = value !== this.field.dependsOnValue;
        if (this.field.hideFieldWidget) {
            if (this.model[this.lang]) {
                this.setMediaContainer(this.constructEmptyMediaContainer());
            }
            const clearSelectMediaContainerEventName = `mediaContainer_${this.selectorEventNameAffix}_${CMS_CLEAR_MEDIA_CONTAINER_SELECTED_OPTION}`;
            this.systemEventService.publishAsync(clearSelectMediaContainerEventName);
        }
    }

    private async initAdvancedMediaContainerManagementEnabled(): Promise<void> {
        const configurations = await this.loadConfigManagerService.loadAsObject();
        this.advancedMediaContainerManagementEnabled =
            (configurations.advancedMediaContainerManagement as boolean) || false;
    }

    private async initHasReadPermissionOnMediaRelatedTypes(): Promise<void> {
        try {
            const permissionsResult =
                await this.typePermissionsRestService.hasAllPermissionsForTypes(
                    this.field.containedTypes
                );
            this.hasReadPermissionOnMediaRelatedTypes = this.field.containedTypes.every(
                (type) => permissionsResult[type].read
            );
        } catch (error) {
            this.hasReadPermissionOnMediaRelatedTypes = false;
            this.logService.warn('Failed to retrieve type permissions', error);
        }
    }

    private setMediaUuidForFormat(uuid: string, format: MediaFormatType): void {
        this.model[this.lang].medias[format] = uuid;
    }

    /**
     * Verifies whether the media container is populated.
     */
    private isMediaContainerSelected(): boolean {
        return !isEmpty(this.model[this.lang]);
    }

    private resetImage(): void {
        this.fileValidationErrors = [];
        this.image = null;
    }

    /**
     * Removes all attributes from the model object. Allows to preserve the same reference to the object.
     */
    private clearModel(): void {
        Object.keys(this.model[this.lang]).forEach((key) => delete this.model[this.lang][key]);
    }

    private resetModelForClone(): void {
        const mediaContainerWithLang = this.model[this.lang];
        if (
            !!this.editor.initialContent.cloneComponent &&
            // Skip clone the empty Media Container.
            mediaContainerWithLang &&
            mediaContainerWithLang.mediaContainerUuid &&
            mediaContainerWithLang.qualifier
        ) {
            // Save the original media container name to session Storage. it will clear after page close.
            this.sessionStorage.setItem(
                this.initialMediaContainerNameKey,
                mediaContainerWithLang.qualifier
            );
            const finalSixNumbersStartIndex = 7;
            const cloneMediaName = `${MediaContainerClonePrefix}${this.model[
                this.lang
            ].qualifier.trim()}_${Date.now().toString().substring(finalSixNumbersStartIndex)}`;
            this.onMediaContainerNameChange(cloneMediaName);
            delete mediaContainerWithLang.mediaContainerUuid;
            delete mediaContainerWithLang.catalogVersion;
        }
    }
}
