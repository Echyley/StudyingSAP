/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ComponentService,
    CMSItemStructure,
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    IPageContentSlotsComponentsRestService,
    ICMSComponent,
    LoadPagedComponentTypesRequestPayload
} from 'cmscommons';
import {
    annotationService,
    pageChangeEvictionTag,
    rarelyChangingContent,
    Cached,
    IPageInfoService,
    IRestService,
    Page,
    RecursivePartial,
    IRestServiceFactory,
    CmsitemsRestService,
    CMSItem
} from 'smarteditcommons';

describe('ComponentService - ', () => {
    const mockCmsItem: CMSItem = {
        name: 'name',
        typeCode: null,
        uid: null,
        uuid: null,
        catalogVersion: null
    };

    const mockComponentInfo = {
        name: 'name',
        slotId: 'slotId',
        targetSlotId: 'targetSlotId',
        pageId: 'pageId',
        componentType: 'componentType',
        position: 1,
        typeCode: 'code',
        itemtype: 'type',
        catalogVersion: 'cv',
        catalogVersionUuid: 'catalogVersionUuid'
    };

    let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let restServiceForAddExistingComponent: jasmine.SpyObj<IRestService<void>>;
    let pageContentSlotsComponentsRestService: jasmine.SpyObj<IPageContentSlotsComponentsRestService>;
    let pageComponentTypesRestService: jasmine.SpyObj<IRestService<Page<CMSItemStructure>>>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;

    let componentService: ComponentService;
    beforeEach(() => {
        restServiceFactory = jasmine.createSpyObj<IRestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        pageComponentTypesRestService = jasmine.createSpyObj<IRestService<Page<CMSItemStructure>>>(
            'pageComponentTypesRestService',
            ['get']
        );

        restServiceForAddExistingComponent = jasmine.createSpyObj<IRestService<void>>(
            'restServiceForAddExistingComponent',
            ['save']
        );

        restServiceFactory.get.and.returnValues(
            pageComponentTypesRestService,
            restServiceForAddExistingComponent
        );

        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get',
            'getById',
            'create',
            'update'
        ]);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

        pageContentSlotsComponentsRestService =
            jasmine.createSpyObj<IPageContentSlotsComponentsRestService>(
                'pageContentSlotsComponentsRestService',
                ['getSlotsToComponentsMapForPageUid']
            );

        componentService = new ComponentService(
            restServiceFactory,
            cmsitemsRestService,
            pageInfoService,
            pageContentSlotsComponentsRestService
        );
    });

    describe('getSupportedComponentTypesForCurrentPage() ', () => {
        let payload: LoadPagedComponentTypesRequestPayload;
        let expectedResult: Page<CMSItemStructure>;
        beforeEach(() => {
            payload = {
                catalogId: 'someCatalogId',
                catalogVersion: 'someCatalogVersion',
                pageId: 'somePageId',
                pageSize: 10,
                currentPage: 1,
                mask: 'someMask'
            };

            expectedResult = {
                pagination: {
                    count: 10,
                    page: 1,
                    totalCount: 100,
                    totalPages: 10
                },
                results: [
                    {
                        attributes: [],
                        category: 'someCategory',
                        code: 'someCode',
                        i18nKey: 'someKey',
                        name: 'someName',
                        type: 'someType'
                    }
                ]
            };
            pageComponentTypesRestService.get.and.returnValue(Promise.resolve(expectedResult));
        });

        it('Retrieves the types for the current page from the pageComponentTypesRestService endpoint', async () => {
            // WHEN
            const result = await componentService.getSupportedComponentTypesForCurrentPage(payload);

            // THEN
            expect(pageComponentTypesRestService.get).toHaveBeenCalledWith(payload as any);
            expect(result).toBe(expectedResult as any);
        });

        it('checks Cached annotation on getSupportedComponentTypesForCurrentPage', async () => {
            // WHEN
            await componentService.getSupportedComponentTypesForCurrentPage(payload);

            // THEN
            const decoratorObj = annotationService.getMethodAnnotation(
                ComponentService,
                'getSupportedComponentTypesForCurrentPage',
                Cached
            );
            expect(decoratorObj).toEqual(
                jasmine.objectContaining([
                    {
                        actions: [rarelyChangingContent],
                        tags: [pageChangeEvictionTag]
                    }
                ])
            );
        });
    });

    describe('loadComponentItem', () => {
        it('delegates to the rest service AND returns Cms Item for a given id', async () => {
            const id = 'id';
            cmsitemsRestService.getById.and.returnValue(Promise.resolve(mockCmsItem));

            const result = await componentService.loadComponentItem(id);

            expect(cmsitemsRestService.getById).toHaveBeenCalledWith(id);
            expect(result).toEqual(mockCmsItem);
        });
    });

    describe('updateComponent', () => {
        it('delegates to the rest service AND returns updated Cms Item', async () => {
            cmsitemsRestService.update.and.returnValue(Promise.resolve(mockCmsItem));

            const result = await componentService.updateComponent(mockCmsItem);

            expect(cmsitemsRestService.update).toHaveBeenCalledWith(mockCmsItem);
            expect(result).toEqual(mockCmsItem);
        });
    });

    describe('loadPagedComponentItemsByCatalogVersion', () => {
        it('delegates to the rest service with proper data', async () => {
            const payload = {
                page: 2,
                pageSize: 1,
                mask: 'mask',
                typeCode: 'AbstractCMSComponent',
                catalogId: CONTEXT_CATALOG,
                catalogVersion: CONTEXT_CATALOG_VERSION
            };

            const expected = {
                pagination: null,
                response: [],
                results: []
            };
            cmsitemsRestService.get.and.returnValue(Promise.resolve(expected));

            const result = await componentService.loadPagedComponentItemsByCatalogVersion(payload);

            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: payload.pageSize,
                currentPage: payload.page,
                mask: payload.mask,
                sort: 'name',
                typeCode: payload.typeCode,
                catalogId: payload.catalogId,
                catalogVersion: payload.catalogVersion,
                itemSearchParams: ''
            });
            expect(result).toEqual(expected);
        });
    });

    describe('addExistingComponent() ', () => {
        it('delegates to the rest service layer with proper data', async () => {
            const payload = {
                pageId: 'pageId',
                componentId: 'componentId',
                slotId: 'slotId',
                position: 1
            };
            restServiceForAddExistingComponent.save.and.returnValue(Promise.resolve());

            await componentService.addExistingComponent(
                payload.pageId,
                payload.componentId,
                payload.slotId,
                payload.position
            );

            expect(restServiceForAddExistingComponent.save).toHaveBeenCalledWith(payload);
        });
    });

    describe('createNewComponent() ', () => {
        it('GIVEN componentPayload WHEN called THEN the rest service is called with proper payload', async () => {
            const componentPayload = {
                name: 'cmpValue',
                typeCode: 'cmpCode',
                uid: 'cmpUid',
                uuid: 'cmpUuid',
                catalogVersion: 'cmpCatalogVersion'
            };

            const createPayload = {
                name: componentPayload.name,
                slotId: mockComponentInfo.targetSlotId,
                pageId: mockComponentInfo.pageId,
                position: mockComponentInfo.position,
                typeCode: componentPayload.typeCode,
                itemtype: mockComponentInfo.componentType,
                catalogVersion: componentPayload.catalogVersion,
                uid: componentPayload.uid,
                uuid: componentPayload.uuid
            };
            cmsitemsRestService.create.and.returnValue(Promise.resolve(mockCmsItem));

            const result = await componentService.createNewComponent(
                mockComponentInfo,
                componentPayload
            );

            expect(cmsitemsRestService.create).toHaveBeenCalledWith(createPayload);
            expect(result).toEqual(mockCmsItem);
        });

        it('GIVEN more properties in componentPayload WHEN called THEN the rest service is called with proper payload', async () => {
            const componentPayload = {
                name: 'cmpValue',
                typeCode: 'cmpCode',
                uid: 'cmpUid',
                uuid: 'cmpUuid',
                catalogVersion: 'cmpCatalogVersion',
                prop1: 'prop1',
                prop2: 'prop2'
            };

            const createPayload = {
                name: componentPayload.name,
                slotId: mockComponentInfo.targetSlotId,
                pageId: mockComponentInfo.pageId,
                position: mockComponentInfo.position,
                typeCode: componentPayload.typeCode,
                itemtype: mockComponentInfo.componentType,
                catalogVersion: componentPayload.catalogVersion,
                uid: componentPayload.uid,
                uuid: componentPayload.uuid,
                prop1: componentPayload.prop1,
                prop2: componentPayload.prop2
            };
            cmsitemsRestService.create.and.returnValue(Promise.resolve(mockCmsItem));

            const result = await componentService.createNewComponent(
                mockComponentInfo,
                componentPayload
            );

            expect(cmsitemsRestService.create).toHaveBeenCalledWith(createPayload);
            expect(result).toEqual(mockCmsItem);
        });
    });

    describe('getSlotsForComponent', () => {
        const mockComponent: RecursivePartial<ICMSComponent> = {
            uuid: 'Section1Slot-Homepage',
            visible: true,
            cloneable: true,
            slots: [],
            catalogVersion: 'Online',
            name: 'slot1',
            typeCode: 'CMSSlot',
            uid: 'uid'
        };

        it('GIVEN componentUuid WHEN called THEN it returns an array of slot IDs for that component', async () => {
            pageInfoService.getPageUID.and.returnValue(Promise.resolve() as any);
            pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid.and.returnValue(
                Promise.resolve<any>({
                    topHeaderSlot: [mockComponent]
                })
            );

            const slotIds = await componentService.getSlotsForComponent(mockComponent.uuid);

            expect(slotIds).toEqual(['topHeaderSlot']);
        });

        it('GIVEN componentUuid for which no slots exist WHEN called THEN it returns an empty array', async () => {
            pageInfoService.getPageUID.and.returnValue(Promise.resolve() as any);
            pageContentSlotsComponentsRestService.getSlotsToComponentsMapForPageUid.and.returnValue(
                Promise.resolve<any>({
                    topHeaderSlot: [mockComponent]
                })
            );

            const slotIds = await componentService.getSlotsForComponent('0');

            expect(slotIds.length).toEqual(0);
        });
    });
});
