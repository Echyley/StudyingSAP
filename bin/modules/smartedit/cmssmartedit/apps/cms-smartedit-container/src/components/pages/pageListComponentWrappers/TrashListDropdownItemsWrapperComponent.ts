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
import {
    DataTableComponentData,
    DATA_TABLE_COMPONENT_DATA,
    ICatalogVersionPermissionService,
    IDropdownMenuItem,
    ICMSPage
} from 'smarteditcommons';
import {
    PermanentlyDeletePageItemComponent,
    RestorePageItemComponent,
    UpdatePageStatusComponent
} from '../pageItems';

@Component({
    selector: 'se-trash-list-dropdown-items-wrapper',
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
export class TrashListDropdownItemsWrapperComponent implements OnInit {
    public dropdownItems: IDropdownMenuItem[] = [];
    public item: ICMSPage;
    public loaded = false;

    constructor(
        private route: ActivatedRoute,
        @Inject(DATA_TABLE_COMPONENT_DATA) public data: DataTableComponentData,
        private catalogVersionPermissionService: ICatalogVersionPermissionService,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): Promise<void> {
        return this.setDropdownItems();
    }

    private async setDropdownItems(): Promise<void> {
        this.item = this.data.item as ICMSPage;
        const canSynchronize =
            await this.catalogVersionPermissionService.hasSyncPermissionToActiveCatalogVersion(
                this.route.snapshot.params.catalogId,
                this.route.snapshot.params.catalogVersion
            );

        this.addDropdownItem(RestorePageItemComponent);

        if (canSynchronize) {
            this.addDropdownItem(UpdatePageStatusComponent);
        }

        this.addDropdownItem(PermanentlyDeletePageItemComponent);

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
