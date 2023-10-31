import { FunctionsUtils, LogService, PromiseUtils, StringUtils } from '@smart/utils';
import { GatewayFactory } from './GatewayFactory';
import { IProxiedService } from './IProxiedService';
/**
 * To seamlessly integrate the gateway factory between two services on different frames, you can use a gateway
 * proxy. The gateway proxy service simplifies using the gateway module by providing an API that registers an
 * instance of a service that requires a gateway for communication.
 *
 * This registration process automatically attaches listeners to each of the service's functions (turned into promises), allowing stub
 * instances to forward calls to these functions using an instance of a gateway from {@link GatewayFactory}.
 * Any function that has an empty body declared on the service is used as a proxy function.
 * It delegates a publish call to the gateway under the same function name, and wraps the result of the call in a Promise.
 */
export declare class GatewayProxy {
    private readonly logService;
    private readonly promiseUtils;
    private readonly stringUtils;
    private readonly functionsUtils;
    private readonly gatewayFactory;
    private readonly nonProxiableMethods;
    constructor(logService: LogService, promiseUtils: PromiseUtils, stringUtils: StringUtils, functionsUtils: FunctionsUtils, gatewayFactory: GatewayFactory);
    /**
     * Mutates the given service into a proxied service.
     * You must provide a unique string gatewayId, in one of 2 ways.
     *
     *
     * 1) Having a gatewayId property on the service provided
     *
     *
     * OR
     *
     *
     * 2) providing a gatewayId as 3rd param of this function
     *
     * @param service Service to mutate into a proxied service.
     * @param methodsSubset An explicit set of methods on which the gatewayProxy will trigger. Otherwise, by default all functions
     * will be proxied. This is particularly useful to avoid inner methods being unnecessarily turned into promises.
     * @param gatewayId The gateway ID to use internaly for the proxy. If not provided, the service <strong>must</strong> have a gatewayId property.
     */
    initForService<T extends IProxiedService>(service: T, methodsSubset?: string[], gatewayId?: string): void;
    /** @ignore */
    private _isNonProxiableMethod;
    /** @ignore */
    private _onGatewayEvent;
    /** @ignore */
    private _turnToProxy;
}
