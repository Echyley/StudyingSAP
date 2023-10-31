/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { BackendError } from '@smart/utils';
export interface ErrorResponse<T = BackendError[]> {
    error: {
        errors: T;
    };
}
