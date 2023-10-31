/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { Injector } from '@angular/core';
import * as lodash from 'lodash';
import { promiseUtils } from '../../utils';
import { HttpErrorInterceptorService } from './http-error-interceptor.service';
import { IHttpErrorInterceptor } from './i-http-error.interceptor';

let injector: Injector;
let customErrorInterceptor: IHttpErrorInterceptor;
let httpErrorInterceptorService: HttpErrorInterceptorService;
let ERROR_COUNTER = 0;
let defaultRequest: HttpRequest<any>;
const FAIL_ERROR_DESCR = 'observable should have been in error';
const FAIL_SUCCESS_DESCR = 'observable should have been successful';
const httpCode400 = 400;

describe('http error interceptor service - http code', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('a resource not found error (404) for Content-type json THEN an error message is expected to see ', (done) => {
        const responseErrorSpy = spyOn(
            httpErrorInterceptorService,
            'responseError'
        ).and.callThrough();
        const request = new HttpRequest('GET', '/error404_json');
        const RESPONSE_MOCK = {
            url: '/error404_json',
            status: 404,
            errors: [{ message: 'Your request could not be processed! Please try again later!' }]
        } as any as HttpErrorResponse;
        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );
        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK);
                expect(error.errors.length).toEqual(1);
                expect(error.errors[0].message).toContain('Your request could not be processed');
                done();
            }
        );
        expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
    });

    it('a resource not found error (404) for Content-type html THEN no error message is displayed ', (done) => {
        const responseErrorSpy = spyOn(
            httpErrorInterceptorService,
            'responseError'
        ).and.callThrough();
        const request = new HttpRequest('GET', '/error404_html');
        const RESPONSE_MOCK = {
            url: '/error404_html',
            status: 404,
            errors: [{}]
        } as any as HttpErrorResponse;
        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );
        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK);
                expect(error.errors.message).toEqual(undefined);
                done();
            }
        );
        expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
    });

    it('a bad request (400) is requested for Content-type json THEN an error message is expected to see ', (done) => {
        const responseErrorSpy = spyOn(
            httpErrorInterceptorService,
            'responseError'
        ).and.callThrough();
        const request = new HttpRequest('GET', '/error400_json');
        const RESPONSE_MOCK = {
            url: '/error400_json',
            status: 400,
            errors: [{ message: '1111' }, { message: 'error: bad request' }]
        } as any as HttpErrorResponse;
        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );
        const errNumber = 2;
        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK);
                expect(error.errors.length).toEqual(errNumber);
                expect(error.errors[1].message).toContain('bad request');
                done();
            }
        );
        expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
    });

    it('a custom interceptor is added for 501 errors of Content-type json AND a 501 error of Content-type json is requested THEN an error message is expected to see ', (done) => {
        const responseErrorSpy = spyOn(
            httpErrorInterceptorService,
            'responseError'
        ).and.callThrough();
        const _request = new HttpRequest('GET', '/error501_json');
        const RESPONSE_MOCK_CODE = {
            url: '/error501_json',
            errors: [{ message: 'error: 501 bad request' }],
            status: 501
        } as any as HttpErrorResponse;
        // WHEN
        const responseErrorObservableMock = httpErrorInterceptorService.responseError(
            _request,
            RESPONSE_MOCK_CODE
        );
        // THEN
        responseErrorObservableMock.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK_CODE);
                expect(error.errors[0].message).toContain('501');
                expect(error.errors.length).toEqual(1);
                done();
            }
        );
        expect(responseErrorSpy).toHaveBeenCalledWith(_request, RESPONSE_MOCK_CODE);
    });

    it(
        'GIVEN a custom retry strategy is registered for 503 error WHEN a 503 error is triggered that ' +
            'correspond to a operation context THEN I expect to see a message when maximum of retry is reached ',
        (done) => {
            const responseErrorSpy = spyOn(
                httpErrorInterceptorService,
                'responseError'
            ).and.callThrough();
            const request = new HttpRequest('GET', '/error503/a123/v1/foobar/');
            const RESPONSE_MOCK = {
                url: '/error503/a123/v1/foobar/',
                status: 503,
                errors: [{ message: 'FAILED' }]
            } as any as HttpErrorResponse;
            // WHEN
            const responseErrorObservable = httpErrorInterceptorService.responseError(
                request,
                RESPONSE_MOCK
            );
            // THEN
            responseErrorObservable.subscribe(
                () => {
                    fail(FAIL_ERROR_DESCR);
                },
                (error: any) => {
                    // THEN
                    expect(error).toEqual(RESPONSE_MOCK);
                    expect(error.errors.length).toEqual(1);
                    expect(error.errors[0].message).toBe('FAILED');
                    done();
                }
            );
            expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
        }
    );

    it(
        'a custom retry strategy is registered for a request that fails twice before being successful WHEN the request is made ' +
            'THEN a retry in progress and THEN a success message are expected to see  ',
        (done) => {
            const responseErrorSpy = spyOn(
                httpErrorInterceptorService,
                'responseError'
            ).and.callThrough();
            const request = new HttpRequest('GET', '/error502/retry');
            const RESPONSE_MOCK = {
                url: '/error502/retry',
                status: 200,
                success: [{ message: 'PASSED' }]
            } as any as HttpErrorResponse;
            // WHEN
            const responseErrorObservable = httpErrorInterceptorService.responseError(
                request,
                RESPONSE_MOCK
            );
            // THEN
            responseErrorObservable.subscribe(
                () => {
                    fail(FAIL_ERROR_DESCR);
                },
                (error: any) => {
                    // THEN
                    expect(error).toEqual(RESPONSE_MOCK);
                    expect(error.success.length).toEqual(1);
                    expect(error.success[0].message).toBe('PASSED');
                    done();
                }
            );
            expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
        }
    );
});

