/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentService, CMSItemStructure } from 'cmscommons';
import { ComponentTypesTabComponent } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/tabs/ComponentTypesTabComponent';
import { ComponentMenuTabModel } from 'cmssmarteditcontainer/components/cmsComponents/componentMenu/types';
import {
    LogService,
    ICatalogService,
    TabData,
    CrossFrameEventService,
    LanguageService,
    IPageService,
    IPage,
    ICMSPage
} from 'smarteditcommons';

describe('ComponentTypesTabComponent', () => {
    let component: ComponentTypesTabComponent;
    let componentAny: any;
    let logService: jasmine.SpyObj<LogService>;
    let componentService: jasmine.SpyObj<ComponentService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let languageService: jasmine.SpyObj<LanguageService>;

    const mockSupportedComponents = [{ code: 'code1' }, { code: 'code2' }] as CMSItemStructure[];
    const mockPage = { uid: 'pageUid' } as ICMSPage;
    const mockContext = {
        CURRENT_CONTEXT_CATALOG: 'catalog',
        CURRENT_CONTEXT_CATALOG_VERSION: 'version'
    };
    let injectedData: TabData<ComponentMenuTabModel>;

    beforeEach(() => {
        injectedData = {
            model: {
                isOpen: true
            },
            tab: {
                active: true
            }
        } as TabData<ComponentMenuTabModel>;
        logService = jasmine.createSpyObj<LogService>('logService', ['error']);

        componentService = jasmine.createSpyObj<ComponentService>('componentService', [
            'getSupportedComponentTypesForCurrentPage'
        ]);

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);

        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );

        languageService = jasmine.createSpyObj<LanguageService>('languageService', [
            'getResolveLocale'
        ]);

        component = new ComponentTypesTabComponent(
            injectedData,
            logService,
            componentService,
            pageService,
            catalogService,
            crossFrameEventService,
            languageService
        );
        componentAny = component;
    });

    beforeEach(() => {
        componentService.getSupportedComponentTypesForCurrentPage.and.returnValue(
            Promise.resolve<any>({
                response: mockSupportedComponents
            })
        );

        pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockPage));

        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockContext));

        languageService.getResolveLocale.and.returnValue(Promise.resolve('en'));
    });

    it('WHEN initialized THEN it should init component values', () => {
        expect(component.componentsContext).toEqual({ items: [] });
        expect(componentAny.pageInfo).toEqual(null);
        expect(component.searchTerm).toEqual('');
        expect(componentAny.uriContext).toEqual(null);
    });

    it('WHEN search value changes THEN it should assign that new value', () => {
        component.onSearchTermChanged('lorem');

        expect(component.searchTerm).toEqual('lorem');
    });

    describe('loadComponentTypes', () => {
        it('WHEN retrieving supported component fails THEN it should handle that error with log service', async () => {
            componentService.getSupportedComponentTypesForCurrentPage.and.returnValue(
                Promise.reject('error')
            );

            await component.loadComponentTypes('', 10, 1);

            expect(logService.error).toHaveBeenCalledWith(
                'ComponentTypesTab - loadComponentTypes - error loading types.',
                'error'
            );
        });

        it('GIVEN getting page info returns empty data WHEN retrieving supported components THEN it should handle that without throwing as there are unaccessible fields from pageInfo', async () => {
            pageService.getCurrentPageInfo.and.returnValue(Promise.resolve() as any);

            await component.loadComponentTypes('', 10, 1);

            expect(logService.error).toHaveBeenCalledWith(
                'ComponentTypesTab - loadComponentTypes - error loading types.',
                jasmine.any(Error)
            );
        });

        it('GIVEN getting uriContext returns empty data WHEN retrieving supported components THEN it should handle that without throwing as there are unaccessible fields from uriContext', async () => {
            catalogService.retrieveUriContext.and.returnValue(Promise.resolve() as any);

            await component.loadComponentTypes('', 10, 1);

            expect(logService.error).toHaveBeenCalledWith(
                'ComponentTypesTab - loadComponentTypes - error loading types.',
                jasmine.any(Error)
            );
        });

        it('should load pageInfo, uriContext and then return supported components', async () => {
            const actual = await component.loadComponentTypes('', 10, 1);

            expect(actual).toEqual({
                response: mockSupportedComponents
            } as IPage<CMSItemStructure>);
            expect(componentAny.pageInfo).toEqual(mockPage);
            expect(componentAny.uriContext).toEqual(mockContext);

            expect(pageService.getCurrentPageInfo).toHaveBeenCalled();
            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(componentService.getSupportedComponentTypesForCurrentPage).toHaveBeenCalledWith({
                pageId: mockPage.uid,
                catalogId: mockContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion: mockContext.CURRENT_CONTEXT_CATALOG_VERSION,
                langIsoCode: 'en',
                mask: '',
                pageSize: 10,
                currentPage: 1
            } as any);
        });
    });
});
