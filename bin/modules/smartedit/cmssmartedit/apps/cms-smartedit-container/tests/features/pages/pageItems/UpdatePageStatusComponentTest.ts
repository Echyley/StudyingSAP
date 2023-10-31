/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef } from '@angular/core';
import { EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV } from 'cmscommons';
import { UpdatePageStatusComponent } from 'cmssmarteditcontainer/components/pages/pageItems/updatePageStatus/UpdatePageStatusComponent';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    CrossFrameEventService,
    ICatalogService,
    IDropdownMenuItemData,
    CmsitemsRestService,
    ICMSPage,
    IUserTrackingService
} from 'smarteditcommons';

describe('UpdatePageStatusComponent', () => {
    let component: UpdatePageStatusComponent;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let managePageService: jasmine.SpyObj<ManagePageService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let dropdownMenuData: IDropdownMenuItemData;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const mockSelectedItem = { uid: 'some_uid' } as ICMSPage;

    beforeEach(() => {
        cdr = jasmine.createSpyObj<ChangeDetectorRef>('cdr', ['detectChanges']);
        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'trashPageInActiveCatalogVersion'
        ]);
        cmsitemsRestService = jasmine.createSpyObj<CmsitemsRestService>('cmsitemsRestService', [
            'get'
        ]);
        catalogService = jasmine.createSpyObj<ICatalogService>('catalogService', [
            'retrieveUriContext',
            'getContentCatalogActiveVersion'
        ]);
        crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>(
            'crossFrameEventService',
            ['publish']
        );
        dropdownMenuData = {
            dropdownItem: {},
            selectedItem: mockSelectedItem
        };

        catalogService.retrieveUriContext.and.returnValue(
            Promise.resolve({ CONTEXT_CATALOG: 'contextCatalog' })
        );
        catalogService.getContentCatalogActiveVersion.and.returnValue(
            Promise.resolve('activeVersion')
        );
        cmsitemsRestService.get.and.returnValue(
            Promise.resolve<any>({
                pagination: {
                    totalCount: 1
                }
            })
        );
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new UpdatePageStatusComponent(
            cdr,
            managePageService,
            cmsitemsRestService,
            catalogService,
            crossFrameEventService,
            userTrackingService,
            dropdownMenuData
        );
    });

    describe('GIVEN component is initialized', () => {
        it('THEN pageInfo is set', async () => {
            await component.ngOnInit();

            expect(component.pageInfo).toEqual(mockSelectedItem);
        });

        it('WHEN page exists in active catalog version THEN it should display the button', async () => {
            await component.ngOnInit();

            expect(component.showButton).toEqual(true);
            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith({
                CONTEXT_CATALOG: 'contextCatalog'
            });
            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: 1,
                currentPage: 0,
                typeCode: 'AbstractPage',
                fields: 'BASIC',
                itemSearchParams: `uid:${mockSelectedItem.uid}`,
                catalogId: 'contextCatalog',
                catalogVersion: 'activeVersion'
            });
        });

        it('WHEN page does NOT exist in active catalog version THEN it should not display the button', async () => {
            cmsitemsRestService.get.and.returnValue(
                Promise.resolve<any>({
                    pagination: {
                        totalCount: 0
                    }
                })
            );

            await component.ngOnInit();

            expect(component.showButton).toEqual(false);
            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith({
                CONTEXT_CATALOG: 'contextCatalog'
            });
            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: 1,
                currentPage: 0,
                typeCode: 'AbstractPage',
                fields: 'BASIC',
                itemSearchParams: `uid:${mockSelectedItem.uid}`,
                catalogId: 'contextCatalog',
                catalogVersion: 'activeVersion'
            });
        });

        describe('WHEN onClickOnSync method is called', () => {
            it('THEN should call trashPageInActiveCatalogVersion from managePageService AND publish event AND track user action', async () => {
                await component.ngOnInit();

                await component.onClickOnSync();

                expect(managePageService.trashPageInActiveCatalogVersion).toHaveBeenCalledWith(
                    mockSelectedItem.uid
                );
                expect(crossFrameEventService.publish).toHaveBeenCalledWith(
                    EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV
                );
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });
    });
});
