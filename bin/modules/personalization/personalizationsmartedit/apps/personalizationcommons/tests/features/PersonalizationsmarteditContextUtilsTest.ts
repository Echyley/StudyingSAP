/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PersonalizationsmarteditContextUtils } from 'personalizationcommons/utils';

describe('personalizationsmarteditContextUtils', () => {
    // Service being tested
    let personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;

    // === SETUP ===
    beforeEach(() => {
        personalizationsmarteditContextUtils = new PersonalizationsmarteditContextUtils();
    });

    describe('getContextObject', () => {
        it('should return context object', () => {
            const contextObject = personalizationsmarteditContextUtils.getContextObject();
            expect(contextObject.personalization).toBeDefined();
            expect(contextObject.customize).toBeDefined();
            expect(contextObject.combinedView).toBeDefined();
            expect(contextObject.seData).toBeDefined();
        });
    });

    describe('clearCustomizeContext', () => {
        it('should be defined', () => {
            expect(personalizationsmarteditContextUtils.clearCustomizeContext).toBeDefined();
        });

        it('should call proper functions in services', () => {
            // given
            const mockCustomize = {
                enabled: false,
                selectedCustomization: 'test',
                selectedVariations: 'test',
                selectedComponents: 'test'
            };
            const mockContextService = jasmine.createSpyObj('mockContextService', {
                getCustomize: mockCustomize,
                setCustomize: null
            });

            // when
            personalizationsmarteditContextUtils.clearCustomizeContext(mockContextService);

            // then
            expect(mockContextService.getCustomize).toHaveBeenCalled();
            expect(mockContextService.setCustomize).toHaveBeenCalledWith({
                enabled: false,
                selectedCustomization: null,
                selectedVariations: null,
                selectedComponents: null
            });
        });
    });

    describe('clearCustomizeContextAndReloadPreview', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview
            ).toBeDefined();
        });

        it('should call proper functions in services', () => {
            // given
            const mockVariations = [
                {
                    name: '1'
                },
                {
                    name: '2'
                }
            ];
            const mockCustomize = {
                enabled: false,
                selectedCustomization: 'test',
                selectedVariations: mockVariations,
                selectedComponents: null as any
            };
            const mockPreviewService = jasmine.createSpyObj('mockPreviewService', [
                'removePersonalizationDataFromPreview'
            ]);
            const mockContextService = jasmine.createSpyObj('mockContextService', {
                getCustomize: mockCustomize,
                setCustomize: null
            });

            // when
            personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(
                mockPreviewService,
                mockContextService
            );

            // then
            expect(mockPreviewService.removePersonalizationDataFromPreview).not.toHaveBeenCalled();
            expect(mockContextService.getCustomize).toHaveBeenCalled();
            expect(mockContextService.setCustomize).toHaveBeenCalledWith({
                enabled: false,
                selectedCustomization: null,
                selectedVariations: null,
                selectedComponents: null
            });
        });
    });

    describe('clearCombinedViewCustomizeContext', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext
            ).toBeDefined();
        });

        it('should call proper functions in services', () => {
            // given
            const mockCustomize = {
                enabled: false,
                selectedCustomization: 'test',
                selectedVariations: 'test',
                selectedComponents: 'test'
            };
            const mockCombinedView = {
                enabled: true,
                selectedItems: [
                    { highlighted: true, otherProperty: 'test' },
                    { highlighted: false, otherProperty: 'test' }
                ],
                customize: mockCustomize
            };
            const mockContextService = jasmine.createSpyObj('mockContextService', {
                getCombinedView: mockCombinedView,
                setCombinedView: null
            });

            // when
            personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
                mockContextService
            );

            // then
            expect(mockContextService.getCombinedView).toHaveBeenCalled();
            expect(mockContextService.setCombinedView).toHaveBeenCalledWith({
                enabled: true,
                selectedItems: [{ otherProperty: 'test' }, { otherProperty: 'test' }],
                customize: {
                    enabled: false,
                    selectedCustomization: null,
                    selectedVariations: null,
                    selectedComponents: null
                }
            });
        });
    });

    describe('clearCombinedViewContext', () => {
        it('should be defined', () => {
            expect(personalizationsmarteditContextUtils.clearCombinedViewContext).toBeDefined();
        });

        it('should call proper functions in services', () => {
            // given
            const mockCombinedView = {
                enabled: true,
                selectedItems: [
                    { highlighted: true, otherProperty: 'test' },
                    { highlighted: false, otherProperty: 'test' }
                ]
            };
            const mockContextService = jasmine.createSpyObj('mockContextService', {
                getCombinedView: mockCombinedView,
                setCombinedView: null
            });

            // when
            personalizationsmarteditContextUtils.clearCombinedViewContext(mockContextService);

            // then
            expect(mockContextService.getCombinedView).toHaveBeenCalled();
            expect(mockContextService.setCombinedView).toHaveBeenCalledWith({
                enabled: false,
                selectedItems: null
            });
        });
    });

    describe('clearCombinedViewContextAndReloadPreview', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview
            ).toBeDefined();
        });

        it('should call proper functions in services and set properties to initial values', () => {
            // given
            const mockCombinedView = {
                enabled: true,
                selectedItems: []
            };
            const mockPreviewService = jasmine.createSpyObj('mockPreviewService', [
                'removePersonalizationDataFromPreview'
            ]);
            const mockContextService = jasmine.createSpyObj('mockContextService', {
                getCombinedView: mockCombinedView,
                setCombinedView: null
            });

            // when
            personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(
                mockPreviewService,
                mockContextService
            );

            // then
            expect(mockPreviewService.removePersonalizationDataFromPreview).toHaveBeenCalled();
            expect(mockContextService.getCombinedView).toHaveBeenCalled();
            expect(mockContextService.setCombinedView).toHaveBeenCalledWith({
                enabled: false,
                selectedItems: null
            });
        });
    });
});
