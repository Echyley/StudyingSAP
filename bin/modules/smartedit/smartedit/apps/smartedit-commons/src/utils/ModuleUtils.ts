/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces angular.module:false */
import { Injectable, NgModule } from '@angular/core';
import { ModuleUtils as ParentModuleUtils } from '@smart/utils';

/**
 * Internal utility service to handle ES6 modules
 *
 * @internal
 * @ignore
 */
@Injectable({ providedIn: 'root' })
export class ModuleUtils extends ParentModuleUtils {
    constructor() {
        super();
    }

    public getNgModule(appName: string): NgModule {
        if (window.__smartedit__.modules) {
            const moduleKey: string = Object.keys(window.__smartedit__.modules).find(
                (key) =>
                    key.toLowerCase().endsWith(appName.toLowerCase()) ||
                    key.toLowerCase().endsWith(appName.toLowerCase() + 'module')
            );

            if (!moduleKey) {
                return null;
            }
            return window.__smartedit__.modules[moduleKey];
        }
        return null;
    }
}

export const moduleUtils = new ModuleUtils();
