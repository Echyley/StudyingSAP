/// <reference types="jquery" />
import { ChangeDetectorRef, OnInit } from '@angular/core';
import { IPermissionService, PopupOverlayConfig, PageVersionSelectionService, IPageVersion, IUserTrackingService } from 'smarteditcommons';
import { ManagePageVersionService, RollbackPageVersionService } from '../../../services';
interface IButtonConfig {
    i18nKey: string;
    permissions?: string[];
    callback?(versionItem?: IPageVersion): void;
}
export declare class VersionItemMenuComponent implements OnInit {
    private managePageVersionService;
    private pageVersionSelectionService;
    private rollbackPageVersionService;
    private permissionService;
    private readonly cdr;
    private readonly yjQuery;
    private userTrackingService;
    item: IPageVersion;
    menuItems: IButtonConfig[];
    popupConfig: PopupOverlayConfig;
    isMenuOpen: boolean;
    constructor(managePageVersionService: ManagePageVersionService, pageVersionSelectionService: PageVersionSelectionService, rollbackPageVersionService: RollbackPageVersionService, permissionService: IPermissionService, cdr: ChangeDetectorRef, yjQuery: JQueryStatic, userTrackingService: IUserTrackingService);
    ngOnInit(): Promise<void>;
    onButtonClick(event: MouseEvent): void;
    hideMenu(): void;
    executeItemCallback(menuItem: IButtonConfig): void;
    private getPermittedButtons;
    private getButtonsConfiguration;
    private deleteItemCallback;
    private editItemCallback;
    private viewItemCallback;
    private rollbackItemCallback;
}
export {};
