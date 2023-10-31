/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Cloneable, CloneableEventHandler } from '@smart/utils';
import { IGatewayPostMessageData, MessageGateway } from './gateway';
import { GatewayFactory } from './gateway/GatewayFactory';

@Injectable()
export class SmarteditBootstrapGateway {
    private readonly instance: MessageGateway;

    constructor(gatewayFactory: GatewayFactory) {
        this.instance = this.instance || gatewayFactory.createGateway('smartEditBootstrap');
    }

    getInstance(): MessageGateway {
        return this.instance;
    }

    subscribe<T extends Cloneable>(
        eventId: string,
        callback: CloneableEventHandler<T>
    ): () => void {
        return this.getInstance().subscribe(eventId, callback);
    }

    publish<Tin extends Cloneable, Tout extends Cloneable>(
        eventId: string,
        _data: Tin,
        retries = 0,
        pk?: string
    ): Promise<void | Tout> {
        return this.getInstance().publish(eventId, _data, retries, pk);
    }

    processEvent(event: IGatewayPostMessageData): Promise<any> {
        return this.getInstance().processEvent(event);
    }
}
