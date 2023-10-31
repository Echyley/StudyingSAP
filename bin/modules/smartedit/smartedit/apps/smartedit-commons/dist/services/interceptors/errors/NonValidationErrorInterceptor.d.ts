import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { IHttpErrorInterceptor } from '@smart/utils';
import { GenericEditorStackService } from '../../../components/genericEditor/services/GenericEditorStackService';
import { IAlertService } from '../../../services/interfaces';
/**
 * Used for HTTP error code 400. It removes all errors of type 'ValidationError' and displays alert messages for non-validation errors.
 */
export declare class NonValidationErrorInterceptor<T = any> implements IHttpErrorInterceptor<T> {
    private readonly alertService;
    private readonly genericEditorStackService;
    constructor(alertService: IAlertService, genericEditorStackService: GenericEditorStackService);
    predicate(request: HttpRequest<T>, response: HttpErrorResponse): boolean;
    responseError(request: HttpRequest<T>, response: HttpErrorResponse): Promise<never>;
}
