/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { noop } from 'lodash';
import {
    CrossFrameEventService,
    GatewayFactory,
    ISharedDataService,
    MessageGateway,
    SmarteditRoutingService,
    STORE_FRONT_CONTEXT,
    WindowUtils
} from 'smarteditcommons';

import { AlertFactory } from 'smarteditcontainer/services/alerts/AlertFactory';
import { HeartBeatService } from 'smarteditcontainer/services/HeartBeatService';
import { getSpyMock, AlertMock } from './mocks/AlertMock';
import { GenericEventer } from './mocks/GenericEventer';

describe('Storefront <-> SmartEdit Heart beat service', () => {
    const mockMessageGateway = new GenericEventer();
    const mockCrossFrameEventService = new GenericEventer();
    let routingService: jasmine.SpyObj<SmarteditRoutingService>;
    let mockRouteChangeSuccess: jasmine.Spy;
    let mockRouteChangeStart: jasmine.Spy;
    let mockAlertFactory: jasmine.SpyObj<AlertFactory>;
    const storefrontPath = STORE_FRONT_CONTEXT;
    let mockDisconnectedAlert: AlertMock;
    let mockReconnectedAlert: AlertMock;
    const MOCK_EVENTS = {
        PAGE_CHANGE: 'PAGE_CHANGE',
        EVENT_STRICT_PREVIEW_MODE_REQUESTED: 'EVENT_STRICT_PREVIEW_MODE_REQUESTED'
    };
    let windowUtils: WindowUtils;
    let heartBeatService: HeartBeatService;
    let sharedDataService: jasmine.SpyObj<ISharedDataService>;

    function createHeartBeatServiceInstance(heartbeatTimeout = 10000) {
        windowUtils = new WindowUtils();
        spyOn(windowUtils, 'runTimeoutOutsideAngular').and.callFake(
            (callback: () => void, timeout: number) => {
                return window.setTimeout(callback, timeout);
            }
        );

        mockRouteChangeStart = jasmine.createSpy();
        mockRouteChangeSuccess = jasmine.createSpy();

        routingService = jasmine.createSpyObj('routingService', [
            'routeChangeSuccess',
            'routeChangeStart',
            'getCurrentUrlFromEvent'
        ]);
        routingService.routeChangeSuccess.and.returnValue({
            subscribe: mockRouteChangeSuccess
        } as any);
        routingService.routeChangeStart.and.returnValue({
            subscribe: mockRouteChangeStart
        } as any);

        // Alerts
        mockDisconnectedAlert = getSpyMock();
        mockReconnectedAlert = getSpyMock();
        mockAlertFactory = jasmine.createSpyObj('alertFactory', ['createInfo']);

        mockAlertFactory.createInfo.and.returnValues(
            mockDisconnectedAlert as any,
            mockReconnectedAlert as any
        );

        // Translate
        const translateMock: jasmine.SpyObj<TranslateService> = jasmine.createSpyObj('$translate', [
            'instant'
        ]);
        translateMock.instant.and.callFake(() => 'some string');

        // Gateway
        const gatewayFactoryMock: jasmine.SpyObj<GatewayFactory> = jasmine.createSpyObj(
            'gatewayFactory',
            ['createGateway']
        );
        gatewayFactoryMock.createGateway.and.returnValue(
            (mockMessageGateway as any) as MessageGateway
        );

        mockCrossFrameEventService.subscribe(MOCK_EVENTS.EVENT_STRICT_PREVIEW_MODE_REQUESTED, noop);

        sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

        sharedDataService.get.and.returnValue(Promise.resolve({}));

        heartBeatService = new HeartBeatService(
            heartbeatTimeout,
            translateMock,
            routingService,
            windowUtils,
            mockAlertFactory,
            (mockCrossFrameEventService as any) as CrossFrameEventService,
            gatewayFactoryMock,
            sharedDataService
        );
    }

    beforeEach(() => {
        jasmine.clock().install();
    });

    afterEach(() => {
        // just to make sure its all cleaned up on window
        heartBeatService.resetAndStop();
    });

    it('On $routeChangeStart will reset and hide all alerts', () => {
        // Given
        createHeartBeatServiceInstance();

        // When
        const callback = mockRouteChangeStart.calls.argsFor(0)[0];
        callback();

        // Then
        expect(mockDisconnectedAlert.shown).toBe(false);
        expect(mockReconnectedAlert.shown).toBe(false);
    });

    it('On timerElapsed reconnecting is shown, then heartBeat event will show reconnected', async () => {
        // Given
        createHeartBeatServiceInstance(20);
        spyOn(window, 'setTimeout').and.callThrough();
        spyOn(mockCrossFrameEventService, 'publish').and.callThrough();
        spyOn(mockCrossFrameEventService, 'subscribe');

        // Then
        expect(mockDisconnectedAlert.shown).toBe(false);
        expect(mockReconnectedAlert.shown).toBe(false);

        // When
        mockCrossFrameEventService.publish(MOCK_EVENTS.PAGE_CHANGE); // trigger very short timer
        await (mockCrossFrameEventService as any).subscribe.calls.argsFor(0)[1];

        jasmine.clock().tick(20);

        // Then disconnected will be shown
        expect(window.setTimeout).toHaveBeenCalled();
        expect(mockDisconnectedAlert.shown).toBe(true);
        expect(mockReconnectedAlert.shown).toBe(false);

        // When - finally we receive a heartbeat...
        mockMessageGateway.publish(HeartBeatService.HEART_BEAT_MSG_ID);
        await (mockCrossFrameEventService as any).subscribe.calls.argsFor(1)[1];

        // Then disconnected should be hidden and reconnected displayed
        expect(mockDisconnectedAlert.shown).toBe(false);
        expect(mockReconnectedAlert.shown).toBe(true);
        expect(mockCrossFrameEventService.publish).toHaveBeenCalledWith(
            MOCK_EVENTS.EVENT_STRICT_PREVIEW_MODE_REQUESTED,
            false
        );

        // When - reconnected time elapses...
        jasmine.clock().tick(3000);

        // Then
        expect(mockReconnectedAlert.shown).toBe(false);
        expect(window.setTimeout).toHaveBeenCalled();
    });

    it('On $routeChangeSuccess will not start heartbeat timer for non-storefront path', () => {
        // Given
        createHeartBeatServiceInstance();
        spyOn(window, 'setTimeout');

        // When
        const callback = mockRouteChangeSuccess.calls.argsFor(0)[0];
        callback(new NavigationEnd(null, '/pages', null));

        // Then
        expect(window.setTimeout).not.toHaveBeenCalled();
    });

    it('On $routeChangeSuccess will start heartbeat timer for storefront path', async () => {
        // Given
        createHeartBeatServiceInstance();
        spyOn(window, 'setTimeout');
        spyOn(mockCrossFrameEventService, 'subscribe');

        // When
        routingService.getCurrentUrlFromEvent.and.returnValue(storefrontPath);
        const callback = mockRouteChangeSuccess.calls.argsFor(0)[0];
        callback(new NavigationEnd(null, storefrontPath, null));

        await (mockCrossFrameEventService as any).subscribe.calls.argsFor(0)[1];

        // Then
        expect(mockDisconnectedAlert.shown).toBe(false);
        expect(mockReconnectedAlert.shown).toBe(false);
        expect(window.setTimeout).toHaveBeenCalled();
    });

    it('On PAGE_CHANGE it will trigger a restart of the heartbeat timer', async () => {
        // Given
        createHeartBeatServiceInstance();
        spyOn(window, 'setTimeout');
        spyOn(mockCrossFrameEventService, 'subscribe');

        // When
        mockCrossFrameEventService.publish(MOCK_EVENTS.PAGE_CHANGE);
        await (mockCrossFrameEventService as any).subscribe.calls.argsFor(0)[1];

        // Then
        expect(mockDisconnectedAlert.shown).toBe(false);
        expect(mockReconnectedAlert.shown).toBe(false);
        expect(window.setTimeout).toHaveBeenCalled();
    });

    it('On Page_Change it will trigger the heartbeat timer with a custom heartBeatTimeoutThreshold provided by the configuration', async () => {
        // Given
        createHeartBeatServiceInstance();
        spyOn(window, 'setTimeout');
        spyOn(mockCrossFrameEventService, 'subscribe');
        sharedDataService.get.and.returnValue(
            Promise.resolve({
                heartBeatTimeoutThreshold: 12000
            })
        );

        // When
        mockCrossFrameEventService.publish(MOCK_EVENTS.PAGE_CHANGE);
        await (mockCrossFrameEventService as any).subscribe.calls.argsFor(0)[1];

        // Then
        expect(window.setTimeout).toHaveBeenCalled();
        expect((window.setTimeout as any).calls.argsFor(0)[1]).toBe(12000);
    });
});
