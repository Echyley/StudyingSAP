import { Cloneable, CloneableEventHandler } from '@smart/utils';
import { IGatewayPostMessageData, MessageGateway } from './gateway';
import { GatewayFactory } from './gateway/GatewayFactory';
export declare class SmarteditBootstrapGateway {
    private readonly instance;
    constructor(gatewayFactory: GatewayFactory);
    getInstance(): MessageGateway;
    subscribe<T extends Cloneable>(eventId: string, callback: CloneableEventHandler<T>): () => void;
    publish<Tin extends Cloneable, Tout extends Cloneable>(eventId: string, _data: Tin, retries?: number, pk?: string): Promise<void | Tout>;
    processEvent(event: IGatewayPostMessageData): Promise<any>;
}
