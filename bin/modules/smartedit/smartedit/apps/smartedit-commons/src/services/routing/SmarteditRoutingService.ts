/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DOCUMENT } from '@angular/common';
import { Inject, Optional } from '@angular/core';
import {
    NavigationEnd,
    NavigationError,
    NavigationStart,
    Router,
    RouterEvent
} from '@angular/router';
import { LogService } from '@smart/utils';
import { Observable, ReplaySubject } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import {
    NavigationEventSource,
    RouteChangeEvent,
    RouteNavigationEnd,
    RouteNavigationError,
    RouteNavigationStart
} from './types';

/**
 * A service that provides navigation and URL manipulation capabilities.
 * It is a reliant source of information on routing state in Smartedit.
 */
export class SmarteditRoutingService {
    private listenersInitialized = false;
    private readonly routeChangeError$ = new ReplaySubject<
        RouteChangeEvent<RouteNavigationError>
    >();
    private readonly routeChangeStart$ = new ReplaySubject<
        RouteChangeEvent<RouteNavigationStart>
    >();
    private readonly routeChangeSuccess$ = new ReplaySubject<
        RouteChangeEvent<RouteNavigationEnd>
    >();
    private previousRouterUrl = '';

    constructor(
        @Optional() private readonly router: Router,
        @Inject(DOCUMENT) private readonly document: Document,
        private readonly logService: LogService
    ) {}

    /**
     *  Initializes listeners for navigation events.
     */
    public init(): void {
        if (!this.listenersInitialized) {
            this.notifyOnAngularRouteEvents();
            this.listenersInitialized = true;
        }
    }

    /** Navigates based on the provided URL (absolute). */
    public go(url: string): Promise<boolean> {
        return this.router.navigateByUrl(url);
    }

    /** Returns the current router URL. */
    public path(): string {
        return this.router.url;
    }

    /** Returns absolute URL. */
    public absUrl(): string {
        return this.document.location.href;
    }

    /** Notifies when the route change has started. */
    public routeChangeStart(): Observable<RouteNavigationStart> {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeStart$.pipe(
            filter(
                (event: RouteChangeEvent<RouteNavigationStart>) =>
                    !!event.url && event.url !== this.previousRouterUrl
            ),
            map((event) => event.routeData)
        );
    }

    /** Notifies when the route change has ended. */
    public routeChangeSuccess(): Observable<RouteNavigationEnd> {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeSuccess$.pipe(
            filter(
                (event: RouteChangeEvent<RouteNavigationEnd>) =>
                    !!event.url && event.url !== this.previousRouterUrl
            ),
            map((event) => event.routeData)
        );
    }

    /** Notifies when the route change has failed. */
    public routeChangeError(): Observable<RouteNavigationError> {
        this.warnAboutListenersNotInitialized();
        return this.routeChangeError$.pipe(map((event) => event.routeData));
    }

    /** Reloads the given URL. If not provided, it will reload the current URL.
     * Add a fake route '/nulldummy' to improve the reload speed
     */
    public reload(url = this.router.url): Promise<boolean> {
        return this.router
            .navigateByUrl('/nulldummy', { skipLocationChange: true })
            .then(() => this.router.navigateByUrl(url));
    }

    /**
     * Extracts `url` from Angular router event
     */
    public getCurrentUrlFromEvent(
        event: RouteNavigationEnd | RouteNavigationError | RouteNavigationStart
    ): string | null {
        if (
            event instanceof NavigationStart ||
            event instanceof NavigationEnd ||
            event instanceof NavigationError
        ) {
            return event.url;
        }
        return null;
    }

    /**
     * @internal
     * @ignore
     */
    private warnAboutListenersNotInitialized(): void {
        if (!this.listenersInitialized) {
            this.logService.warn('Listeners not initialized, run `init()` first.');
        }
    }

    /**
     * @internal
     * @ignore
     */
    private notifyOnAngularRouteEvents(): void {
        this.router.events.subscribe((event: RouterEvent) => {
            // if the event url is equal to null
            if (!event.url) {
                return;
            }

            switch (true) {
                case event instanceof NavigationStart: {
                    this.routeChangeStart$.next({
                        from: NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event as NavigationStart
                    });
                    break;
                }
                case event instanceof NavigationError: {
                    this.routeChangeError$.next({
                        from: NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event as NavigationError
                    });
                    break;
                }
                case event instanceof NavigationEnd: {
                    this.routeChangeSuccess$.next({
                        from: NavigationEventSource.SRC_URL,
                        url: event.url,
                        routeData: event as NavigationEnd
                    });
                    this.previousRouterUrl = event && event.url;
                    break;
                }
            }
        });
    }
}
