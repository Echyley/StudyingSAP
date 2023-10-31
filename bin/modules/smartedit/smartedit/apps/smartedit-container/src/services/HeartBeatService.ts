/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    Alert,
    CrossFrameEventService,
    EVENT_STRICT_PREVIEW_MODE_REQUESTED,
    EVENTS,
    GatewayFactory,
    HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN,
    IConfiguration,
    ISharedDataService,
    MessageGateway,
    SmarteditRoutingService,
    STORE_FRONT_CONTEXT,
    WindowUtils
} from 'smarteditcommons';
import { HeartBeatAlertComponent } from '../components/heartBeat/HeartBeatAlertComponent';

import { AlertFactory } from './alerts';

/* @internal */
@Injectable()
export class HeartBeatService {
    static readonly HEART_BEAT_GATEWAY_ID = 'heartBeatGateway';
    static readonly HEART_BEAT_MSG_ID = 'heartBeat';

    private reconnectingAlert: Alert;
    private reconnectedAlert: Alert;

    private reconnectingInProgress = false;
    private cancellableTimeoutTimer: number;

    constructor(
        @Inject(HEART_BEAT_TIMEOUT_THRESHOLD_MS_TOKEN)
        private HEART_BEAT_TIMEOUT_THRESHOLD_MS: number,
        translate: TranslateService,
        private routingService: SmarteditRoutingService,
        private windowUtils: WindowUtils,
        alertFactory: AlertFactory,
        private crossFrameEventService: CrossFrameEventService,
        gatewayFactory: GatewayFactory,
        private sharedDataService: ISharedDataService
    ) {
        this.reconnectingAlert = alertFactory.createInfo({
            component: HeartBeatAlertComponent,
            duration: -1,
            dismissible: false,
            minWidth: '',
            mousePersist: true,
            width: '300px'
        });

        this.reconnectedAlert = alertFactory.createInfo({
            message: translate.instant('se.heartbeat.reconnection'),
            duration: 1000,
            minWidth: '',
            mousePersist: true,
            dismissible: true,
            width: '300px'
        });

        const heartBeatGateway: MessageGateway = gatewayFactory.createGateway(
            HeartBeatService.HEART_BEAT_GATEWAY_ID
        );
        heartBeatGateway.subscribe(HeartBeatService.HEART_BEAT_MSG_ID, () =>
            this.heartBeatReceived()
        );

        this.crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
            this.resetAndStop();
            // assume every page is smarteditable ¯\_(ツ)_/¯
            return this.heartBeatReceived();
        });

        this.routingService.routeChangeSuccess().subscribe((event) => {
            const url = this.routingService.getCurrentUrlFromEvent(event);
            if (url === STORE_FRONT_CONTEXT) {
                return this.heartBeatReceived();
            }
            return Promise.resolve();
        });

        this.routingService.routeChangeStart().subscribe(() => {
            this.resetAndStop();
        });
    }

    /**
     * @internal
     * Hide all alerts and cancel all pending actions and timers.
     */
    public resetAndStop = (): void => {
        this.reconnectingInProgress = false;
        if (this.cancellableTimeoutTimer) {
            clearTimeout(this.cancellableTimeoutTimer);
            this.cancellableTimeoutTimer = null;
        }
        this.reconnectingAlert.hide();
        this.reconnectedAlert.hide();
    };

    /**
     * @internal
     * Heartbeat received from iframe, show reconnected if connection was previously
     * lost, and restart the timer to wait for iframe heartbeat
     */
    private heartBeatReceived(): Promise<void> {
        const reconnecting = this.reconnectingInProgress;
        this.resetAndStop();
        if (reconnecting) {
            if (!!this.windowUtils.getGatewayTargetFrame()) {
                this.reconnectedAlert.show();
            }
            this.reconnectingInProgress = false;

            // Publish an event to enable the perspective selector in case if it is disabled
            this.crossFrameEventService.publish(EVENT_STRICT_PREVIEW_MODE_REQUESTED, false);
        }

        return this.sharedDataService
            .get('configuration')
            .then(({ heartBeatTimeoutThreshold }: IConfiguration) => {
                if (!heartBeatTimeoutThreshold) {
                    heartBeatTimeoutThreshold = this.HEART_BEAT_TIMEOUT_THRESHOLD_MS;
                }

                this.cancellableTimeoutTimer = this.windowUtils.runTimeoutOutsideAngular(
                    this.connectionLost,
                    +heartBeatTimeoutThreshold
                );
            });
    }

    /**
     * Connection to iframe has been lost, show reconnected alert to user
     */
    private connectionLost = (): void => {
        this.resetAndStop();
        if (!!this.windowUtils.getGatewayTargetFrame()) {
            this.reconnectingAlert.show();
        }
        this.reconnectingInProgress = true;
    };
}
