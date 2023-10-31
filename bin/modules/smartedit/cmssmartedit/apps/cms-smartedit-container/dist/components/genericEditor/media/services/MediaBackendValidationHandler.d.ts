import { ErrorResponse } from 'smarteditcommons';
/**
 * Service used to handle standard OCC validation errors received from the backend.
 */
export declare class MediaBackendValidationHandler {
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
    handleResponse(response: ErrorResponse, errorsContext?: any[]): any[];
    private isValidationError;
    private isUploadSizeError;
    private isMediaFolderError;
}
