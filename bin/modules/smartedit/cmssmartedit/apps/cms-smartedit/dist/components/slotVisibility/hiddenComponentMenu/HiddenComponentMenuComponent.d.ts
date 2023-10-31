import { ChangeDetectorRef, OnInit } from '@angular/core';
import { ICMSComponent } from 'cmscommons';
import { IContextualMenuButton, PopupOverlayConfig, IComponentHandlerService } from 'smarteditcommons';
import { HiddenComponentMenuService } from '../../../services';
export declare class HiddenComponentMenuComponent implements OnInit {
    private componentHandlerService;
    private hiddenComponentMenuService;
    private cdr;
    component: ICMSComponent;
    slotId: string;
    isMenuOpen: boolean;
    menuItems: IContextualMenuButton[];
    popupConfig: PopupOverlayConfig;
    private slotUuid;
    private configuration;
    constructor(componentHandlerService: IComponentHandlerService, hiddenComponentMenuService: HiddenComponentMenuService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    toggle(event: MouseEvent): void;
    hideMenu(): void;
    executeMenuItemCallback(item: IContextualMenuButton, event: MouseEvent): void;
}
