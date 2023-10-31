/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { HttpClient, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { of } from 'rxjs';
import { IAuthenticationService, IEventService } from '../../../interfaces';
import { httpUtils, promiseUtils } from '../../../utils';
import { LogService } from '../../log.service';
import { UnauthorizedErrorInterceptor } from './unauthorized-error.interceptor';

let httpClient: jasmine.SpyObj<HttpClient>;
let authenticationService: jasmine.SpyObj<IAuthenticationService>;
let unauthorizedErrorInterceptor: UnauthorizedErrorInterceptor;
let defaultRequest: HttpRequest<any>;
let eventService: jasmine.SpyObj<IEventService>;
let logService: jasmine.SpyObj<LogService>;

const WHO_AM_I_RESOURCE_URI = 'WHO_AM_I_RESOURCE_URI';
const REAUTH_STARTED = 'REAUTH_STARTED';
const FAIL_DESCR = 'the final request should have been successful';
const authEntryPoint = 'authEntryPoint';
const fakeFilterEntryPoints = (url: string) => {
    if (url === 'request1') {
        return Promise.resolve(['authEntryPoint1']);
    } else if (url === 'request2') {
        return Promise.resolve(['authEntryPoint2']);
    }
    throw new Error(`unexpected url ${url} passed top authenticationService.filterEntryPoints`);
};

describe('unauthorized error interceptor', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('should match predicate for a xhr request with a HTTP Error 401', () => {
        // GIVEN
        const matchMockResponse = {
            status: 401
        } as HttpErrorResponse;

        // WHEN
        const matchPredicate = unauthorizedErrorInterceptor.predicate(
            defaultRequest,
            matchMockResponse
        );

        // THEN
        expect(matchPredicate).toBe(true);
    });

    it('should not match predicate for a 401 returned by the "Who am I" service', () => {
        // GIVEN
        const matchMockResponse = {
            status: 401
        } as HttpErrorResponse;

        // WHEN
        const matchPredicate = unauthorizedErrorInterceptor.predicate(
            new HttpRequest('GET', WHO_AM_I_RESOURCE_URI),
            matchMockResponse
        );

        // THEN
        expect(matchPredicate).toBe(false);
    });

    it('should not match predicate for a xhr request with a HTTP Error 400 or 404', () => {
        let predicate;
        // GIVEN
        [400, 404].forEach((status) => {
            // WHEN
            predicate = unauthorizedErrorInterceptor.predicate(defaultRequest, {
                status
            } as HttpErrorResponse);

            // THEN
            expect(predicate).toBe(false);
        });
    });

    it('should handle a 401 unauthorized request and reattempt the same request after a successfull user authentication', (done) => {
        const mockResponse = {
            status: 401,
            url: '/any_url',
            error: {}
        } as HttpErrorResponse;
        // final response once authenticated
        const newResponse = 'anyReponse';

        authenticationService.filterEntryPoints.and.returnValue(Promise.resolve([authEntryPoint]));
        authenticationService.isAuthEntryPoint.and.returnValue(Promise.resolve(false));
        authenticationService.isReAuthInProgress.and.returnValue(Promise.resolve(false));
        authenticationService.authenticate.and.returnValue(Promise.resolve());

        httpClient.request.and.callFake(((request: HttpRequest<any>) => {
            if (request === defaultRequest) {
                return of(newResponse);
            }
            throw new Error(`unexpected http request ${JSON.stringify(request)}`);
        }) as any);

        unauthorizedErrorInterceptor.responseError(defaultRequest, mockResponse).then(
            (success) => {
                expect(success).toBe(newResponse);

                expect(authenticationService.isAuthEntryPoint).toHaveBeenCalledWith(
                    defaultRequest.url
                );
                expect(authenticationService.isReAuthInProgress).toHaveBeenCalledWith(
                    authEntryPoint
                );
                expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(
                    authEntryPoint
                );
                expect(eventService.publish).toHaveBeenCalledWith(REAUTH_STARTED);
                expect(authenticationService.authenticate).toHaveBeenCalledWith(defaultRequest.url);

                done();
            },
            (error) => {
                fail(FAIL_DESCR);
            }
        );
    });

    it('if more than one response error is 401 and not auth URL then authentication is invoked ONLY when same authEntryPoint and all promises of a reattempt are sent back', (done) => {
        const request1 = new HttpRequest<any>('GET', 'request1');
        const request2 = new HttpRequest<any>('POST', 'request2', {});

        const mockResponse1 = {
            status: 401,
            url: 'request1'
        } as HttpErrorResponse;
        const mockResponse2 = {
            status: 401,
            url: 'request2'
        } as HttpErrorResponse;

        // final response once authenticated
        const newResponse1 = 'anyReponse';
        const newResponse2 = 'anyReponse2';

        const authDeferred = promiseUtils.defer();

        authenticationService.filterEntryPoints.and.returnValue(Promise.resolve([authEntryPoint]));
        let counter = 0;
        authenticationService.isReAuthInProgress.and.callFake((entryPoint: string) => {
            if (entryPoint === authEntryPoint) {
                counter++;
                return Promise.resolve(counter === 2);
            } else {
                return Promise.resolve(false);
            }
        });
        authenticationService.isAuthEntryPoint.and.returnValue(Promise.resolve(false));
        authenticationService.authenticate.and.returnValue(authDeferred.promise as any);

        httpClient.request.and.callFake(((request: HttpRequest<any>) => {
            if (request === request1) {
                return of(newResponse1);
            } else if (request === request2) {
                return of(newResponse2);
            }
            throw new Error(`unexpected http request ${JSON.stringify(request)}`);
        }) as any);

        const promise1 = unauthorizedErrorInterceptor.responseError(request1, mockResponse1);

        const promise2 = unauthorizedErrorInterceptor.responseError(request2, mockResponse2);

        promise1.then(
            (success) => {
                expect(success).toBe(newResponse1);

                promise2.then(
                    (success2) => {
                        expect(success2).toBe(newResponse2);
                        expect(authenticationService.isReAuthInProgress.calls.count()).toBe(2);
                        expect(authenticationService.setReAuthInProgress.calls.count()).toBe(1);
                        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(
                            authEntryPoint
                        );
                        expect(authenticationService.authenticate.calls.count()).toBe(1);

                        done();
                    },
                    (error) => {
                        fail(FAIL_DESCR);
                    }
                );
            },
            (error) => {
                fail(FAIL_DESCR);
            }
        );

        authDeferred.resolve();
    });

    it('if more than one response error is 401 and not auth URL then authentication is invoked once per authEntryPoint and all promises of a reattempt are sent back', (done) => {
        const request1 = new HttpRequest<any>('GET', 'request1');
        const request2 = new HttpRequest<any>('POST', 'request2', {});

        const response1 = {
            status: 401,
            url: 'request1'
        } as HttpErrorResponse;
        const response2 = {
            status: 401,
            url: 'request2'
        } as HttpErrorResponse;
        // final response once authenticated
        const newResponse1 = 'anyReponse1';
        const newResponse2 = 'anyReponse2';

        const authDeferred = promiseUtils.defer();

        authenticationService.filterEntryPoints.and.callFake(fakeFilterEntryPoints);

        authenticationService.isAuthEntryPoint.and.returnValue(Promise.resolve(false));
        authenticationService.isReAuthInProgress.and.returnValue(Promise.resolve(false));
        authenticationService.authenticate.and.returnValue(authDeferred.promise as any);

        httpClient.request.and.callFake(((request: HttpRequest<any>) => {
            if (request === request1) {
                return of(newResponse1);
            } else if (request === request2) {
                return of(newResponse2);
            }
            throw new Error(`unexpected http request ${JSON.stringify(request)}`);
        }) as any);

        unauthorizedErrorInterceptor.responseError(request1, response1).then(
            (success) => {
                expect(success).toBe(newResponse1);

                authenticationService.isReAuthInProgress.and.callFake(
                    (curAuthEntryPoint: string) => {
                        if (curAuthEntryPoint === 'authEntryPoint1') {
                            return Promise.resolve(true);
                        } else if (curAuthEntryPoint === 'authEntryPoint2') {
                            return Promise.resolve(false);
                        }
                        throw new Error(
                            `unexpected authEntryPint ${curAuthEntryPoint} passed top authenticationService.isReAuthInProgress`
                        );
                    }
                );

                unauthorizedErrorInterceptor.responseError(request2, response2).then(
                    (success2) => {
                        expect(success2).toBe(newResponse2);

                        expect(authenticationService.isReAuthInProgress.calls.count()).toBe(2);
                        expect(authenticationService.setReAuthInProgress.calls.count()).toBe(2);
                        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(
                            'authEntryPoint1'
                        );
                        expect(authenticationService.setReAuthInProgress).toHaveBeenCalledWith(
                            'authEntryPoint2'
                        );
                        expect(authenticationService.authenticate.calls.count()).toBe(2);

                        done();
                    },
                    (error) => {
                        fail(FAIL_DESCR);
                    }
                );
            },
            (error) => {
                fail(FAIL_DESCR);
            }
        );

        authDeferred.resolve();
    });
});

function buildCommonSpyObjects() {
    defaultRequest = new HttpRequest('GET', '/any_url');

    httpClient = jasmine.createSpyObj<HttpClient>('authenticationService', ['request']);

    authenticationService = jasmine.createSpyObj('authenticationService', [
        'isReAuthInProgress',
        'setReAuthInProgress',
        'isAuthEntryPoint',
        'filterEntryPoints',
        'authenticate'
    ]);
    authenticationService.setReAuthInProgress.and.callFake(() => Promise.resolve());
    authenticationService.isAuthEntryPoint.and.returnValue(Promise.resolve(true));

    eventService = jasmine.createSpyObj<IEventService>('eventService', ['publish']);

    eventService.publish.and.resolveTo([]);

    logService = jasmine.createSpyObj<LogService>('logService', ['error']);

    unauthorizedErrorInterceptor = new UnauthorizedErrorInterceptor(
        httpClient,
        authenticationService,
        promiseUtils,
        httpUtils,
        WHO_AM_I_RESOURCE_URI,
        eventService,
        logService
    );
}
