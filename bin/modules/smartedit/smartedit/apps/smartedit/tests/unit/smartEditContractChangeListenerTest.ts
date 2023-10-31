/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { noop } from 'rxjs';
import { SmartEditContractChangeListener } from 'smartedit/services/SmartEditContractChangeListener';
import {
    promiseUtils,
    ComponentEntry,
    COMPONENT_CLASS,
    CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS,
    CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS,
    ID_ATTRIBUTE,
    IPageInfoService,
    IPositionRegistry,
    IResizeListener,
    JQueryUtilsService,
    LogService,
    PolyfillService,
    SystemEventService,
    TypedMap,
    UUID_ATTRIBUTE,
    IComponentHandlerService,
    IPageTreeNodeService
} from 'smarteditcommons';
const INITIAL_PAGE_UUID = 'INITIAL_PAGE_UUID';
const ANY_PAGE_UUID = 'ANY_PAGE_UUID';
const BODY_TAG = 'body';
const BODY = {};
describe('smartEditContractChangeListener in polyfill mode', () => {
    const logService = new LogService();
    let systemEventService: SystemEventService;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let pageinfoService: jasmine.SpyObj<IPageInfoService>;
    let yjQuery: any;
    const SMARTEDIT_COMPONENT_PROCESS_STATUS = 'smartEditComponentProcessStatus';
    let isInExtendedViewPort: jasmine.Spy;
    let smartEditContractChangeListener: SmartEditContractChangeListener;
    let mutationObserverMock: jasmine.SpyObj<MutationObserver>;
    let intersectionObserverMock: jasmine.SpyObj<IntersectionObserver>;
    let mutationObserverCallback: any;
    let intersectionObserverCallback: any;
    let onComponentRepositionedCallback: jasmine.Spy;
    let onComponentResizedCallback: (component: HTMLElement) => void;
    let onComponentsAddedCallback: jasmine.Spy;
    let onComponentsRemovedCallback: jasmine.Spy;
    let onComponentChangedCallback: jasmine.Spy;
    let resizeListener: jasmine.SpyObj<IResizeListener>;
    let positionRegistry: jasmine.SpyObj<IPositionRegistry>;
    let runIntersectionObserver: (queue: IntersectionObserverEntry[]) => Promise<any>;
    let parent: jasmine.SpyObj<HTMLElement>;
    let component0: jasmine.SpyObj<HTMLElement>;
    let component1: jasmine.SpyObj<HTMLElement>;
    let component2: jasmine.SpyObj<HTMLElement>;
    let component21: jasmine.SpyObj<HTMLElement>;
    let component3: jasmine.SpyObj<HTMLElement>;
    let invisibleComponent: jasmine.SpyObj<HTMLElement>;
    let nonProcessableComponent: jasmine.SpyObj<HTMLElement>;
    let detachedComponent: jasmine.SpyObj<HTMLElement>;
    let polyfillService: jasmine.SpyObj<PolyfillService>;
    let yjQueryUtilsService: jasmine.SpyObj<JQueryUtilsService>;
    let pageTreeNodeService: jasmine.SpyObj<IPageTreeNodeService>;

    const holder: any = {};

    let SECOND_LEVEL_CHILDREN: jasmine.SpyObj<HTMLElement>[];
    let INTERSECTIONS_MAPPING: IntersectionObserverEntry[];

    async function playMutationObserverCallback(mutations: any) {
        mutationObserverCallback(mutations);

        await promiseUtils.sleep(4);
    }

    const getComponents = () => {
        return Array.from(
            (smartEditContractChangeListener as any).componentsQueue.values(),
            ({ component }) => component
        );
    };

    beforeEach(() => {
        systemEventService = new SystemEventService(logService, promiseUtils);

        (window as any).elementResizeDetectorMaker = () => {
            return {
                uninstall: noop
            };
        };

        polyfillService = jasmine.createSpyObj('polyfillService', ['isEligibleForExtendedView']);
        polyfillService.isEligibleForExtendedView.and.returnValue(true);

        isInExtendedViewPort = jasmine.createSpy('isInExtendedViewPort');

        // we here give isInExtendedViewPort the same beahviour as isIntersecting
        isInExtendedViewPort.and.callFake((element: HTMLElement) => {
            const obj = INTERSECTIONS_MAPPING.find((object) => {
                return object.target === element;
            });
            return obj ? obj.isIntersecting : false;
        });

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', [
            'getClosestSmartEditComponent',
            'isSmartEditComponent',
            'getFirstSmartEditComponentChildren',
            'getParent'
        ]);
        pageinfoService = jasmine.createSpyObj('pageinfoService', ['getPageUUID']);

        resizeListener = jasmine.createSpyObj('resizeListener', [
            'register',
            'unregister',
            'fix',
            'dispose',
            'init'
        ]);
        positionRegistry = jasmine.createSpyObj('positionRegistry', [
            'register',
            'unregister',
            'getRepositionedComponents',
            'dispose'
        ]);

        positionRegistry.getRepositionedComponents.and.returnValue([]);

        const contains = jasmine.createSpy('contains');
        yjQuery = jasmine.createSpy('yjQuery');
        yjQuery.contains = contains;

        yjQuery.contains.and.callFake((container: any, element: any) => {
            return element.name !== (detachedComponent as any).name;
        });

        yjQuery.fn = {
            extend() {
                return;
            }
        };

        yjQueryUtilsService = jasmine.createSpyObj('yjQueryUtilsService', [
            'isInExtendedViewPort',
            'isInDOM'
        ]);

        yjQueryUtilsService.isInExtendedViewPort.and.callFake((element: HTMLElement) => {
            const obj = INTERSECTIONS_MAPPING.find((object) => {
                return object.target === element;
            });
            return obj ? obj.isIntersecting : false;
        });

        yjQueryUtilsService.isInDOM.and.callFake((element: any) => {
            return element.name !== (detachedComponent as any).name;
        });

        pageTreeNodeService = jasmine.createSpyObj('pageTreeNodeService', [
            'updateSlotNodes',
            'handleBodyWidthChange'
        ]);
    });

    beforeEach(() => {
        smartEditContractChangeListener = new SmartEditContractChangeListener(
            yjQueryUtilsService,
            componentHandlerService,
            pageinfoService,
            resizeListener,
            positionRegistry,
            logService,
            yjQuery,
            systemEventService,
            polyfillService,
            pageTreeNodeService
        );

        mutationObserverMock = jasmine.createSpyObj('MutationObserver', ['observe', 'disconnect']);
        spyOn(smartEditContractChangeListener, '_newMutationObserver').and.callFake(function (
            callback: jasmine.SpyObj<MutationCallback>
        ) {
            mutationObserverCallback = callback;
            this.observe = noop;
            this.disconnect = noop;
            return mutationObserverMock;
        });

        intersectionObserverMock = jasmine.createSpyObj('IntersectionObserver', [
            'observe',
            'unobserve',
            'disconnect'
        ]);
        spyOn(smartEditContractChangeListener, '_newIntersectionObserver').and.callFake(
            (callback: jasmine.SpyObj<IntersectionObserverCallback>) => {
                intersectionObserverCallback = callback;
                return intersectionObserverMock;
            }
        );

        intersectionObserverMock.observe.and.callFake((comp: HTMLElement) => {
            // run time intersectionObserver would indeed trigger a callback immediately after observing
            intersectionObserverCallback(
                INTERSECTIONS_MAPPING.filter((intersection) => {
                    return intersection.target === comp;
                })
            );
        });

        runIntersectionObserver = (queue) => {
            const sleepTime = 4;
            intersectionObserverCallback(queue);
            return promiseUtils.sleep(sleepTime);
        };

        onComponentRepositionedCallback = jasmine.createSpy('onComponentRepositioned');
        smartEditContractChangeListener.onComponentRepositioned(onComponentRepositionedCallback);

        onComponentResizedCallback = noop;
        smartEditContractChangeListener.onComponentResized(onComponentResizedCallback);

        onComponentsAddedCallback = jasmine.createSpy('onComponentsAdded');
        smartEditContractChangeListener.onComponentsAdded(onComponentsAddedCallback);

        onComponentsRemovedCallback = jasmine.createSpy('onComponentsRemoved');
        smartEditContractChangeListener.onComponentsRemoved(onComponentsRemovedCallback);

        onComponentChangedCallback = jasmine.createSpy('onComponentChangedCallback');
        smartEditContractChangeListener.onComponentChanged(onComponentChangedCallback);
    });

    beforeEach(() => {
        parent = stubParent();
        component0 = stubComponent0();
        component1 = stubComponent1();
        component21 = stubComponent21();
        component2 = stubComponent2(component2);
        component3 = stubComponent3();
        invisibleComponent = stubInvisibleComponent();
        nonProcessableComponent = stubNonProcessableComponent();
        detachedComponent = stubDetachedComponent();

        let pageUUIDCounter = 0;
        pageinfoService.getPageUUID.and.callFake(() => {
            pageUUIDCounter++;
            return getPageUUIDFake(pageUUIDCounter);
        });

        yjQuery.and.callFake(yjQueryFake);

        componentHandlerService.isSmartEditComponent.and.callFake((node: HTMLElement) => {
            return node.className && node.className.split(/[\s]+/).indexOf(COMPONENT_CLASS) > -1;
        });

        componentHandlerService.getClosestSmartEditComponent.and.callFake(((node: HTMLElement) => {
            if (
                node === parent ||
                node === component1 ||
                node === component2 ||
                node === component21 ||
                node === component3
            ) {
                return [node];
            } else {
                return getClosestSmartEditComponentPartialFake(node, parent, component0);
            }
        }) as any);

        componentHandlerService.getParent.and.callFake(((node: HTMLElement) => {
            return getParentFake(node, parent, component1, component2, component3, component21);
        }) as any);

        SECOND_LEVEL_CHILDREN = [component1];
        INTERSECTIONS_MAPPING = [
            {
                isIntersecting: true,
                target: component1 // child before 'parent'
            },
            {
                isIntersecting: true,
                target: parent
            },
            {
                isIntersecting: false,
                target: invisibleComponent
            },
            {
                isIntersecting: true,
                target: nonProcessableComponent
            }
        ] as any;

        componentHandlerService.getFirstSmartEditComponentChildren.and.callFake(((
            node: HTMLElement
        ) => {
            return getFirstSmartEditComponentChildrenFake(
                node,
                parent,
                component0,
                component2,
                component21,
                SECOND_LEVEL_CHILDREN
            );
        }) as any);

        holder.canProcess = (comp: HTMLElement) => {
            return comp !== nonProcessableComponent;
        };

        systemEventService.subscribe(
            CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.PROCESS_COMPONENTS,
            (eventId, components) => {
                const result = components.map((component: HTMLElement) => {
                    component.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = holder.canProcess(
                        component
                    )
                        ? CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS
                        : CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.REMOVE;
                    return component;
                });
                return Promise.resolve(result);
            }
        );
    });

    beforeEach(async () => {
        smartEditContractChangeListener.initListener();

        await promiseUtils.sleep(4);
    });

    describe('DOM intersections', () => {
        it('should register resize and position listeners on existing visible smartedit components that are processable', () => {
            expect(resizeListener.register.calls.count()).toEqual(2);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(parent);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component1);

            expect(positionRegistry.register.calls.count()).toEqual(2);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(parent);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component1);

            expect(onComponentsAddedCallback.calls.count()).toBe(2);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([parent]);
            expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component1]);
        });

        it('event with same intersections for components should not retrigger anything', async () => {
            await runIntersectionObserver(INTERSECTIONS_MAPPING);

            resizeListener.register.calls.reset();
            positionRegistry.register.calls.reset();
            onComponentsAddedCallback.calls.reset();
            onComponentsRemovedCallback.calls.reset();

            await runIntersectionObserver(INTERSECTIONS_MAPPING);

            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(positionRegistry.register).not.toHaveBeenCalled();
            expect(onComponentsAddedCallback).not.toHaveBeenCalled();
            expect(onComponentsRemovedCallback).not.toHaveBeenCalled();
        });

        it('when components are no longer visible, they are destroyed', async () => {
            await runIntersectionObserver(INTERSECTIONS_MAPPING);

            resizeListener.register.calls.reset();
            resizeListener.unregister.calls.reset();
            positionRegistry.register.calls.reset();
            positionRegistry.unregister.calls.reset();
            onComponentsAddedCallback.calls.reset();
            onComponentsRemovedCallback.calls.reset();

            INTERSECTIONS_MAPPING.forEach((element: IntersectionObserverEntry) => {
                (element as any).isIntersecting = false;
            });

            await runIntersectionObserver(INTERSECTIONS_MAPPING);

            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(resizeListener.unregister).not.toHaveBeenCalled();

            expect(positionRegistry.register).not.toHaveBeenCalled();
            expect(positionRegistry.unregister).not.toHaveBeenCalled();

            expect(onComponentsAddedCallback).not.toHaveBeenCalled();
            expect(onComponentsRemovedCallback.calls.count()).toBe(1);
            expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([
                {
                    component: parent,
                    parent: undefined
                },
                {
                    component: component1,
                    parent
                }
            ]);
        });
    });

    describe('DOM mutations', () => {
        beforeEach(() => {
            resizeListener.unregister.calls.reset();
            resizeListener.register.calls.reset();
            positionRegistry.unregister.calls.reset();
            positionRegistry.register.calls.reset();
            onComponentsAddedCallback.calls.reset();
        });

        it('should init the Mutation Observer and observe on body element with the expected configuration', () => {
            const expectedConfig = {
                attributes: true,
                attributeOldValue: true,
                childList: true,
                characterData: false,
                subtree: true
            };
            expect(mutationObserverMock.observe).toHaveBeenCalledWith(
                document.getElementsByTagName('body')[0],
                expectedConfig
            );
        });

        it('should be able to observe a page change and execute a registered page change callback', async () => {
            // GIVEN
            const pageChangedCallback = jasmine.createSpy('callback');
            smartEditContractChangeListener.onPageChanged(pageChangedCallback);
            pageChangedCallback.calls.reset();

            // WHEN
            const mutations = [
                {
                    attributeName: 'class',
                    type: 'attributes',
                    target: {
                        tagName: 'BODY'
                    }
                }
            ];
            await playMutationObserverCallback(mutations);

            // THEN
            expect(pageChangedCallback.calls.argsFor(0)[0]).toEqual(ANY_PAGE_UUID);

            expect(pageChangedCallback.calls.count()).toBe(1);
        });

        it('when a parent and a child are in the same operation (can occur), the child is NOT ignored but is process AFTER the parent', async () => {
            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [
                {
                    isIntersecting: true,
                    target: component21 // child before parent component2
                },
                {
                    isIntersecting: true,
                    target: component2
                },
                {
                    isIntersecting: true,
                    target: component3
                },
                {
                    isIntersecting: true,
                    target: detachedComponent
                }
            ]);

            SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
            const mutations = [
                {
                    type: 'childList',
                    addedNodes: [component21, component2, invisibleComponent, component3]
                }
            ];
            await playMutationObserverCallback(mutations);

            // THEN
            expect(onComponentsAddedCallback.calls.count()).toBe(3);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
            expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
            expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
        });

        it('should be able to observe sub tree of smartEditComponent component added', async () => {
            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [
                {
                    isIntersecting: true,
                    target: component2
                },
                {
                    isIntersecting: true,
                    target: component21
                },
                {
                    isIntersecting: true,
                    target: component3
                }
            ]);

            SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
            const mutations = [
                {
                    type: 'childList',
                    addedNodes: [component2, component3, invisibleComponent]
                }
            ];
            await playMutationObserverCallback(mutations);

            // THEN
            expect(resizeListener.unregister.calls.count()).toEqual(0);

            expect(resizeListener.register.calls.count()).toEqual(3);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component21);
            expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

            expect(positionRegistry.register.calls.count()).toEqual(3);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component21);
            expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

            expect(onComponentsAddedCallback.calls.count()).toBe(3);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
            expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
            expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
        });

        it('should be able to observe sub tree of non smartEditComponent component added', async () => {
            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [
                {
                    isIntersecting: true,
                    target: component2
                },
                {
                    isIntersecting: true,
                    target: component21
                },
                {
                    isIntersecting: true,
                    target: component3
                }
            ]);

            SECOND_LEVEL_CHILDREN = [component1, component3, invisibleComponent];
            const mutations = [
                {
                    type: 'childList',
                    addedNodes: [component0, component3, invisibleComponent]
                }
            ];
            await playMutationObserverCallback(mutations);

            // THEN

            expect(resizeListener.unregister.calls.count()).toEqual(0);

            expect(resizeListener.register.calls.count()).toEqual(3);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component21);
            expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

            expect(positionRegistry.register.calls.count()).toEqual(3);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component21);
            expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

            expect(onComponentsAddedCallback.calls.count()).toBe(3);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2]);
            expect(onComponentsAddedCallback.calls.argsFor(1)[0]).toEqual([component21]);
            expect(onComponentsAddedCallback.calls.argsFor(2)[0]).toEqual([component3]);
        });

        it('should be able to observe sub tree of smartEditComponent (and parent) removed', async () => {
            (smartEditContractChangeListener as any).componentsQueue.set(0, {
                component: component2,
                isIntersecting: true,
                processed: 'added',
                parent
            } as any);
            (smartEditContractChangeListener as any).componentsQueue.set(1, {
                component: component21,
                isIntersecting: true,
                processed: 'added',
                parent: component2
            } as any);
            (smartEditContractChangeListener as any).componentsQueue.set(2, {
                component: component3,
                isIntersecting: true,
                processed: 'added',
                parent
            } as any);

            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [
                {
                    isIntersecting: false,
                    target: component2
                },
                {
                    isIntersecting: false,
                    target: component21
                },
                {
                    isIntersecting: false,
                    target: component3
                }
            ]);
            SECOND_LEVEL_CHILDREN = [component1, component2, component3];

            await runIntersectionObserver(INTERSECTIONS_MAPPING);

            // THEN
            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(resizeListener.unregister).not.toHaveBeenCalled();
            expect(positionRegistry.unregister).not.toHaveBeenCalled();

            expect(onComponentsRemovedCallback.calls.count()).toBe(1);
            expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([
                {
                    component: component2,
                    parent
                },
                {
                    component: component21,
                    parent: component2
                },
                {
                    component: component3,
                    parent
                }
            ]);
        });

        it('should be able to stop all the listeners', () => {
            smartEditContractChangeListener.stopListener();

            expect(mutationObserverMock.disconnect).toHaveBeenCalled();
            expect(intersectionObserverMock.disconnect).toHaveBeenCalled();
            expect(resizeListener.dispose).toHaveBeenCalled();
            expect(positionRegistry.dispose).toHaveBeenCalled();
        });

        it('should call the componentRepositionedCallback when a component is repositioned after updating the registry', async () => {
            positionRegistry.getRepositionedComponents.and.returnValue([component1]);

            await promiseUtils.sleep(100);

            expect(onComponentRepositionedCallback.calls.count()).toBe(1);
            expect(onComponentRepositionedCallback).toHaveBeenCalledWith(component1);
        });

        it('should cancel the repositionListener interval when calling stopListener', () => {
            positionRegistry.getRepositionedComponents.and.returnValue([]);

            smartEditContractChangeListener.stopListener();

            expect((smartEditContractChangeListener as any).repositionListener).toBe(null);
        });

        it('should be able to observe a component change', async () => {
            // WHEN
            const mutations = [
                {
                    type: 'attributes',
                    attributeName: UUID_ATTRIBUTE,
                    target: component1,
                    oldValue: 'random_uuid'
                },
                {
                    type: 'attributes',
                    attributeName: ID_ATTRIBUTE,
                    target: component1,
                    oldValue: 'random_id'
                }
            ];
            await playMutationObserverCallback(mutations);

            // THEN
            const expectedOldAttributes: TypedMap<string> = {};
            expectedOldAttributes[UUID_ATTRIBUTE] = 'random_uuid';
            expectedOldAttributes[ID_ATTRIBUTE] = 'random_id';
            expect(onComponentChangedCallback.calls.argsFor(0)[0]).toEqual(
                component1,
                expectedOldAttributes
            );
        });

        it('should be able to observe a component remove', async () => {
            // GIVEN
            const mutations = [
                {
                    type: 'childList',
                    removedNodes: [component1],
                    target: parent
                }
            ];

            // WHEN
            await playMutationObserverCallback(mutations);

            // THEN
            expect(onComponentsRemovedCallback.calls.count()).toBe(1);
            expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([
                {
                    component: component1,
                    parent
                }
            ]);
        });

        it('should add a component to the components queue if it was generated from a simple div by adding smartedit attributes (change operation)', async () => {
            // GIVEN
            (smartEditContractChangeListener as any).componentsQueue = new Map<
                Element,
                ComponentEntry
            >();
            const mutations: any = [
                {
                    type: 'attributes',
                    attributeName: UUID_ATTRIBUTE,
                    target: component1,
                    oldValue: null
                }
            ];

            // WHEN
            await playMutationObserverCallback(mutations);

            // THEN
            const componentsInQueue = getComponents();
            expect(componentsInQueue).toContain(component1);
        });

        it('should remove component from the queue if smartedit attributes were removed from it (change operation)', async () => {
            // GIVEN
            (component1 as any).hasAttribute = () => {
                return false;
            };
            const mutations: any = [
                {
                    type: 'attributes',
                    attributeName: UUID_ATTRIBUTE,
                    target: component1,
                    oldValue: null
                }
            ];
            let componentsInQueue = getComponents();
            expect(componentsInQueue).toContain(component1);

            // WHEN
            await playMutationObserverCallback(mutations);

            // THEN
            componentsInQueue = getComponents();

            expect(componentsInQueue).not.toContain(component1);
            expect(intersectionObserverMock.unobserve).toHaveBeenCalledWith(component1);
            expect(onComponentsRemovedCallback).toHaveBeenCalled();
        });
    });
});

