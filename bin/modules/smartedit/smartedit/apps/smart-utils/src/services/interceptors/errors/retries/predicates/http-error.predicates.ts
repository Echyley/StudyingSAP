/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import * as lodash from 'lodash';
import { booleanUtils } from '../../../../../utils';
const SERVER_ERROR_PREDICATE_HTTP_STATUSES = [500, 502, 503, 504];
const CLIENT_ERROR_PREDICATE_HTTP_STATUSES = [429];
const TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES = [408];

export function serverErrorPredicate(
    request: HttpRequest<any>,
    response: HttpErrorResponse
): boolean {
    return response && lodash.includes(SERVER_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
export function clientErrorPredicate(
    request: HttpRequest<any>,
    response: HttpErrorResponse
): boolean {
    return response && lodash.includes(CLIENT_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
export function timeoutErrorPredicate(
    request: HttpRequest<any>,
    response: HttpErrorResponse
): boolean {
    return response && lodash.includes(TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES, response.status);
}
export function retriableErrorPredicate(
    request: HttpRequest<any>,
    response: HttpErrorResponse
): boolean {
    return (
        response &&
        booleanUtils.isAnyTruthy(
            serverErrorPredicate,
            clientErrorPredicate,
            timeoutErrorPredicate
        )(request, response)
    );
}
export function noInternetConnectionErrorPredicate(
    request: HttpRequest<any>,
    response: HttpErrorResponse
): boolean {
    return response && response.status === 0;
}
