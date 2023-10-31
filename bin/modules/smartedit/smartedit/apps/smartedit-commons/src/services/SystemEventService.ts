/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IEventService, LogService, PromiseUtils, TypedMap } from '@smart/utils';

export type EventHandler = (eventId: string, eventData?: any) => Promise<any> | any;

/**
 * Used to transmit events synchronously or asynchronously. It is supported by the SmartEdit [gatewayFactory]{@link GatewayFactory} to propagate events between SmartEditContainer and SmartEdit.
 * It also contains options to publish events, as well as subscribe the event handlers.
 */
@Injectable()
export class SystemEventService implements IEventService {
    private _eventHandlers: TypedMap<EventHandler[]> = {};

    constructor(
        private readonly logService: LogService,
        private readonly promiseUtils: PromiseUtils
    ) {}

    /**
     * Send the event with data synchronously.
     *
     * @returns A promise with resolved data of last subscriber or with the rejected error reason
     */
    publish(eventId: string, data?: any): Promise<any> {
        if (!eventId) {
            this.logService.error('Failed to send event. No event ID provided for data: ' + data);
        } else {
            if (this._eventHandlers[eventId] && this._eventHandlers[eventId].length > 0) {
                return this._invokeEventHandlers(eventId, data);
            }
        }
        return Promise.resolve();
    }

    /**
     * Send the event with data asynchronously.
     */
    publishAsync(eventId: string, data?: any): Promise<any> {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                this.publish(eventId, data).then(
                    (resolvedData: any) => resolve(resolvedData),
                    (reason: any) => reject(reason)
                );
            }, 0);
        });
    }

    /**
     * Method to subscribe the event handler given the eventId and handler
     *
     * @param handler The event handler, a callback function which can either return a promise or directly a value.
     *
     * @returns Function to unsubscribe the event handler
     */
    subscribe(eventId: string, handler: EventHandler): () => void {
        let unsubscribeFn: () => void;
        if (!eventId || !handler) {
            this.logService.error('Failed to subscribe event handler for event: ' + eventId);
        } else {
            // create handlers array for this event if not already created
            if (this._eventHandlers[eventId] === undefined) {
                this._eventHandlers[eventId] = [];
            }
            this._eventHandlers[eventId].push(handler);

            unsubscribeFn = (): void => {
                this._unsubscribe(eventId, handler);
            };
        }
        return unsubscribeFn;
    }

    /**
     * @internal
     */
    private _unsubscribe(eventId: string, handler: EventHandler): void {
        const handlersArray = this._eventHandlers[eventId];
        const index = handlersArray ? this._eventHandlers[eventId].indexOf(handler) : -1;
        if (index >= 0) {
            this._eventHandlers[eventId].splice(index, 1);
        } else {
            this.logService.warn(
                `Attempting to remove event handler for ${eventId} but handler not found.`
            );
        }
    }

    /**
     * @internal
     */
    private _invokeEventHandlers(eventId: string, data?: any): Promise<any> {
        return Promise.all(
            this._eventHandlers[eventId].map((eventHandler: EventHandler) => {
                const promiseClosure = this.promiseUtils.toPromise(eventHandler);
                return promiseClosure(eventId, data);
            })
        ).then(
            (results: any) => Promise.resolve(results.pop()),
            (reason: any) => Promise.reject(`eventId: ${eventId}, reason: ${reason}`)
        );
    }
}
