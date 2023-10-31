import { ErrorContext } from '@smart/utils';
export declare abstract class IFileValidation {
    buildAcceptedFileTypesList(): string;
    validate(file: File, maxUploadFileSize: number, errorsContext: ErrorContext[]): Promise<ErrorContext[] | void>;
}
