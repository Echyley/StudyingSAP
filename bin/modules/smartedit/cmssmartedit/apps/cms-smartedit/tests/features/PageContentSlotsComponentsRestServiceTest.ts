/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent } from 'cmscommons';
import {
    PageContentSlotsComponentsRestService,
    PageContentSlotComponent,
    PageContentSlotComponentResponse
} from 'cmssmartedit/services/PageContentSlotsComponentsRestServiceInner';
import {
    annotationService,
    GatewayProxied,
    IPageInfoService,
    IRestService,
    IRestServiceFactory,
    CmsitemsRestService
} from 'smarteditcommons';

describe('PageContentSlotsComponentsRestService - ', () => {
    const PAGE_UID = 'homepage';

    const COMPONENT1 = {
        name: 'component1',
        typeCode: 'component1',
        itemtype: 'component1',
        uid: 'component1',
        uuid: 'component1',
        visible: true,
        cloneable: true,
        slots: []
    } as ICMSComponent;

    const COMPONENT2 = {
        name: 'component2',
        typeCode: 'component2',
        itemtype: 'component2',
        uid: 'component2',
        uuid: 'component2',
        visible: true,
        cloneable: true,
        slots: []
    } as ICMSComponent;

    const COMPONENT3 = {
        name: 'component3',
        typeCode: 'component3',
        itemtype: 'component3',
        uid: 'component3',
        uuid: 'component3',
        visible: true,
        cloneable: true,
        slots: []
    } as ICMSComponent;

    let pageContentSlotsComponentsRestService: PageContentSlotsComponentsRestService;
    let sampleContentSlotsComponentsInfo: PageContentSlotComponent[];
    let sampleComponentsInfo: ICMSComponent[];

    const restServiceFactory: jasmine.SpyObj<IRestServiceFactory> = jasmine.createSpyObj<
        IRestServiceFactory
    >('restServiceFactory', ['get']);
    const pagesContentSlotsRestService: jasmine.SpyObj<IRestService<
        PageContentSlotComponentResponse
    >> = jasmine.createSpyObj<IRestService<PageContentSlotComponentResponse>>(
        'pagesContentSlotsRestService',
        ['get']
    );
    const cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService> = jasmine.createSpyObj(
        'cmsitemsRestService',
        ['getByIds']
    );

    const pageInfoService: jasmine.SpyObj<IPageInfoService> = jasmine.createSpyObj<
        IPageInfoService
    >('pageInfoService', ['getPageUID']);

    beforeEach(() => {
        sampleContentSlotsComponentsInfo = [
            {
                pageId: PAGE_UID,
                slotId: 'topHeaderSlot',
                componentId: 'component1',
                componentUuid: 'component1',
                position: 0
            },
            {
                pageId: PAGE_UID,
                slotId: 'topHeaderSlot',
                componentId: 'component2',
                componentUuid: 'component2',
                position: 1
            },
            {
                pageId: PAGE_UID,
                slotId: 'bottomHeaderSlot',
                componentId: 'component3',
                componentUuid: 'component3',
                position: 0
            },
            {
                pageId: PAGE_UID,
                slotId: 'bottomHeaderSlot',
                componentId: 'component1',
                componentUuid: 'component1',
                position: 1
            }
        ];

        sampleComponentsInfo = [COMPONENT1, COMPONENT2, COMPONENT3];

        pagesContentSlotsRestService.get.and.returnValue(
            Promise.resolve({
                pageContentSlotComponentList: sampleContentSlotsComponentsInfo
            })
        );

        cmsitemsRestService.getByIds.and.returnValue(
            Promise.resolve({
                response: sampleComponentsInfo
            }) as any
        );

        restServiceFactory.get.and.returnValue(pagesContentSlotsRestService);

        pageInfoService.getPageUID.and.returnValue(Promise.resolve(PAGE_UID));

        pagesContentSlotsRestService.get.calls.reset();
        cmsitemsRestService.getByIds.calls.reset();

        pageContentSlotsComponentsRestService = new PageContentSlotsComponentsRestService(
            restServiceFactory,
            pageInfoService,
            cmsitemsRestService
        );
    });

    it('checks GatewayProxied', () => {
        const decoratorObj = annotationService.getClassAnnotation(
            PageContentSlotsComponentsRestService,
            GatewayProxied
        );
        expect(decoratorObj).toEqual([
            'clearCache',
            'getSlotsToComponentsMapForPageUid',
            'getComponentsForSlot'
        ]);
    });

    describe('getComponentsForSlot', () => {
        it(`WHEN called THEN it fetches the components[] for the slot`, async () => {
            // WHEN
            const value = await pageContentSlotsComponentsRestService.getComponentsForSlot(
                'topHeaderSlot'
            );

            // THEN
            expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
                pageId: PAGE_UID
            });

            expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith(
                [COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid],
                'DEFAULT'
            );
            expect(pageInfoService.getPageUID).toHaveBeenCalled();

            expect(value).toEqual([COMPONENT1, COMPONENT2]);
        });

        it(`WHEN called AND not slot info is found THEN it returns an empty array`, async () => {
            // WHEN
            const value = await pageContentSlotsComponentsRestService.getComponentsForSlot(
                'someUnknownSlot'
            );

            // THEN
            expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
                pageId: PAGE_UID
            });

            expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith(
                [COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid],
                'DEFAULT'
            );
            expect(pageInfoService.getPageUID).toHaveBeenCalled();

            expect(value).toEqual([]);
        });
    });

    it(`WHEN getSlotsToComponentsMapForPageUid is called
        THEN it fetches the Page Content Slots Components, calls cmsitemsRestService.getByIds with a unique Set of component uuids AND converts them into slot-components[] map`, async () => {
        // WHEN
        const value = await pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid(
            PAGE_UID
        );

        // THEN
        expect(pagesContentSlotsRestService.get).toHaveBeenCalledWith({
            pageId: PAGE_UID
        });

        expect(cmsitemsRestService.getByIds).toHaveBeenCalledWith(
            [COMPONENT1.uuid, COMPONENT2.uuid, COMPONENT3.uuid],
            'DEFAULT'
        );

        expect(value).toEqual({
            topHeaderSlot: [COMPONENT1, COMPONENT2],
            bottomHeaderSlot: [COMPONENT3, COMPONENT1]
        });
    });
});
