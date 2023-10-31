import { Router } from '@angular/router';
import { LogService } from '@smart/utils';
import { Observable } from 'rxjs';
import { RouteNavigationEnd, RouteNavigationError, RouteNavigationStart } from './types';
/**
 * A service that provides navigation and URL manipulation capabilities.
 * It is a reliant source of information on routing state in Smartedit.
 */
export declare class SmarteditRoutingService {
    private readonly router;
    private readonly document;
    private readonly logService;
    private listenersInitialized;
    private readonly routeChangeError$;
    private readonly routeChangeStart$;
    private readonly routeChangeSuccess$;
    private previousRouterUrl;
    constructor(router: Router, document: Document, logService: LogService);
    /**
     *  Initializes listeners for navigation events.
     */
    init(): void;
    /** Navigates based on the provided URL (absolute). */
    go(url: string): Promise<boolean>;
    /** Returns the current router URL. */
    path(): string;
    /** Returns absolute URL. */
    absUrl(): string;
    /** Notifies when the route change has started. */
    routeChangeStart(): Observable<RouteNavigationStart>;
    /** Notifies when the route change has ended. */
    routeChangeSuccess(): Observable<RouteNavigationEnd>;
    /** Notifies when the route change has failed. */
    routeChangeError(): Observable<RouteNavigationError>;
    /** Reloads the given URL. If not provided, it will reload the current URL.
     * Add a fake route '/nulldummy' to improve the reload speed
     */
    reload(url?: string): Promise<boolean>;
    /**
     * Extracts `url` from Angular router event
     */
    getCurrentUrlFromEvent(event: RouteNavigationEnd | RouteNavigationError | RouteNavigationStart): string | null;
    /**
     * @internal
     * @ignore
     */
    private warnAboutListenersNotInitialized;
    /**
     * @internal
     * @ignore
     */
    private notifyOnAngularRouteEvents;
}
