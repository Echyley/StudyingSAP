import { InjectionToken } from '@angular/core';
import { GatewayFactory } from '../gateway/GatewayFactory';
/**
 * @internal
 * @ignore
 */
export declare class CrossFrameEventServiceGateway {
    static crossFrameEventServiceGatewayToken: InjectionToken<string>;
    constructor(gatewayFactory: GatewayFactory);
}
