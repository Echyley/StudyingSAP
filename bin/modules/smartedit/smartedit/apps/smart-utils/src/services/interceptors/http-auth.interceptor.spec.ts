/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { HttpHandler, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injector } from '@angular/core';
import { of, Observable } from 'rxjs';
import { IAuthenticationService, IStorageService } from '../../interfaces';
import { httpUtils } from '../../utils';
import { HttpAuthInterceptor } from './http-auth.interceptor';

describe('httpAuthInterceptor', () => {
    let response: HttpResponse<any>;
    let observable: Observable<any>;
    let next: jasmine.SpyObj<HttpHandler>;
    let injector: jasmine.SpyObj<Injector>;
    let authenticationService: jasmine.SpyObj<IAuthenticationService>;
    let storageService: jasmine.SpyObj<IStorageService>;
    let httpAuthInterceptor: HttpAuthInterceptor;

    const I18N_RESOURCE_URI = 'I18N_RESOURCE_URI';
    const FAIL_DESCR = 'did not expect observable to fail';

    beforeEach(() => {
        response = new HttpResponse({ body: {}, status: 200, url: 'someurl' });
        observable = of(response);
        next = jasmine.createSpyObj<HttpHandler>('next', ['handle']);
        injector = jasmine.createSpyObj<Injector>('injector', ['get']);
        authenticationService = jasmine.createSpyObj<IAuthenticationService>(
            'authenticationService',
            ['filterEntryPoints']
        );
        storageService = jasmine.createSpyObj<IStorageService>('storageService', ['getAuthToken']);
        storageService.getAuthToken.and.returnValue(Promise.resolve(null));

        injector.get.and.callFake((token: any) => {
            if (token === IStorageService) {
                return storageService;
            }
            throw new Error(`unexpected injection token ${token}`);
        });

        httpAuthInterceptor = new HttpAuthInterceptor(
            authenticationService,
            injector,
            httpUtils,
            I18N_RESOURCE_URI
        );
    });

    it('if url is html, config is returned, not a promise and neiher authenticationService nor storageService are ever invoked', () => {
        const request = new HttpRequest<any>('GET', 'somepath/somefile.html');
        request.headers.set('key', 'value');

        mockHandleForRequest(request);

        expect(httpAuthInterceptor.intercept(request, next)).toBe(observable);

        expect(authenticationService.filterEntryPoints).not.toHaveBeenCalled();
        expect(storageService.getAuthToken).not.toHaveBeenCalled();
    });

    it('if access_token present found, it will be added to outgoing request', (done) => {
        const request = new HttpRequest<any>('GET', 'aurl');

        const authToken = {
            access_token: 'access-token1',
            token_type: 'bearer'
        };

        const entryPoints = ['entryPoint1'];
        authenticationService.filterEntryPoints.and.returnValue(Promise.resolve(entryPoints));

        storageService.getAuthToken.and.returnValue(Promise.resolve(authToken));

        next.handle.and.callFake((_request: HttpRequest<any>) => {
            if (
                _request.url === 'aurl' &&
                _request.headers.get('Authorization') ===
                    ['bearer', authToken.access_token].join(' ')
            ) {
                return observable;
            }
            throw new Error(`unexpected request handled: ${request}`);
        });

        httpAuthInterceptor.intercept(request, next).subscribe(
            (result) => {
                expect(result).toBe(response);
                expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith(request.url);
                expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');

                done();
            },
            () => {
                fail(FAIL_DESCR);
            }
        );
    });

    it('if access_token not found in storage, no authorization header is added to outgoing request', (done) => {
        const request = new HttpRequest<any>('GET', 'aurl');

        const entryPoints = ['entryPoint1'];
        authenticationService.filterEntryPoints.and.returnValue(Promise.resolve(entryPoints));

        storageService.getAuthToken.and.returnValue(Promise.resolve(null));

        next.handle.and.callFake((_request: HttpRequest<any>) => {
            if (_request.url === 'aurl' && !_request.headers.get('Authorization')) {
                return observable;
            }
            throw new Error(`unexpected request handled: ${request}`);
        });

        httpAuthInterceptor.intercept(request, next).subscribe(
            (result) => {
                expect(result).toBe(response);
                expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('aurl');
                expect(storageService.getAuthToken).toHaveBeenCalledWith('entryPoint1');

                done();
            },
            () => {
                fail(FAIL_DESCR);
            }
        );
    });

    it('if API pattern not recognised, no authorization header is added to outgoing request', (done) => {
        const request = new HttpRequest<any>('GET', 'aurl');

        authenticationService.filterEntryPoints.and.returnValue(Promise.resolve([]));

        next.handle.and.callFake((_request: HttpRequest<any>) => {
            if (_request.url === 'aurl' && !_request.headers.get('Authorization')) {
                return observable;
            }
            throw new Error(`unexpected request handled: ${request}`);
        });

        httpAuthInterceptor.intercept(request, next).subscribe(
            (result) => {
                expect(result).toBe(response);
                expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('aurl');
                expect(storageService.getAuthToken).not.toHaveBeenCalled();

                done();
            },
            () => {
                fail(FAIL_DESCR);
            }
        );
    });

    function mockHandleForRequest(request: HttpRequest<any>) {
        next.handle.and.callFake((_request: HttpRequest<any>) => {
            if (_request === request) {
                return observable;
            }
            throw new Error(`unexpected request handled: ${request}`);
        });
    }
});
