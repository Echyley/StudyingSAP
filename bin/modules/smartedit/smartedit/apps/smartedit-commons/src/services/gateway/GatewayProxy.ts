/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Cloneable, FunctionsUtils, LogService, PromiseUtils, StringUtils } from '@smart/utils';
import { GatewayFactory } from './GatewayFactory';
import { IProxiedService } from './IProxiedService';
import { MessageGateway } from './MessageGateway';

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
@Injectable()
export class GatewayProxy {
    private readonly nonProxiableMethods = [
        'getMethodForVoid',
        'getMethodForSingleInstance',
        'getMethodForArray'
    ];

    constructor(
        private readonly logService: LogService,
        private readonly promiseUtils: PromiseUtils,
        private readonly stringUtils: StringUtils,
        private readonly functionsUtils: FunctionsUtils,
        private readonly gatewayFactory: GatewayFactory
    ) {}

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
    initForService<T extends IProxiedService>(
        service: T,
        methodsSubset?: string[],
        gatewayId?: string
    ): void {
        const gwId = gatewayId || service.gatewayId;

        if (!gwId) {
            this.logService.error(
                `initForService() - service expected to have an associated gatewayId - methodsSubset: ${
                    methodsSubset && methodsSubset.length ? methodsSubset.join(',') : []
                }`
            );
            return null;
        }

        const gateway: MessageGateway = this.gatewayFactory.createGateway(gwId);

        let loopedOver = methodsSubset;
        if (!loopedOver) {
            loopedOver = this.functionsUtils
                .getInstanceMethods(service)
                .filter((key) => !this._isNonProxiableMethod(key));
        }

        loopedOver.forEach((fnName: Extract<keyof T, string>) => {
            if (typeof service[fnName] === 'function') {
                if (this.functionsUtils.isEmpty(service[fnName] as any)) {
                    this._turnToProxy(fnName, service, gateway);
                } else {
                    service[fnName] = this.promiseUtils.toPromise(service[fnName], service) as any;
                    gateway.subscribe(fnName, this._onGatewayEvent.bind(null, fnName, service));
                }
            }
        });
    }

    /** @ignore */
    private _isNonProxiableMethod(key: string): boolean {
        return (
            this.nonProxiableMethods.indexOf(key) > -1 ||
            key.startsWith('$') ||
            key === 'lodash' ||
            key === 'jQuery'
        );
    }

    /** @ignore */
    private _onGatewayEvent<T>(
        fnName: Extract<keyof T, string>,
        service: T,
        eventId: string,
        data: { arguments: Cloneable }
    ): any {
        return (service[fnName] as any).apply(service, data.arguments);
    }

    /** @ignore */
    private _turnToProxy<T>(
        fnName: Extract<keyof T, string>,
        service: T,
        gateway: MessageGateway
    ): void {
        delete service[fnName];

        service[fnName] = ((...args: Cloneable[]) =>
            gateway
                .publish(fnName, {
                    arguments: args
                } as Cloneable)
                .then(
                    (resolvedData: Cloneable) => {
                        if (!this.stringUtils.isBlank(resolvedData)) {
                            delete (resolvedData as any).$resolved;
                            delete (resolvedData as any).$promise;
                        }
                        return resolvedData;
                    },
                    (error: any) => {
                        if (error) {
                            this.logService.debug(
                                `gatewayProxy - publish failed for gateway ${gateway.gatewayId} method ${fnName} and arguments ${args}`
                            );
                        }
                        return Promise.reject(error);
                    }
                )) as any;
    }
}
