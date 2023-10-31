import { NgModule } from '@angular/core';
import { ModuleUtils as ParentModuleUtils } from '@smart/utils';
/**
 * Internal utility service to handle ES6 modules
 *
 * @internal
 * @ignore
 */
export declare class ModuleUtils extends ParentModuleUtils {
    constructor();
    getNgModule(appName: string): NgModule;
}
export declare const moduleUtils: ModuleUtils;
