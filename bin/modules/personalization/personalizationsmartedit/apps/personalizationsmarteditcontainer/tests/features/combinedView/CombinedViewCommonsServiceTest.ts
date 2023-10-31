import { DialogRef } from '@fundamental-ngx/core';
import {
    CombinedView,
    CombinedViewSelectItem,
    Customization,
    CustomizationVariation,
    Customize,
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditUtils,
    SeData
} from 'personalizationcommons';
import { CombinedViewCommonsService } from 'personalizationsmarteditcontainer/combinedView';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditPreviewService,
    PersonalizationsmarteditRestService
} from 'personalizationsmarteditcontainer/service';
import { IModalService, LogService } from 'smarteditcommons';

describe('Test Combined View Commons Service', () => {
    let personalizationsmarteditContextUtils: jasmine.SpyObj<PersonalizationsmarteditContextUtils>;
    let personalizationsmarteditContextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
    let personalizationsmarteditPreviewService: jasmine.SpyObj<PersonalizationsmarteditPreviewService>;
    let personalizationsmarteditUtils: jasmine.SpyObj<PersonalizationsmarteditUtils>;
    let personalizationsmarteditRestService: jasmine.SpyObj<PersonalizationsmarteditRestService>;
    let modalService: jasmine.SpyObj<IModalService>;
    let logService: jasmine.SpyObj<LogService>;

    let combinedViewCommonsService: CombinedViewCommonsService;

    const mockCombinedView = {
        enabled: false,
        selectedItems: []
    } as CombinedView;
    const mockVariation = {
        code: 'testVariation'
    } as CustomizationVariation;
    const mockCustomization = ({
        code: 'testCustomization',
        variations: [mockVariation]
    } as unknown) as CombinedViewSelectItem;

    beforeEach(() => {
        personalizationsmarteditContextUtils = jasmine.createSpyObj(
            'personalizationsmarteditContextUtils',
            ['clearCombinedViewCustomizeContext']
        );

        personalizationsmarteditContextService = jasmine.createSpyObj(
            'personalizationsmarteditContextService',
            ['getCombinedView', 'setCombinedView', 'getCustomize', 'setCustomize', 'getSeData']
        );
        personalizationsmarteditContextService.getCombinedView.and.callFake(() => {
            return mockCombinedView;
        });
        personalizationsmarteditContextService.getCustomize.and.callFake(() => {
            return (mockCustomization as unknown) as Customize;
        });
        personalizationsmarteditPreviewService = jasmine.createSpyObj(
            'personalizationsmarteditPreviewService',
            ['updatePreviewTicketWithVariations']
        );
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', [
            'isItemFromCurrentCatalog'
        ]);
        personalizationsmarteditRestService = jasmine.createSpyObj(
            'personalizationsmarteditRestService',
            ['getActions']
        );
        modalService = jasmine.createSpyObj('modalService', ['open']);
        logService = jasmine.createSpyObj('logService', ['debug']);
        logService.debug.and.callFake((info, e) => {
            console.debug(info, e);
        });

        combinedViewCommonsService = new CombinedViewCommonsService(
            personalizationsmarteditContextUtils,
            personalizationsmarteditContextService,
            personalizationsmarteditPreviewService,
            personalizationsmarteditUtils,
            personalizationsmarteditRestService,
            modalService,
            logService
        );
    });

    it('WHEN combinedViewCommonsService is instantiated THEN it should contains proper functions', () => {
        expect(combinedViewCommonsService.openManagerAction).toBeDefined();
        expect(combinedViewCommonsService.updatePreview).toBeDefined();
        expect(combinedViewCommonsService.getVariationsForPreviewTicket).toBeDefined();
        expect(combinedViewCommonsService.combinedViewEnabledEvent).toBeDefined();
        expect(combinedViewCommonsService.isItemFromCurrentCatalog).toBeDefined();
    });

    describe('openManagerAction', () => {
        beforeEach(() => {
            spyOn(combinedViewCommonsService, 'getVariationsForPreviewTicket').and.returnValue([]);
            spyOn(combinedViewCommonsService, 'updatePreview');
        });

        describe('WHEN modal for configure combined view is opened', () => {
            beforeEach(() => {
                modalService.open.and.returnValue({
                    afterClosed: {
                        toPromise: () => Promise.resolve()
                    }
                } as DialogRef);
            });

            it('AND with empty selectedItems, THEN proper functions should be called', async () => {
                // given
                const combinedViewMock = {
                    enabled: false,
                    selectedItems: []
                } as CombinedView;
                personalizationsmarteditContextService.getCombinedView.and.callFake(() => {
                    return combinedViewMock;
                });

                // then
                await combinedViewCommonsService.openManagerAction();

                // so that
                expect(modalService.open).toHaveBeenCalled();
                expect(
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext
                ).toHaveBeenCalled();
                expect(personalizationsmarteditContextService.getCombinedView).toHaveBeenCalled();
                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith(
                    combinedViewMock
                );
                expect(combinedViewCommonsService.getVariationsForPreviewTicket).toHaveBeenCalled();
                expect(combinedViewCommonsService.updatePreview).toHaveBeenCalled();
            });

            it('AND with non-empty selectedItems, THEN proper functions should be called', async () => {
                // given
                const combinedViewMock = {
                    enabled: false,
                    selectedItems: [mockCustomization]
                } as CombinedView;
                personalizationsmarteditContextService.getCombinedView.and.callFake(() => {
                    return combinedViewMock;
                });

                // then
                await combinedViewCommonsService.openManagerAction();

                // so that
                expect(modalService.open).toHaveBeenCalled();
                expect(
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext
                ).toHaveBeenCalled();
                expect(personalizationsmarteditContextService.getCombinedView).toHaveBeenCalled();
                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith(
                    {
                        enabled: true,
                        selectedItems: [mockCustomization]
                    } as CombinedView
                );
                expect(combinedViewCommonsService.getVariationsForPreviewTicket).toHaveBeenCalled();
                expect(combinedViewCommonsService.updatePreview).toHaveBeenCalled();
            });
        });

        it("WHEN modal for configure combined view can't open, THEN proper functions should be called", async () => {
            // given

            modalService.open.and.returnValue(({
                afterClosed: {
                    toPromise: () => Promise.reject()
                }
            } as unknown) as DialogRef);

            // then
            await combinedViewCommonsService.openManagerAction();

            // so that
            expect(modalService.open).toHaveBeenCalled();
            expect(
                personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext
            ).not.toHaveBeenCalled();
            expect(personalizationsmarteditContextService.getCombinedView).not.toHaveBeenCalled();
            expect(personalizationsmarteditContextService.setCombinedView).not.toHaveBeenCalled();
            expect(combinedViewCommonsService.getVariationsForPreviewTicket).not.toHaveBeenCalled();
            expect(combinedViewCommonsService.updatePreview).not.toHaveBeenCalled();
            expect(logService.debug).toHaveBeenCalled();
        });
    });

    it('WHEN call updatePreview, THEN proper functions are called', () => {
        combinedViewCommonsService.updatePreview([]);
        expect(
            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations
        ).toHaveBeenCalledWith([]);
    });

    it(
        'WHEN call getVariationsForPreviewTicket with non-empty combined view selected items ' +
            'THEN it should return proper previewTicketVariations',
        () => {
            const combinedView = {
                enabled: false,
                selectedItems: [
                    {
                        customization: {
                            code: 'customization code1'
                        },
                        variation: {
                            code: 'variation code1',
                            catalog: 'catalog1',
                            catalogVersion: 'catalogVersion1'
                        }
                    },
                    {
                        customization: {
                            code: 'customization code2'
                        },
                        variation: {
                            code: 'variation code2',
                            catalog: 'catalog2',
                            catalogVersion: 'catalogVersion2'
                        }
                    }
                ]
            } as CombinedView;
            personalizationsmarteditContextService.getCombinedView.and.returnValue(combinedView);

            const previewTicketVariations = combinedViewCommonsService.getVariationsForPreviewTicket();

            expect(previewTicketVariations).toEqual([
                {
                    customizationCode: 'customization code1',
                    variationCode: 'variation code1',
                    catalog: 'catalog1',
                    catalogVersion: 'catalogVersion1'
                },
                {
                    customizationCode: 'customization code2',
                    variationCode: 'variation code2',
                    catalog: 'catalog2',
                    catalogVersion: 'catalogVersion2'
                }
            ]);
        }
    );

    it('WHEN call combinedViewEnabledEvent with true THEN it should call proper functions', () => {
        spyOn(combinedViewCommonsService, 'getVariationsForPreviewTicket').and.returnValue([]);
        spyOn(combinedViewCommonsService, 'updatePreview');

        combinedViewCommonsService.combinedViewEnabledEvent(true);

        expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith({
            enabled: true,
            selectedItems: []
        } as CombinedView);
        expect(personalizationsmarteditContextService.setCustomize).toHaveBeenCalledWith(({
            code: 'testCustomization',
            selectedCustomization: null,
            selectedVariations: null,
            selectedComponents: null,
            variations: [mockVariation]
        } as unknown) as Customize);
        expect(combinedViewCommonsService.getVariationsForPreviewTicket).toHaveBeenCalled();
        expect(combinedViewCommonsService.updatePreview).toHaveBeenCalled();
    });

    it('WHEN call combinedViewEnabledEvent with false THEN it should call proper functions', () => {
        spyOn(combinedViewCommonsService, 'getVariationsForPreviewTicket').and.returnValue([]);
        spyOn(combinedViewCommonsService, 'updatePreview');

        combinedViewCommonsService.combinedViewEnabledEvent(false);

        expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith({
            enabled: false,
            selectedItems: []
        } as CombinedView);
        expect(personalizationsmarteditContextService.setCustomize).toHaveBeenCalledWith(({
            code: 'testCustomization',
            selectedCustomization: null,
            selectedVariations: null,
            selectedComponents: null,
            variations: [mockVariation]
        } as unknown) as Customize);
        expect(combinedViewCommonsService.getVariationsForPreviewTicket).not.toHaveBeenCalled();
        expect(combinedViewCommonsService.updatePreview).toHaveBeenCalled();
    });

    it('WHEN call isItemFromCurrentCatalog THEN is should call proper functions with proper parameters', () => {
        const seDataMock = {
            pageId: 'testPageId'
        } as SeData;
        personalizationsmarteditContextService.getSeData.and.returnValue(seDataMock);
        const item = {};
        combinedViewCommonsService.isItemFromCurrentCatalog(item);
        expect(personalizationsmarteditContextService.getSeData).toHaveBeenCalled();
        expect(personalizationsmarteditUtils.isItemFromCurrentCatalog).toHaveBeenCalledWith(
            item,
            seDataMock
        );
    });
});
