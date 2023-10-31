/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    IPageContentSlotsComponentsRestService as IPageContentSlotsRestService,
    TypePermissionsRestService,
    CmsDragAndDropDragInfo
} from 'cmscommons';
import { ContentSlot, SlotRestrictionsService } from 'cmssmartedit/services';
import { CmsDragAndDropCachedSlot } from 'cmssmartedit/services/dragAndDrop';
import { noop } from 'lodash';
import {
    annotationService,
    CrossFrameEventService,
    GatewayProxied,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    LogService,
    CMSModesService,
    IComponentHandlerService,
    SlotSharedService
} from 'smarteditcommons';

describe('SlotRestrictionsServiceInner', () => {
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let logService: jasmine.SpyObj<LogService>;
    let pageContentSlotsComponentsRestService: jasmine.SpyObj<IPageContentSlotsRestService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let slotSharedService: jasmine.SpyObj<SlotSharedService>;
    let typePermissionsRestService: jasmine.SpyObj<TypePermissionsRestService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let slotsRestrictionsRestService: jasmine.SpyObj<IRestService<ContentSlot[]>>;
    let cMSModesService: jasmine.SpyObj<CMSModesService>;

    let pageChangeCallback: () => Promise<void>;

    let MOCK_SLOTS_RESTRICTIONS: Partial<ContentSlot>[];
    let MOCK_PAGE_UID: string;
    const COMPONENT_IN_SLOT_STATUS = {
        ALLOWED: 'allowed',
        DISALLOWED: 'disallowed',
        MAYBEALLOWED: 'mayBeAllowed'
    };

    let slotRestrictionsService: SlotRestrictionsService;
    let slotRestrictionsServiceAny: any;
    beforeEach(() => {
        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['isExternalComponent', 'getAllSlotUids']
        );

        logService = jasmine.createSpyObj<LogService>('logService', ['info']);

        pageContentSlotsComponentsRestService = jasmine.createSpyObj<IPageContentSlotsRestService>(
            'pageContentSlotsComponentsRestService',
            ['getComponentsForSlot']
        );

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        slotSharedService = jasmine.createSpyObj<SlotSharedService>('slotSharedService', [
            'isSlotShared',
            'areSharedSlotsDisabled'
        ]);

        typePermissionsRestService = jasmine.createSpyObj<TypePermissionsRestService>(
            'typePermissionsRestService',
            ['hasUpdatePermissionForTypes']
        );

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        cMSModesService = jasmine.createSpyObj<CMSModesService>('cMSModesService', [
            'isVersioningPerspectiveActive'
        ]);
        cMSModesService.isVersioningPerspectiveActive.and.returnValue(Promise.resolve(false));

        slotRestrictionsService = new SlotRestrictionsService(
            componentHandlerService,
            logService,
            pageContentSlotsComponentsRestService,
            pageInfoService,
            restServiceFactory,
            slotSharedService,
            typePermissionsRestService,
            null,
            crossFrameEventService,
            cMSModesService
        );
        slotRestrictionsServiceAny = slotRestrictionsService as any;
        (slotRestrictionsService as any).CONTENT_SLOTS_TYPE_RESTRICTION_FETCH_LIMIT = 2;

        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
            'PAGE_CHANGE',
            jasmine.any(Function)
        );
        pageChangeCallback = crossFrameEventService.subscribe.calls.argsFor(0)[1] as any;

        spyOn(slotRestrictionsServiceAny, 'emptyCache').and.callThrough();
    });

    beforeEach(() => {
        slotsRestrictionsRestService = jasmine.createSpyObj<IRestService<any>>(
            'slotsRestrictionsRestService',
            ['save']
        );
        restServiceFactory.get.and.returnValue(slotsRestrictionsRestService);
    });

    beforeEach(() => {
        MOCK_PAGE_UID = 'SomePageUID';
        MOCK_SLOTS_RESTRICTIONS = [
            {
                contentSlotUid: 'SomeSlotUID',
                validComponentTypes: [
                    'SomeComponentType1',
                    'SomeComponentType2',
                    'SomeComponentType3'
                ]
            },
            {
                contentSlotUid: 'SomeOtherSlotUID',
                validComponentTypes: ['SomeComponentType1', 'SomeComponentType2']
            },
            {
                contentSlotUid: 'SomeAnotherSlotUID',
                validComponentTypes: ['SomeComponentType1']
            }
        ];
    });

    describe('getSlotRestrictions', () => {
        it('initializes with gatewayProxy', () => {
            const decoratorObj = annotationService.getClassAnnotation(
                SlotRestrictionsService,
                GatewayProxied
            );
            expect(decoratorObj).toEqual([
                'getAllComponentTypesSupportedOnPage',
                'getSlotRestrictions',
                'determineComponentStatusInSlot',
                'isSlotEditable'
            ]);
        });

        it('should cache the page ID', async () => {
            // GIVEN
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));

            // WHEN
            await slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            await slotRestrictionsService.getSlotRestrictions('SomeSlotUID');

            // THEN
            expect(pageInfoService.getPageUID.calls.count()).toBe(
                1,
                'Expected pageInfoService.getPageUID() to have been called only once'
            );
        });

        it('should fetch slots restrictions and cache them in-memory', async () => {
            // GIVEN
            spyOn(slotRestrictionsServiceAny, 'fetchSlotsRestrictions').and.callThrough();
            slotsRestrictionsRestService.save.and.returnValue(
                Promise.resolve<any>(MOCK_SLOTS_RESTRICTIONS)
            );
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

            // WHEN - Before caching
            const slotRestrictionsBeforeCaching = await slotRestrictionsService.getSlotRestrictions(
                'SomeSlotUID'
            );

            // THEN - undefined
            expect(slotRestrictionsBeforeCaching).toEqual([]);

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            const savePromise = slotRestrictionsServiceAny.fetchSlotsRestrictions(['SomeSlotUID']);

            await savePromise;
            // THEN
            expect(slotsRestrictionsRestService.save).toHaveBeenCalledWith({
                slotIds: ['SomeSlotUID'],
                pageUid: 'SomePageUID'
            });

            // WHEN - After caching
            const slotRestrictionsAfterCaching = await slotRestrictionsService.getSlotRestrictions(
                'SomeSlotUID'
            );

            // THEN - data
            expect(slotRestrictionsAfterCaching).toEqual([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
        });

        it('should return no valid component types for external slots', async () => {
            // GIVEN
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(true));

            // WHEN
            const slotRestrictions = await slotRestrictionsService.getSlotRestrictions(
                'SomeSlotUID'
            );

            // THEN
            expect(slotRestrictions).toEqual([]);
        });

        it('should recursively call slot restriction REST service depending on the number of slots on the page and also on fetch limit', async () => {
            // GIVEN
            componentHandlerService.getAllSlotUids.and.returnValue([
                'SomeSlotUID',
                'SomeOtherSlotUID',
                'SomeAnotherSlotUID'
            ]);
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));
            slotsRestrictionsRestService.save.and.callFake(((obj) => {
                if (obj.slotIds.length === 2) {
                    return Promise.resolve([
                        {
                            contentSlotUid: 'SomeSlotUID',
                            validComponentTypes: [
                                'SomeComponentType1',
                                'SomeComponentType2',
                                'SomeComponentType3'
                            ]
                        },
                        {
                            contentSlotUid: 'SomeOtherSlotUID',
                            validComponentTypes: ['SomeComponentType1', 'SomeComponentType2']
                        }
                    ]);
                } else {
                    return Promise.resolve([
                        {
                            contentSlotUid: 'SomeAnotherSlotUID',
                            validComponentTypes: ['SomeComponentType1']
                        }
                    ]);
                }
            }) as any);

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await slotRestrictionsServiceAny.cacheSlotsRestrictions();

            // THEN
            expect(slotsRestrictionsRestService.save.calls.count()).toBe(
                2,
                'Expected slot restrictions save REST service to have been called twice'
            );

            expect(slotsRestrictionsRestService.save).toHaveBeenCalledWith({
                slotIds: ['SomeSlotUID', 'SomeOtherSlotUID'],
                pageUid: 'SomePageUID'
            });
            expect(slotsRestrictionsRestService.save).toHaveBeenCalledWith({
                slotIds: ['SomeAnotherSlotUID'],
                pageUid: 'SomePageUID'
            });
        });

        it('should not retrieve slot restrictions for external slot ids when cacheSlotsRestrictions is called with slotIds', async () => {
            // GIVEN
            componentHandlerService.getAllSlotUids.and.returnValue([
                'SomeSlotUID',
                'SomeOtherSlotUID',
                'SomeAnotherSlotUID'
            ]);
            pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            // if all slots are external slots
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(true));

            // WHEN
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            await slotRestrictionsServiceAny.cacheSlotsRestrictions();

            // THEN
            expect(slotsRestrictionsRestService.save).not.toHaveBeenCalled();
        });

        describe('isComponentAllowedInSlot', () => {
            it('should return a promise resolving to ALLOWED if the component type is allowed in the given slot AND source and target slots are the same AND the target slot already contains the component', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                const slotRestriction = MOCK_SLOTS_RESTRICTIONS.find(
                    (restriction) => restriction.contentSlotUid === 'SomeSlotUID'
                );

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve(slotRestriction.validComponentTypes)
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'something'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'something'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeSlotUID',
                    componentType: 'SomeComponentType1',
                    componentId: 'something'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowed = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowed).toBe(COMPONENT_IN_SLOT_STATUS.ALLOWED);
            });

            it('should return a promise resolving to ALLOWED if the component type is allowed in the given slot AND the slot does not already contain the component', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                const slotRestriction = MOCK_SLOTS_RESTRICTIONS.find(
                    (restriction) => restriction.contentSlotUid === 'SomeSlotUID'
                );

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve(slotRestriction.validComponentTypes)
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'something'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'something'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeOtherSlotUID',
                    componentType: 'SomeComponentType1',
                    componentId: 'SomeComponentId1'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowedInSlot = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowedInSlot).toBe(COMPONENT_IN_SLOT_STATUS.ALLOWED);
            });

            it('should return a promise resolving to DISALLOWED if the component type is allowed in the given slot AND source and target slots are different AND the slot already contains the component', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                const slotRestriction = MOCK_SLOTS_RESTRICTIONS.find(
                    (restriction) => restriction.contentSlotUid === 'SomeSlotUID'
                );

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve(slotRestriction.validComponentTypes)
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'SomeComponentId1'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'SomeComponentId1'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeOtherSlotUID',
                    componentType: 'SomeComponentType1',
                    componentId: 'SomeComponentId1'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowedInSlot = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowedInSlot).toBe(COMPONENT_IN_SLOT_STATUS.DISALLOWED);
            });

            it('should return a promise resolving to DISALLOWED if the component type is not allowed in the given slot', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                const slotRestriction = MOCK_SLOTS_RESTRICTIONS.find(
                    (restriction) => restriction.contentSlotUid === 'SomeSlotUID'
                );

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve(slotRestriction.validComponentTypes)
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'something'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'something'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeOtherSlotUID',
                    componentType: 'SomeComponentType4',
                    componentId: 'SomeComponentId4'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowedInSlot = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowedInSlot).toBe(COMPONENT_IN_SLOT_STATUS.DISALLOWED);
            });

            it('should return a promise resolving to MAYBEALLOWED if source and target slots are the same AND the target slot already contains the component and the slot restriction for a given slot is not cached', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve()
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'something'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'something'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeSlotUID',
                    componentType: 'SomeComponentType1',
                    componentId: 'something'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowedInSlot = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowedInSlot).toBe(COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED);
            });

            it('should return a promise resolving to MAYBEALLOWED if the slot does not already contain the component and the slot restriction for a given slot is not cached', async () => {
                // GIVEN
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));

                spyOn(slotRestrictionsService, 'getSlotRestrictions').and.returnValue(
                    Promise.resolve()
                );

                pageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue(
                    Promise.resolve<any>([
                        {
                            uid: 'something'
                        }
                    ])
                );
                const slot = {
                    id: 'SomeSlotUID',
                    components: [
                        {
                            id: 'something'
                        }
                    ]
                } as CmsDragAndDropCachedSlot;
                const dragInfo = {
                    slotId: 'SomeOtherSlotUID',
                    componentType: 'SomeComponentType1',
                    componentId: 'SomeComponentId1'
                } as CmsDragAndDropDragInfo;

                // WHEN
                const isComponentAllowedInSlot = await slotRestrictionsService.isComponentAllowedInSlot(
                    slot,
                    dragInfo
                );

                // THEN
                expect(isComponentAllowedInSlot).toBe(COMPONENT_IN_SLOT_STATUS.MAYBEALLOWED);
            });
        });

        describe('emptyCache', () => {
            beforeEach(() => {
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
            });

            it('when page changes _emptyCache is called', async () => {
                expect(slotRestrictionsServiceAny.emptyCache).not.toHaveBeenCalled();
                await pageChangeCallback();
                expect(slotRestrictionsServiceAny.emptyCache).toHaveBeenCalled();
            });

            it('when page changes _cacheSlotsRestrictions is called', async () => {
                spyOn(slotRestrictionsServiceAny, 'cacheSlotsRestrictions').and.callFake(noop);
                expect(slotRestrictionsServiceAny.cacheSlotsRestrictions).not.toHaveBeenCalled();

                await pageChangeCallback();
                expect(slotRestrictionsServiceAny.cacheSlotsRestrictions).toHaveBeenCalled();
            });

            it('should invalidate the cache when _emptyCache is called', async () => {
                // GIVEN
                slotsRestrictionsRestService.save.and.returnValue(
                    Promise.resolve<any>(MOCK_SLOTS_RESTRICTIONS)
                );
                pageInfoService.getPageUID.and.returnValue(Promise.resolve(MOCK_PAGE_UID));
                componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));

                // eslint-disable-next-line @typescript-eslint/no-unsafe-call
                await slotRestrictionsServiceAny.fetchSlotsRestrictions(['SomeSlotUID']);

                // WHEN
                const slotRestrictions = await slotRestrictionsService.getSlotRestrictions(
                    'SomeSlotUID'
                );

                // THEN - before empty cache
                expect(slotRestrictions).toEqual([
                    'SomeComponentType1',
                    'SomeComponentType2',
                    'SomeComponentType3'
                ]);

                // WHEN
                // eslint-disable-next-line @typescript-eslint/no-unsafe-call
                slotRestrictionsServiceAny.emptyCache();

                // THEN - after empty cache
                const slotRestrictionsAfterCacheCleaned = await slotRestrictionsService.getSlotRestrictions(
                    'SomeSlotUID'
                );
                expect(slotRestrictionsAfterCacheCleaned).toEqual([]);
            });
        });

        describe('slotEditable - ', () => {
            const slotId = 'some slot';
            let areSharedSlotsDisabled: boolean;
            let isSlotShared: boolean;
            let isExternalComponent: Promise<boolean>;

            beforeEach(() => {
                slotSharedService.isSlotShared.and.callFake(() => Promise.resolve(isSlotShared));

                componentHandlerService.isExternalComponent.and.callFake(() => isExternalComponent);

                slotSharedService.areSharedSlotsDisabled.and.callFake(() => areSharedSlotsDisabled);
            });

            describe('with CHANGE permissions ', () => {
                beforeEach(() => {
                    typePermissionsRestService.hasUpdatePermissionForTypes.and.callFake(() =>
                        Promise.resolve({
                            ContentSlot: true
                        })
                    );
                });

                it('GIVEN shared slots are disabled WHEN isSlotEditable is called AND the slot is shared THEN it returns false', async () => {
                    // GIVEN
                    areSharedSlotsDisabled = true;
                    isSlotShared = true;
                    isExternalComponent = Promise.resolve(false);

                    // WHEN
                    const isSlotEditable = await slotRestrictionsService.isSlotEditable(slotId);

                    // THEN
                    expect(isSlotEditable).toBe(false);
                });

                it('GIVEN shared slots are disabled WHEN isSlotEditable is called AND the slot is not shared THEN it returns true', async () => {
                    // GIVEN
                    areSharedSlotsDisabled = true;
                    isSlotShared = false;
                    isExternalComponent = Promise.resolve(false);

                    // WHEN
                    const isSlotEditable = await slotRestrictionsService.isSlotEditable(slotId);

                    // THEN
                    expect(isSlotEditable).toBe(true);
                });

                it('GIVEN shared slots are enabled WHEN isSlotEditable is called AND the slot is shared THEN it returns true', async () => {
                    // GIVEN
                    areSharedSlotsDisabled = false;
                    isSlotShared = true;
                    isExternalComponent = Promise.resolve(false);

                    // WHEN
                    const isSlotEditable = await slotRestrictionsService.isSlotEditable(slotId);

                    // THEN
                    expect(isSlotEditable).toBe(true);
                });

                it('GIVEN shared slots are enabled WHEN isSlotEditable is called AND the slot is external THEN it returns false', async () => {
                    // GIVEN
                    areSharedSlotsDisabled = false;
                    isSlotShared = true;
                    isExternalComponent = Promise.resolve(true);

                    // WHEN
                    const isSlotEditable = await slotRestrictionsService.isSlotEditable(slotId);

                    // THEN
                    expect(isSlotEditable).toBe(false);
                });
            });

            describe('without CHANGE permissions ', () => {
                it('GIVEN slot without CHANGE permission WHEN isSlotEditable is called THEN it returns false', async () => {
                    // GIVEN
                    typePermissionsRestService.hasUpdatePermissionForTypes.and.callFake(() =>
                        Promise.resolve({
                            ContentSlot: false
                        })
                    );

                    // WHEN
                    const isSlotEditable = await slotRestrictionsService.isSlotEditable(slotId);

                    // THEN
                    expect(isSlotEditable).toBe(false);
                });
            });
        });
    });
});
