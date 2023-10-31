/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';

import { DialogRef } from '@fundamental-ngx/core';
import * as lodash from 'lodash';
import { of, throwError } from 'rxjs';
import { ISessionService, IStorageService, IEventService } from '../../interfaces';
import { LogService } from '../../services';
import { SSOAuthenticationHelper } from '../../services/authentication';
import { urlUtils } from '../../utils';
import { LoginDialogComponent } from './login-dialog.component';

const MESSAGE = 'Bad credentials';

const DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK = '/authorizationserver/oauth/token';

const AUTH_URI_AND_CLIENT_CREDENTIALS_MOCK = {
    isFullScreen: true,
    authURI: DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
    clientCredentials: {
        client_id: 'client_id_1'
    }
};

const DUMMY_ERROR = {
    status: 401,
    error: {
        error_description: MESSAGE
    }
} as HttpErrorResponse;

let loginDialogComponent: LoginDialogComponent;
let modalRef: jasmine.SpyObj<DialogRef>;
let sessionServiceMock: jasmine.SpyObj<ISessionService>;
let logService: jasmine.SpyObj<LogService>;
let httpClient: jasmine.SpyObj<HttpClient>;
let storageService: jasmine.SpyObj<IStorageService>;
let ssoAuthenticationHelper: jasmine.SpyObj<SSOAuthenticationHelper>;
let eventService: jasmine.SpyObj<IEventService>;

const oauthToken = {
    access_token: 'access-token1',
    token_type: 'bearer'
};
const expectedPayload = {
    client_id: 'client_id_1',
    username: 'someusername',
    password: 'somepassword',
    grant_type: 'password'
};

