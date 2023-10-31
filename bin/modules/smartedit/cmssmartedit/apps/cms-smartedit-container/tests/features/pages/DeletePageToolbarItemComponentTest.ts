/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { DeletePageToolbarItemComponent } from 'cmssmarteditcontainer/components/pages/deletePageMenu';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    CrossFrameEventService,
    SystemEventService,
    IPageService,
    IUserTrackingService
} from 'smarteditcommons';

describe('DeletePageToolbarItemComponent', () => {
    let pageService: jasmine.SpyObj<IPageService>;
    let pageFacade: jasmine.SpyObj<PageFacade>;
    let managePageService: jasmine.SpyObj<ManagePageService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    let component: DeletePageToolbarItemComponent;
    beforeEach(() => {
        pageService = jasmine.createSpyObj<IPageService>('pageService', ['getCurrentPageInfo']);
        pageFacade = jasmine.createSpyObj<PageFacade>('pageFacade', ['retrievePageUriContext']);
        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'softDeletePage',
            'isPageTrashable',
            'getDisabledTrashTooltipMessage'
        ]);
        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['subscribe']
        );
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new DeletePageToolbarItemComponent(
            pageService,
            pageFacade,
            managePageService,
            systemEventService,
            crossFrameEventService,
            cdr,
            userTrackingService,
            null
        );
    });

    describe('initialize', () => {
        it('subscribes to Page Change event', async () => {
            await component.ngOnInit();

            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_CHANGE',
                jasmine.any(Function)
            );
        });

        describe('updates toolbar', () => {
            it('GIVEN no uriContext THEN the component is not ready', async () => {
                pageFacade.retrievePageUriContext.and.returnValue(Promise.resolve() as any);

                await component.ngOnInit();

                expect(component.uriContext).toBeUndefined();
                expect(component.isReady).toBe(false);
                expect(component.isDeletePageEnabled).toBe(false);
                expect(component.tooltipMessage).toBe(null);
            });

            it(`GIVEN uriContext THEN it sets uriContext
              AND sets pageInfo
              AND checks if Page can be deleted
              AND sets Tooltip Message
              AND marks component as ready`, async () => {
                pageFacade.retrievePageUriContext.and.returnValue(Promise.resolve({}));
                pageService.getCurrentPageInfo.and.returnValue(Promise.resolve({} as any));
                managePageService.isPageTrashable.and.returnValue(Promise.resolve(false));
                managePageService.getDisabledTrashTooltipMessage.and.returnValue(
                    Promise.resolve('tooltip message')
                );

                await component.ngOnInit();

                expect(component.pageInfo).toBeDefined();
                expect(component.isDeletePageEnabled).toBeDefined();
                expect(component.tooltipMessage).toBeDefined();
                expect(component.isReady).toBe(true);
            });
        });
    });

    it('WHEN component is destroyed THEN it unsubscribes from Page Change event', async () => {
        const unRegPageChangeSpy = jasmine.createSpy('unRegPageChange');
        (component as any).unRegPageChange = unRegPageChangeSpy;

        await component.ngOnDestroy();

        expect(unRegPageChangeSpy).toHaveBeenCalled();
    });

    it('WHEN deletePage is called THEN it delegates deletion to a service AND publishes Content Catalog Update after AND track user action', async () => {
        const mockPageInfo: any = {};
        const mockUriContext = {};
        pageService.getCurrentPageInfo.and.returnValue(mockPageInfo);
        component.uriContext = mockUriContext;

        await component.deletePage();

        expect(managePageService.softDeletePage).toHaveBeenCalledWith(mockPageInfo, mockUriContext);

        expect(systemEventService.publishAsync).toHaveBeenCalledWith(
            'EVENT_CONTENT_CATALOG_UPDATE'
        );
        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });

    describe('resolveTooltipMessage', () => {
        it('GIVEN page can be deleted THEN it resolves with null', async () => {
            const message = await (component as any).resolveTooltipMessage(true, null, null);

            expect(message).toBeNull();
        });

        it('GIVEN page cannot be deleted THEN it gets the message from a service', async () => {
            const mockMessage = 'tooltip message';
            managePageService.getDisabledTrashTooltipMessage.and.returnValue(
                Promise.resolve(mockMessage)
            );
            const message = await (component as any).resolveTooltipMessage(false, null, null);

            expect(message).toBe(mockMessage);
        });
    });
});
