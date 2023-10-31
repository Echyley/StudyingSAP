/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FunctionsUtils, PromiseUtils } from '@smart/utils';
import { noop } from 'lodash';
import {
    CloneableUtils,
    GatewayFactory,
    IGatewayPostMessageData,
    LogService,
    MessageGateway,
    SystemEventService,
    WindowUtils
} from 'smarteditcommons';

let gatewayFactory: GatewayFactory;
let systemEventService: jasmine.SpyObj<SystemEventService>;
let logService: jasmine.SpyObj<LogService>;
let windowUtils: jasmine.SpyObj<WindowUtils>;
let windowMock: jasmine.SpyObj<Window>;
let promiseUtils: PromiseUtils;
let functionsUtils: jasmine.SpyObj<FunctionsUtils>;
let targetFrame: Window;
const trustedUrl = 'https://trusted';

function initTest() {
    systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync', 'subscribe']);
    logService = jasmine.createSpyObj('logService', ['error', 'debug']);
    windowUtils = jasmine.createSpyObj<WindowUtils>('windowUtils', [
        'getWindow',
        'isIframe',
        'getGatewayTargetFrame',
        'getTrustedIframeDomain'
    ]);
    promiseUtils = new PromiseUtils();
    targetFrame = jasmine.createSpyObj<Window>('targetFrame', ['postMessage']);
    functionsUtils = jasmine.createSpyObj<FunctionsUtils>('functionsUtils', ['isUnitTestMode']);
    functionsUtils.isUnitTestMode.and.returnValue(false);

    gatewayFactory = new GatewayFactory(
        logService,
        systemEventService,
        new CloneableUtils(),
        windowUtils,
        promiseUtils,
        functionsUtils
    );
    windowUtils.isIframe.and.returnValue(false);
    windowUtils.getTrustedIframeDomain.and.returnValue(trustedUrl);
    windowMock = jasmine.createSpyObj<Window>('windowMock', ['addEventListener']);
    windowUtils.getWindow.and.returnValue(windowMock);
    windowUtils.getGatewayTargetFrame.and.returnValue(targetFrame);
}

