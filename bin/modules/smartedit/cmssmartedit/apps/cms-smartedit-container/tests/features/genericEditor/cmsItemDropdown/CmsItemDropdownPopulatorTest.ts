/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { CMSItemDropdownDropdownPopulator } from 'cmssmarteditcontainer/components/cmsComponents/cmsItemDropdown/services';
import {
    DropdownPopulatorPagePayload,
    DropdownPopulatorPayload,
    GenericEditorStackService,
    LanguageService,
    UriDropdownPopulator
} from 'smarteditcommons';

describe('CmsItemDropdownPopulator', () => {
    const STACK_ID = 'some stack id';
    const PAYLOAD = {
        field: {
            editorStackId: STACK_ID,
            uri: undefined
        }
    } as DropdownPopulatorPayload;
    const EXPECTED_URI = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';

    let genericEditorStackService: jasmine.SpyObj<GenericEditorStackService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let translateService: jasmine.SpyObj<TranslateService>;
    let uriDropdownPopulator: jasmine.SpyObj<UriDropdownPopulator>;

    let service: CMSItemDropdownDropdownPopulator;
    let serviceAny: any;
    beforeEach(() => {
        genericEditorStackService = jasmine.createSpyObj<GenericEditorStackService>(
            'genericEditorStackService',
            ['getEditorsStack']
        );

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);

        uriDropdownPopulator = jasmine.createSpyObj<UriDropdownPopulator>('uriDropdownPopulator', [
            'fetchAll',
            'fetchPage',
            'getItem'
        ]);

        service = new CMSItemDropdownDropdownPopulator(
            genericEditorStackService,
            languageService,
            translateService,
            uriDropdownPopulator
        );
        serviceAny = service;
    });

    beforeEach(() => {
        serviceAny.CMS_ITEMS_URI = EXPECTED_URI;

        genericEditorStackService.getEditorsStack.and.returnValue([
            {
                component: {
                    uuid: 'comp3'
                }
            },
            {
                component: {
                    uuid: 'comp1'
                }
            }
        ] as any);
    });

    it('WHEN fetchAll is called THEN it is properly delegated AND removes nested items', async () => {
        // GIVEN
        const items = [
            {
                uuid: 'comp1'
            },
            {
                uuid: 'comp2'
            }
        ];
        uriDropdownPopulator.fetchAll.and.returnValue(Promise.resolve(items));

        // WHEN
        const actualItems = await service.fetchAll(PAYLOAD);

        // THEN
        expect(uriDropdownPopulator.fetchAll).toHaveBeenCalledWith(PAYLOAD);
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        expect(actualItems.length).toBe(1);
        expect(actualItems[0]).toEqual({
            uuid: 'comp2'
        });
    });

    it('WHEN fetchPage is called THEN it is properly delegated AND removes nested items', async () => {
        // GIVEN
        const pagedResult = {
            response: [
                {
                    uuid: 'comp1'
                },
                {
                    uuid: 'comp2'
                }
            ],
            pagination: {
                count: 2
            }
        };
        uriDropdownPopulator.fetchPage.and.returnValue(Promise.resolve<any>(pagedResult));

        // WHEN
        const actualPage = await service.fetchPage(PAYLOAD as DropdownPopulatorPagePayload);

        // THEN
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(PAYLOAD as any);
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        expect(actualPage.pagination.count).toBe(2);
        expect(actualPage.response.length).toBe(1);
        expect(actualPage.response[0]).toEqual({
            uuid: 'comp2'
        });
    });

    it('WHEN fetchItem is called THEN it is properly delegated AND does not remove nested items', async () => {
        // GIVEN
        const expectedItem: any = { id: 'some item' };
        uriDropdownPopulator.getItem.and.returnValue(expectedItem);

        // WHEN
        const item = await service.getItem(PAYLOAD);

        // THEN
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        expect(item).toBe(expectedItem);
    });
});
