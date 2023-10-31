/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { ComponentCloneInfoFormComponent } from 'cmssmarteditcontainer/components/pages/clonePageWizard/components/clonePageInfo/ComponentCloneInfoFormComponent';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import {
    GenericEditorAPI,
    GENERIC_EDITOR_LOADED_EVENT,
    ICatalogVersion,
    LanguageService,
    SystemEventService,
    IPageService
} from 'smarteditcommons';

describe('ComponentCloneInfoForm', () => {
    const mockPages = [
        {
            uid: 'somePageUid',
            name: 'Some Page Name',
            typeCode: 'somePageTypeCode',
            label: 'somePageLabel'
        }
    ];
    const mockUriContext = {
        siteId: 'testSite',
        catalogId: 'testCatalog',
        version: 'testVersion'
    };
    const mockCatalogVersion = {
        siteId: 'otherTestSite',
        catalogId: 'otherTestCatalog',
        version: 'otherTestVersion'
    } as unknown as ICatalogVersion;
    const mockEditorApi = {
        getContent() {
            return mockPages[0];
        },
        clearMessages() {}
    } as unknown as GenericEditorAPI;
    let component: ComponentCloneInfoFormComponent;
    let componentAny: any;
    let translateService: jasmine.SpyObj<TranslateService>;
    let languageService: jasmine.SpyObj<LanguageService>;
    let pageFacade: jasmine.SpyObj<PageFacade>;
    let pageService: jasmine.SpyObj<IPageService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;

    beforeEach(() => {
        translateService = jasmine.createSpyObj<TranslateService>('translateService', ['instant']);
        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getLanguagesForSite'
        ]);
        pageFacade = jasmine.createSpyObj<PageFacade>('pageFacade', ['contentPageWithLabelExists']);
        pageService = jasmine.createSpyObj<IPageService>('pageService', [
            'primaryPageForPageTypeExists'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync',
            'subscribe'
        ]);

        component = new ComponentCloneInfoFormComponent(
            translateService,
            languageService,
            pageFacade,
            pageService,
            systemEventService
        );
        component.uriContext = mockUriContext;
        component.targetCatalogVersion = mockCatalogVersion;
        componentAny = component;
    });

    it('GIVEN a category page WHEN component is initialized and there exists a primary category page in the selected catalog version THEN it should set "catalogVersionContainsPageWithSameTypeCode" to true', async () => {
        pageService.primaryPageForPageTypeExists.and.returnValue(Promise.resolve(true));

        //GIVEN
        component.pageTypeCode = 'CategoryPage';

        //WHEN
        await component.ngOnInit();

        //THEN
        expect(component.catalogVersionContainsPageWithSameTypeCode).toBe(true);
    });

    it('GIVEN a content page WHEN component is initialized THEN "catalogVersionContainsPageWithSameTypeCode" should be false', async () => {
        //GIVEN
        component.pageTypeCode = 'ContentPage';

        //WHEN
        await component.ngOnInit();

        //THEN
        expect(component.catalogVersionContainsPageWithSameTypeCode).toBe(false);
    });

    it('GIVEN a content page WHEN the page exists in the selected catalog version with that label AND generic editor has loaded THEN it should trigger an editor validation event', async () => {
        pageFacade.contentPageWithLabelExists.and.returnValue(Promise.resolve(true));
        component.setGenericEditorApi(mockEditorApi);

        //GIVEN
        component.pageTypeCode = 'ContentPage';

        //WHEN
        componentAny.pageLabel = 'alreadyExists';
        await component.ngOnInit();
        const warningMessageHandler = systemEventService.subscribe.calls.argsFor(0)[1];

        await warningMessageHandler(
            GENERIC_EDITOR_LOADED_EVENT,
            'COMPONENT_CLONE_INFO_FORM_GENERIC_ID'
        );

        //THEN
        expect(systemEventService.publishAsync).toHaveBeenCalled();
    });

    it('GIVEN a content page WHEN the page does not exist in the selected catalog version with that label AND generic editor has loaded THEN it should clear all validation messages from the editor', async () => {
        pageFacade.contentPageWithLabelExists.and.returnValue(Promise.resolve(false));
        component.setGenericEditorApi(mockEditorApi);
        spyOn(componentAny.pageInfoEditorApi, 'clearMessages').and.callThrough();

        //GIVEN
        component.pageTypeCode = 'ContentPage';

        //WHEN
        componentAny.pageLabel = 'somethingElse';
        await component.ngOnInit();
        const warningMessageHandler = systemEventService.subscribe.calls.argsFor(0)[1];

        await warningMessageHandler(
            GENERIC_EDITOR_LOADED_EVENT,
            'COMPONENT_CLONE_INFO_FORM_GENERIC_ID'
        );

        //THEN
        expect(componentAny.pageInfoEditorApi.clearMessages).toHaveBeenCalled();
    });

    describe('setGenericEditorApi', () => {
        it('WHEN target catalog version and uri context are different THEN it should set getLanguages method for editor API', () => {
            component.setGenericEditorApi(mockEditorApi);

            expect(componentAny.pageInfoEditorApi.getLanguages).toEqual(jasmine.any(Function));
        });

        it('GIVEN get languages method for editor api is set WHEN it gets called THEN it should call language service', async () => {
            component.targetCatalogVersion = { siteId: 'catalogsiteId' } as ICatalogVersion;

            component.setGenericEditorApi(mockEditorApi);
            const method = componentAny.pageInfoEditorApi.getLanguages;

            await method();

            expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('catalogsiteId');
        });
    });
});
