/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSItemStructure } from 'cmscommons';
import { PageInfoMenuComponent } from 'cmssmarteditcontainer/components/pages/pageInfoMenu/PageInfoMenuComponent';
import {
    PageInfoMenuService,
    PageInfoForViewing
} from 'cmssmarteditcontainer/components/pages/services';
import {
    ToolbarItemInternal,
    ICatalogService,
    SystemEventService,
    IUriContext,
    IPageService,
    ICMSPage,
    IUserTrackingService
} from 'smarteditcommons';

describe('PageInfoMenuComponent', () => {
    let component: PageInfoMenuComponent;
    let toolbarItem: ToolbarItemInternal;
    let pageInfoMenuService: jasmine.SpyObj<PageInfoMenuService>;
    let pageService: jasmine.SpyObj<IPageService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    let unsubscribe: jasmine.Spy;

    const mockUriContext = {
        context: 'context'
    } as IUriContext;
    const mockPageInfo = {
        uid: 'uid',
        typeCode: 'ContentPage',
        defaultPage: false
    } as ICMSPage;
    const mockCurrentPageInfo = {
        ...mockPageInfo,
        content: { uid: 'contentUid' }
    } as PageInfoForViewing;
    const mockPageStructure = {
        category: 'category',
        code: 'code'
    } as CMSItemStructure;

    beforeEach(() => {
        unsubscribe = jasmine.createSpy();

        toolbarItem = {
            isOpen: true
        } as ToolbarItemInternal;

        pageInfoMenuService = jasmine.createSpyObj<PageInfoMenuService>('pageInfoMenuService', [
            'openPageEditor',
            'getCurrentPageInfo',
            'getPageStructureForViewing'
        ]);
        pageInfoMenuService.getCurrentPageInfo.and.returnValue(
            Promise.resolve(mockCurrentPageInfo)
        );
        pageInfoMenuService.getPageStructureForViewing.and.returnValue(
            Promise.resolve(mockPageStructure)
        );

        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);
        pageService.getCurrentPageInfo.and.returnValue(Promise.resolve(mockPageInfo));

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);
        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockUriContext));

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'subscribe'
        ]);
        systemEventService.subscribe.and.returnValue(unsubscribe);

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new PageInfoMenuComponent(
            toolbarItem,
            pageInfoMenuService,
            pageService,
            catalogService,
            systemEventService,
            userTrackingService
        );
    });

    describe('initialize', () => {
        it('WHEN ngOnInit is called THEN it should set cmsPage, uriContext and permissions and subscribe an system event', async () => {
            await component.ngOnInit();

            expect(component.cmsPage).toEqual(mockPageInfo);
            expect(component.uriContext).toEqual(mockUriContext);
            expect(component.editPagePermission).toEqual([
                {
                    names: ['se.edit.page.type'],
                    context: {
                        typeCode: 'ContentPage'
                    }
                },
                {
                    names: ['se.edit.page.link']
                }
            ]);

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                'EVENT_CONTENT_CATALOG_UPDATE',
                jasmine.any(Function)
            );
        });
    });

    describe('on destroy', () => {
        it('WHEN component gets destroyed it should unsubscribe from an event', async () => {
            await component.ngOnInit();

            component.ngOnDestroy();

            expect(unsubscribe).toHaveBeenCalled();
        });
    });

    describe('onEditPageClick', () => {
        it('WHEN edit is clicked THEN it should open page editor and close dropdown', async () => {
            await component.ngOnInit();
            component.pageInfo = mockCurrentPageInfo;

            component.onEditPageClick();

            expect(component.toolbarItem.isOpen).toEqual(false);

            expect(pageInfoMenuService.openPageEditor).toHaveBeenCalledWith({
                uid: 'contentUid'
            } as any);
        });
    });

    describe('onDropdownToggle', () => {
        it('WHEN open is false THEN it should do nothing', async () => {
            await component.onDropdownToggle(false);

            expect(pageInfoMenuService.getCurrentPageInfo).not.toHaveBeenCalled();
            expect(pageInfoMenuService.getPageStructureForViewing).not.toHaveBeenCalled();
        });

        it('WHEN open is true THEN it should set page info, page structure and component ready to true AND track user action', async () => {
            await component.onDropdownToggle(true);

            expect(component.pageInfo).toEqual(mockCurrentPageInfo);
            expect(component.pageStructure).toEqual(mockPageStructure);

            expect(pageInfoMenuService.getCurrentPageInfo).toHaveBeenCalled();
            expect(pageInfoMenuService.getPageStructureForViewing).toHaveBeenCalledWith(
                'ContentPage',
                false
            );
            expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
        });
    });
});
