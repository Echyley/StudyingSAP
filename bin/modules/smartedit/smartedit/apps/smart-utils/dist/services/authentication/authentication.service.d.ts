/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { IAuthenticationManagerService, IAuthenticationService, ICredentialsMapRecord, IEventService, ILoginData, IModalService, ISettingsService, ISharedDataService, IStorageService } from '../../interfaces';
import { LogService } from '../log.service';
import { ITranslationsFetchService } from '../translations';
import { SSOAuthenticationHelper } from './sso-authentication.helper';
export interface ICredentialsMap {
    [entryPoint: string]: ICredentialsMapRecord;
}
export interface IAuthMap {
    [entryPoint: string]: string;
}
export declare class AuthenticationService extends IAuthenticationService {
    protected translationsFetchService: ITranslationsFetchService;
    protected modalService: IModalService;
    protected sharedDataService: ISharedDataService;
    protected storageService: IStorageService;
    protected eventService: IEventService;
    protected ssoAuthenticationHelper: SSOAuthenticationHelper;
    protected settingsService: ISettingsService;
    protected authenticationManager: IAuthenticationManagerService;
    private readonly logService;
    constructor(translationsFetchService: ITranslationsFetchService, modalService: IModalService, sharedDataService: ISharedDataService, storageService: IStorageService, eventService: IEventService, ssoAuthenticationHelper: SSOAuthenticationHelper, settingsService: ISettingsService, authenticationManager: IAuthenticationManagerService, logService: LogService);
    filterEntryPoints(resource: string): Promise<string[]>;
    isAuthEntryPoint(resource: string): Promise<boolean>;
    authenticate(resource: string): Promise<void>;
    logout(): Promise<void>;
    isReAuthInProgress(entryPoint: string): Promise<boolean>;
    setReAuthInProgress(entryPoint: string): Promise<void>;
    isAuthenticated(url: string): Promise<boolean>;
    protected _findLoginData(resource: string): Promise<ILoginData>;
    protected _launchAuth(loginData: ILoginData): Promise<any>;
}
