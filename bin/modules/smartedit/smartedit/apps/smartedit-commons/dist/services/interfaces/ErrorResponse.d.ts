import { BackendError } from '@smart/utils';
export interface ErrorResponse<T = BackendError[]> {
    error: {
        errors: T;
    };
}
