/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { stringUtils, FunctionsUtils, PromiseUtils } from '@smart/utils';
import { GatewayFactory, GatewayProxy, LogService, MessageGateway } from 'smarteditcommons';

describe('test gatewayProxy', () => {
    const gatewayId = 'toolbar';
    let logService: jasmine.SpyObj<LogService>;
    const functionsUtils: FunctionsUtils = new FunctionsUtils();
    let promiseUtils: PromiseUtils;
    let toPromiseSpy: jasmine.Spy;
    let gatewayFactory: jasmine.SpyObj<GatewayFactory>;
    let gateway: jasmine.SpyObj<MessageGateway>;
    let gatewayProxy: GatewayProxy;

    beforeEach(() => {
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway', 'initListener']);
        gateway = jasmine.createSpyObj('gateway', ['publish', 'subscribe']);
        gatewayFactory.createGateway.and.returnValue(gateway);

        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        promiseUtils = new PromiseUtils();
        toPromiseSpy = spyOn(promiseUtils, 'toPromise').and.callThrough();

        gatewayProxy = new GatewayProxy(
            logService,
            promiseUtils,
            stringUtils,
            functionsUtils,
            gatewayFactory
        );
    });

    it('gatewayProxy will proxy empty functions and subscribe listeners for non empty functions now returning promises resolving to the return value of the method', (done) => {
        const service = {
            gatewayId,
            methodToBeProxied: () => void 0,
            method2ToBeProxied: () => void 0,
            methodToBeRemotelyInvokable(arg: string) {
                return arg + 'Suffix';
            },
            method2ToBeRemotelyInvokable(arg: string) {
                return arg + 'Suffix2';
            }
        };
        toPromiseSpy.calls.reset();

        gateway.publish.and.returnValue(promiseUtils.defer().promise as any);

        gatewayProxy.initForService(service);

        expect(gatewayFactory.createGateway).toHaveBeenCalledWith(gatewayId);

        expect(toPromiseSpy.calls.count()).toBe(2);

        expect(gateway.subscribe).toHaveBeenCalledWith(
            'methodToBeRemotelyInvokable',
            jasmine.any(Function)
        );
        expect(gateway.subscribe).toHaveBeenCalledWith(
            'method2ToBeRemotelyInvokable',
            jasmine.any(Function)
        );

        (service.methodToBeRemotelyInvokable('anything') as any).then(
            function (data: string) {
                expect(data).toBe('anythingSuffix');
                done();
            },
            () => {
                fail();
            }
        );

        expect(gateway.publish).not.toHaveBeenCalled();

        service.methodToBeProxied();

        expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
            arguments: []
        });

        service.method2ToBeProxied();

        expect(gateway.publish).toHaveBeenCalledWith('method2ToBeProxied', {
            arguments: []
        });
    });

    it('gatewayProxy will proxy empty functions and subscribe listeners for a subset of methods', (done) => {
        const service = {
            gatewayId,
            methodToBeProxied: () => void 0,
            method2ToBeProxied: () => void 0,
            methodToBeRemotelyInvokable(arg: string) {
                return arg + 'Suffix';
            },
            method2ToBeRemotelyInvokable(arg: string) {
                return arg + 'Suffix2';
            }
        };

        gateway.publish.and.returnValue(promiseUtils.defer().promise as any);
        gatewayProxy.initForService(service, ['methodToBeProxied', 'methodToBeRemotelyInvokable']);

        expect(gatewayFactory.createGateway).toHaveBeenCalledWith(gatewayId);
        expect(gateway.subscribe).toHaveBeenCalledWith(
            'methodToBeRemotelyInvokable',
            jasmine.any(Function)
        );
        expect(gateway.subscribe).not.toHaveBeenCalledWith(
            'method2ToBeRemotelyInvokable',
            jasmine.any(Function)
        );

        (service.methodToBeRemotelyInvokable('anything') as any).then(
            function (data: string) {
                expect(data).toBe('anythingSuffix');
                done();
            },
            () => {
                fail();
            }
        );

        expect(gateway.publish).not.toHaveBeenCalled();

        service.methodToBeProxied();

        expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
            arguments: []
        });

        service.method2ToBeProxied();

        expect(gateway.publish).not.toHaveBeenCalledWith('method2ToBeProxied', {
            arguments: []
        });
    });

    it('gatewayProxy will proxy empty functions and will wrap the value returned by the proxy in a promise', (done) => {
        const expectedReturnValue = 'This is a return value';
        const service = {
            gatewayId,
            methodToBeProxied(str1: string, str2: string): any {
                'proxyFunction';
                return null;
            }
        };

        gateway.publish.and.returnValue(Promise.resolve(expectedReturnValue));
        gatewayProxy.initForService(service);

        // Act
        service.methodToBeProxied('arg1', 'arg2').then(
            (value: any) => {
                expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
                    arguments: ['arg1', 'arg2']
                });

                expect(value).toEqual(expectedReturnValue);
                done();
            },
            () => {
                fail('service.methodToBeProxied should not have rejected');
            }
        );
    });

    it('gatewayProxy will proxy empty functions and will wrap an undefined value in a promise if the remote method returns void', (done) => {
        // Arrange
        const service = {
            gatewayId,
            methodToBeProxied(str1: string, str2: string): any {
                'proxyFunction';
                return null;
            }
        };

        gateway.publish.and.returnValue(Promise.resolve());
        gatewayProxy.initForService(service);

        // Act
        service.methodToBeProxied('arg1', 'arg2').then(
            (value: any) => {
                expect(value).toEqual(undefined);
                expect(gateway.publish).toHaveBeenCalledWith('methodToBeProxied', {
                    arguments: ['arg1', 'arg2']
                });
                done();
            },
            () => {
                fail('service.methodToBeProxied should not have rejected');
            }
        );
    });

    it('gatewayProxy will wrap the result of non-empty functions in a promise', (done) => {
        // Arrange
        const providedArg = 'some argument';
        const expectedResult = 'some result';
        const service = {
            gatewayId,
            methodToBeRemotelyInvokable(arg: string) {
                return expectedResult + arg;
            }
        };

        gatewayProxy.initForService(service);
        const eventID = gateway.subscribe.calls.argsFor(0)[0];
        const onGatewayEvent = gateway.subscribe.calls.argsFor(0)[1];

        // Act
        onGatewayEvent(eventID, {
            arguments: [providedArg]
        }).then(
            (value: any) => {
                expect(value).toEqual(expectedResult + providedArg);
                done();
            },
            () => {
                fail('onGatewayEvent should not have rejected');
            }
        );
    });
});