function getPageUUIDFake(pageUUIDCounter: number): Promise<string> {
    if (pageUUIDCounter === 1) {
        return Promise.resolve(INITIAL_PAGE_UUID);
    } else if (pageUUIDCounter === 2) {
        return Promise.resolve(ANY_PAGE_UUID);
    }
    return Promise.resolve(null);
}

function yjQueryFake(arg: string): any {
    if (arg === BODY_TAG) {
        return BODY;
    }
    return null;
}

function getClosestSmartEditComponentPartialFake(
    node: HTMLElement,
    parent: HTMLElement,
    component0: HTMLElement
) {
    if (node === component0) {
        return [parent];
    } else {
        return [];
    }
}

function getParentFake(
    node: HTMLElement,
    parent: HTMLElement,
    component1: HTMLElement,
    component2: HTMLElement,
    component3: HTMLElement,
    component21: HTMLElement
): HTMLElement[] {
    if (node === component21) {
        return [component2];
    } else if (node === component1 || node === component2 || node === component3) {
        return [parent];
    } else {
        return [];
    }
}

function getFirstSmartEditComponentChildrenFake(
    node: HTMLElement,
    parent: HTMLElement,
    component0: HTMLElement,
    component2: HTMLElement,
    component21: HTMLElement,
    second_level_chidren: HTMLElement[]
): HTMLElement[] {
    switch (node) {
        case BODY:
            return [parent];
        case parent:
            return second_level_chidren;
        case component2:
            return [component21];
        case component0:
            return [component2]; // ok to just return array, slice is applied on it
        default:
            return [];
    }
}

