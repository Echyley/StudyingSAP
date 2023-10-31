/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import {
    ComponentAttributes,
    ICatalogService,
    IPageInfoService,
    ISharedDataService,
    CMSItem,
    IEditorModalService,
    PageContentSlotsService,
    IComponentHandlerService,
    SlotSharedService
} from 'smarteditcommons';

let pageContentSlotsService: jasmine.SpyObj<PageContentSlotsService>;
let pageInfoService: jasmine.SpyObj<IPageInfoService>;
let translateService: jasmine.SpyObj<TranslateService>;
let editorModalService: jasmine.SpyObj<IEditorModalService>;
let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
let catalogService: jasmine.SpyObj<ICatalogService>;
let sharedDataService: jasmine.SpyObj<ISharedDataService>;

const newCustomSlot = {
    catalogVersion: 'electronics-ukContentCatalog/Staged',
    uid: 'cmsitem_1234567890',
    originalSlot: 'originalSlotUuid',
    itemtype: 'ContentSlot',
    name: 'new-component',
    uuid: 'uuid-1234567890',
    cmsComponents: []
};

let componentAttributes: ComponentAttributes;
const PAGE_UUID = '1234';
const mockContentApi = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';

let service: SlotSharedService;

describe('SlotSharedService', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    beforeEach(() => {
        componentAttributes = {
            smarteditComponentId: 'smarteditComponentId',
            smarteditComponentUuid: 'smarteditComponentUuid',
            smarteditComponentType: 'smarteditComponentType'
        } as ComponentAttributes;
    });

    describe('isSlotShared ', function () {
        it('should return a promise which resolves to true when the backend response indicates the slot is shared', async () => {
            // GIVEN
            pageContentSlotsService.isSlotShared.and.returnValue(Promise.resolve(true));

            // WHEN
            const isSlotShared = await service.isSlotShared('topHeaderSlot');

            // THEN
            expect(isSlotShared).toBe(true);
        });

        it('should return a promise which resolves to false when the backend response indicates the slot is not shared', async () => {
            // GIVEN
            pageContentSlotsService.isSlotShared.and.returnValue(Promise.resolve(false));

            // WHEN
            const isSlotShared = await service.isSlotShared('footerSlot');

            // THEN
            expect(isSlotShared).toBe(false);
        });
    });

    describe('isGlobalSlot', () => {
        it('should return a promise which resolves to true when the the current catalog is multicountry related and if the given slot is external slot', async () => {
            // GIVEN
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(true));
            catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(true));

            // WHEN
            const isGlobalService = await service.isGlobalSlot('footerSlot', '');

            // THEN
            expect(isGlobalService).toBe(true);
        });

        it('should return a promise which resolves to true when the the current catalog is multicountry related and if the given slot is not external slot but the current page is from parent catalog', async () => {
            // GIVEN
            const experience = {
                pageContext: {
                    catalogVersionUuid: 'electronics'
                },
                catalogDescriptor: {
                    catalogVersionUuid: 'electronics-us'
                }
            };
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));
            catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(true));
            sharedDataService.get.and.returnValue(Promise.resolve(experience));

            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            const isCurrentPageFromParentCatalog = (service as any).isCurrentPageFromParentCatalog(
                experience
            ) as boolean;
            expect(isCurrentPageFromParentCatalog).toBe(true);

            // WHEN
            const isGlobalSlot = await service.isGlobalSlot('footerSlot', '');

            // THEN
            expect(isGlobalSlot).toBe(true);
        });

        it('should return a promise which resolves to false when the the current catalog is not multicountry related', async () => {
            // GIVEN
            componentHandlerService.isExternalComponent.and.returnValue(Promise.resolve(false));
            catalogService.isCurrentCatalogMultiCountry.and.returnValue(Promise.resolve(false));

            // WHEN
            const isGlobalSlot = await service.isGlobalSlot('footerSlot', '');

            // THEN
            expect(isGlobalSlot).toBe(false);
        });
    });

    describe('replaceGlobalSlot and replaceSharedSlot', () => {
        it('should return a promise which resolves to the new slot object when replaceGlobalSlot is called', async () => {
            // WHEN
            const replacedGlobalSlot = await service.replaceGlobalSlot(componentAttributes);

            // THEN
            const componentData = {
                title: 'se.cms.slot.shared.replace.editor.title',
                structure: {
                    attributes: [
                        {
                            cmsStructureType: 'SlotSharedSlotTypeField',
                            qualifier: 'isSlotCustom',
                            required: true
                        },
                        {
                            cmsStructureType: 'SlotSharedCloneActionField',
                            qualifier: 'cloneAction',
                            required: true
                        }
                    ]
                },
                contentApi: mockContentApi,
                saveLabel: 'se.cms.slot.shared.replace.editor.save',
                content: {
                    name: PAGE_UUID + '-smarteditComponentId-clone',
                    smarteditComponentId: 'smarteditComponentId',
                    contentSlotUuid: 'smarteditComponentUuid',
                    itemtype: 'smarteditComponentType',
                    catalogVersion: 'targetCatalogVersionUuid',
                    pageUuid: PAGE_UUID,
                    onlyOneRestrictionMustApply: false
                },
                initialDirty: true
            };

            // resolvedPromise.then(function (cmsItem) {
            expect(editorModalService.openGenericEditor).toHaveBeenCalledWith(componentData);
            expect(replacedGlobalSlot).toEqual(newCustomSlot);
            // });
        });

        it('should return a promise which resolves to the new slot object when replaceSharedSlot is called', async () => {
            // WHEN
            const replacedGlobalSlot = await service.replaceSharedSlot(componentAttributes);

            const componentData = {
                title: 'se.cms.slot.shared.replace.editor.title',
                structure: {
                    attributes: [
                        {
                            cmsStructureType: 'SlotSharedCloneActionField',
                            qualifier: 'cloneAction',
                            required: true
                        }
                    ]
                },
                contentApi: mockContentApi,
                saveLabel: 'se.cms.slot.shared.replace.editor.save',
                content: {
                    name: PAGE_UUID + '-smarteditComponentId-clone',
                    smarteditComponentId: 'smarteditComponentId',
                    contentSlotUuid: 'smarteditComponentUuid',
                    itemtype: 'smarteditComponentType',
                    catalogVersion: 'targetCatalogVersionUuid',
                    pageUuid: PAGE_UUID,
                    onlyOneRestrictionMustApply: false,
                    isSlotCustom: true
                },
                initialDirty: true
            };

            expect(editorModalService.openGenericEditor).toHaveBeenCalledWith(componentData);
            expect(replacedGlobalSlot).toEqual(newCustomSlot);
        });

        it('should return a constructed cmsItem Parameter', async () => {
            // eslint-disable-next-line @typescript-eslint/no-unsafe-call
            const constructedCmsItemParam = (await (service as any).constructCmsItemParameter(
                componentAttributes
            )) as Partial<CMSItem>;

            expect(constructedCmsItemParam).toEqual({
                name: `${PAGE_UUID}-` + `smarteditComponentId-clone`,
                smarteditComponentId: 'smarteditComponentId',
                contentSlotUuid: 'smarteditComponentUuid',
                itemtype: 'smarteditComponentType',
                catalogVersion: 'targetCatalogVersionUuid',
                pageUuid: PAGE_UUID,
                onlyOneRestrictionMustApply: false
            });
        });

        it('should throw an error if componentAttributes is not set', async () => {
            componentAttributes = null;

            await service.replaceGlobalSlot(componentAttributes).catch((error) => {
                expect(error).toEqual(
                    new Error('Parameter: componentAttributes needs to be supplied!')
                );
            });
        });

        it('should throw an error if componentAttributes.smarteditComponentId parameter is not set', async () => {
            componentAttributes.smarteditComponentId = null;

            await service.replaceGlobalSlot(componentAttributes).catch((error) => {
                expect(error).toEqual(
                    new Error(
                        'Parameter: componentAttributes.smarteditComponentId needs to be supplied!'
                    )
                );
            });
        });

        it('should throw an error if componentAttributes.smarteditComponentUuid parameter is not set', async () => {
            componentAttributes.smarteditComponentUuid = null;

            await service.replaceGlobalSlot(componentAttributes).catch((error) => {
                expect(error).toEqual(
                    new Error(
                        'Parameter: componentAttributes.smarteditComponentUuid needs to be supplied!'
                    )
                );
            });
        });

        it('should throw an error if componentAttributes.smarteditComponentType parameter is not set', async () => {
            componentAttributes.smarteditComponentType = null;

            await service.replaceGlobalSlot(componentAttributes).catch((error) => {
                expect(error).toEqual(
                    new Error(
                        'Parameter: componentAttributes.smarteditComponentType needs to be supplied!'
                    )
                );
            });
        });
    });
});
function buildCommonSpyObjects() {
    pageContentSlotsService = jasmine.createSpyObj<PageContentSlotsService>(
        'pageContentSlotsService',
        ['isSlotShared']
    );

    pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', [
        'getPageUID',
        'getCatalogVersionUUIDFromPage'
    ]);

    translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

    editorModalService = jasmine.createSpyObj<IEditorModalService>('editorModalService', [
        'openGenericEditor'
    ]);

    componentHandlerService = jasmine.createSpyObj<IComponentHandlerService>(
        'componentHandlerService',
        ['isExternalComponent']
    );

    catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
        'isCurrentCatalogMultiCountry'
    ]);
    sharedDataService = jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

    service = new SlotSharedService(
        pageContentSlotsService,
        pageInfoService,
        translateService,
        editorModalService,
        componentHandlerService,
        catalogService,
        sharedDataService
    );

    editorModalService.openGenericEditor.and.returnValue(Promise.resolve(newCustomSlot));
    pageInfoService.getPageUID.and.returnValue(Promise.resolve(PAGE_UUID));
    pageInfoService.getCatalogVersionUUIDFromPage.and.returnValue(
        Promise.resolve('targetCatalogVersionUuid')
    );
    translateService.instant.and.returnValue('clone');
}
