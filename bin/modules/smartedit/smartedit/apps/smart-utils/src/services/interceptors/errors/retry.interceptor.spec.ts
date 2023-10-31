/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

/* tslint:disable:max-classes-per-file */
import { HttpClient, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { TypedMap } from '../../../dtos';
import { IAlertService } from '../../../interfaces';
import { Class } from '../../../types';
import { booleanUtils } from '../../../utils';
import {
    defaultRetryStrategyFactory,
    exponentialRetryStrategyFactory,
    linearRetryStrategyFactory,
    ExponentialRetry,
    IRetryStrategy,
    LinearRetry,
    OperationContextService,
    SimpleRetry
} from './retries';
import { RetryInterceptor, RetryPredicate } from './retry.interceptor';

describe('retry interceptor', () => {
    let httpClient: jasmine.SpyObj<HttpClient>;
    let operationContextService: jasmine.SpyObj<OperationContextService>;
    let translate: jasmine.SpyObj<TranslateService>;
    let alertService: jasmine.SpyObj<IAlertService>;
    let defaultRetryStrategy: jasmine.SpyObj<Class<IRetryStrategy>>;
    let exponentialRetryStrategy: jasmine.SpyObj<Class<IRetryStrategy>>;
    let linearRetryStrategy: jasmine.SpyObj<Class<IRetryStrategy>>;

    let retryInterceptor: RetryInterceptor;

    const CUSTOM_URI = '/ABC.com';

    const OPERATION_CONTEXT: TypedMap<string> = {};

    const customPredicate: RetryPredicate = function (
        request: HttpRequest<any>,
        response: HttpErrorResponse,
        operationContext?: string
    ) {
        return response.status === 500 && request.url === CUSTOM_URI;
    };

    class MockStrategy implements IRetryStrategy {
        firstFastRetry = false;
        attemptCount = 0;
        canRetry(): boolean {
            throw new Error('Method not implemented.');
        }
        calculateNextDelay(): number {
            throw new Error('Method not implemented.');
        }
    }

    let CUSTOM_MOCK_REQUEST: HttpRequest<any>;

    let CUSTOM_MOCK_RESPONSE: HttpErrorResponse;

    beforeEach(() => {
        jasmine.clock().uninstall();
        jasmine.clock().install();
        CUSTOM_MOCK_REQUEST = new HttpRequest('GET', CUSTOM_URI);

        CUSTOM_MOCK_RESPONSE = {
            url: CUSTOM_URI,
            status: 500
        } as HttpErrorResponse;

        httpClient = jasmine.createSpyObj<HttpClient>('httpClient', ['request']);
        alertService = jasmine.createSpyObj<IAlertService>('alertService', [
            'showDanger',
            'showWarning'
        ]);
        operationContextService = jasmine.createSpyObj<OperationContextService>(
            'operationContextService',
            ['findOperationContext', 'register']
        );
        translate = jasmine.createSpyObj<TranslateService>('translate', ['instant']);

        defaultRetryStrategy = defaultRetryStrategyFactory(new SimpleRetry());
        exponentialRetryStrategy = exponentialRetryStrategyFactory(new ExponentialRetry());
        linearRetryStrategy = linearRetryStrategyFactory(new LinearRetry());

        retryInterceptor = new RetryInterceptor(
            httpClient,
            translate,
            operationContextService,
            alertService,
            booleanUtils,
            defaultRetryStrategy,
            exponentialRetryStrategy,
            linearRetryStrategy,
            OPERATION_CONTEXT
        );
    });

    afterEach(() => {
        jasmine.clock().uninstall();
    });

    it('should match predicate if there is one predicate/strategyHolder matches', () => {
        retryInterceptor.register(customPredicate, MockStrategy);

        expect(retryInterceptor.predicate(CUSTOM_MOCK_REQUEST, CUSTOM_MOCK_RESPONSE)).toBe(true);
    });

    it('should match predicate if there is one predicate/strategyHolder with an operation context matches', () => {
        operationContextService.findOperationContext.and.returnValue('TOOLING');

        const predicate = function (
            request: HttpRequest<any>,
            response: HttpErrorResponse,
            operationContext?: string
        ) {
            return operationContext === 'TOOLING';
        };

        retryInterceptor.register(predicate, MockStrategy);

        expect(retryInterceptor.predicate(CUSTOM_MOCK_REQUEST, CUSTOM_MOCK_RESPONSE)).toBe(true);
    });

    it('should be able to chain the register function', () => {
        expect(
            retryInterceptor
                .register(customPredicate, MockStrategy)
                .register(customPredicate, MockStrategy)
        ).toEqual(retryInterceptor);
    });

    it('should be able to handle a request error when the reponse error config already has an existing retryStrategy instance', async () => {
        class MockStrategy3 implements IRetryStrategy {
            firstFastRetry = false;
            attemptCount = 0;

            canRetry() {
                return true;
            }
            calculateNextDelay() {
                return 500;
            }
        }

        const retryStrategy = new MockStrategy3();

        const mockResponse = {
            url: CUSTOM_URI,
            status: 500
        } as HttpErrorResponse;

        spyOn(retryInterceptor as any, 'retrieveRetryStrategy').and.callFake(
            (request: HttpRequest<any>) => {
                if (request.url === CUSTOM_URI) {
                    return retryStrategy;
                }
                return null;
            }
        );

        const retryStrategyCanRetrySpy = spyOn(retryStrategy, 'canRetry').and.callThrough();
        const retryStrategyCalculateSpy = spyOn(
            retryStrategy,
            'calculateNextDelay'
        ).and.callThrough();

        const finalResponse = {
            mockValue: 1
        };

        httpClient.request.and.callFake(((request: HttpRequest<any>) => {
            if (request.url === CUSTOM_URI) {
                return of(finalResponse);
            }
            return null;
        }) as any);

        const waitResponseError = retryInterceptor.responseError(CUSTOM_MOCK_REQUEST, mockResponse);
        jasmine.clock().tick(500);
        const success = await waitResponseError;
        expect(success).toEqual(finalResponse);
        expect(retryStrategyCanRetrySpy).toHaveBeenCalled();
        expect(retryStrategyCalculateSpy).toHaveBeenCalled();
        expect(retryStrategy.attemptCount).toEqual(1);
    });

    it('should be able to register a predicate/strategyHolder and handle a reponse error that match the given predicate', async () => {
        class MockStrategy2 implements IRetryStrategy {
            firstFastRetry = false;
            attemptCount = 0;

            canRetry() {
                return true;
            }
            calculateNextDelay() {
                return 500;
            }
        }

        retryInterceptor.register(customPredicate, MockStrategy2);

        const mockResponse = {
            url: CUSTOM_URI,
            status: 500
        } as HttpErrorResponse;

        const finalResponse = {
            mockValue: 1
        };

        httpClient.request.and.callFake(((request: HttpRequest<any>) => {
            if (request.url === CUSTOM_URI) {
                return of(finalResponse);
            }
            return null;
        }) as any);

        const waitResponseError = retryInterceptor.responseError(CUSTOM_MOCK_REQUEST, mockResponse);
        jasmine.clock().tick(500);
        const success = await waitResponseError;

        expect(success).toEqual(finalResponse);
    });
});
