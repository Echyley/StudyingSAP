/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    CustomizationsFilter,
    PaginationHelper,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import {
    IPermissionService,
    CONTEXTUAL_MENU_ITEM_DATA,
    ContextualMenuItemData
} from 'smarteditcommons';
import {
    PersonalizationsmarteditComponentHandlerService,
    PersonalizationsmarteditContextualMenuService,
    PersonalizationsmarteditRestService,
    PersonalizationsmarteditContextService,
    ActionDetailsResponseDto
} from '../../service';

interface ActionCustomization {
    catalog: string;
    catalogVersion: string;
    catalogVersionNameL10N: string;
    isFromCurrentCatalog: boolean;
}
export interface ActionWithCustomization extends ActionDetailsResponseDto {
    customization: ActionCustomization;
}
@Component({
    selector: 'perso-show-component-info-list',
    templateUrl: './ShowComponentInfoListComponent.html'
})
export class ShowComponentInfoListComponent {
    /** Whether Personalization is blocked on the page. */
    public containerId: string;
    public isPageBlocked: boolean;
    public isContextualMenuInfoEnabled: boolean;
    /** Whether there is fetch request processing. It is used to toggle the spinner. */
    public moreCustomizationsRequestProcessing: boolean;
    public isPersonalizationAllowedInWorkflow: boolean;
    /** Whether there are any actions affecting container id.  */
    public isCustomizationVisible: boolean;
    public actions: ActionWithCustomization[];
    public fetchActionsPage: () => void;

    private containerSourceId: string;
    private containerIdExists: boolean;
    private pagination: PaginationHelper;
    private customizationsFilter: CustomizationsFilter;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) item: ContextualMenuItemData,
        private persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
        private persoContextService: PersonalizationsmarteditContextService,
        private persoContextualMenuService: PersonalizationsmarteditContextualMenuService,
        private persoMessageHandler: PersonalizationsmarteditMessageHandler,
        private persoRestService: PersonalizationsmarteditRestService,
        private persoUtils: PersonalizationsmarteditUtils,
        private permissionService: IPermissionService,
        private translateService: TranslateService,
        private cdr: ChangeDetectorRef
    ) {
        ({
            componentAttributes: { smarteditContainerId: this.containerId }
        } = item);

        this.pagination = new PaginationHelper({});
        this.pagination.reset();

        this.isPageBlocked = false;
        this.isContextualMenuInfoEnabled = false;
        this.isPersonalizationAllowedInWorkflow = false;
        this.moreCustomizationsRequestProcessing = false;

        const pageSize = 25;
        this.customizationsFilter = {
            currentSize: pageSize,
            currentPage: this.pagination.getPage() + 1
        };

        this.actions = [];
        this.isCustomizationVisible = false;
        this.fetchActionsPage = (): Promise<void> => this.fetchMoreActions();
    }

    async ngOnInit(): Promise<void> {
        this.containerIdExists = !!this.containerId;
        this.containerSourceId = this.containerIdExists
            ? this.persoComponentHandlerService.getContainerSourceIdForContainerId(this.containerId)
            : '';
        this.setCustomizationVisible();

        this.isPageBlocked = await this.isPersonalizationBlockedOnPage();
        this.isPersonalizationAllowedInWorkflow =
            this.persoContextualMenuService.isPersonalizationAllowedInWorkflow();
        this.isContextualMenuInfoEnabled =
            this.persoContextualMenuService.isContextualMenuInfoEnabled();

        this.cdr.detectChanges();
    }

    public async fetchMoreActions(): Promise<void> {
        if (
            !this.pagination.isLastPage() &&
            !this.moreCustomizationsRequestProcessing &&
            this.containerIdExists
        ) {
            this.moreCustomizationsRequestProcessing = true;

            const customizationsFilter = this.setCustomizationsFilterForNextPage();
            await this.fetchAllActionsForContainerId(this.containerSourceId, customizationsFilter);
        }
    }

    private async isPersonalizationBlockedOnPage(): Promise<boolean> {
        const isPermitted = await this.permissionService.isPermitted([
            { names: ['se.personalization.page'] }
        ]);
        return !isPermitted;
    }

    private setCustomizationsFilterForNextPage(): CustomizationsFilter {
        const nextPage = this.pagination.getPage() + 1;
        this.customizationsFilter.currentPage = nextPage;
        return this.customizationsFilter;
    }

    private async fetchAllActionsForContainerId(
        containerId: string,
        filter: CustomizationsFilter
    ): Promise<void> {
        try {
            const response = await this.persoRestService.getCxCmsAllActionsForContainer(
                containerId,
                filter
            );

            const actionsWithCustomizationsPromises = (response.actions || []).map((action) =>
                this.getActionsWithCustomizations(action)
            );
            const actionsWithCustomizations = await Promise.all(actionsWithCustomizationsPromises);
            // incremental load to get only new results when user scrolls down
            this.persoUtils.uniqueArray(this.actions, actionsWithCustomizations, 'variationCode');

            this.setCustomizationVisible();

            this.pagination = new PaginationHelper(response.pagination);
        } catch {
            this.persoMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingactions')
            );
        } finally {
            this.moreCustomizationsRequestProcessing = false;
        }
    }

    private async getActionsWithCustomizations(
        action: ActionDetailsResponseDto
    ): Promise<ActionWithCustomization> {
        const customization: ActionCustomization = {
            catalog: action.actionCatalog,
            catalogVersion: action.actionCatalogVersion,
            catalogVersionNameL10N: '',
            isFromCurrentCatalog: false
        };
        customization.isFromCurrentCatalog = this.persoUtils.isItemFromCurrentCatalog(
            customization,
            this.persoContextService.getSeData()
        );

        await this.persoUtils.getAndSetCatalogVersionNameL10N(customization);

        return {
            ...action,
            customization
        };
    }

    private setCustomizationVisible(): void {
        this.isCustomizationVisible = this.containerIdExists && this.actions.length > 0;
    }
}
