import { MessageGateway } from '../gateway';
import { GatewayFactory } from '../gateway/GatewayFactory';
/**
 * @internal
 * @ignore
 */
export declare class LanguageServiceGateway {
    static instance: MessageGateway;
    constructor(gatewayFactory: GatewayFactory);
    getInstance(): MessageGateway;
}
