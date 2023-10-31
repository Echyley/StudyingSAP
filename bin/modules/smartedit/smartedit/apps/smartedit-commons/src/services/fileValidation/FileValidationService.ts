/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    FileValidatorFactory,
    ErrorContext,
    FileValidatorByProperty,
    FileMimeTypeService
} from '@smart/utils';
import { IFileValidation } from '../interfaces/IFileValidation';
const BYTE = 1024;
export const FILE_VALIDATION_CONFIG = {
    /** A list of file types supported by the platform. */
    ACCEPTED_FILE_TYPES: {
        VIDEO: ['mp4', 'avi', 'x-msvideo', 'wmv', 'mpg', 'mpeg', 'flv'],
        IMAGE: ['jpeg', 'jpg', 'gif', 'bmp', 'tiff', 'tif', 'png', 'svg'],
        PDF_DOCUMENT: ['pdf'],
        DEFAULT: ['jpeg', 'jpg', 'gif', 'bmp', 'tiff', 'tif', 'png', 'svg']
    },

    /** default max of upload file size is 20MB, unit is MB. */
    DEFAULT_MAX_UPLOAD_FILE_SIZE: 20,
    /** A map of all the internationalization keys used by the file validation service. */
    I18N_KEYS: {
        FILE_TYPE_INVALID: 'se.upload.file.type.invalid',
        FILE_SIZE_INVALID: 'se.upload.file.size.invalid'
    }
};

/**
 * Validates if a specified file meets the required file type and file size constraints of SAP Hybris Commerce.
 */
@Injectable()
export class FileValidationService extends IFileValidation {
    private readonly validators: FileValidatorByProperty[] = [
        {
            subject: 'size',
            message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_SIZE_INVALID,
            validate: (size: number, maxSize: number): boolean => size <= maxSize * BYTE * BYTE
        }
    ];

    constructor(
        private readonly fileMimeTypeService: FileMimeTypeService,
        private readonly fileValidatorFactory: FileValidatorFactory
    ) {
        super();
    }

    /**
     * Validates the specified file object against custom validator and its mimetype.
     * It appends the errors to the error context array provided or it creates a new error context array.
     *
     * @param file The web API file object to be validated.
     * @param context The contextual error array to append the errors to. It is an output parameter.
     * @returns A promise that resolves if the file is valid otherwise it rejects with a list of errors.
     */
    public async validate(
        file: File,
        maxUploadFileSize: number,
        errorsContext: ErrorContext[]
    ): Promise<ErrorContext[] | void> {
        this.fileValidatorFactory
            .build(this.validators)
            .validate(file, maxUploadFileSize, errorsContext);
        try {
            await this.fileMimeTypeService.isFileMimeTypeValid(file);
            if (errorsContext.length > 0) {
                return Promise.reject(errorsContext);
            }
            return Promise.resolve();
        } catch {
            errorsContext.push({
                subject: 'type',
                message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_TYPE_INVALID
            });
            return Promise.reject(errorsContext);
        }
    }
}
