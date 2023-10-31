/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnInit,
    ViewRef
} from '@angular/core';
import { ICMSComponent } from 'cmscommons';
import { cloneDeep } from 'lodash';
import {
    CONTENT_SLOT_TYPE,
    IContextualMenuButton,
    IContextualMenuConfiguration,
    PopupOverlayConfig,
    IComponentHandlerService
} from 'smarteditcommons';
import { HiddenComponentMenuService } from '../../../services';

/** Represents a menu for hidden components. */
@Component({
    selector: 'se-hidden-component-menu',
    templateUrl: './HiddenComponentMenuComponent.html',
    styleUrls: ['./HiddenComponentMenuComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class HiddenComponentMenuComponent implements OnInit {
    @Input() component: ICMSComponent;
    @Input() slotId: string;

    public isMenuOpen: boolean;
    public menuItems: IContextualMenuButton[];
    public popupConfig: PopupOverlayConfig;

    private slotUuid: string;
    private configuration: IContextualMenuConfiguration;

    constructor(
        private componentHandlerService: IComponentHandlerService,
        private hiddenComponentMenuService: HiddenComponentMenuService,
        private cdr: ChangeDetectorRef
    ) {
        this.isMenuOpen = false;
        this.menuItems = [];
        this.popupConfig = {
            halign: 'left',
            valign: 'bottom'
        };
    }

    async ngOnInit(): Promise<void> {
        const slot = this.componentHandlerService.getOriginalComponent(
            this.slotId,
            CONTENT_SLOT_TYPE
        );
        this.slotUuid = this.componentHandlerService.getUuid(slot);

        const hiddenComponentMenu =
            await this.hiddenComponentMenuService.getItemsForHiddenComponent(
                this.component,
                this.slotId
            );
        this.configuration = cloneDeep(hiddenComponentMenu.configuration);
        this.configuration.slotUuid = this.slotUuid;
        this.menuItems = cloneDeep(hiddenComponentMenu.buttons);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public toggle(event: MouseEvent): void {
        event.stopPropagation();

        this.isMenuOpen = !this.isMenuOpen;
    }

    /**
     * Used to close the menu when clicked outside of its parent container.
     * The component is not destroyed so when a user opens a parent container, the menu should be closed.
     */
    public hideMenu(): void {
        this.isMenuOpen = false;
    }

    public executeMenuItemCallback(item: IContextualMenuButton, event: MouseEvent): void {
        item.action.callback(this.configuration, event);
        this.isMenuOpen = false;
    }
}
