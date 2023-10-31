/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { DeletePageItemComponent } from 'cmssmarteditcontainer/components/pages/pageItems/deletePageItem/DeletePageItemComponent';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    ICatalogService,
    SystemEventService,
    IDropdownMenuItemData,
    IUserTrackingService
} from 'smarteditcommons';

describe('DeletePageItemComponent', () => {
    let component: DeletePageItemComponent;

    let managePageService: jasmine.SpyObj<ManagePageService>;
    let systemEventService: jasmine.SpyObj<SystemEventService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let dropdownMenuData: IDropdownMenuItemData;
    let ref: jasmine.SpyObj<ChangeDetectorRef>;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const mockPageInfo = {
        uid: 'MOCKED_SELECTED_ITEM_UID',
        typeCode: 'typeCode'
    };
    const mockUriContext = { context: 'context' };
    const mockTooltipMessage = 'Cannot delete page';

    beforeEach(() => {
        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'isPageTrashable',
            'getDisabledTrashTooltipMessage',
            'softDeletePage'
        ]);
        managePageService.getDisabledTrashTooltipMessage.and.returnValue(
            Promise.resolve(mockTooltipMessage)
        );
        managePageService.softDeletePage.and.returnValue(Promise.resolve());

        systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', [
            'publishAsync'
        ]);

        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext'
        ]);
        catalogService.retrieveUriContext.and.returnValue(Promise.resolve(mockUriContext));

        ref = jasmine.createSpyObj<ChangeDetectorRef>('ref', ['markForCheck']);
        dropdownMenuData = {
            dropdownItem: {},
            selectedItem: mockPageInfo
        };

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new DeletePageItemComponent(
            dropdownMenuData,
            managePageService,
            systemEventService,
            catalogService,
            ref,
            userTrackingService
        );
    });

    describe('GIVEN component is initialized', () => {
        it('WHEN page is trashable THEN it should set delete permissions, isDeletePageEnable to true and set tooltipMessage to null', async () => {
            managePageService.isPageTrashable.and.returnValue(Promise.resolve(true));

            await component.ngOnInit();

            expect(component.tooltipMessage).toEqual(null);
            expect(component.isDeletePageEnabled).toEqual(true);
            expect(component.deletePagePermission).toEqual([
                {
                    names: ['se.delete.page.type'],
                    context: {
                        typeCode: 'typeCode'
                    }
                },
                {
                    names: ['se.act.on.page.in.workflow'],
                    context: {
                        pageInfo: mockPageInfo
                    }
                }
            ]);

            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(managePageService.isPageTrashable).toHaveBeenCalledWith(
                mockPageInfo as any,
                mockUriContext
            );
            expect(managePageService.getDisabledTrashTooltipMessage).not.toHaveBeenCalled();
        });

        it('WHEN page is NOT trashable THEN it should set delete permissions, isDeletePageEnable to false and set tooltipMessage by getting it from service', async () => {
            managePageService.isPageTrashable.and.returnValue(Promise.resolve(false));

            await component.ngOnInit();

            expect(component.tooltipMessage).toEqual(mockTooltipMessage);
            expect(component.isDeletePageEnabled).toEqual(false);
            expect(component.deletePagePermission).toEqual([
                {
                    names: ['se.delete.page.type'],
                    context: {
                        typeCode: 'typeCode'
                    }
                },
                {
                    names: ['se.act.on.page.in.workflow'],
                    context: {
                        pageInfo: mockPageInfo
                    }
                }
            ]);

            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(managePageService.isPageTrashable).toHaveBeenCalledWith(
                mockPageInfo as any,
                mockUriContext
            );
            expect(managePageService.getDisabledTrashTooltipMessage).toHaveBeenCalledWith(
                mockPageInfo as any,
                mockUriContext
            );
        });
    });

    it('GIVEN component is initialized WHEN delete is clicked THEN it should get uri context, soft delete page and publish an event AND track user action ', async () => {
        await component.ngOnInit();
        await component.onClickOnDeletePage();

        expect(catalogService.retrieveUriContext).toHaveBeenCalled();
        expect(managePageService.softDeletePage).toHaveBeenCalledWith(
            mockPageInfo as any,
            mockUriContext
        );
        expect(systemEventService.publishAsync).toHaveBeenCalledWith(
            'EVENT_CONTENT_CATALOG_UPDATE'
        );
        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });
});
