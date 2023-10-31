/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import { PageEditorModalConfigService } from 'cmssmarteditcontainer/components/pages/services';
import { ContextAwarePageStructureService } from 'cmssmarteditcontainer/services';
import {
    GenericEditorAttribute,
    CmsitemsRestService,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

describe('PageEditorModalService', () => {
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let contextAwarePageStructureService: jasmine.SpyObj<ContextAwarePageStructureService>;
    let contextAwareEditableItemService: jasmine.SpyObj<IContextAwareEditableItemService>;
    let translateService: jasmine.SpyObj<TranslateService>;

    let service: PageEditorModalConfigService;
    beforeEach(() => {
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'getById'
        ]);

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['isPagePrimary']);

        contextAwarePageStructureService = jasmine.createSpyObj<ContextAwarePageStructureService>(
            'contextAwarePageStructureService',
            ['getPageStructureForPageEditing']
        );

        contextAwareEditableItemService = jasmine.createSpyObj<IContextAwareEditableItemService>(
            'contextAwareEditableItemService',
            ['isItemEditable']
        );

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        service = new PageEditorModalConfigService(
            cmsitemsRestService,
            pageService,
            contextAwarePageStructureService,
            contextAwareEditableItemService,
            translateService
        );
    });

    describe('create', () => {
        let mockCmsPageResponse: ICMSPage;
        let mockFields: GenericEditorAttribute[];
        beforeEach(() => {
            mockCmsPageResponse = {
                typeCode: '',
                uid: '',
                masterTemplateId: '0'
            } as ICMSPage;

            mockFields = [
                {
                    qualifier: 'restrictions'
                },
                { qualifier: 'name' }
            ] as GenericEditorAttribute[];

            cmsitemsRestService.getById.and.returnValue(Promise.resolve(mockCmsPageResponse));
        });

        it('GIVEN Page is Primary Page THEN it creates config properly', async () => {
            const mockPage = {} as ICMSPage;
            const mockFieldsResponse = {
                attributes: mockFields
            };

            const expectedFields = [{ qualifier: 'name' }] as GenericEditorAttribute[];

            contextAwarePageStructureService.getPageStructureForPageEditing.and.returnValue(
                Promise.resolve(mockFieldsResponse)
            );
            pageService.isPagePrimary.and.returnValue(Promise.resolve(true));

            const config = await service.create(mockPage);

            expect(config.content).toBe(mockCmsPageResponse);
            expect(config.content.template).toBe('0');
            expect(config.structure.attributes).toEqual(jasmine.objectContaining(expectedFields));
        });

        it('GIVEN Page uid is provided AND Page is in Readonly Mode THEN it sets readOnlyMode and messages', async () => {
            const mockPage = { uid: '1' } as ICMSPage;
            contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(false));

            const config = await service.create(mockPage);

            expect(config.readOnlyMode).toBe(true);
            expect(config.messages).toBeDefined();
        });

        it('GIVEN Page uid is provided AND Page is not in Readonly Mode THEN it sets only readOnlyMode', async () => {
            const mockPage = { uid: '1' } as ICMSPage;
            contextAwareEditableItemService.isItemEditable.and.returnValue(Promise.resolve(true));

            const config = await service.create(mockPage);

            expect(config.readOnlyMode).toBe(false);
            expect(config.messages).toBeUndefined();
        });

        it('GIVEN Page uid is not provided THEN it does not set readOnlyMode', async () => {
            const mockPage = {} as ICMSPage;

            const config = await service.create(mockPage);

            expect(config.readOnlyMode).toBeUndefined();
            expect(config.messages).toBeUndefined();
        });
    });
});
