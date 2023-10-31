/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injector } from '@angular/core';
import { of, throwError } from 'rxjs';
import { IAuthToken } from '../../interfaces';
import { promiseUtils, WindowUtils } from '../../utils';
import { SSOAuthenticationHelper } from './sso-authentication.helper';

describe('ssoAuthenticationHelper', () => {
    let ssoAuthenticationHelper: SSOAuthenticationHelper;
    let windowUtils: jasmine.SpyObj<WindowUtils>;
    let httpClient: jasmine.SpyObj<HttpClient>;
    let injector: jasmine.SpyObj<Injector>;
    let _window: jasmine.SpyObj<Window>;
    let opener: jasmine.SpyObj<Window>;
    let logoutIframe: jasmine.SpyObj<HTMLIFrameElement>;
    let _location: jasmine.SpyObj<Location>;

    const SSO_AUTHENTICATION_ENTRY_POINT = '/saml';
    const SSO_LOGOUT_ENTRY_POINT = '/saml/logout';
    const SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = '/authenticate';
    const SSO_CLIENT_ID = 'smartApp';

    const authToken: IAuthToken = {
        access_token: 'access_token',
        token_type: 'token_type'
    };

    const error = {
        some: 'error'
    };

    const errorResponse = {
        status: 404,
        error,
        headers: new HttpHeaders()
    } as HttpErrorResponse;

    beforeEach(() => {
        //

        opener = jasmine.createSpyObj<Window>('mockWindow', ['postMessage']);
        _window = jasmine.createSpyObj<Window>('mockWindow', [
            'open',
            'opener',
            'close',
            'addEventListener'
        ]);
        (_window as any).opener = opener;

        logoutIframe = jasmine.createSpyObj<HTMLIFrameElement>('logoutIframe', ['remove']);

        const _document = jasmine.createSpyObj<Document>('mockDocument', [
            'location',
            'querySelector'
        ]);

        _document.querySelector.and.callFake((selector: string) => {
            if (selector === 'iframe#logoutIframe') {
                return logoutIframe;
            }
            throw new Error(`unexpected selector '${selector}' passed to document.querySelector`);
        });

        _location = jasmine.createSpyObj<Location>('mockLocation', ['pathname', 'href']);
        (_window as any).document = _document;
        (_document as any).location = _location;
        (_location as any).pathname = '/app';
        (_location as any).origin = 'someorigin';
        (_location as any).href = '/app?sso';

        windowUtils = jasmine.createSpyObj<WindowUtils>('windowUtils', ['getWindow']);
        windowUtils.getWindow.and.returnValue(_window);
        httpClient = jasmine.createSpyObj<HttpClient>('httpClient', ['post']);
        injector = jasmine.createSpyObj<Injector>('injector', ['get']);

        injector.get.and.callFake((token: any) => {
            if (token === 'SSO_AUTHENTICATION_ENTRY_POINT') {
                return SSO_AUTHENTICATION_ENTRY_POINT;
            } else if (token === 'SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT') {
                return SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT;
            } else if (token === 'SSO_CLIENT_ID') {
                return SSO_CLIENT_ID;
            } else if (token === 'SSO_LOGOUT_ENTRY_POINT') {
                return SSO_LOGOUT_ENTRY_POINT;
            }
            throw new Error(`unexpected token ${token} passed to injector`);
        });
        ssoAuthenticationHelper = new SSOAuthenticationHelper(
            windowUtils,
            promiseUtils,
            httpClient,
            injector
        );
    });

    it('launchSSODialog will open a pop-up and listen for token being sent back', (done) => {
        //

        expect(ssoAuthenticationHelper.isAutoSSOMain()).toBe(false);
        ssoAuthenticationHelper.launchSSODialog().then((token) => {
            expect(_window.open).toHaveBeenCalledWith(
                '/saml/app?sso',
                'SSODIALOG_WINDOW',
                'toolbar=no,scrollbars=no,resizable=no,top=200,left=200,width=1000,height=800'
            );
            expect(token).toBe(authToken);

            expect(ssoAuthenticationHelper.isAutoSSOMain()).toBe(true);

            expect(logoutIframe.remove).toHaveBeenCalled();

            done();
        });

        const callback: any = _window.addEventListener.calls.argsFor(0)[1];

        callback({
            origin: document.location.origin,
            data: {
                eventId: 'ssoAuthenticate',
                authToken
            }
        });
    });

    it('completeDialog will open a pop-up and listen for error being sent back', (done) => {
        //

        httpClient.post.and.callFake((url: string, payload: any) => {
            if (
                url === SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT &&
                payload.client_id === SSO_CLIENT_ID
            ) {
                return throwError(errorResponse);
            }
            throw new Error(`unexpected url ${url} passed to httpClient.post`);
        });

        ssoAuthenticationHelper.completeDialog().then(
            () => fail('should have rejected'),
            () => {
                expect(opener.postMessage).toHaveBeenCalledWith(
                    {
                        eventId: 'ssoAuthenticateError',
                        error: {
                            status: 404,
                            error
                        }
                    },
                    'someorigin'
                );
                expect(_window.close).toHaveBeenCalled();

                done();
            }
        );
    });

    it('completeDialog will open a pop-up and listen for token being sent back', async () => {
        //

        httpClient.post.and.callFake(((url: string, payload: any) => {
            if (
                url === SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT &&
                payload.client_id === SSO_CLIENT_ID
            ) {
                return of(authToken);
            }
            throw new Error(`unexpected url ${url} passed to httpClient.post`);
        }) as any);

        await ssoAuthenticationHelper.completeDialog();

        expect(opener.postMessage).toHaveBeenCalledWith(
            {
                eventId: 'ssoAuthenticate',
                authToken
            },
            'someorigin'
        );
        expect(_window.close).toHaveBeenCalled();
    });

    it('logout will call sso logout in an iframe and reload the app in non SSO mode', () => {
        ssoAuthenticationHelper.logout();

        expect(logoutIframe.src).toBe(SSO_LOGOUT_ENTRY_POINT);

        expect((_location as any).href).toBe('/app');
        //
    });
});
