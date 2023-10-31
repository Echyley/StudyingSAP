import { TranslateService } from '@ngx-translate/core';
import { CrossFrameEventService, GatewayFactory, ISharedDataService, SmarteditRoutingService, WindowUtils } from 'smarteditcommons';
import { AlertFactory } from './alerts';
export declare class HeartBeatService {
    private HEART_BEAT_TIMEOUT_THRESHOLD_MS;
    private routingService;
    private windowUtils;
    private crossFrameEventService;
    private sharedDataService;
    static readonly HEART_BEAT_GATEWAY_ID = "heartBeatGateway";
    static readonly HEART_BEAT_MSG_ID = "heartBeat";
    private reconnectingAlert;
    private reconnectedAlert;
    private reconnectingInProgress;
    private cancellableTimeoutTimer;
    constructor(HEART_BEAT_TIMEOUT_THRESHOLD_MS: number, translate: TranslateService, routingService: SmarteditRoutingService, windowUtils: WindowUtils, alertFactory: AlertFactory, crossFrameEventService: CrossFrameEventService, gatewayFactory: GatewayFactory, sharedDataService: ISharedDataService);
    /**
     * @internal
     * Hide all alerts and cancel all pending actions and timers.
     */
    resetAndStop: () => void;
    /**
     * @internal
     * Heartbeat received from iframe, show reconnected if connection was previously
     * lost, and restart the timer to wait for iframe heartbeat
     */
    private heartBeatReceived;
    /**
     * Connection to iframe has been lost, show reconnected alert to user
     */
    private connectionLost;
}
