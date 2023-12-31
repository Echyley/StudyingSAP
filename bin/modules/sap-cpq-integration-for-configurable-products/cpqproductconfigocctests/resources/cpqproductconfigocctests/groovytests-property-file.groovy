/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
HOST = 'localhost'
PORT = 8001
SECURE_PORT = 8002
BASE_SITE = 'wsB2BTest'

DEFAULT_HTTP_URI = "http://${HOST}:${PORT}"
DEFAULT_HTTPS_URI = "https://${HOST}:${SECURE_PORT}"

BASE_PATH = "/${WEBROOT}/${VERSION}"
BASE_PATH_WITH_SITE = "/${WEBROOT}/${VERSION}/${BASE_SITE}"

FULL_BASE_URI = DEFAULT_HTTP_URI + BASE_PATH_WITH_SITE
FULL_SECURE_BASE_URI = DEFAULT_HTTPS_URI + BASE_PATH_WITH_SITE

OAUTH2_TOKEN_URI = DEFAULT_HTTPS_URI
OAUTH2_TOKEN_ENDPOINT_PATH = "/${AUTHWEBROOT}/oauth/token"
OAUTH2_TOKEN_ENDPOINT_URI = OAUTH2_TOKEN_URI + OAUTH2_TOKEN_ENDPOINT_PATH

HTTP_WEBROOT = DEFAULT_HTTP_URI + "/${WEBROOT}"
HTTPS_WEBROOT = DEFAULT_HTTPS_URI + "/${WEBROOT}"
HTTPS_AUTHWEBROOT = DEFAULT_HTTPS_URI + "/${AUTHWEBROOT}"

OAUTH2_CALLBACK_URI = "https://${HOST}:${SECURE_PORT}/${AUTHWEBROOT}/oauth2_callback"

CLIENT_ID = 'mobile_android'
CLIENT_SECRET = 'secret'
TRUSTED_CLIENT_ID = 'trusted_client'
TRUSTED_CLIENT_SECRET = 'secret'

FAIL_ON_NAMING_CONVENTION_ERROR = false
