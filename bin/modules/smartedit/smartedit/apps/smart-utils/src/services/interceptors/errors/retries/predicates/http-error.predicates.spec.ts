/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import {
    clientErrorPredicate,
    noInternetConnectionErrorPredicate,
    retriableErrorPredicate,
    serverErrorPredicate,
    timeoutErrorPredicate
} from './http-error.predicates';

describe('http error predicates', () => {
    const HTTP_ERROR_CODES_NON_RETRIABLE = [400, 401, 403, 404, 501];
    const SERVER_ERROR_PREDICATE_HTTP_STATUSES = [500, 502, 503, 504];
    const CLIENT_ERROR_PREDICATE_HTTP_STATUSES = [429];
    const TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES = [408];

    const dummyRequest = new HttpRequest('GET', 'someurl');

    it('server error predicate should match only with http server errors', () => {
        SERVER_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                serverErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('server error predicate should not match non retriable http error codes', () => {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach((status) => {
            expect(
                serverErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeFalsy();
        });
    });

    it('client error predicate should match only with http client errors', () => {
        CLIENT_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                clientErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('client error predicate should not match non retriable http error codes', () => {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach((status) => {
            expect(
                clientErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeFalsy();
        });
    });

    it('timeout error predicate should match only with timeout client error', () => {
        TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                timeoutErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('timeout error predicate should not match non retriable http error codes', () => {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach((status) => {
            expect(
                timeoutErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeFalsy();
        });
    });

    it('retriable error predicate should match with http server errors', () => {
        SERVER_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                retriableErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('retriable error predicate should match with http client errors', () => {
        CLIENT_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                retriableErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('retriable error predicate should match with http timeout errors', () => {
        TIMEOUT_ERROR_PREDICATE_HTTP_STATUSES.forEach((status) => {
            expect(
                retriableErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeTruthy();
        });
    });

    it('retriable error predicate should not match non retriable http error codes', () => {
        HTTP_ERROR_CODES_NON_RETRIABLE.forEach((status) => {
            expect(
                retriableErrorPredicate(dummyRequest, {
                    status
                } as HttpErrorResponse)
            ).toBeFalsy();
        });
    });

    it('no internet connection error predicate should match http status code 0', () => {
        expect(
            noInternetConnectionErrorPredicate(dummyRequest, {
                status: 0
            } as HttpErrorResponse)
        ).toBeTruthy();
    });
});
