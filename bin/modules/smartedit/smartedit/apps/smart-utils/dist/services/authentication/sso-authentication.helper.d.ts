/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpClient } from '@angular/common/http';
import { Injector } from '@angular/core';
import { IAuthToken } from '../../interfaces';
import { PromiseUtils, WindowUtils } from '../../utils';
export declare class SSOAuthenticationHelper {
    private windowUtils;
    private promiseUtils;
    private httpClient;
    private injector;
    private static lastAuthenticatedWithSSO;
    private readonly logoutIframeId;
    private deferred;
    constructor(windowUtils: WindowUtils, promiseUtils: PromiseUtils, httpClient: HttpClient, injector: Injector);
    launchSSODialog(): Promise<IAuthToken>;
    completeDialog(): Promise<void>;
    isSSODialog(): boolean;
    isAutoSSOMain(): boolean;
    logout(): any;
    private getSSOContextPath;
    private listenForAuthTokenBeingSentBack;
    private get window();
    private get document();
    private get logoutIframe();
}
