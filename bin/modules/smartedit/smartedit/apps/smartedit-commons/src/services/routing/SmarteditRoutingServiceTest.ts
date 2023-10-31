/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { fakeAsync } from '@angular/core/testing';
import { NavigationEnd, NavigationError, NavigationStart, Router } from '@angular/router';
import { LogService } from '@smart/utils';
import { Observable } from 'rxjs';

import { SmarteditRoutingService } from './SmarteditRoutingService';

function mockRouter(url: string): jasmine.SpyObj<Router> {
    const router = jasmine.createSpyObj<Router>('router', ['navigateByUrl']);

    router.navigateByUrl.and.returnValue(Promise.resolve(false));
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    (router.url as string) = url;
    (router.events as unknown as jasmine.SpyObj<Observable<Event>>) = jasmine.createSpyObj<
        Observable<Event>
    >('events', ['subscribe']);

    return router;
}

describe('SmarteditRoutingService', () => {
    let service: SmarteditRoutingService;
    let router: jasmine.SpyObj<Router>;
    let document: jasmine.SpyObj<Document>;
    let logService: jasmine.SpyObj<LogService>;
    const routerCallbackDescription =
        'WHEN router emits an event THEN it should invoke given callback';
    const routerPath = '/smartedit';

    beforeEach(() => {
        router = mockRouter(routerPath);

        document = jasmine.createSpyObj('document', ['location']);
        document.location.href = 'https://smarteditabsoluteurl';
        logService = jasmine.createSpyObj('logService', ['warn']);
        service = new SmarteditRoutingService(router, document, logService);

        service.init();
    });

    it('WHEN "init" and routeChangeSuccess are called THEN listeners are created', () => {
        service.routeChangeSuccess();

        expect(router.events.subscribe).toHaveBeenCalledTimes(1);
    });

    it('WHEN "init" is called twice THEN listeners are created only once', () => {
        service.routeChangeSuccess();

        expect(router.events.subscribe).toHaveBeenCalledTimes(1);

        // clear calls
        (router.events.subscribe as jasmine.Spy).calls.reset();

        // simulating second init()
        service.init();
        expect(router.events.subscribe).not.toHaveBeenCalledTimes(1);
    });

    it('WHEN "go" method is called THEN it will call navigation', () => {
        service.go('some-url');

        expect(router.navigateByUrl).toHaveBeenCalledWith('some-url');
    });

    it('WHEN "path" method is called THEN it will return the current router URL', () => {
        expect(service.path()).toBe(routerPath);
    });

    it('WHEN "absUrl" method is called THEN will return absolute URL', () => {
        expect(service.absUrl()).toBe('https://smarteditabsoluteurl');
    });

    describe('"routeChangeSuccess"', () => {
        it(
            routerCallbackDescription,
            fakeAsync(() => {
                service.routeChangeSuccess().subscribe((event) => {
                    expect(event instanceof NavigationEnd).toBe(true);
                });

                const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
                callback(new NavigationEnd(null, '/', null));
            })
        );

        it('WHEN router emits an event with url equal null THEN it should not invoke given callback when route change success', () => {
            const handler = jasmine.createSpy();
            service.routeChangeSuccess().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationEnd(null, null, null));

            expect(handler).not.toHaveBeenCalled();
        });

        it('WHEN router emits an event with url does not start with "/" THEN it should invoke given callback as well', () => {
            const handler = jasmine.createSpy();
            service.routeChangeSuccess().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationEnd(null, '/navigation', null));

            expect(handler).toHaveBeenCalled();
        });

        it('WHEN router emits an event with url equal the last router url THEN it should not invoke given callback', fakeAsync(() => {
            service.routeChangeSuccess().subscribe((event) => {
                expect(event instanceof NavigationEnd).toBe(true);
            });

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationEnd(null, '/', null));

            const handler = jasmine.createSpy();
            service.routeChangeSuccess().subscribe(handler);

            callback(new NavigationEnd(null, '/', null));
            expect(handler).not.toHaveBeenCalled();
        }));
    });

    describe('"routeChangeStart"', () => {
        it(
            routerCallbackDescription,
            fakeAsync(() => {
                service.routeChangeStart().subscribe((event) => {
                    expect(event instanceof NavigationStart).toBe(true);
                });

                const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
                callback(new NavigationStart(null, '/', null));
            })
        );

        it('WHEN router emits an event with url equal null THEN it should not invoke given callback when route change start', () => {
            const handler = jasmine.createSpy();
            service.routeChangeStart().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationStart(null, null, null));

            expect(handler).not.toHaveBeenCalled();
        });

        it('WHEN router emits an event with url does not start with "/" THEN it should invoke given callback', () => {
            const handler = jasmine.createSpy();
            service.routeChangeStart().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationStart(null, '/navigation', null));

            expect(handler).toHaveBeenCalled();
        });
    });

    describe('"routeChangeError"', () => {
        it(
            routerCallbackDescription,
            fakeAsync(() => {
                service.routeChangeError().subscribe((event) => {
                    expect(event instanceof NavigationError).toBe(true);
                });

                const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
                callback(new NavigationError(null, '/ng', null));
            })
        );

        it('WHEN router emits an event with url equal null THEN it should not invoke given callback when route change error', () => {
            const handler = jasmine.createSpy();
            service.routeChangeError().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationError(null, null, null));

            expect(handler).not.toHaveBeenCalled();
        });

        it('WHEN router emits an event with url does not start with "/" THEN it should not invoke given callback', () => {
            const handler = jasmine.createSpy();
            service.routeChangeError().subscribe(handler);

            const callback = (router.events.subscribe as jasmine.Spy).calls.argsFor(0)[0];
            callback(new NavigationError(null, null, null));

            expect(handler).not.toHaveBeenCalled();
        });
    });

    describe('WHEN "reload" method is called', () => {
        it('THEN will reload the current URL by default', () => {
            service.reload().then(() => {
                expect(router.navigateByUrl).toHaveBeenCalledTimes(2);
                expect(router.navigateByUrl.calls.argsFor(1)[0]).toBe(routerPath);
            });
        });

        it('THEN will reload the given URL', () => {
            service.reload('/other').then(() => {
                expect(router.navigateByUrl).toHaveBeenCalledTimes(2);
                expect(router.navigateByUrl.calls.argsFor(1)[0]).toBe('/other');
            });
        });
    });

    describe('WHEN "getCurrentUrlFromEvent" method is called', () => {
        it('with Angular "NavigationEnd" event THEN it will return url from this event', () => {
            const event = new NavigationEnd(null, '/ng/path', null);
            expect(service.getCurrentUrlFromEvent(event)).toEqual('/ng/path');
        });

        it('with Angular "NavigationStart" event THEN it will return url from this event', () => {
            const event = new NavigationStart(null, '/ng/path', null);
            expect(service.getCurrentUrlFromEvent(event)).toEqual('/ng/path');
        });

        it('with Angular "NavigationError" event THEN it will return url from this event', () => {
            const event = new NavigationError(null, '/ng/path', null);
            expect(service.getCurrentUrlFromEvent(event)).toEqual('/ng/path');
        });
    });
});
