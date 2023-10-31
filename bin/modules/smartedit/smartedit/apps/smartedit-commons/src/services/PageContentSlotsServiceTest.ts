/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    CrossFrameEventService,
    IPageInfoService,
    IRestService,
    RestServiceFactory,
    IPageContentSlotsResponse,
    PageContentSlotsService,
    PAGES_CONTENT_SLOT_RESOURCE_URI
} from 'smarteditcommons';

describe('pageContentSlotsService', () => {
    let service: PageContentSlotsService;
    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let restServiceResource: jasmine.SpyObj<any>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;

    const pagescontentslots: IPageContentSlotsResponse = {
        pageContentSlotList: [
            {
                pageId: 'homepage',
                slotId: 'topHeaderSlot',
                position: '0',
                slotShared: true,
                slotStatus: 'TEMPLATE'
            },
            {
                pageId: 'homepage',
                slotId: 'bottomHeaderSlot',
                position: '1',
                slotShared: false,
                slotStatus: 'OVERRIDE'
            },
            {
                pageId: 'homepage',
                slotId: 'footerSlot',
                position: '2',
                slotShared: false,
                slotStatus: 'PAGE'
            },
            {
                pageId: 'homepage',
                slotId: 'otherSlot',
                position: '3',
                slotShared: true,
                slotStatus: 'TEMPLATE'
            }
        ]
    };

    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);
        restServiceResource = jasmine.createSpyObj<IRestService<IPageContentSlotsResponse>>(
            'restServiceResource',
            ['get']
        );
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

        restServiceFactory.get.and.returnValue(restServiceResource);
        restServiceResource.get.and.returnValue(Promise.resolve(pagescontentslots) as any);
        pageInfoService.getPageUID.and.returnValue(Promise.resolve('homepage'));

        service = new PageContentSlotsService(
            restServiceFactory,
            crossFrameEventService,
            pageInfoService
        );
    });

    describe('constructor', () => {
        it('should call restServiceFactory and subscribe to PAGE_CHANGE event', () => {
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_CHANGE',
                jasmine.any(Function)
            );
            expect(restServiceFactory.get).toHaveBeenCalledWith(PAGES_CONTENT_SLOT_RESOURCE_URI);
        });

        it('should load fresh data when PAGE_CHANGE event is fired', async () => {
            const cb = crossFrameEventService.subscribe.calls.argsFor(0)[1];

            await cb(null);

            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(restServiceResource.get).toHaveBeenCalledWith({ pageId: 'homepage' } as any);
        });
    });

    describe('getPageContentSlots ', () => {
        it('should resolve with a list of page content slots', async () => {
            const result = await service.getPageContentSlots();
            expect(result).toEqual(pagescontentslots.pageContentSlotList);
        });

        it('should call load fresh data once when getting page content slots', async () => {
            const result = await service.getPageContentSlots();
            expect(result).toEqual(pagescontentslots.pageContentSlotList);
            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(restServiceResource.get).toHaveBeenCalledWith({ pageId: 'homepage' } as any);

            const secondResult = await service.getPageContentSlots();

            expect(pageInfoService.getPageUID).toHaveBeenCalledTimes(1);
            expect(restServiceResource.get).toHaveBeenCalledTimes(1);
            expect(secondResult).toEqual(pagescontentslots.pageContentSlotList);
        });
    });

    describe('getSlotStatus ', () => {
        it('should return the slotStatus parameter for the provide slot', async () => {
            const resolvedPromise = await service.getSlotStatus('topHeaderSlot');
            expect(resolvedPromise).toEqual('TEMPLATE');

            const resolvedPromise1 = await service.getSlotStatus('bottomHeaderSlot');
            expect(resolvedPromise1).toEqual('OVERRIDE');

            const resolvedPromise2 = await service.getSlotStatus('footerSlot');
            expect(resolvedPromise2).toEqual('PAGE');
        });
    });

    describe('isSlotShared ', () => {
        it('should return the slotShared parameter for the provide slot', async () => {
            const resolvedPromise = await service.isSlotShared('topHeaderSlot');
            expect(resolvedPromise).toEqual(true);

            const resolvedPromise1 = await service.isSlotShared('bottomHeaderSlot');
            expect(resolvedPromise1).toEqual(false);
        });
    });
});
