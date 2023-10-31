/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { PersonalizationsmarteditContextUtils } from 'personalizationcommons';
import { ManagerViewService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewService';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditPreviewService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditPreviewService';
import { ToolbarItemInternal, TOOLBAR_ITEM } from 'smarteditcommons';
import { ManageCustomizationViewManager } from '../management/manageCustomizationView';

@Component({
    selector: 'manage-customization-view-menu',
    templateUrl: './ManageCustomizationViewMenuComponent.html',
    styles: [
        `
            .se-toolbar-menu-content--pe-customized__item__link {
                color: var(--sapLinkColor);
                cursor: pointer;
            }
        `
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ManageCustomizationViewMenuComponent {
    constructor(
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
        private personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService,
        private manageCustomizationViewManager: ManageCustomizationViewManager,
        private personalizationsmarteditManagerView: ManagerViewService,
        @Inject(TOOLBAR_ITEM) private toolbarItem: ToolbarItemInternal
    ) {}

    public createCustomizationClick(): void {
        this.manageCustomizationViewManager.openCreateCustomizationModal();

        this.toolbarItem.isOpen = false;
    }

    public managerViewClick(): void {
        this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
            this.personalizationsmarteditContextService
        );
        this.personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(
            this.personalizationsmarteditPreviewService,
            this.personalizationsmarteditContextService
        );
        this.personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(
            this.personalizationsmarteditPreviewService,
            this.personalizationsmarteditContextService
        );
        this.personalizationsmarteditManagerView.openManagerAction();

        this.toolbarItem.isOpen = false;
    }
}