describe('Login Dialog', () => {
    beforeEach(() => {
        createCommonSpyObj();

        expect(loginDialogComponent.isFullScreen).toBe(true);
    });

    describe('in non SSO', () => {
        it('when credentials are marked as wrong then return and set errors on form', (done) => {
            spyOn(
                (LoginDialogComponent as any).prototype,
                'hasRequiredCredentialsError'
            ).and.returnValue(true);

            loginDialogComponent.signinWithCredentials().then(() => {
                expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
                expect(storageService.storeAuthToken).not.toHaveBeenCalled();
                expect(storageService.removeAuthToken).toHaveBeenCalled();
                expect(loginDialogComponent.form.errors.credentialsRequiredError).toEqual(
                    'se.logindialogform.username.and.password.required'
                );
                done();
            });
        });

        it('when backend rejects the authentication then returns a rejected promise with API failure error', (done) => {
            // when
            loginDialogComponent.form.patchValue({
                username: 'unexpected',
                password: 'unexpected'
            });

            loginDialogComponent.signinWithCredentials().then(() => {
                expect(storageService.removeAuthToken).toHaveBeenCalledWith(
                    DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK
                );
                expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
                expect(storageService.storeAuthToken).not.toHaveBeenCalled();
                expect((loginDialogComponent.form.errors || {}).asyncValidationError).toEqual(
                    MESSAGE
                );
                expect(modalRef.close).not.toHaveBeenCalledWith({
                    userHasChanged: false
                });
                done();
            });
        });

        it(`when backend accepts the authentication on default entry point
            and user has not changed
            then auth token is stored
            and current user is updated`, async () => {
            sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

            // when
            loginDialogComponent.form.patchValue({
                username: 'someusername',
                password: 'somepassword'
            });

            // then
            await loginDialogComponent.signinWithCredentials();

            expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith(
                DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
                oauthToken
            );
            expect(eventService.publish).toHaveBeenCalled();
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalRef.close).toHaveBeenCalledWith({
                userHasChanged: false
            });
        });

        it(`when backend accepts the authentication on default entry point
            and user has changed
            then auth token is stored
            and current user is updated`, async () => {
            sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(true));

            // when
            loginDialogComponent.form.patchValue({
                username: 'someusername',
                password: 'somepassword'
            });

            // then
            await loginDialogComponent.signinWithCredentials();

            expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith(
                DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
                oauthToken
            );
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalRef.close).toHaveBeenCalledWith({
                userHasChanged: true
            });
        });

        it(`when backend accepts the authentication on non default entry point
            then auth token is stored
            and current user is not updated`, async () => {
            const AUTH_URI = '/authEntryPoint';
            // when

            sessionServiceMock.hasUserChanged.and.throwError(
                'sessionServiceMock.hasUserChanged should not be invoked'
            );

            loginDialogComponent.form.patchValue({
                username: 'someusername',
                password: 'somepassword'
            });

            loginDialogComponent.authURI = AUTH_URI;

            // then
            await loginDialogComponent.signinWithCredentials();

            expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith(AUTH_URI, oauthToken);
            expect(eventService.publish).toHaveBeenCalled();
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalRef.close).toHaveBeenCalledWith({
                userHasChanged: false
            });
        });
    });

    describe('in SSO', () => {
        it('when backend rejects the authentication then returns a rejected promise with API failure error', (done) => {
            // when
            ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.reject(DUMMY_ERROR));

            loginDialogComponent.signinWithSSO().then(() => {
                expect(storageService.removeAuthToken).toHaveBeenCalledWith(
                    DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK
                );
                expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
                expect(storageService.storeAuthToken).not.toHaveBeenCalled();
                expect(loginDialogComponent.form.errors.asyncValidationError).toEqual(MESSAGE);
                expect(modalRef.close).not.toHaveBeenCalledWith({
                    userHasChanged: false
                });
                done();
            });
        });

        it(`when backend accepts the authentication on default entry point
            and user has not changed
            then auth token is stored
            and current user is updated`, async () => {
            sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

            // when
            ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.resolve(oauthToken));

            // then
            await loginDialogComponent.signinWithSSO();

            expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith(
                DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
                oauthToken
            );
            expect(eventService.publish).toHaveBeenCalled();
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalRef.close).toHaveBeenCalledWith({
                userHasChanged: false
            });
        });

        it(`when backend accepts the authentication on default entry point
            and user has changed
            then auth token is stored
            and current user is updated`, async () => {
            sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(true));

            // when
            ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.resolve(oauthToken));

            // then
            await loginDialogComponent.signinWithSSO();

            expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
            expect(storageService.storeAuthToken).toHaveBeenCalledWith(
                DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
                oauthToken
            );
            expect(eventService.publish).toHaveBeenCalled();
            expect(storageService.removeAuthToken).toHaveBeenCalled();
            expect(modalRef.close).toHaveBeenCalledWith({
                userHasChanged: true
            });
        });
    });
});
function createCommonSpyObj() {
    modalRef = jasmine.createSpyObj<DialogRef>('modalRef', ['close', 'data']);
    modalRef.data = AUTH_URI_AND_CLIENT_CREDENTIALS_MOCK;

    ssoAuthenticationHelper = jasmine.createSpyObj<SSOAuthenticationHelper>(
        'ssoAuthenticationHelper',
        ['isAutoSSOMain', 'launchSSODialog']
    );

    eventService = jasmine.createSpyObj('eventService', ['publish', 'subscribe']);
    sessionServiceMock = jasmine.createSpyObj<ISessionService>('sessionService', [
        'hasUserChanged',
        'setCurrentUsername'
    ]);
    sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

    logService = jasmine.createSpyObj<LogService>('logService', ['debug', 'error']);
    httpClient = jasmine.createSpyObj<HttpClient>('httpClient', ['request']);

    httpClient.request.and.callFake(((method: string, url: string, payload: any) => {
        if (url !== DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK && url !== '/authEntryPoint') {
            throw new Error(`unexpected http url ${url}`);
        }
        if (method !== 'POST') {
            throw new Error(`unexpected http method ${payload.method}`);
        }
        if (payload.headers.get('Content-Type') !== 'application/x-www-form-urlencoded') {
            throw new Error(`unexpected http Content-Type ${payload.headers['Content-Type']}`);
        }
        if (lodash.isEqual(expectedPayload, urlUtils.parseQuery(payload.body))) {
            return of(new HttpResponse({ body: oauthToken }));
        } else {
            return throwError(DUMMY_ERROR);
        }
    }) as any);

    storageService = jasmine.createSpyObj<IStorageService>('storageService', [
        'removeAuthToken',
        'storeAuthToken'
    ]);

    loginDialogComponent = new LoginDialogComponent(
        modalRef,
        logService,
        httpClient,
        sessionServiceMock,
        storageService,
        urlUtils,
        ssoAuthenticationHelper,
        { topLogoURL: '', bottomLogoURL: '' },
        eventService
    );

    loginDialogComponent.ngOnInit();
}
