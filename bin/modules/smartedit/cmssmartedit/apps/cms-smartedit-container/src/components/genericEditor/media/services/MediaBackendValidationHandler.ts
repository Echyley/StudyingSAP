/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ValidationError, BackendError, ErrorResponse } from 'smarteditcommons';

/**
 * Service used to handle standard OCC validation errors received from the backend.
 */
@Injectable()
export class MediaBackendValidationHandler {
    /**
     * Extracts validation errors from the provided response and appends them to a specified contextual errors list.
     *
     * The expected error response from the backend matches the contract of the following response example:
     *
     * ```
     *      var response = {
     *          data: {
     *              errors: [{
     *                  type: 'ValidationError',
     *                  subject: 'mySubject',
     *                  message: 'Some validation exception occurred'
     *              }, {
     *                  type: 'SomeOtherError',
     *                  subject: 'mySubject'
     *                  message: 'Some other exception occurred'
     *              }]
     *          }
     *      }
     * ```
     * or just matched the backend error for media folder
     *      var response = {
     *          data: {
     *              errors: [{
     *                  type: 'UnknownIdentifierError',
     *                  message: 'No media folder with qualifier xxx can be found.'
     *              }, {
     *                  type: 'AmbiguousIdentifierException',
     *                  message: 'More than one media folder with qualifier xxx found.'
     *              }]
     *          }
     *      }
     *
     * Example of use:
     * ```
     *      const errorsContext = [];
     *      mediaBackendValidationHandler.handleResponse(response, errorsContext);
     * ```
     *
     * The resulting errorsContext would be as follows:
     * ```
     *      [{
     *          subject: 'mySubject',
     *          message: 'Some validation exception occurred'
     *      }]
     * ```
     *
     * @param response A response consisting of a list of errors. For details of the expected format, see the example above.
     * @param errorsContext An array that all validation errors are appended to. It is an output parameter.
     * @returns The error context list originally provided, or a new list, appended with the validation errors.
     */
    public handleResponse(response: ErrorResponse, errorsContext?: any[]): any[] {
        errorsContext = errorsContext || [];
        if (response?.error?.errors) {
            response.error.errors
                .filter((error) => this.isValidationError(error))
                .forEach(({ subject, message }: ValidationError) => {
                    if (subject) {
                        errorsContext.push({
                            subject,
                            message
                        });
                    }
                });
            response.error.errors
                .filter((error) => this.isMediaFolderError(error))
                .forEach(({ type, message }: BackendError) => {
                    errorsContext.push({
                        subject: 'folder',
                        message
                    });
                });
            response.error.errors
                .filter((error) => this.isUploadSizeError(error))
                .forEach(() => {
                    errorsContext.push({
                        subject: 'code',
                        message: 'se.max.upload.size.exceeded.error'
                    });
                });
        }
        return errorsContext;
    }

    private isValidationError(error: BackendError): error is ValidationError {
        return error.type === 'ValidationError';
    }

    private isUploadSizeError(error: BackendError): error is ValidationError {
        return error.type === 'MaxUploadSizeExceededError';
    }

    private isMediaFolderError(error: BackendError): boolean {
        return (
            error.type === 'UnknownIdentifierError' || error.type === 'AmbiguousIdentifierException'
        );
    }
}
