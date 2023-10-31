/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { PageType } from 'cmssmarteditcontainer/dao/PageTypeService';
import { IRestrictionsStepHandler } from 'cmssmarteditcontainer/interfaces';
import { ContextAwarePageStructureService } from 'cmssmarteditcontainer/services/ContextAwarePageStructureService';
import {
    PageBuilderFactory,
    PageBuilder
} from 'cmssmarteditcontainer/services/pages/PageBuilderFactory';
import {
    GenericEditorStructure,
    ICatalogService,
    IUriContext,
    ICMSPage,
    CMSPageTypes,
    PageTemplateType
} from 'smarteditcommons';

describe('PageBuilderFactory', () => {
    let factory: PageBuilderFactory;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let contextAwarePageStructureService: jasmine.SpyObj<ContextAwarePageStructureService>;

    const mockRestrictionsStepHandler: IRestrictionsStepHandler = {
        hideStep: jasmine.createSpy(),
        showStep: jasmine.createSpy(),
        isStepValid: jasmine.createSpy(),
        save: jasmine.createSpy(),
        getStepId: jasmine.createSpy(),
        goToStep: jasmine.createSpy()
    };
    const mockUriContext: IUriContext = { context: 'context' };
    const mockInfoFields: GenericEditorStructure = {
        attributes: [
            {
                cmsStructureType: 'structure type',
                qualifier: 'label'
            }
        ],
        category: 'category',
        type: 'type'
    };
    const mockConditionResult = {
        isPrimary: true,
        homepage: true,
        primaryPage: {
            label: 'label'
        }
    } as unknown as ICMSPage;

    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'getCatalogVersionUUid'
        ]);
        contextAwarePageStructureService = jasmine.createSpyObj<ContextAwarePageStructureService>(
            'contextAwarePageStructureService',
            ['getPageStructureForNewPage']
        );

        catalogService.getCatalogVersionUUid.and.returnValue(Promise.resolve('uuid'));
        contextAwarePageStructureService.getPageStructureForNewPage.and.returnValue(
            Promise.resolve(mockInfoFields)
        );

        factory = new PageBuilderFactory(catalogService, contextAwarePageStructureService);
    });

    it('should create instance of PageBuilder', () => {
        const instance = factory.createPageBuilder(mockRestrictionsStepHandler, mockUriContext);

        expect(instance instanceof PageBuilder).toBe(true);
    });

    describe('PageBuilder', () => {
        let builder: PageBuilder;

        const pageType = {
            code: CMSPageTypes.ContentPage,
            type: 'type'
        } as unknown as PageType;
        const pageTemplate = {
            name: 'template',
            uuid: 'uuid',
            uid: 'uid'
        } as unknown as PageTemplateType;

        beforeEach(() => {
            builder = factory.createPageBuilder(mockRestrictionsStepHandler, mockUriContext);
        });

        it('WHEN created THEN it should get catalog version', () => {
            expect(catalogService.getCatalogVersionUUid).toHaveBeenCalledWith(mockUriContext);
        });

        it('WHEN page type is selected THEN it should set page type and template', () => {
            spyOn(builder as any, 'updatePageInfoFields');

            builder.pageTypeSelected(pageType);

            expect((builder as any).model.pageType).toEqual(pageType);
            expect((builder as any).model.pageTemplate).toEqual(null);
            expect((builder as any).updatePageInfoFields).toHaveBeenCalled();
        });

        it('WHEN page template is selected THEN it should set only page template', () => {
            builder.pageTemplateSelected(pageTemplate);

            expect((builder as any).model.pageTemplate).toEqual(pageTemplate);
        });

        describe('#getPageTypeCode', () => {
            it('WHEN page type is set in model THEN it should be returned', () => {
                builder.pageTypeSelected(pageType);
                const actual = builder.getPageTypeCode();

                expect(actual).toEqual(CMSPageTypes.ContentPage);
            });

            it('WHEN page type code is not set in model THEN it should return null', () => {
                builder.pageTypeSelected({ ...pageType, code: null });
                const actual = builder.getPageTypeCode();

                expect(actual).toEqual(null);
            });
        });

        describe('#getTemplateUuid', () => {
            it('WHEN page template is set in model THEN it should return its uuid', () => {
                builder.pageTemplateSelected(pageTemplate);

                const actual = builder.getTemplateUuid();

                expect(actual).toEqual('uuid');
            });

            it('WHEN page template is not set in model THEN it should return empty string', () => {
                builder.pageTemplateSelected({ ...pageTemplate, uuid: null });

                const actual = builder.getTemplateUuid();

                expect(actual).toEqual('');
            });
        });

        describe('#getPage', () => {
            it('WHEN type code, type, uuid and uid are provided THEN it should assign them to page', () => {
                builder.pageTypeSelected(pageType);
                builder.pageTemplateSelected(pageTemplate);

                const actual = builder.getPage();

                expect(actual).toEqual({
                    typeCode: CMSPageTypes.ContentPage,
                    itemtype: CMSPageTypes.ContentPage,
                    type: 'type',
                    masterTemplate: 'uuid',
                    template: 'uid',
                    restrictions: []
                } as unknown as ICMSPage);
            });

            it('WHEN type code, type, uuid and uid are not provided THEN it should assign null to proper properties in page', () => {
                const actual = builder.getPage();

                expect(actual).toEqual({
                    typeCode: null,
                    itemtype: null,
                    type: null,
                    masterTemplate: null,
                    template: null,
                    restrictions: []
                } as unknown as ICMSPage);
            });
        });

        it('should set page uid', () => {
            builder.setPageUid('UID');

            expect((builder as any).page.uid).toEqual('UID');
        });

        it('should set page restrictions', () => {
            const onlyOneRestrictionMustApply = true;
            const restrictions: string[] = ['uuidOfRestrictionOne'];

            builder.setRestrictions(onlyOneRestrictionMustApply, restrictions);
            expect((builder as any).page.onlyOneRestrictionMustApply).toEqual(true);
            expect((builder as any).page.restrictions).toEqual(restrictions);
        });

        it('should return page info structure', async () => {
            (builder as any).page.defaultPage = true;
            await builder.pageTypeSelected(pageType);

            const actual = builder.getPageInfoStructure();

            expect(actual).toEqual(mockInfoFields);
        });

        describe('#displayConditionSelected', () => {
            it('WHEN provided param is primary page THEN it should set label to null and hide step by calling restrictions handler AND update page info structure', async () => {
                spyOn(builder as any, 'updatePageInfoFields');
                await builder.displayConditionSelected(mockConditionResult);

                expect((builder as any).page.label).toEqual(null);
                expect((builder as any).page.defaultPage).toEqual(true);
                expect((builder as any).page.homepage).toEqual(true);
                expect(mockRestrictionsStepHandler.hideStep).toHaveBeenCalled();
                expect((builder as any).updatePageInfoFields).toHaveBeenCalled();
            });

            it('WHEN provided param is not primary page THEN it should set label from primary page provided in param, show step by calling restrictions handler AND update page info structure', async () => {
                spyOn(builder as any, 'updatePageInfoFields');
                await builder.displayConditionSelected({
                    ...mockConditionResult,
                    isPrimary: false
                });

                expect((builder as any).page.label).toEqual('label');
                expect((builder as any).page.defaultPage).toEqual(false);
                expect((builder as any).page.homepage).toEqual(true);
                expect(mockRestrictionsStepHandler.showStep).toHaveBeenCalled();
                expect((builder as any).updatePageInfoFields).toHaveBeenCalled();
            });
        });
    });
});
