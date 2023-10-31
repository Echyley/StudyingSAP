/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
export declare const LIBRARY_NAME = "@smart/utils";
export declare const WHO_AM_I_RESOURCE_URI_TOKEN: string;
export declare const I18N_RESOURCE_URI_TOKEN: string;
export declare const EVENT_SERVICE: string;
export declare const REAUTH_STARTED = "REAUTH_STARTED";
export declare const DEFAULT_AUTHENTICATION_ENTRY_POINT = "/authorizationserver/oauth/token";
/**
 * Root resource URI of i18n API
 */
export declare const I18N_ROOT_RESOURCE_URI = "/smarteditwebservices/v1/i18n";
export declare const DEFAULT_AUTHENTICATION_CLIENT_ID = "smartedit";
export declare const DEFAULT_AUTH_MAP: {
    [x: string]: string;
};
export declare const DEFAULT_CREDENTIALS_MAP: {
    "/authorizationserver/oauth/token": {
        client_id: string;
    };
};
export declare const LANDING_PAGE_PATH = "/";
export declare const SWITCH_LANGUAGE_EVENT = "SWITCH_LANGUAGE_EVENT";
export declare const SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
export declare const EVENTS: {
    TOKEN_STORED: string;
    AUTHORIZATION_SUCCESS: string;
    USER_HAS_CHANGED: string;
    LOGOUT: string;
    CLEAR_PERSPECTIVE_FEATURES: string;
    EXPERIENCE_UPDATE: string;
    PERMISSION_CACHE_CLEANED: string;
    PAGE_CHANGE: string;
    PAGE_CREATED: string;
    PAGE_UPDATED: string;
    PAGE_DELETED: string;
    PAGE_SELECTED: string;
    PAGE_RESTORED: string;
    REAUTH_STARTED: string;
};
export declare const DEFAULT_LANGUAGE_ISO = "en";
export declare const LANGUAGE_SERVICE_CONSTANTS: InjectionToken<unknown>;
export declare const LANGUAGE_SERVICE: InjectionToken<unknown>;
export declare const YJQUERY_TOKEN = "yjQuery";