describe('http error interceptor service - interceptors', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('should call httpErrorInterceptorService.responseError when an error is intercepted', (done) => {
        const responseErrorSpy = spyOn(
            httpErrorInterceptorService,
            'responseError'
        ).and.callThrough();

        // GIVEN
        const request = new HttpRequest('GET', '/any_url');
        const RESPONSE_MOCK = {
            url: '/any_url',
            status: 400
        } as any as HttpErrorResponse;

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );

        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK);
                done();
            }
        );
        expect(responseErrorSpy).toHaveBeenCalledWith(request, RESPONSE_MOCK);
    });

    it('should be able to register interceptors', () => {
        // WHEN
        const errorInterceptorMock1 = getErrorInterceptorMock(true);
        const errorInterceptorMock2 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(errorInterceptorMock1);
        httpErrorInterceptorService.addInterceptor(errorInterceptorMock2);

        // THEN
        expect((httpErrorInterceptorService as any)._errorInterceptors.length).toEqual(2);
        expect((httpErrorInterceptorService as any)._errorInterceptors).toEqual([
            errorInterceptorMock2,
            errorInterceptorMock1
        ]);
    });

    it('should be able to register an interceptor with angular recipe', () => {
        httpErrorInterceptorService.addInterceptor(customErrorInterceptor);

        // THEN
        expect((httpErrorInterceptorService as any)._errorInterceptors.length).toEqual(1);
        expect((httpErrorInterceptorService as any)._errorInterceptors[0]).toEqual(
            customErrorInterceptor
        );
    });

    it('should be able to unregister an interceptor', () => {
        // GIVEN
        const interceptor1 = getErrorInterceptorMock(true);

        const interceptor2 = getErrorInterceptorMock(true);

        const unregisterErrorInterceptor1 =
            httpErrorInterceptorService.addInterceptor(interceptor1);
        httpErrorInterceptorService.addInterceptor(interceptor2);

        // WHEN
        unregisterErrorInterceptor1();

        // THEN
        expect((httpErrorInterceptorService as any)._errorInterceptors).toEqual([interceptor2]);
    });

    it('should return a failing observable when there is no interceptors available', (done) => {
        // GIVEN
        const RESPONSE_MOCK = {
            status: 400,
            error: {
                errors: []
            }
        } as HttpErrorResponse;

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            defaultRequest,
            RESPONSE_MOCK
        );

        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_MOCK);
                done();
            }
        );
    });
});

