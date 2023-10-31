/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { IAuthToken } from '../../interfaces';
import { Deferred, PromiseUtils, WindowUtils } from '../../utils';

const SSO_DIALOG_MARKER = 'sso';

enum SSO_PROPERTIES {
    SSO_CLIENT_ID = 'SSO_CLIENT_ID',
    SSO_AUTHENTICATION_ENTRY_POINT = 'SSO_AUTHENTICATION_ENTRY_POINT',
    SSO_LOGOUT_ENTRY_POINT = 'SSO_LOGOUT_ENTRY_POINT',
    SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = 'SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT'
}

const CHILD_SMARTEDIT_SENDING_AUTHTOKEN = 'ssoAuthenticate';
const CHILD_SMARTEDIT_SENDING_AUTH_ERROR = 'ssoAuthenticateError';

const SSODIALOG_WINDOW = 'SSODIALOG_WINDOW';
/*
 * Helper to initiate a SAML /SSO autentication sequence through a pop-up
 * (because the sequence involves auto-submiting html form at some point that causes a redirect and hence would
 * loose app context if not executed in a different window)
 * that ultimately loads the app again which in turn will detect its context and do the following:
 * - will not continue loading
 * - wil post the loginToken to the /authenticate end point to retrieve oAuth access
 * - will send back to parent (through postMessage) the retrieved oAuth access
 * - will close;
 */
@Injectable()
export class SSOAuthenticationHelper {
    // static in order to be shared by multiple instances
    private static lastAuthenticatedWithSSO = false;

    private readonly logoutIframeId = 'logoutIframe';

    private deferred: Deferred<IAuthToken> | null = null;

    constructor(
        private windowUtils: WindowUtils,
        private promiseUtils: PromiseUtils,
        private httpClient: HttpClient,
        private injector: Injector
    ) {
        this.listenForAuthTokenBeingSentBack();
    }

    /*
     * Initiates the SSO dialog through a pop-up
     */
    launchSSODialog(): Promise<IAuthToken> {
        this.deferred = this.promiseUtils.defer<IAuthToken>();

        const ssoAuthenticationEntryPoint =
            this.injector.get(SSO_PROPERTIES.SSO_AUTHENTICATION_ENTRY_POINT) +
            this.getSSOContextPath();
        this.window.open(
            ssoAuthenticationEntryPoint,
            SSODIALOG_WINDOW,
            'toolbar=no,scrollbars=no,resizable=no,top=200,left=200,width=1000,height=800'
        );

        return this.deferred.promise;
    }

    /*
     * SSO happen in a popup window launched by AuthenticationHelper#launchSSODialog().
     * Once SSO is successful, a 'LoginToken' cookie is present, this is a pre-requisite for doing a POST to the /authenticate
     * endpoint that will return the Authorization bearer token.
     * This bearer is then sent with postMessage to the opener window, i.e. the SmartEdit application that will resume the pending 401 request.
     */
    completeDialog(): Promise<void> {
        return new Promise<void>((resolve, reject) => {
            this.httpClient
                .post<IAuthToken>(
                    this.injector.get(SSO_PROPERTIES.SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT),
                    { client_id: this.injector.get(SSO_PROPERTIES.SSO_CLIENT_ID) }
                )
                .subscribe(
                    (authToken) => {
                        this.window.opener.postMessage(
                            {
                                eventId: CHILD_SMARTEDIT_SENDING_AUTHTOKEN,
                                authToken
                            },
                            this.document.location.origin
                        );
                        this.window.close();
                        resolve();
                    },
                    (httpErrorResponse: HttpErrorResponse) => {
                        const clonableHttpErrorResponse = {
                            error: httpErrorResponse.error,
                            status: httpErrorResponse.status
                        };
                        this.window.opener.postMessage(
                            {
                                eventId: CHILD_SMARTEDIT_SENDING_AUTH_ERROR,
                                error: clonableHttpErrorResponse
                            },
                            this.document.location.origin
                        );
                        this.window.close();
                        reject();
                    }
                );
        });
    }

    /*
     * case of the App being a popup only meant for authentication and spun up buy the main app
     */
    isSSODialog(): boolean {
        return (
            this.window.name === SSODIALOG_WINDOW &&
            new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search)
        );
    }

    /*
     * case of:
     * - the App called from another app in an SSO context and that should therefore auto-authenticate with SSO
     * - last manual authentication was with SSO
     */
    isAutoSSOMain(): boolean {
        return (
            SSOAuthenticationHelper.lastAuthenticatedWithSSO ||
            (this.window.name !== SSODIALOG_WINDOW &&
                new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search))
        );
    }

    logout(): any {
        let logoutIframe = this.logoutIframe;
        if (!logoutIframe) {
            logoutIframe = this.document.createElement('iframe');
            logoutIframe.id = this.logoutIframeId;
            logoutIframe.style.display = 'none';
            this.document.body.appendChild(logoutIframe);
        }
        logoutIframe.src = this.injector.get(SSO_PROPERTIES.SSO_LOGOUT_ENTRY_POINT);

        SSOAuthenticationHelper.lastAuthenticatedWithSSO = false;
        this.document.location.href = this.document.location.href.replace(
            this.getSSOContextPath(),
            this.document.location.pathname
        );
    }

    // context path of app in an SSO mode
    private getSSOContextPath(): string {
        return `${this.document.location.pathname}?${SSO_DIALOG_MARKER}`;
    }

    private listenForAuthTokenBeingSentBack(): void {
        this.window.addEventListener(
            'message',
            (event: MessageEvent) => {
                if (event.origin !== document.location.origin) {
                    return;
                }

                this.logoutIframe && this.logoutIframe.remove();

                if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTHTOKEN) {
                    SSOAuthenticationHelper.lastAuthenticatedWithSSO = true;
                    this.deferred && this.deferred.resolve(event.data.authToken);
                } else if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTH_ERROR) {
                    this.deferred && this.deferred.reject(event.data.error);
                }
            },
            false
        );
    }

    private get window(): Window {
        return this.windowUtils.getWindow();
    }

    private get document(): Document {
        return this.window.document;
    }

    private get logoutIframe(): HTMLIFrameElement | null {
        return this.document.querySelector<HTMLIFrameElement>(`iframe#${this.logoutIframeId}`);
    }
}
