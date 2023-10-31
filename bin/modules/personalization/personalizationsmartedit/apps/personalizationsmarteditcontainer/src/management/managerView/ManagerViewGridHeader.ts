/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';

@Component({
    selector: 'manager-view-grid-header',
    template: `
        <div class="tree-head hidden-sm hidden-xs">
            <div
                class="tree-head__col--lg perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.customization"
                title="{{ 'personalization.modal.manager.grid.header.customization' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs"
                translate="personalization.modal.manager.grid.header.variations"
                title="{{ 'personalization.modal.manager.grid.header.variations' | translate }}"
            ></div>
            <div
                class="tree-head__col--xs perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.components"
                title="{{ 'personalization.modal.manager.grid.header.components' | translate }}"
            ></div>
            <div
                class="tree-head__col--md"
                translate="personalization.modal.manager.grid.header.status"
                title="{{ 'personalization.modal.manager.grid.header.status' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.startdate"
                title="{{ 'personalization.modal.manager.grid.header.startdate' | translate }}"
            ></div>
            <div
                class="tree-head__col--sm perso-wrap-ellipsis"
                translate="personalization.modal.manager.grid.header.enddate"
                title="{{ 'personalization.modal.manager.grid.header.enddate' | translate }}"
            ></div>
        </div>
    `
})
export class ManagerViewGridHeader {}
