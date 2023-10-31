/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { EditPageItemComponent } from 'cmssmarteditcontainer/components/pages/pageItems/editPageItem/EditPageItemComponent';
import { PageInfoMenuService } from 'cmssmarteditcontainer/components/pages/services';
import { IDropdownMenuItemData, ICMSPage, IUserTrackingService } from 'smarteditcommons';

describe('EditPageItemComponent', () => {
    let component: EditPageItemComponent;

    let pageInfoMenuService: PageInfoMenuService;
    let dropdownMenuData: IDropdownMenuItemData;
    let userTrackingService: jasmine.SpyObj<IUserTrackingService>;

    const mockPageInfo = {
        name: 'MOCKED_PAGE_INFO_UID',
        typeCode: 'pageTypeCode'
    } as ICMSPage;

    beforeEach(() => {
        pageInfoMenuService = jasmine.createSpyObj('pageInfoMenuService', ['openPageEditor']);

        dropdownMenuData = {
            dropdownItem: {},
            selectedItem: mockPageInfo
        };

        userTrackingService = jasmine.createSpyObj<IUserTrackingService>('userTrackingService', [
            'trackingUserAction'
        ]);

        component = new EditPageItemComponent(
            dropdownMenuData,
            pageInfoMenuService,
            userTrackingService
        );
    });

    it('WHEN initialized THEN it should set edit page permissions', () => {
        component.ngOnInit();

        expect(component.editPagePermission).toEqual([
            {
                names: ['se.edit.page.type'],
                context: {
                    typeCode: 'pageTypeCode'
                }
            },
            {
                names: ['se.act.on.page.in.workflow'],
                context: {
                    pageInfo: mockPageInfo
                }
            }
        ]);
    });

    it('onClickOnEdit - sends an event when the page edition is resolved AND track user action', () => {
        component.ngOnInit();
        component.onClickOnEdit();

        expect(pageInfoMenuService.openPageEditor).toHaveBeenCalledWith(component.pageInfo);
        expect(userTrackingService.trackingUserAction).toHaveBeenCalled();
    });
});
