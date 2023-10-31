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

import 'jasmine';
import { of } from 'rxjs';
import { LoginDialogComponent } from '../../components/login-dialog';
import { DEFAULT_AUTHENTICATION_ENTRY_POINT, EVENTS } from '../../constants';
import {
    IAuthenticationManagerService,
    IEventService,
    IModalService,
    ISettingsService,
    ISharedDataService,
    IStorageService
} from '../../interfaces';
import { ISessionService } from '../../interfaces/i-session.service';
import { LogService } from '../log.service';
import { ITranslationsFetchService } from '../translations';
import { AuthenticationService } from './authentication.service';
import { SSOAuthenticationHelper } from './sso-authentication.helper';

let authenticationService: AuthenticationService;

let translationsFetchService: jasmine.SpyObj<ITranslationsFetchService>;
let sessionServiceMock: jasmine.SpyObj<ISessionService>;
let modalServiceMock: jasmine.SpyObj<IModalService>;
let sharedDataServiceMock: jasmine.SpyObj<ISharedDataService>;
let storageServiceMock: jasmine.SpyObj<IStorageService>;
let eventService: jasmine.SpyObj<IEventService>;
let ssoAuthenticationHelper: jasmine.SpyObj<SSOAuthenticationHelper>;
let settingsService: jasmine.SpyObj<ISettingsService>;
let authenticationManager: jasmine.SpyObj<IAuthenticationManagerService>;
let logService: jasmine.SpyObj<LogService>;
const FAILURE_DESCR = 'failed to resolve';

