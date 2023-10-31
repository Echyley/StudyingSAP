import 'jasmine';
import { TranslateService } from '@ngx-translate/core';
import { cloneDeep } from 'lodash';
import { ContextMenuDto, PersonalizationsmarteditMessageHandler } from 'personalizationcommons';
import { PersonalizationsmarteditContextMenuServiceProxy } from 'personalizationsmarteditcontainer/contextMenu/PersonalizationsmarteditContextMenuServiceOuterProxy';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import { IEditorModalService, IModalService, IRenderService, LogService } from 'smarteditcommons';

describe('personalizationsmarteditContextMenuProxy', () => {
    let translateService: jasmine.SpyObj<TranslateService>;

    let modalService: jasmine.SpyObj<IModalService>;
    let renderService: jasmine.SpyObj<IRenderService>;
    let editorModalService: jasmine.SpyObj<IEditorModalService>;
    let personalizationsmarteditContextService: jasmine.SpyObj<PersonalizationsmarteditContextService>;
    let personalizationsmarteditRestService: jasmine.SpyObj<PersonalizationsmarteditRestService>;
    let personalizationsmarteditMessageHandler: jasmine.SpyObj<PersonalizationsmarteditMessageHandler>;
    let logService: jasmine.SpyObj<LogService>;

    let contextMenuProxy: PersonalizationsmarteditContextMenuServiceProxy;

    const mockVariation1 = {
        code: 'variation1'
    };
    const mockVariation2 = {
        code: 'variation2'
    };
    const mockSelectedComponents = ['comp1', 'comp2', 'comp3'];
    let mockSelectedItems: any = [];
    let mockCombinedView: any = {};
    let mockCustomize: any = {};

    beforeEach(() => {
        mockSelectedItems = [
            {
                customization: {
                    code: 'customization1'
                },
                variation: mockVariation1
            },
            {
                customization: {
                    code: 'customization2'
                },
                variation: mockVariation2
            }
        ];

        mockCombinedView = {
            enabled: true,
            customize: {
                // cloning components because service is modifying references
                selectedComponents: cloneDeep(mockSelectedComponents)
            },
            selectedItems: mockSelectedItems
        };

        mockCustomize = {
            // cloning components because service is modifying references
            selectedComponents: cloneDeep(mockSelectedComponents)
        };
    });

    beforeEach(() => {
        modalService = jasmine.createSpyObj('modalService', ['open']);

        renderService = jasmine.createSpyObj('renderService', ['renderSlots']);
        editorModalService = jasmine.createSpyObj('editorModalService', ['open']);
        translateService = jasmine.createSpyObj('translateService', ['instant']);
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        personalizationsmarteditContextService = jasmine.createSpyObj<
            PersonalizationsmarteditContextService
        >('personalizationsmarteditContextService', [
            'getCombinedView',
            'setCombinedView',
            'getCustomize',
            'setCustomize'
        ]);

        personalizationsmarteditMessageHandler = jasmine.createSpyObj<
            PersonalizationsmarteditMessageHandler
        >('PersonalizationsmarteditMessageHandler', ['sendError']);

        personalizationsmarteditRestService = jasmine.createSpyObj<
            PersonalizationsmarteditRestService
        >('personalizationsmarteditRestService', ['getActions', 'deleteAction']);

        contextMenuProxy = new PersonalizationsmarteditContextMenuServiceProxy(
            modalService,
            renderService,
            editorModalService,
            personalizationsmarteditContextService,
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            translateService,
            logService
        );
    });

    beforeEach(() => {
        (modalService.open as any).and.returnValue({
            afterClosed: {
                toPromise: () => Promise.resolve()
            }
        });

        translateService.instant.and.callFake((val) => val);

        personalizationsmarteditContextService.getCombinedView.and.returnValue({
            ...mockCombinedView
        });

        personalizationsmarteditContextService.getCustomize.and.returnValue({
            ...mockCustomize
        });

        personalizationsmarteditRestService.getActions.and.returnValue(
            Promise.resolve({
                actions: []
            })
        );
    });

    describe('openDeleteAction', () => {
        describe('GIVEN modal has been opened AND combined view is enabled and there is no API error', () => {
            it('WHEN there are selected components in customize inside combined view THEN it should remove element which is the same as container source id provided in config', async () => {
                await contextMenuProxy.openDeleteAction({
                    selectedCustomizationCode: 'code1',
                    selectedVariationCode: 'code2',
                    containerSourceId: mockSelectedComponents[1],
                    slotsToRefresh: ['slot1'],
                    actionId: 'action#1',
                    catalog: 'catalog',
                    catalogVersion: 'staged'
                } as ContextMenuDto);

                expect(renderService.renderSlots).toHaveBeenCalledWith(['slot1']);
                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith(
                    {
                        ...mockCombinedView,
                        customize: {
                            selectedComponents: [
                                mockSelectedComponents[0],
                                mockSelectedComponents[2]
                            ]
                        }
                    }
                );
                expect(personalizationsmarteditRestService.deleteAction).toHaveBeenCalledWith(
                    'code1',
                    'code2',
                    'action#1',
                    {
                        catalog: 'catalog',
                        catalogVersion: 'staged'
                    }
                );
                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalled();
            });

            it('THEN should add actions to particular items AND update combined view', async () => {
                await contextMenuProxy.openDeleteAction({
                    selectedCustomizationCode: 'customization2',
                    selectedVariationCode: 'variation2',
                    containerSourceId: mockSelectedComponents[1],
                    slotsToRefresh: ['slot1']
                } as ContextMenuDto);

                expect(renderService.renderSlots).toHaveBeenCalledWith(['slot1']);

                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith(
                    {
                        ...mockCombinedView,
                        customize: {
                            selectedComponents: [
                                mockSelectedComponents[0],
                                mockSelectedComponents[2]
                            ]
                        },
                        selectedItems: [
                            mockSelectedItems[0],
                            {
                                ...mockSelectedItems[1],
                                variation: {
                                    ...mockSelectedItems[1].variation,
                                    actions: []
                                }
                            }
                        ]
                    }
                );

                expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalled();
            });
        });

        it('GIVEN modal has been opened and dismissed THEN it should not do any further actions', async () => {
            (modalService.open as any).and.returnValue({
                afterClosed: {
                    toPromise: () => Promise.reject()
                }
            });

            await contextMenuProxy.openDeleteAction({
                selectedCustomizationCode: 'code1',
                selectedVariationCode: 'code2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(renderService.renderSlots).not.toHaveBeenCalled();
            expect(personalizationsmarteditContextService.setCombinedView).not.toHaveBeenCalled();
        });

        it('GIVEN modal has been opened AND combined view is enabled and there is API error THEN it should sent translated error to message handler', async () => {
            personalizationsmarteditRestService.getActions.and.returnValue(Promise.reject());

            await contextMenuProxy.openDeleteAction({
                selectedCustomizationCode: 'customization2',
                selectedVariationCode: 'variation2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(translateService.instant).toHaveBeenCalledWith(
                'personalization.error.gettingactions'
            );
            expect(personalizationsmarteditMessageHandler.sendError).toHaveBeenCalledWith(
                'personalization.error.gettingactions'
            );
        });

        it('GIVEN modal has been opened AND combined view is NOT enabled THEN it should get customization, remove element having the same ID as container source ID and update customization', async () => {
            personalizationsmarteditContextService.getCombinedView.and.returnValue({
                ...mockCombinedView,
                enabled: false
            });

            await contextMenuProxy.openDeleteAction({
                selectedCustomizationCode: 'customization2',
                selectedVariationCode: 'variation2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(personalizationsmarteditContextService.getCustomize).toHaveBeenCalled();
            expect(personalizationsmarteditContextService.setCustomize).toHaveBeenCalledWith({
                ...mockCustomize,
                selectedComponents: [mockSelectedComponents[0], mockSelectedComponents[2]]
            });
        });
    });

    describe('openAddAction', () => {
        beforeEach(() => {
            (modalService.open as any).and.returnValue({
                afterClosed: {
                    toPromise: () => Promise.resolve('comp4')
                }
            });
        });

        it('GIVEN modal has been opened WHEN combined view is enabled THEN it should add result from modal to selected components in combined view AND update it', async () => {
            await contextMenuProxy.openAddAction({
                selectedCustomizationCode: 'customization2',
                selectedVariationCode: 'variation2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(personalizationsmarteditContextService.setCombinedView).toHaveBeenCalledWith({
                ...mockCombinedView,
                customize: {
                    ...mockCombinedView.customize,
                    selectedComponents: mockSelectedComponents.concat('comp4')
                }
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith(['slot1']);
        });

        it('GIVEN modal has been opened WHEN combined view is NOT enabled THEN it should add result from to customize and update it', async () => {
            personalizationsmarteditContextService.getCombinedView.and.returnValue({
                ...mockCombinedView,
                enabled: false
            });

            await contextMenuProxy.openAddAction({
                selectedCustomizationCode: 'customization2',
                selectedVariationCode: 'variation2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(personalizationsmarteditContextService.setCustomize).toHaveBeenCalledWith({
                ...mockCustomize,
                selectedComponents: mockSelectedComponents.concat('comp4')
            });
            expect(renderService.renderSlots).toHaveBeenCalledWith(['slot1']);
        });
    });

    describe('openEditAction', () => {
        it('should set edit mode in passed config, open modal and render slots', async () => {
            await contextMenuProxy.openEditAction({
                selectedCustomizationCode: 'customization2',
                selectedVariationCode: 'variation2',
                containerSourceId: mockSelectedComponents[1],
                slotsToRefresh: ['slot1']
            } as ContextMenuDto);

            expect(modalService.open).toHaveBeenCalledWith(
                jasmine.objectContaining({
                    data: jasmine.objectContaining({ editEnabled: true })
                })
            );
            expect(renderService.renderSlots).toHaveBeenCalledWith(['slot1']);
        });
    });

    describe('openEditComponentAction', () => {
        it('WHEN called THEN it should call editorModalService open method with given config', () => {
            const config = {
                smarteditComponentType: 'cmpType',
                slotsToRefresh: []
            } as ContextMenuDto;

            contextMenuProxy.openEditComponentAction(config);

            expect(editorModalService.open).toHaveBeenCalledWith(config);
        });
    });
});
