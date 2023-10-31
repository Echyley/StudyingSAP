/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    Input,
    OnInit,
    ViewRef
} from '@angular/core';
import {
    IPermissionService,
    PopupOverlayConfig,
    PageVersionSelectionService,
    IPageVersion,
    YJQUERY_TOKEN,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

import { ManagePageVersionService, RollbackPageVersionService } from '../../../services';

interface IButtonConfig {
    i18nKey: string;
    permissions?: string[];
    callback?(versionItem?: IPageVersion): void;
}

@Component({
    selector: 'se-version-item-menu',
    templateUrl: 'VersionItemMenuComponent.html',
    styleUrls: ['./VersionItemMenuComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VersionItemMenuComponent implements OnInit {
    @Input() item: IPageVersion;

    public menuItems: IButtonConfig[] = [];
    public popupConfig: PopupOverlayConfig = {
        halign: 'left',
        valign: 'bottom'
    };
    public isMenuOpen = false;

    constructor(
        private managePageVersionService: ManagePageVersionService,
        private pageVersionSelectionService: PageVersionSelectionService,
        private rollbackPageVersionService: RollbackPageVersionService,
        private permissionService: IPermissionService,
        private readonly cdr: ChangeDetectorRef,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic,
        private userTrackingService: IUserTrackingService
    ) {}

    async ngOnInit(): Promise<void> {
        this.menuItems = await this.getPermittedButtons(this.getButtonsConfiguration());
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public onButtonClick(event: MouseEvent): void {
        event.stopPropagation();

        this.isMenuOpen = !this.isMenuOpen;
    }

    public hideMenu(): void {
        this.isMenuOpen = false;
    }

    public executeItemCallback(menuItem: IButtonConfig): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.VERSION_MANAGEMENT,
            menuItem.i18nKey
        );
        menuItem.callback(this.item);
        this.hideMenu();
        // wokaround to close parent popover
        this.yjQuery('[data-item-key="se.cms.pageVersionsMenu"]').trigger('click');
    }

    private async getPermittedButtons(menuItemsConfig: IButtonConfig[]): Promise<IButtonConfig[]> {
        const buttonsPermissions = menuItemsConfig.map((menuItem: IButtonConfig) =>
            this.permissionService.isPermitted([
                {
                    names: menuItem.permissions
                }
            ])
        );

        const permissions = await Promise.all(buttonsPermissions);
        return menuItemsConfig.filter(
            (_menuItem: IButtonConfig, index: number) => permissions[index]
        );
    }

    private getButtonsConfiguration(): IButtonConfig[] {
        return [
            {
                i18nKey: 'se.cms.version.item.menu.view.label',
                callback: (versionItem: IPageVersion): void => this.viewItemCallback(versionItem),
                permissions: ['se.version.page']
            },
            {
                i18nKey: 'se.cms.version.item.menu.edit.label',
                callback: (versionItem: IPageVersion): void => this.editItemCallback(versionItem),
                permissions: ['se.edit.version.page']
            },
            {
                i18nKey: 'se.cms.version.item.menu.rollback.label',
                callback: (versionItem: IPageVersion): void =>
                    this.rollbackItemCallback(versionItem),
                permissions: ['se.rollback.version.page.versions.menu']
            },
            {
                i18nKey: 'se.cms.version.item.menu.delete.label',
                callback: (versionItem: IPageVersion): void => this.deleteItemCallback(versionItem),
                permissions: ['se.delete.version.page']
            }
        ];
    }

    private deleteItemCallback(versionItem: IPageVersion): void {
        this.managePageVersionService.deletePageVersion(versionItem.uid);
    }

    private editItemCallback(versionItem: IPageVersion): void {
        this.managePageVersionService.editPageVersion(versionItem);
    }

    private viewItemCallback(versionItem: IPageVersion): void {
        this.pageVersionSelectionService.selectPageVersion(versionItem);
    }

    private rollbackItemCallback(versionItem: IPageVersion): void {
        this.rollbackPageVersionService.rollbackPageVersion(versionItem);
    }
}
