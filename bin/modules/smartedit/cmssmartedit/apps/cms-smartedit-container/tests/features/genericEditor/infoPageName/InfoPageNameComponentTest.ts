/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InfoPageNameComponent } from 'cmssmarteditcontainer/components/genericEditor/infoPageName';
import {
    GenericEditorWidgetData,
    ICatalogService,
    IUriContext,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

describe('InfoPageNameComponent', () => {
    let component: InfoPageNameComponent;
    let injectedData: GenericEditorWidgetData<ICMSPage>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let pageService: jasmine.SpyObj<IPageService>;

    const mockUri = {
        context: 'context'
    } as IUriContext;

    const mockCmsPage = {
        uid: 'uid'
    } as ICMSPage;

    beforeEach(() => {
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);
        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockUri));

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);
        pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockCmsPage));

        injectedData = {
            qualifier: 'name',
            field: { cmsStructureType: 'InfoPageName', qualifier: 'name' },
            model: { name: 'someName' }
        } as GenericEditorWidgetData<ICMSPage>;

        component = new InfoPageNameComponent(catalogService, pageService, injectedData);
    });

    it('WHEN created THEN it should set field, model and qualifier', () => {
        expect(component.qualifier).toEqual('name');
        expect(component.model).toEqual({ name: 'someName' } as ICMSPage);
        expect(component.field).toEqual({ cmsStructureType: 'InfoPageName', qualifier: 'name' });
    });

    it('WHEN initialized THEN it should call page and catalog service and set info', async () => {
        await component.ngOnInit();

        expect(component.cmsPage).toEqual(mockCmsPage);
        expect(component.uriContext).toEqual(mockUri);
    });
});
