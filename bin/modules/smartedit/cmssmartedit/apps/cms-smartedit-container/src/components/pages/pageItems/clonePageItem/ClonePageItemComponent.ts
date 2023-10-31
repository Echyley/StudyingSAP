/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Inject } from '@angular/core';
import {
    MultiNamePermissionContext,
    DROPDOWN_MENU_ITEM_DATA,
    IDropdownMenuItemData,
    ICMSPage,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { ClonePageWizardService } from '../../clonePageWizard';

/**
 * ClonePageItemComponent builds an item allowing for the cloning of a given CMS
 * page.
 */
@Component({
    selector: 'se-clone-page-item',
    templateUrl: './ClonePageItemComponent.html'
})
export class ClonePageItemComponent implements OnInit {
    public pageInfo: ICMSPage;
    public clonePagePermission: MultiNamePermissionContext[];

    constructor(
        @Inject(DROPDOWN_MENU_ITEM_DATA) private dropdownMenuData: IDropdownMenuItemData,
        private clonePageWizardService: ClonePageWizardService,
        private userTrackingService: IUserTrackingService
    ) {}

    ngOnInit(): void {
        this.pageInfo = this.dropdownMenuData.selectedItem;
        this.clonePagePermission = [
            {
                names: ['se.clone.page.type'],
                context: {
                    typeCode: this.pageInfo.typeCode
                }
            }
        ];
    }

    onClickOnClone(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.PAGE_MANAGEMENT,
            'Clone'
        );
        this.clonePageWizardService.openClonePageWizard(this.pageInfo);
    }
}
