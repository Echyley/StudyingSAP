/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV } from 'cmscommons';
import { PermanentlyDeletePageItemComponent } from 'cmssmarteditcontainer/components/pages/pageItems';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    ICatalogService,
    CrossFrameEventService,
    IDropdownMenuItemData,
    CmsitemsRestService,
    ICMSPage,
    CMSPageStatus,
    IUserTrackingService
} from 'smarteditcommons';

describe('PermanentlyDeletePageItemComponent', () => {
    let component: PermanentlyDeletePageItemComponent;
    let managePageService: jasmine.SpyObj<ManagePageService>;
    let cmsitemsRestService: jasmine.SpyObj<CmsitemsRestService>;
    let catalogService: jasmine.SpyObj<ICatalogService>;
    let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
    let dropdownMenuData: IDropdownMenuItemData;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    let subscribeSpy: jasmine.Spy;

    const mockSelectedItem = { uid: 'some_uid', typeCode: 'typeCode' } as ICMSPage;

    const setCmsItemsGetAndUpdateComponent = async (response: any[] = []): Promise<void> => {
        cmsitemsRestService.get.and.returnValue(
            Promise.resolve<any>({
                pagination: { totalCount: response.length },
                response
            })
        );
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new PermanentlyDeletePageItemComponent(
            managePageService,
            cmsitemsRestService,
            catalogService,
            crossFrameEventService,
            userTrackingService,
            dropdownMenuData
        );

        await component.ngOnInit();
    };

    beforeEach(() => {
        managePageService = jasmine.createSpyObj<ManagePageService>('managePageService', [
            'hardDeletePage'
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
            ['subscribe']
        );
        dropdownMenuData = {
            dropdownItem: {},
            selectedItem: mockSelectedItem
        };

        catalogService.retrieveUriContext.and.returnValue(
            Promise.resolve({ CURRENT_CONTEXT_CATALOG: 'currentContextCatalog' })
        );
        catalogService.getContentCatalogActiveVersion.and.returnValue(
            Promise.resolve('activeVersion')
        );
        cmsitemsRestService.get.and.returnValue(
            Promise.resolve<any>({
                pagination: {
                    totalCount: 0
                },
                response: []
            })
        );
        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);
        subscribeSpy = jasmine.createSpy();
        crossFrameEventService.subscribe.and.returnValue(subscribeSpy);

        component = new PermanentlyDeletePageItemComponent(
            managePageService,
            cmsitemsRestService,
            catalogService,
            crossFrameEventService,
            userTrackingService,
            dropdownMenuData
        );
    });

    describe('GIVEN component is initialized', () => {
        beforeEach(async () => {
            await component.ngOnInit();
        });

        it('THEN should subscribe to event', () => {
            expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(
                EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV,
                jasmine.any(Function)
            );
        });

        it('THEN should fetch page deleting conditions', () => {
            expect(catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(catalogService.getContentCatalogActiveVersion).toHaveBeenCalledWith({
                CURRENT_CONTEXT_CATALOG: 'currentContextCatalog'
            });
            expect(cmsitemsRestService.get).toHaveBeenCalledWith({
                pageSize: 1,
                currentPage: 0,
                typeCode: 'AbstractPage',
                fields: 'BASIC',
                itemSearchParams: `uid:${mockSelectedItem.uid}`,
                catalogId: 'currentContextCatalog',
                catalogVersion: 'activeVersion'
            });
        });

        it('THEN should set page information', () => {
            expect(component.pageInfo).toEqual(mockSelectedItem);
        });

        it('THEN should set permissions in order to delete page', () => {
            expect(component.permanentlyDeletePagePermission).toEqual([
                {
                    names: ['se.permanently.delete.page.type'],
                    context: {
                        typeCode: mockSelectedItem.typeCode
                    }
                }
            ]);
        });

        it('WHEN component is destroyed THEN it should unsubscribe from an event', () => {
            component.ngOnDestroy();

            expect(subscribeSpy).toHaveBeenCalled();
        });

        describe('WHEN component is about to be permanently deleted', () => {
            it('AND component can be deleted THEN it should delete component AND track user action', () => {
                // default mock setup make hard delete possible
                component.permanentlyDelete();

                expect(managePageService.hardDeletePage).toHaveBeenCalledWith(mockSelectedItem);
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });

            it('AND component cannot be deleted THEN it should not delete component', async () => {
                await setCmsItemsGetAndUpdateComponent([{ pageStatus: CMSPageStatus.ACTIVE }]);

                component.permanentlyDelete();

                expect(managePageService.hardDeletePage).not.toHaveBeenCalled();
                expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
            });
        });

        describe('WHEN checking if component can be deleted', () => {
            it('AND there is no result from API THEN permanently delete button should be enabled', async () => {
                await setCmsItemsGetAndUpdateComponent([]);

                expect(component.isDeleteButtonDisabled()).toEqual(false);
            });

            it('AND there is result from API and first result status is deleted THEN permanently delete button should be enabled', async () => {
                await setCmsItemsGetAndUpdateComponent([{ pageStatus: CMSPageStatus.DELETED }]);

                expect(component.isDeleteButtonDisabled()).toEqual(false);
            });

            it('AND there is result from API and first result status is not deleted THEN permanently delete button should be disabled', async () => {
                await setCmsItemsGetAndUpdateComponent([{ pageStatus: CMSPageStatus.ACTIVE }]);

                expect(component.isDeleteButtonDisabled()).toEqual(true);
            });
        });
    });
});
