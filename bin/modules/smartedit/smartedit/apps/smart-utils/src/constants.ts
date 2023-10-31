/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';

export const LIBRARY_NAME = '@smart/utils';

/* TOKENS */

export const WHO_AM_I_RESOURCE_URI_TOKEN = `${LIBRARY_NAME}_WHO_AM_I_RESOURCE_URI`;

export const I18N_RESOURCE_URI_TOKEN = `${LIBRARY_NAME}_I18N_RESOURCE_URI`;

export const EVENT_SERVICE = `${LIBRARY_NAME}_EVENTSERVICE`;

/* EVENTS */

export const REAUTH_STARTED = 'REAUTH_STARTED';

export const DEFAULT_AUTHENTICATION_ENTRY_POINT = '/authorizationserver/oauth/token';

/**
 * Root resource URI of i18n API
 */

export const I18N_ROOT_RESOURCE_URI = '/smarteditwebservices/v1/i18n';

export const DEFAULT_AUTHENTICATION_CLIENT_ID = 'smartedit';

export const DEFAULT_AUTH_MAP = {
    ['^(?!' + I18N_ROOT_RESOURCE_URI + '/.*$).*$']: DEFAULT_AUTHENTICATION_ENTRY_POINT
};

export const DEFAULT_CREDENTIALS_MAP = {
    [DEFAULT_AUTHENTICATION_ENTRY_POINT]: {
        client_id: DEFAULT_AUTHENTICATION_CLIENT_ID
    }
};

export const LANDING_PAGE_PATH = '/';

export const SWITCH_LANGUAGE_EVENT = 'SWITCH_LANGUAGE_EVENT';
export const SELECTED_LANGUAGE = 'SELECTED_LANGUAGE';

export const EVENTS = {
    TOKEN_STORED: 'TOKEN_STORED',
    AUTHORIZATION_SUCCESS: 'AUTHORIZATION_SUCCESS',
    USER_HAS_CHANGED: 'USER_HAS_CHANGED',
    LOGOUT: 'SE_LOGOUT_EVENT',
    CLEAR_PERSPECTIVE_FEATURES: 'CLEAR_PERSPECTIVE_FEATURES',
    EXPERIENCE_UPDATE: 'experienceUpdate',
    PERMISSION_CACHE_CLEANED: 'PERMISSION_CACHE_CLEANED',
    PAGE_CHANGE: 'PAGE_CHANGE',
    PAGE_CREATED: 'PAGE_CREATED_EVENT',
    PAGE_UPDATED: 'PAGE_UPDATED_EVENT',
    PAGE_DELETED: 'PAGE_DELETED_EVENT',
    PAGE_SELECTED: 'PAGE_SELECTED_EVENT',
    PAGE_RESTORED: 'PAGE_RESTORED_EVENT',
    REAUTH_STARTED: 'REAUTH_STARTED'
};
export const DEFAULT_LANGUAGE_ISO = 'en';

export const LANGUAGE_SERVICE_CONSTANTS = new InjectionToken('LANGUAGE_SERVICE_CONSTANTS');
export const LANGUAGE_SERVICE = new InjectionToken('LANGUAGE_SERVICE');
export const YJQUERY_TOKEN = 'yjQuery';
