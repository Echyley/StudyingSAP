/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';
export interface LoginDialogResource {
    topLogoURL: string;
    bottomLogoURL: string;
}
export declare const LoginDialogResourceProvider: InjectionToken<LoginDialogResource>;
