/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { InjectionToken } from '@angular/core';

export interface LoginDialogResource {
    topLogoURL: string;
    bottomLogoURL: string;
}

export const LoginDialogResourceProvider: InjectionToken<LoginDialogResource> = new InjectionToken(
    'LoginDialogResourceProvider'
);
