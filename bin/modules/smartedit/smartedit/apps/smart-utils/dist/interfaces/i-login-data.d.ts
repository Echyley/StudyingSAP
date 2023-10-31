/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { ICredentialsMapRecord } from './i-credentials-map-record';
export interface ILoginData {
    isFullScreen?: boolean;
    ssoEnabled?: boolean;
    authURI: string;
    clientCredentials: ICredentialsMapRecord;
}
export interface ILoginModalFeedback {
    userHasChanged: boolean;
}
