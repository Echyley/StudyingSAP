/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnInit,
    Type,
    ViewRef
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ManagePageService } from 'cmssmarteditcontainer/services/pages/ManagePageService';
import {
    DataTableComponentData,
    DATA_TABLE_COMPONENT_DATA,
    ICatalogVersionPermissionService,
    IDropdownMenuItem,
    ICMSPage
} from 'smarteditcommons';
import {
    EditPageItemComponent,
    ClonePageItemComponent,
    DeletePageItemComponent,
    SyncPageItemComponent
} from '../pageItems';

@Component({
    selector: 'se-page-list-dropdown-items-wrapper',
    template: `<div
        *seHasOperationPermission="'se.edit.page'"
        class="paged-list-table__body__td paged-list-table__body__td-menu">
        <se-dropdown-menu
            [dropdownItems]="dropdownItems"
            [selectedItem]="item"
            class="pull-right"></se-dropdown-menu>
    </div>`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageListDropdownItemsWrapperComponent implements OnInit {
    public dropdownItems: IDropdownMenuItem[] = [];
    public item: ICMSPage;

    constructor(
        private route: ActivatedRoute,
        @Inject(DATA_TABLE_COMPONENT_DATA) public data: DataTableComponentData<ICMSPage>,
        private catalogVersionPermissionService: ICatalogVersionPermissionService,
        private managePageService: ManagePageService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): Promise<void> {
        return this.setDropdownItems();
    }

    private async setDropdownItems(): Promise<void> {
        this.item = this.data.item;

        const { catalogId, catalogVersion } = this.route.snapshot.params;

        const [hasSyncPermission, hasClonePermission] = await Promise.all([
            this.catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion(
                catalogId,
                catalogVersion
            ),
            /**
             * Normally we would use something like `isPermitted` from PermissionService but as we are in pages
             * which are container (outer) part of app and PermissionService uses some data retrieved from inner
             * (which in this case can be also obtained from outer) we had to use methods available in outer
             * to see if the clone button can be cloned or not.
             *
             * Using here PermissionService.isPermitted will throw error
             * as there is no iframe in Pages site
             */
            this.managePageService.isPageCloneable(
                this.data.item.uuid,
                this.data.item.catalogVersion
            )
        ]);

        this.addDropdownItem(EditPageItemComponent);

        if (hasSyncPermission) {
            this.addDropdownItem(SyncPageItemComponent);
        }

        if (hasClonePermission) {
            this.addDropdownItem(ClonePageItemComponent);
        }

        this.addDropdownItem(DeletePageItemComponent);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private addDropdownItem(item: Type<any>): void {
        this.dropdownItems = this.dropdownItems.concat({
            component: item
        });
    }
}