describe('test GatewayFactory and MessageGateway', () => {
    beforeEach(() => {
        initTest();
    });

    it('should attach a W3C postMessage event when addEventListener exists on window', () => {
        gatewayFactory.initListener();

        expect(windowMock.addEventListener).toHaveBeenCalledWith(
            'message',
            jasmine.any(Function),
            false
        );
    });

    describe('GIVEN that the parent frame receives message', () => {
        let gateway: MessageGateway;
        let listener: (e: MessageEvent) => void;
        let processEventSpy: jasmine.Spy;
        const gatewayId = 'test';
        beforeEach(() => {
            windowMock.addEventListener.and.returnValue(null);

            gateway = gatewayFactory.createGateway(gatewayId);

            systemEventService.publishAsync.and.returnValue(Promise.resolve('systemEventService'));

            gatewayFactory.initListener();

            processEventSpy = spyOn<MessageGateway, any>(gateway, 'processEvent').and.returnValue(
                ''
            );

            listener = windowMock.addEventListener.calls.argsFor(0)[1] as any;
        });

        it('SHOULD allow and not process event, GIVEN the domain is qualtrics url', () => {
            const e = {
                origin: 'https://sapinsights.eu.qualtrics.com'
            } as MessageEvent;
            listener(e);
            expect(gateway.processEvent).not.toHaveBeenCalled();
            expect(logService.debug).toHaveBeenCalledWith(
                'qualtrics url https://sapinsights.eu.qualtrics.com should be allowed'
            );
        });

        it("SHOULD have the listener's callback log error and not process event, GIVEN the domain is not same origin as loaded iframe", () => {
            const e = {
                origin: 'https://untrusted'
            } as MessageEvent;
            listener(e);
            expect(gateway.processEvent).not.toHaveBeenCalled();
            expect(logService.error).toHaveBeenCalledWith(
                'disallowed storefront is trying to communicate with smarteditcontainer'
            );
        });

        it("SHOULD have the listener's callback process event of gateway only once, GIVEN url is same origin as loaded iframe and incoming gatewayId is expected", () => {
            const e = {
                data: {
                    pk: 'somepk',
                    gatewayId: 'test'
                },
                origin: trustedUrl
            } as MessageEvent;

            listener(e);

            expect(gateway.processEvent).toHaveBeenCalledWith(e.data);
            expect(logService.error).not.toHaveBeenCalled();

            listener(e);
            expect(processEventSpy.calls.count()).toBe(1);
        });

        it("SHOULD have the listener callback's not process the event of the gateway, GIVEN url is same origin as loaded iframe and incoming gatewayId is not expected", () => {
            const e = {
                data: {
                    pk: 'sometrusteddomain',
                    gatewayId: 'nottest'
                },
                origin: trustedUrl
            } as MessageEvent;
            listener(e);
            expect(gateway.processEvent).not.toHaveBeenCalled();
            expect(logService.error).not.toHaveBeenCalled();
        });
    });

    it('SHOULD return no gateway on subsequent calls to createGateway with the same gateway id', () => {
        const gateway = gatewayFactory.createGateway('TestChannel1');

        const duplicateGateway = gatewayFactory.createGateway('TestChannel1');

        expect(gateway).toBeDefined();
        expect(duplicateGateway).toBeNull();
    });

    it('SHOULD subscribe to the system event service with the event id <gateway_id>:<event_id>', () => {
        const CHANNEL_ID = 'TestChannel';
        const EVENT_ID = 'someEvent';
        const SYSTEM_EVENT_ID = `${CHANNEL_ID}:${EVENT_ID}`;

        const handler = noop;

        const gateway = gatewayFactory.createGateway(CHANNEL_ID);

        gateway.subscribe(EVENT_ID, handler);

        expect(systemEventService.subscribe).toHaveBeenCalledWith(SYSTEM_EVENT_ID, handler);
    });

    describe('publish', () => {
        let gatewayId: string;
        let eventId: string;
        let data: any;
        let gateway: MessageGateway;
        let pk: string;
        let successEvent: any;

        const clock = jasmine.clock();
        beforeEach(() => {
            clock.install();
            gatewayId = 'TestChannel';
            eventId = '_testEvent';
            data = {
                arguments: [
                    {
                        key: 'testKey'
                    }
                ]
            };

            gateway = gatewayFactory.createGateway(gatewayId);

            pk = '1234567890';
            spyOn(gateway as any, '_generateIdentifier').and.returnValue(pk);
            successEvent = {
                eventId: 'promiseReturn',
                data: {
                    pk,
                    type: 'success',
                    resolvedDataOfLastSubscriber: 'someData'
                }
            };
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('SHOULD post a W3C message to the target frame and return a hanging promise', () => {
            const promise = gateway.publish(eventId, data);

            expect(promise).toBeDefined();
            expect(promise.then).toBeDefined();
            expect(promise.then).not.toBeEmptyFunction();
        });

        it('SHOULD return a promise from publish that is resolved to event.data.resolvedDataOfLastSubscriber when incoming success promiseReturn with same pk', (done) => {
            const promise = gateway.publish(eventId, data);

            gateway.processEvent(successEvent);

            promise.then(
                () => done(),
                (error) => fail('should have resolved' + JSON.stringify(error))
            );
        });

        it('SHOULD return a promise from publish that is rejected WHEN incoming failure promiseReturn with same pk', (done) => {
            const promise = gateway.publish(eventId, data);

            const failureEvent = {
                pk,
                gatewayId,
                eventId: 'promiseReturn',
                data: {
                    pk,
                    type: 'failure'
                }
            } as IGatewayPostMessageData;

            gateway.processEvent(failureEvent);

            promise.then(
                () => fail('should have reject'),
                () => done()
            );
        });

        it('SHOULD return a promise from publish that is still hanging WHEN incoming promiseReturn with different pk', () => {
            const promise = gateway.publish(eventId, data);
            const randomPk = 'fgsdfgssf';

            const differentEvent = {
                gatewayId,
                pk: randomPk,
                eventId: 'promiseReturn',
                data: {
                    pk: randomPk,
                    type: 'success',
                    resolvedDataOfLastSubscriber: 'someData'
                }
            };
            gateway.processEvent(differentEvent);

            expect(promise).toBeDefined();
            expect(promise.then).toBeDefined();
            expect(promise.then).not.toBeEmptyFunction();
        });

        it('SHOULD return a rejected promise even when there is no target frame', (done) => {
            windowUtils.getGatewayTargetFrame.and.returnValue(null);
            const promise = gateway.publish(eventId, data);

            gateway.processEvent(successEvent);

            promise.then(
                () => fail('should have reject'),
                () => done()
            );
        });

        it('SHOULD reject a promise after retrying publish for 5 times', (done) => {
            const publishSpy = spyOn<MessageGateway, any>(gateway, 'publish').and.callThrough();

            gateway.publish(eventId, data).then(
                () => fail('should have rejected'),
                () => {
                    expect(publishSpy.calls.count()).toBe(6);

                    expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 1, pk);
                    expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 2, pk);
                    expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 3, pk);
                    expect(gateway.publish).toHaveBeenCalledWith(eventId, data, 4, pk);
                    done();
                }
            );
            // After calling publish method, we do not want to wait for the implementation setTimeout ms.
            // Instead it should be resolved immediately without slowing the test.
            // 6 timeouts
            clock.tick(3000);
        });
    });
});

describe('processEvent', () => {
    let gateway: MessageGateway;
    let event: any;

    beforeEach(() => {
        initTest();

        gateway = gatewayFactory.createGateway('TestChannel');
        event = {
            pk: 'rlktqnvghsliutergwe',
            eventId: 'someEvent',
            data: {
                key1: 'abc'
            }
        };
    });

    it(
        "SHOULD be different from 'promiseReturn' and 'promiseAcknowledgement' will call " +
            'systemEventService.publishAsync and publish a success promiseReturn event with the last resolved data from subscribers',
        (done) => {
            systemEventService.publishAsync.and.returnValue(Promise.resolve('someResolvedData'));

            spyOn(gateway, 'publish').and.returnValue(Promise.resolve());

            gateway.processEvent(event).then(() => {
                expect(systemEventService.publishAsync).toHaveBeenCalledWith(
                    'TestChannel:someEvent',
                    {
                        key1: 'abc'
                    }
                );

                expect(gateway.publish).toHaveBeenCalledWith('promiseReturn', {
                    pk: 'rlktqnvghsliutergwe',
                    type: 'success',
                    resolvedDataOfLastSubscriber: 'someResolvedData'
                });

                done();
            });
        }
    );

    it("SHOULD be different from 'promiseReturn' and 'promiseAcknowldgement' will call systemEventService.publishAsync and publish a failure promiseReturn event", (done) => {
        systemEventService.publishAsync.and.returnValue(Promise.reject('some reason'));

        spyOn(gateway, 'publish').and.returnValue(Promise.resolve());

        gateway.processEvent(event).then(() => {
            expect(systemEventService.publishAsync).toHaveBeenCalledWith('TestChannel:someEvent', {
                key1: 'abc'
            });

            expect(gateway.publish).toHaveBeenCalledWith('promiseReturn', {
                pk: 'rlktqnvghsliutergwe',
                type: 'failure',
                rejectedDataOfLastSubscriber: 'some reason'
            });
            done();
        });
    });
});