function stubCommon(): any {
    return {
        outerHTML: '<div></div>',
        nodeType: 1,
        className: COMPONENT_CLASS,
        dataset: {},
        tagName: 'DIV',
        hasAttribute: jasmine.createSpy().and.returnValue(true)
    };
}
function stubParent(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        tagName: 'PARENT',
        contains: jasmine.createSpy().and.returnValue(true),
        name: 'parent',
        sourceIndex: 0,
        attr: jasmine.createSpy().and.returnValue('parent')
    };
}

function stubComponent0(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('component0'),
        className: 'nonSmartEditComponent',
        name: 'component0',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 2
    };
}

function stubComponent1(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('component1'),
        name: 'component1',
        contains: jasmine.createSpy().and.returnValue(true),
        sourceIndex: 3
    };
}

function stubComponent21(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('component2_1'),
        name: 'component2_1',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 5
    };
}

function stubComponent2(component21: jasmine.SpyObj<HTMLElement>): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('component2'),
        name: 'component2',
        contains: (node: HTMLElement) => {
            return node === component21;
        },
        sourceIndex: 4
    };
}

function stubComponent3(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('component3'),
        name: 'component3',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 6
    };
}

function stubInvisibleComponent(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('invisibleComponent'),
        name: 'invisibleComponent',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 8
    };
}

function stubNonProcessableComponent(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('nonProcessableComponent'),
        name: 'nonProcessableComponent',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 8
    };
}

function stubDetachedComponent(): jasmine.SpyObj<HTMLElement> {
    return {
        ...stubCommon(),
        attr: jasmine.createSpy().and.returnValue('detachedComponent'),
        name: 'detachedComponent',
        contains: jasmine.createSpy().and.returnValue(false),
        sourceIndex: 9
    };
}
