import { Cloneable, CloneableEventHandler, IEventService } from '@smart/utils';
import { WindowUtils } from 'smarteditcommons/utils';
import { MessageGateway } from '../gateway/MessageGateway';
import { SystemEventService } from '../SystemEventService';
/**
 * The Cross Frame Event Service is responsible for publishing and subscribing events within and between frames.
 *
 * It uses {@link GatewayFactory} and {@link SystemEventService} to transmit events.
 */
export declare class CrossFrameEventService implements IEventService {
    private readonly systemEventService;
    private readonly crossFrameEventServiceGateway;
    private readonly windowUtils;
    constructor(systemEventService: SystemEventService, crossFrameEventServiceGateway: MessageGateway, windowUtils: WindowUtils);
    /**
     * Publishes an event within and across the gateway.
     *
     * The publish method is used to send events using [publishAsync]{@link SystemEventService#publishAsync}
     * and as well send the message across the gateway by using [publish]{@link MessageGateway#publish} of the {@link GatewayFactory}.
     */
    publish(eventId: string, data?: any): Promise<any[]>;
    /**
     * Subscribe to an event across both frames.
     *
     * The subscribe method is used to register for listening to events using subscribe method of
     * {@link SystemEventService} and as well send the registration message across the gateway by using
     * [subscribe]{@link MessageGateway#subscribe} of the {@link GatewayFactory}.
     *
     * @param handler Callback function to be invoked
     * @returns The function to call in order to unsubscribe the event listening.
     * This will unsubscribe both from the systemEventService and the crossFrameEventServiceGatway.
     */
    subscribe<T extends Cloneable>(eventId: string, handler: CloneableEventHandler<T>): () => void;
    isIframe(): boolean;
}
