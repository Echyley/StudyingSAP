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

import { Inject, Optional } from '@angular/core';
import * as lodash from 'lodash';
import { LoginDialogComponent } from '../../components/login-dialog';
import {
    DEFAULT_AUTH_MAP,
    DEFAULT_AUTHENTICATION_ENTRY_POINT,
    DEFAULT_CREDENTIALS_MAP,
    EVENT_SERVICE,
    EVENTS
} from '../../constants';
import { Cloneable } from '../../dtos';
import {
    IAuthenticationManagerService,
    IAuthenticationService,
    IAuthToken,
    ICredentialsMapRecord,
    IEventService,
    ILoginData,
    ILoginModalFeedback,
    IModalService,
    ISettingsService,
    ISharedDataService,
    IStorageService
} from '../../interfaces';
import { functionsUtils } from '../../utils';
import { LogService } from '../log.service';
import { ITranslationsFetchService } from '../translations';
import { SSOAuthenticationHelper } from './sso-authentication.helper';

export interface ICredentialsMap {
    [entryPoint: string]: ICredentialsMapRecord;
}

export interface IAuthMap {
    [entryPoint: string]: string;
}

interface IAuthenticationMapEntry {
    key: string;
    value: string;
}

export class AuthenticationService extends IAuthenticationService {
    constructor(
        protected translationsFetchService: ITranslationsFetchService,
        protected modalService: IModalService,
        protected sharedDataService: ISharedDataService,
        protected storageService: IStorageService,
        @Inject(EVENT_SERVICE) protected eventService: IEventService,
        protected ssoAuthenticationHelper: SSOAuthenticationHelper,
        protected settingsService: ISettingsService,
        @Optional() protected authenticationManager: IAuthenticationManagerService,
        private readonly logService: LogService
    ) {
        super();
    }

    filterEntryPoints(resource: string): Promise<string[]> {
        return this.sharedDataService.get('authenticationMap').then((authenticationMap) =>
            functionsUtils
                .convertToArray<string>({
                    ...((authenticationMap as unknown as IAuthenticationMapEntry) || {}),
                    ...DEFAULT_AUTH_MAP
                })
                .filter((entry: IAuthenticationMapEntry) =>
                    new RegExp(entry.key, 'g').test(resource)
                )
                .map((element: IAuthenticationMapEntry) => element.value)
        );
    }

    isAuthEntryPoint(resource: string): Promise<boolean> {
        return this.sharedDataService.get('authenticationMap').then((authenticationMap) => {
            const authEntryPoints = functionsUtils
                .convertToArray((authenticationMap as unknown as IAuthMap) || {})
                .map((element: IAuthenticationMapEntry) => element.value);
            return (
                authEntryPoints.indexOf(resource) > -1 ||
                resource === DEFAULT_AUTHENTICATION_ENTRY_POINT
            );
        });
    }

    authenticate(resource: string): Promise<void> {
        return this._findLoginData(resource).then((loginData: ILoginData) =>
            this._launchAuth(loginData).then((modalFeedback: ILoginModalFeedback) => {
                Promise.resolve(
                    this.eventService.publish(EVENTS.AUTHORIZATION_SUCCESS, {
                        userHasChanged: modalFeedback.userHasChanged
                    })
                )
                    .then(() => {
                        if (modalFeedback.userHasChanged) {
                            this.eventService.publish(EVENTS.USER_HAS_CHANGED);
                        }
                        /**
                         * We only need to reload when the user has changed and all authentication forms were closed.
                         * There can be many authentication forms if some modules use different (from default one) end points.
                         */
                        const reauthInProcess = lodash
                            .values(this.reauthInProgress)
                            .some((inProcess: boolean) => inProcess);

                        if (
                            modalFeedback.userHasChanged &&
                            !reauthInProcess &&
                            this.authenticationManager &&
                            this.authenticationManager.onUserHasChanged
                        ) {
                            this.authenticationManager.onUserHasChanged();
                        }
                    })
                    .catch((reason) => {
                        this.logService.debug(
                            `AuthenticationService - authenticate: ${JSON.stringify(reason)}`
                        );
                    });
                this.reauthInProgress[loginData.authURI] = false;
            })
        );
    }

    logout(): Promise<void> {
        // First, indicate the services that SmartEdit is logging out. This should give them the opportunity to clean up.
        // NOTE: This is not synchronous since some clean-up might be lengthy, and logging out should be fast.
        return this.eventService.publish(EVENTS.LOGOUT).then(() => {
            this.storageService.removeAllAuthTokens();
            if (this.ssoAuthenticationHelper.isAutoSSOMain()) {
                this.ssoAuthenticationHelper.logout();
            } else if (this.authenticationManager && this.authenticationManager.onLogout) {
                this.authenticationManager.onLogout();
            }
        });
    }

    isReAuthInProgress(entryPoint: string): Promise<boolean> {
        return Promise.resolve(this.reauthInProgress[entryPoint] === true);
    }

    setReAuthInProgress(entryPoint: string): Promise<void> {
        this.reauthInProgress[entryPoint] = true;
        return Promise.resolve();
    }

    isAuthenticated(url: string): Promise<boolean> {
        return this.filterEntryPoints(url).then((entryPoints: string[]) => {
            const authURI = entryPoints && entryPoints[0];
            return Promise.resolve(
                this.storageService.getAuthToken(authURI) as unknown as IAuthToken
            ).then((authToken: IAuthToken) => !!authToken);
        });
    }

    /*
     * will try determine first relevant authentication entry point from authenticationMap and retrieve potential client credentials to be added on top of user credentials
     */
    protected _findLoginData(resource: string): Promise<ILoginData> {
        return this.filterEntryPoints(resource).then((entryPoints: string[]) =>
            Promise.resolve(
                this.sharedDataService.get('credentialsMap').then((credentialsMap: Cloneable) => {
                    const map: ICredentialsMap = {
                        ...((credentialsMap as unknown as ICredentialsMap) || {}),
                        ...DEFAULT_CREDENTIALS_MAP
                    };
                    const authURI = entryPoints[0];
                    return {
                        authURI,
                        clientCredentials: map[authURI]
                    };
                })
            )
        );
    }

    protected _launchAuth(loginData: ILoginData): Promise<any> {
        return this.translationsFetchService
            .waitToBeReady()
            .then(() =>
                Promise.all([
                    this.storageService.isInitialized(),
                    this.settingsService.getBoolean('smartedit.sso.enabled')
                ])
            )
            .then(([isFullScreen, ssoEnabled]: [boolean, boolean]) => {
                const modalRef = this.modalService.open<ILoginData>({
                    component: LoginDialogComponent,
                    data: {
                        ...loginData,
                        isFullScreen,
                        ssoEnabled
                    },
                    config: {
                        hasBackdrop: false
                    }
                });

                return new Promise((resolve, reject) => {
                    modalRef.afterClosed.subscribe(resolve, reject);
                });
            });
    }
}
