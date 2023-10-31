import { FileValidatorFactory, ErrorContext, FileMimeTypeService } from '@smart/utils';
import { IFileValidation } from '../interfaces/IFileValidation';
export declare const FILE_VALIDATION_CONFIG: {
    /** A list of file types supported by the platform. */
    ACCEPTED_FILE_TYPES: {
        VIDEO: string[];
        IMAGE: string[];
        PDF_DOCUMENT: string[];
        DEFAULT: string[];
    };
    /** default max of upload file size is 20MB, unit is MB. */
    DEFAULT_MAX_UPLOAD_FILE_SIZE: number;
    /** A map of all the internationalization keys used by the file validation service. */
    I18N_KEYS: {
        FILE_TYPE_INVALID: string;
        FILE_SIZE_INVALID: string;
    };
};
/**
 * Validates if a specified file meets the required file type and file size constraints of SAP Hybris Commerce.
 */
export declare class FileValidationService extends IFileValidation {
    private readonly fileMimeTypeService;
    private readonly fileValidatorFactory;
    private readonly validators;
    constructor(fileMimeTypeService: FileMimeTypeService, fileValidatorFactory: FileValidatorFactory);
    /**
     * Validates the specified file object against custom validator and its mimetype.
     * It appends the errors to the error context array provided or it creates a new error context array.
     *
     * @param file The web API file object to be validated.
     * @param context The contextual error array to append the errors to. It is an output parameter.
     * @returns A promise that resolves if the file is valid otherwise it rejects with a list of errors.
     */
    validate(file: File, maxUploadFileSize: number, errorsContext: ErrorContext[]): Promise<ErrorContext[] | void>;
}
