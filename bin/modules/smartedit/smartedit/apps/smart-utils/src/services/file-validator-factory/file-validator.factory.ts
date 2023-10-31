/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface FileValidatorByProperty {
    /** Property name of validated object which value is passed to the predicate. */
    subject: string;
    /** i18n key of the error message. */
    message: string;
    /** Predicate that determines whether the value is valid. */
    validate: (propValue: any, compareValue: any) => boolean;
}

export interface FileValidator {
    validate: (
        fileUnderValidation: any,
        maxUploadFileSize?: number,
        errorsContext?: ErrorContext[]
    ) => boolean;
}

export interface ErrorContext {
    /** A key used to identify error for specific context. */
    subject: string;
    /** i18n key. */
    message: string;
}

/** Used to build a validator for a specified list of file validator. */
export class FileValidatorFactory {
    /**
     * Builds a new validator for a specified list of validator objects.
     * Each validator object must consist of a parameter to validate, a predicate function to run against the value and
     * a message to associate with this predicate function's fail case.
     *
     * For example, the resulting Object Validator has a validate method that takes two parameters:
     * an Object to validate against and an optional Contextual Error List to append errors to.
     *
     * ```
     *      const validators = [{
     *          subject: 'code',
     *          validate: function(code) {
     *              return code !== 'Invalid';
     *          },
     *          message: 'Code must not be "Invalid"'
     *      }]
     *
     *      const validator = fileValidatorFactory.build(validators);
     *      const errorsContext = []
     *      const objectUnderValidation = {
     *          code: 'Invalid'
     *      };
     *      const isValid = validator.validate(objectUnderValidation, errorsContext);
     * ```
     *
     * The result of the above code block would be that isValid is false because it failed the predicate function of
     * the single validator in the validator list and the errorsContext would be as follows:
     *
     * ```
     *      [{
     *          subject: 'code',
     *          message: 'Code must not be "Invalid"'
     *      }]
     * ```
     *
     * @param validators A list of validator objects as specified above.
     * @returns A validator that consists of a validate function.
     */
    public build(validators: FileValidatorByProperty[]): FileValidator {
        return {
            validate: (
                objectUnderValidation: any,
                maxUploadFileSize?: number,
                errorsContext?: ErrorContext[]
            ): boolean =>
                this.validate(validators, objectUnderValidation, maxUploadFileSize, errorsContext)
        };
    }

    private validate(
        validators: FileValidatorByProperty[],
        objectUnderValidation: File,
        objectCompareValidation: number,
        errorsContext?: ErrorContext[]
    ): boolean {
        errorsContext = errorsContext || [];
        validators.forEach((validator) => {
            const valueToValidate = objectUnderValidation[validator.subject];
            if (!validator.validate(valueToValidate, objectCompareValidation)) {
                errorsContext.push({
                    subject: validator.subject,
                    message: validator.message
                });
            }
        });
        return errorsContext.length === 0;
    }
}
