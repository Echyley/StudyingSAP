import { IEventService, LogService, PromiseUtils } from '@smart/utils';
export declare type EventHandler = (eventId: string, eventData?: any) => Promise<any> | any;
/**
 * Used to transmit events synchronously or asynchronously. It is supported by the SmartEdit [gatewayFactory]{@link GatewayFactory} to propagate events between SmartEditContainer and SmartEdit.
 * It also contains options to publish events, as well as subscribe the event handlers.
 */
export declare class SystemEventService implements IEventService {
    private readonly logService;
    private readonly promiseUtils;
    private _eventHandlers;
    constructor(logService: LogService, promiseUtils: PromiseUtils);
    /**
     * Send the event with data synchronously.
     *
     * @returns A promise with resolved data of last subscriber or with the rejected error reason
     */
    publish(eventId: string, data?: any): Promise<any>;
    /**
     * Send the event with data asynchronously.
     */
    publishAsync(eventId: string, data?: any): Promise<any>;
    /**
     * Method to subscribe the event handler given the eventId and handler
     *
     * @param handler The event handler, a callback function which can either return a promise or directly a value.
     *
     * @returns Function to unsubscribe the event handler
     */
    subscribe(eventId: string, handler: EventHandler): () => void;
    /**
     * @internal
     */
    private _unsubscribe;
    /**
     * @internal
     */
    private _invokeEventHandlers;
}
