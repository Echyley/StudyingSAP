import { ModuleWithProviders } from '@angular/core';
import { ExtraOptions, Route, RouterModule } from '@angular/router';
/**
 * The interface represents a shortcut component
 */
export interface SeShortcutComponent {
    shortcutLink: SeRouteShortcutConfig;
}
/**
 * The shortcut configuration.
 * - titleI18nKey an optional title of the page shortcut link
 * - priority an optional number ranging from 0 to 1000 used for sorting where 0 is the first.
 * - shortcutComponent an optional component that will be rendered. This attribute is used only if titleI18nKey is not provided.
 * 	 Must implement SeShortcutComponent interface.
 */
interface ShortcutConfig {
    titleI18nKey?: string;
    priority?: number;
    shortcutComponent?: new (...args: any[]) => SeShortcutComponent;
}
/**
 * Shortcut route configuration.
 */
export interface SeRouteShortcutConfig extends ShortcutConfig {
    fullPath: string;
}
/**
 * SmartEdit route configuration
 */
export interface SeRoute extends Route, ShortcutConfig {
    children?: SeRoutes;
}
/**
 * Type represents an array of route configuration.
 */
export declare type SeRoutes = SeRoute[];
/**
 * The SeRouteService is Angular service that allows to add Angular routes to the application.
 * It also collects information about each route to build route related shortcut links.
 */
export declare class SeRouteService {
    static CATALOG_AWARE_PATH_PLACEHOLDERS: string[];
    private static readonly routeShortcuts;
    /**
     * Adds new Angular route to the application. For more information please see documentation for RouterModule.forRoot.
     * @returns A wrapper around an NgModule that associates it with the providers.
     */
    static provideNgRoute(routes: SeRoutes, config?: ExtraOptions): ModuleWithProviders<RouterModule>;
    /**
     * Returns a list of all shortcut configs.
     */
    static get routeShortcutConfigs(): SeRouteShortcutConfig[];
    /**
     * Populates the route shortcut list. It filters route shortcuts that cannot be used
     * as shortcuts.
     */
    private static provideRouteShortcutConfigs;
    /**
     * Recursively reads the list of routes and calculates the full path for each route.
     * Then populates the shortcut configs array with calculated data.
     */
    private static generateRouteShortcutConfig;
    /**
     * Returns the full path concatenating parent route with current one.
     */
    private static getFullPath;
    /**
     * Validates whether the route shortcut config can be registered.
     * It's not registered if:
     * - the fullPath is not provided
     * - the fullPath contains placeholders that are not in CATALOG_AWARE_PATH_PLACEHOLDERS list.
     */
    private static canRegisterRouteShortcutConfig;
}
export {};
