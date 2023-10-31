import { Cloneable, CloneableEventHandler, CloneableUtils, LogService, Payload, PromiseUtils } from '@smart/utils';
import { WindowUtils } from 'smarteditcommons/utils';
import { SystemEventService } from '../SystemEventService';
export interface IGatewayPostMessageData extends Payload {
    pk: string;
    gatewayId: string;
    eventId: string;
    data: Cloneable;
}
/**
 * The Message Gateway is a private channel that is used to publish and subscribe to events across iFrame
 * boundaries. The gateway uses the W3C-compliant postMessage as its underlying technology. The benefits of
 * the postMessage are that:
 *
 *      <ul>
 *          <li>It works in cross-origin scenarios.</li>
 *          <li>The receiving end can reject messages based on their origins.</li>
 *      </ul>
 *
 * The creation of instances is controlled by the {@link GatewayFactory}.
 * Only one instance can exist for each gateway ID.
 *
 */
export declare class MessageGateway {
    private readonly logService;
    private readonly systemEventService;
    private readonly cloneableUtils;
    private readonly windowUtils;
    private readonly promiseUtils;
    private readonly TIMEOUT_TO_RETRY_PUBLISHING;
    readonly gatewayId: string;
    private readonly PROMISE_ACKNOWLEDGEMENT_EVENT_ID;
    private readonly PROMISE_RETURN_EVENT_ID;
    private readonly SUCCESS;
    private readonly FAILURE;
    private readonly MAX_RETRIES;
    private promisesToResolve;
    /**
     * @param gatewayId The channel identifier
     */
    constructor(logService: LogService, systemEventService: SystemEventService, cloneableUtils: CloneableUtils, windowUtils: WindowUtils, promiseUtils: PromiseUtils, TIMEOUT_TO_RETRY_PUBLISHING: number, gatewayId: string);
    /**
     * Publishes a message across the gateway using the postMessage.
     *
     * The gateway's publish method implements promises. To resolve a
     * publish promise, all listener promises on the side of the channel must resolve. If a failure occurs in the
     * chain, the chain is interrupted and the publish promise is rejected.
     *
     * @param data Message payload
     * @param retries The current number of attempts to publish a message. By default it is 0.
     * @param pk An optional parameter. It is a primary key for the event, which is generated after
     * the first attempt to send a message.
     */
    publish<Tin extends Cloneable, Tout extends Cloneable>(eventId: string, _data: Tin, retries?: number, pk?: string): Promise<void | Tout>;
    /**
     * Registers a given callback function to the given event ID.
     *
     * @param callback Callback function to be invoked
     * @returns The function to call in order to unsubscribe the event listening
     */
    subscribe<T extends Cloneable>(eventId: string, callback: CloneableEventHandler<T>): () => void;
    processEvent(event: IGatewayPostMessageData): Promise<any>;
    private _processEvent;
    private _processPromiseReturnEvent;
    private _setTimeout;
    private _generateIdentifier;
    private _getSystemEventId;
}
