/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent, IPageContentSlotsComponentsRestService } from 'cmscommons';
import { SlotVisibilityService } from 'cmssmartedit/services/SlotVisibilityServiceInner';
import {
    CrossFrameEventService,
    IPageInfoService,
    LogService,
    functionsUtils,
    IComponentHandlerService
} from 'smarteditcommons';

describe('SlotVisibilityService', () => {
    const PAGE_UID = 'homepage';

    let service: SlotVisibilityService;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let logService: jasmine.SpyObj<LogService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let pageContentSlotsComponentsRestService: jasmine.SpyObj<IPageContentSlotsComponentsRestService>;

    const yjQueryObject: any = {
        get() {
            return [
                {
                    visible: true,
                    uuid: '20'
                }
            ];
        }
    };

    beforeEach(() => {
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
            'componentHandlerService',
            ['getOriginalComponentsWithinSlot', 'getId']
        );
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);
        pageContentSlotsComponentsRestService = jasmine.createSpyObj<
            IPageContentSlotsComponentsRestService
        >('pageContentSlotsComponentsRestService', [
            'getSlotsToComponentsMapForPageUid',
            'clearCache'
        ]);

        pageInfoService.getPageUID.and.returnValue(Promise.resolve(PAGE_UID));

        componentHandlerService.getOriginalComponentsWithinSlot.and.returnValue(yjQueryObject);

        service = new SlotVisibilityService(
            crossFrameEventService,
            componentHandlerService,
            logService,
            pageInfoService,
            pageContentSlotsComponentsRestService
        );
    });

    describe('reloadSlotsInfo', () => {
        it('WHEN reloadSlotsInfo is called, it will clear cache and reload SlotsToComponentsMap', async () => {
            await service.reloadSlotsInfo();

            expect(pageContentSlotsComponentsRestService.clearCache).toHaveBeenCalled();
            expect(
                pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid
            ).toHaveBeenCalledWith(PAGE_UID);
        });

        it('WHEN loading pageUid throws error THEN logService is called and error is rethrow', async () => {
            pageInfoService.getPageUID.and.throwError('Error occurred');

            try {
                await service.reloadSlotsInfo();

                functionsUtils.assertFail();
            } catch (e) {
                expect(logService.error).toHaveBeenCalled();
            }
        });
    });

    describe('content slots per page is empty', () => {
        it('should have an empty hidden component list.', async () => {
            const SLOT = 'some-slot-id';
            pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid.and.returnValue(
                Promise.resolve({
                    some_data: []
                })
            );

            const promise = await service.getHiddenComponents(SLOT);

            expect(promise).toEqual([]);
        });
    });

    describe('content slots per page is not empty', () => {
        const SLOT1 = 'some-slot-id-1';

        const COMPONENT1 = ({
            visible: false,
            uid: '10'
        } as any) as ICMSComponent;
        const COMPONENT2 = ({
            visible: true,
            uid: '20'
        } as any) as ICMSComponent;
        const COMPONENT3 = ({
            visible: true,
            uid: '30'
        } as any) as ICMSComponent;

        beforeEach(() => {
            pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid.and.returnValue(
                Promise.resolve({
                    'some-slot-id-1': [COMPONENT1, COMPONENT2],
                    'some-slot-id-2': [COMPONENT3]
                })
            );

            componentHandlerService.getId.and.returnValue('20');
        });

        it('WHEN loading pageUid throws error THEN logService is called and error is rethrow', async () => {
            pageInfoService.getPageUID.and.throwError('Error occurred');

            try {
                await service.getHiddenComponents(SLOT1);

                functionsUtils.assertFail();
            } catch (e) {
                expect(logService.error).toHaveBeenCalled();
            }
        });

        it('should return a non-empty the hidden component list', async () => {
            const promise = await service.getHiddenComponents(SLOT1);

            expect(promise).toEqual([COMPONENT1]);
        });

        it('should subscribe to COMPONENT_CREATED_EVENT and clear component cache on trigger', () => {
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'COMPONENT_CREATED_EVENT',
                jasmine.any(Function)
            );

            const componentEventListener = crossFrameEventService.subscribe.calls.argsFor(0)[1];
            componentEventListener(null);

            expect(pageContentSlotsComponentsRestService.clearCache).toHaveBeenCalled();
        });

        it('should subscribe to COMPONENT_UPDATED_EVENT and clear component cache on trigger', () => {
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'COMPONENT_UPDATED_EVENT',
                jasmine.any(Function)
            );

            const componentEventListener = crossFrameEventService.subscribe.calls.argsFor(1)[1];
            componentEventListener(null);

            expect(pageContentSlotsComponentsRestService.clearCache).toHaveBeenCalled();
        });

        it('should subscribe to COMPONENT_REMOVED_EVENT and clear component cache on trigger', () => {
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'COMPONENT_REMOVED_EVENT',
                jasmine.any(Function)
            );

            const componentEventListener = crossFrameEventService.subscribe.calls.argsFor(2)[1];
            componentEventListener(null);

            expect(pageContentSlotsComponentsRestService.clearCache).toHaveBeenCalled();
        });
    });
});
