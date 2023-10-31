/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FileMimeTypeService, FileValidator, FileValidatorFactory } from '@smart/utils';
import { functionsUtils } from '../../utils';
import { FileValidationService, FILE_VALIDATION_CONFIG } from './FileValidationService';

describe('FileValidationService', () => {
    let fileValidator: jasmine.SpyObj<FileValidator>;
    let fileMimeTypeService: jasmine.SpyObj<FileMimeTypeService>;
    let fileValidatorFactory: jasmine.SpyObj<FileValidatorFactory>;

    let fileValidationService: FileValidationService;
    const DEFAULT_MAX_UPLOAD_FILE_SIZE = 20;
    const LOWER_MAX_UPLOAD_FILE_SIZE = 4;
    beforeEach(() => {
        fileMimeTypeService = jasmine.createSpyObj<FileMimeTypeService>('fileMimeTypeService', [
            'isFileMimeTypeValid'
        ]);

        fileValidator = jasmine.createSpyObj<FileValidator>('fileValidator', ['validate']);
        fileValidatorFactory = jasmine.createSpyObj<FileValidatorFactory>('fileValidatorFactory', [
            'build'
        ]);

        fileValidationService = new FileValidationService(
            fileMimeTypeService,
            fileValidatorFactory
        );
    });

    beforeEach(() => {
        fileValidatorFactory.build.and.returnValue(fileValidator);
    });

    beforeEach(() => {
        FILE_VALIDATION_CONFIG.DEFAULT_MAX_UPLOAD_FILE_SIZE = 8;
        FILE_VALIDATION_CONFIG.I18N_KEYS = {
            FILE_TYPE_INVALID: 'se.upload.file.type.invalid',
            FILE_SIZE_INVALID: 'se.upload.file.size.invalid'
        };
    });

    describe('validate', () => {
        const errorContextItem = {
            subject: 'size',
            message: 'se.upload.file.size.invalid'
        };
        it('should resolve a promise promise if the given file is valid', async () => {
            const file = {} as File;
            const context = [];
            fileMimeTypeService.isFileMimeTypeValid.and.returnValue(Promise.resolve(false));
            fileValidator.validate.and.returnValue(true);

            await fileValidationService.validate(file, DEFAULT_MAX_UPLOAD_FILE_SIZE, context);
            expect(true).toBe(true);
        });

        it('should reject with the errors context if the file header is valid but there are object validation errors', async () => {
            const file = {} as File;
            const context = [];
            fileMimeTypeService.isFileMimeTypeValid.and.returnValue(Promise.resolve(false));
            fileValidator.validate.and.callFake((_file, _any, errorsContext) => {
                errorsContext.push({
                    ...errorContextItem
                });
                return false;
            });

            try {
                await fileValidationService.validate(file, LOWER_MAX_UPLOAD_FILE_SIZE, context);
                functionsUtils.assertFail();
            } catch (error) {
                expect(error).toEqual([
                    {
                        ...errorContextItem
                    }
                ]);
            }
        });

        it('should reject with the errors context if the file is invalid', async () => {
            const file = {} as File;
            const context = [];
            fileMimeTypeService.isFileMimeTypeValid.and.returnValue(Promise.reject());
            fileValidator.validate.and.callFake((_file, _any, errorsContext) => {
                errorsContext.push({
                    ...errorContextItem
                });
                return false;
            });

            try {
                await fileValidationService.validate(file, LOWER_MAX_UPLOAD_FILE_SIZE, context);
                functionsUtils.assertFail();
            } catch (error) {
                expect(error).toEqual([
                    {
                        ...errorContextItem
                    },
                    {
                        message: 'se.upload.file.type.invalid',
                        subject: 'type'
                    }
                ]);
            }
        });
    });
});
