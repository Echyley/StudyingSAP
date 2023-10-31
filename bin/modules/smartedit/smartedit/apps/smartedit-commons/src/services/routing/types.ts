/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationEnd, NavigationError, NavigationStart } from '@angular/router';

export enum NavigationEventSource {
    SRC_URL = '/'
}

export type RouteNavigationEnd = NavigationEnd;
export type RouteNavigationStart = NavigationStart;
export type RouteNavigationError = NavigationError;

export interface RouteChangeEvent<T> {
    from: NavigationEventSource;
    url: string;
    routeData: T;
}