describe('outer AuthenticationService', () => {
    beforeEach(() => {
        createCommonSpyObjects();
    });

    it('isReAuthInProgress reads status set by setReAuthInProgress', async () => {
        expect(await authenticationService.isReAuthInProgress('someURL')).toEqual(false);
        authenticationService.setReAuthInProgress('someURL');
        expect(await authenticationService.isReAuthInProgress('someURL')).toEqual(true);
    });

    describe('#filterEntryPoints', () => {
        it('WHEN an entry point is filtered using filterEntryPoints AND the entry point matches one in the default auth map THEN the auth entry points returned will include the matched entry point', async () => {
            expect(await authenticationService.filterEntryPoints('api3')).toEqual([
                DEFAULT_AUTHENTICATION_ENTRY_POINT
            ]);
        });

        it('filterEntryPoints only keeps the values of authenticationMap the regex keys of which match the resource', async () => {
            expect(await authenticationService.filterEntryPoints('api1moreandmore')).toEqual([
                'authEntryPoint1',
                'authEntryPoint2',
                DEFAULT_AUTHENTICATION_ENTRY_POINT
            ]);

            expect(await authenticationService.filterEntryPoints('api2/more')).toEqual([
                'authEntryPoint3',
                DEFAULT_AUTHENTICATION_ENTRY_POINT
            ]);

            expect(await authenticationService.filterEntryPoints('notfound')).toEqual([
                DEFAULT_AUTHENTICATION_ENTRY_POINT
            ]);
        });
    });

    describe('#isAuthEntryPoint', () => {
        it('isAuthEntryPoint returns true only if resource exactly matches at least one of the auth entry points or default auth entry point', async () => {
            expect(await authenticationService.isAuthEntryPoint('api1moreandmore')).toEqual(false);
            expect(await authenticationService.isAuthEntryPoint('authEntryPoint1')).toEqual(true);
            expect(await authenticationService.isAuthEntryPoint('authEntryPoint1more')).toEqual(
                false
            );
            expect(
                await authenticationService.isAuthEntryPoint(DEFAULT_AUTHENTICATION_ENTRY_POINT)
            ).toEqual(true);
        });
    });

    it('WHEN the entry point matches one in the default, default auth entry point is returned along with default client id', async () => {
        // WHEN
        expect(await (authenticationService as any)._findLoginData('api3')).toEqual({
            authURI: DEFAULT_AUTHENTICATION_ENTRY_POINT,
            clientCredentials: {
                client_id: 'smartedit'
            }
        });
    });

    it('WHEN the entry point matches one in the auth map, corresponding entry point is returned along with relevant credentials', async () => {
        // WHEN
        const result = await (authenticationService as any)._findLoginData('api1more');
        expect(result).toEqual({
            authURI: 'authEntryPoint1',
            clientCredentials: {
                client_id: 'client_id_1',
                client_secret: 'client_secret_1'
            }
        });
    });

    describe('#authenticate', () => {
        it('returns launch modalService and remove authInprogress flag', async () => {
            modalServiceMock.open.and.returnValue({ afterClosed: of('') } as any);
            translationsFetchService.waitToBeReady.and.returnValue(Promise.resolve());
            sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

            authenticationService.setReAuthInProgress('authEntryPoint1');
            await authenticationService.authenticate('api1/more').catch(() => {
                fail(FAILURE_DESCR);
            });

            expect(modalServiceMock.open).toHaveBeenCalledWith({
                component: LoginDialogComponent,
                data: {
                    authURI: 'authEntryPoint1',
                    clientCredentials: {
                        client_id: 'client_id_1',
                        client_secret: 'client_secret_1'
                    },
                    isFullScreen: undefined,
                    ssoEnabled: undefined
                },
                config: {
                    hasBackdrop: false
                }
            });

            expect(translationsFetchService.waitToBeReady).toHaveBeenCalled();

            const result = await authenticationService.isReAuthInProgress('authEntryPoint1');

            expect(result).toBe(false);
        });

        it('when user changed then authenticationManager.onUserHasChanged is called and USER_HAS_CHANGED is published', () => {
            modalServiceMock.open.and.returnValue({
                afterClosed: of({ userHasChanged: true })
            } as any);
            translationsFetchService.waitToBeReady.and.returnValue(Promise.resolve());
            authenticationService
                .authenticate('api1more')
                .catch(() => {
                    fail(FAILURE_DESCR);
                })
                .then(() => {
                    expect(authenticationManager.onUserHasChanged).toHaveBeenCalled();
                    expect(eventService.publish).toHaveBeenCalledWith(EVENTS.USER_HAS_CHANGED);
                });
        });

        it('when user not changed then it does not call manager and USER_HAS_CHANGED is not published', () => {
            modalServiceMock.open.and.returnValue({
                afterClosed: of({ userHasChanged: false })
            } as any);
            translationsFetchService.waitToBeReady.and.returnValue(Promise.resolve());
            authenticationService
                .authenticate('api1more')
                .catch(() => {
                    fail(FAILURE_DESCR);
                })
                .then(() => {
                    expect(authenticationManager.onUserHasChanged).not.toHaveBeenCalled();
                    expect(eventService.publish).not.toHaveBeenCalledWith(EVENTS.USER_HAS_CHANGED);
                });
        });
    });

    it('should return false when the access_token is not found in storage', async () => {
        const ENTRY_POINTS = ['entryPoint1'];
        spyOn(authenticationService, 'filterEntryPoints').and.returnValue(
            Promise.resolve(ENTRY_POINTS)
        );
        storageServiceMock.getAuthToken.and.returnValue(Promise.resolve(null));

        const result = await authenticationService.isAuthenticated('url');
        expect(result).toBe(false);
        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('should return true when the access_token is found in the storage', async () => {
        const ENTRY_POINTS = ['entryPoint1'];
        spyOn(authenticationService, 'filterEntryPoints').and.returnValue(
            Promise.resolve(ENTRY_POINTS)
        );

        const AUTH_TOKEN = {
            access_token: 'access-token1',
            token_type: 'bearer'
        };
        storageServiceMock.getAuthToken.and.returnValue(Promise.resolve(AUTH_TOKEN));

        const result = await authenticationService.isAuthenticated('url');
        expect(result).toBe(true);

        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith('entryPoint1');
    });

    it('should return false when the entry point is not found in the authentication', async () => {
        spyOn(authenticationService, 'filterEntryPoints').and.returnValue(Promise.resolve(null));
        storageServiceMock.getAuthToken.and.returnValue(Promise.resolve(null));

        const result = await authenticationService.isAuthenticated('url');
        expect(result).toBe(false);

        expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
        expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith(null);
    });

    describe('logout', () => {
        describe('in non SSO', () => {
            beforeEach(() => {
                ssoAuthenticationHelper.isAutoSSOMain.and.returnValue(false);
            });
            it('calls authenticationManager.onLogout', () => {
                authenticationService.logout().then(() => {
                    expect(authenticationManager.onLogout).toHaveBeenCalledWith();
                });
            });
        });

        describe('in SSO', () => {
            beforeEach(() => {
                ssoAuthenticationHelper.isAutoSSOMain.and.returnValue(true);
            });

            it('will call logout on ssoAuthenitcationHelper and not on authenticationManager', () => {
                authenticationService.logout().then(() => {
                    expect(ssoAuthenticationHelper.logout).toHaveBeenCalled();
                    expect(authenticationManager.onLogout).not.toHaveBeenCalled();
                });
            });
        });

        it('WHEN logout is called THEN a LOGOUT event is raised and auth tokens removed from storage', () => {
            authenticationService.logout().then(() => {
                expect(eventService.publish).toHaveBeenCalledWith(EVENTS.LOGOUT);
                expect(storageServiceMock.removeAllAuthTokens).toHaveBeenCalled();
            });
        });
    });
});
function createCommonSpyObjects() {
    authenticationManager = jasmine.createSpyObj<IAuthenticationManagerService>('manager', [
        'onLogout',
        'onUserHasChanged'
    ]);
    ssoAuthenticationHelper = jasmine.createSpyObj<SSOAuthenticationHelper>(
        'ssoAuthenticationHelper',
        ['isAutoSSOMain', 'logout']
    );
    settingsService = jasmine.createSpyObj<ISettingsService>('settingsService', ['getBoolean']);

    translationsFetchService = jasmine.createSpyObj<ITranslationsFetchService>(
        'translationsFetchService',
        ['waitToBeReady']
    );
    sessionServiceMock = jasmine.createSpyObj<ISessionService>('sessionService', [
        'hasUserChanged'
    ]);
    modalServiceMock = jasmine.createSpyObj<any>('modalService', ['open', 'open<any>(conf)']);
    sharedDataServiceMock = jasmine.createSpyObj('sharedDataService', ['get']);
    storageServiceMock = jasmine.createSpyObj<IStorageService>('storageService', [
        'isInitialized',
        'getAuthToken',
        'removeAllAuthTokens'
    ]);
    eventService = jasmine.createSpyObj<IEventService>('eventService', ['publish']);

    sharedDataServiceMock.get.and.callFake((key: string) => {
        const AUTHENTICATION_MAP = {
            api1: 'authEntryPoint1',
            api1more: 'authEntryPoint2',
            api2: 'authEntryPoint3'
        };

        const CREDENTIALS_MAP = {
            authEntryPoint1: {
                client_id: 'client_id_1',
                client_secret: 'client_secret_1'
            },
            authEntryPoint2: {
                client_id: 'client_id_2',
                client_secret: 'client_secret_2'
            }
        };

        const CONFIGURATION = {
            domain: 'thedomain'
        };

        switch (key) {
            case 'authenticationMap':
                return Promise.resolve(AUTHENTICATION_MAP);
            case 'credentialsMap':
                return Promise.resolve(CREDENTIALS_MAP);
            case 'configuration':
                return Promise.resolve(CONFIGURATION);
            default:
                throw new Error('Unexpected value passed to sharedDataServiceMock.get()' + key);
        }
    });

    eventService.publish.and.returnValue(Promise.resolve(null));

    logService = jasmine.createSpyObj<LogService>('logService', ['error']);

    authenticationService = new AuthenticationService(
        translationsFetchService,
        modalServiceMock,
        sharedDataServiceMock,
        storageServiceMock,
        eventService,
        ssoAuthenticationHelper,
        settingsService,
        authenticationManager,
        logService
    );
}