describe('http error interceptor service - handle responseError', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('should reject the responseError promise when no predicate matches the response', (done) => {
        // GIVEN
        const RESPONSE_STATUS_500_MOCK = {
            status: 500,
            error: {
                errors: []
            }
        } as HttpErrorResponse;
        // The Interceptor listen only on response.status '400'
        const errorStatus400InterceptorMock = getErrorInterceptorMock(true);
        httpErrorInterceptorService.addInterceptor(errorStatus400InterceptorMock);

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            defaultRequest,
            RESPONSE_STATUS_500_MOCK
        );

        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(RESPONSE_STATUS_500_MOCK);
                done();
            }
        );
    });

    it('should reject the responseError promise with expected data if all interceptors reject the response', (done) => {
        // GIVEN
        const RESPONSE_MOCK = {
            status: 400,
            error: {
                errors: []
            }
        } as HttpErrorResponse;

        // getErrorInterceptorMock function mutate the response error array
        const expectedResponse = lodash.cloneDeep(RESPONSE_MOCK);
        expectedResponse.error.errors = [1, 2];

        const interceptorMock1 = getErrorInterceptorMock(true);
        const interceptorMock2 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(interceptorMock1);
        httpErrorInterceptorService.addInterceptor(interceptorMock2);

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            defaultRequest,
            RESPONSE_MOCK
        );

        // THEN
        responseErrorObservable.subscribe(
            () => {
                fail(FAIL_ERROR_DESCR);
            },
            (error: any) => {
                // THEN
                expect(error).toEqual(expectedResponse);
                done();
            }
        );
    });

    it('should resolve the responseError promise with expected data if one interceptor resolve the response and should not call subsequent interceptors', (done) => {
        // GIVEN
        const interceptorMock1 = getErrorInterceptorMock(true);
        const interceptorMock2 = getErrorInterceptorMock(false);
        const interceptorMock3 = getErrorInterceptorMock(true);

        httpErrorInterceptorService.addInterceptor(interceptorMock1);
        httpErrorInterceptorService.addInterceptor(interceptorMock2);
        httpErrorInterceptorService.addInterceptor(interceptorMock3);

        // last interceptor to be called is the first added
        const lastInterceptorResponseErrorSpy = spyOn(interceptorMock1, 'responseError');

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(defaultRequest, {
            status: 400,
            error: {
                errors: []
            }
        } as HttpErrorResponse);

        // THEN

        // THEN
        responseErrorObservable.subscribe(
            (response: any) => {
                expect(response).toEqual({
                    status: 400,
                    error: {
                        errors: [1, 2] // getErrorInterceptorMock function mutate the response error array
                    }
                });
                expect(lastInterceptorResponseErrorSpy).not.toHaveBeenCalled();
                done();
            },
            () => {
                // THEN
                fail(FAIL_SUCCESS_DESCR);
            }
        );
    });

    it('should resolve the responseError promise if one interceptor resolve the response', (done) => {
        // GIVEN
        const request = new HttpRequest('GET', '/any_url');

        const RESPONSE_MOCK = {
            url: '/any_url',
            status: 400,
            error: {
                errors: []
            }
        } as HttpErrorResponse;
        // getErrorInterceptorMock function mutate the response error array
        const expectedResponse = lodash.cloneDeep(RESPONSE_MOCK);
        expectedResponse.error.errors = [1];

        const errorInterceptorMock = getErrorInterceptorMock(false);
        httpErrorInterceptorService.addInterceptor(errorInterceptorMock);

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );

        // THEN
        responseErrorObservable.subscribe(
            (response: any) => {
                expect(response).toEqual(expectedResponse);
                done();
            },
            () => {
                // THEN
                fail(FAIL_SUCCESS_DESCR);
            }
        );
    });

    it('should resolve the responseError promise if adding a interceptor with angular recipe resolve the response', (done) => {
        // GIVEN

        const request = new HttpRequest('GET', '/any_url');

        const RESPONSE_MOCK = {
            url: '/any_url',
            status: 400,
            error: {
                errors: []
            }
        } as HttpErrorResponse;
        httpErrorInterceptorService.addInterceptor(customErrorInterceptor);

        // WHEN
        const responseErrorObservable = httpErrorInterceptorService.responseError(
            request,
            RESPONSE_MOCK
        );

        // THEN
        responseErrorObservable.subscribe(
            (response: any) => {
                expect(response).toEqual(RESPONSE_MOCK);
                done();
            },
            () => {
                // THEN
                fail(FAIL_SUCCESS_DESCR);
            }
        );
    });
});

function buildCommonSpyObjects() {
    injector = jasmine.createSpyObj<Injector>('injector', ['get']);

    customErrorInterceptor = {
        predicate: () => true,
        responseError(request, response) {
            return Promise.resolve(response);
        }
    } as IHttpErrorInterceptor;
    ERROR_COUNTER = 0;

    httpErrorInterceptorService = new HttpErrorInterceptorService(injector, promiseUtils);

    defaultRequest = new HttpRequest('GET', '/any_url');
}

function getErrorInterceptorMock(rejectPromise: boolean) {
    return {
        predicate(request: HttpRequest<any>, response: HttpErrorResponse) {
            return response.status === httpCode400;
        },
        responseError(request: HttpRequest<any>, response: HttpErrorResponse) {
            // mutating the response error
            ++ERROR_COUNTER;
            response.error.errors.push(ERROR_COUNTER);
            if (rejectPromise) {
                return Promise.reject(response);
            } else {
                return Promise.resolve(response);
            }
        }
    };
}
