import { ChangeDetectorRef } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PersonalizationsmarteditMessageHandler, PersonalizationsmarteditUtils } from 'personalizationcommons';
import { IPermissionService, ContextualMenuItemData } from 'smarteditcommons';
import { PersonalizationsmarteditComponentHandlerService, PersonalizationsmarteditContextualMenuService, PersonalizationsmarteditRestService, PersonalizationsmarteditContextService, ActionDetailsResponseDto } from '../../service';
interface ActionCustomization {
    catalog: string;
    catalogVersion: string;
    catalogVersionNameL10N: string;
    isFromCurrentCatalog: boolean;
}
export interface ActionWithCustomization extends ActionDetailsResponseDto {
    customization: ActionCustomization;
}
export declare class ShowComponentInfoListComponent {
    private persoComponentHandlerService;
    private persoContextService;
    private persoContextualMenuService;
    private persoMessageHandler;
    private persoRestService;
    private persoUtils;
    private permissionService;
    private translateService;
    private cdr;
    containerId: string;
    isPageBlocked: boolean;
    isContextualMenuInfoEnabled: boolean;
    moreCustomizationsRequestProcessing: boolean;
    isPersonalizationAllowedInWorkflow: boolean;
    isCustomizationVisible: boolean;
    actions: ActionWithCustomization[];
    fetchActionsPage: () => void;
    private containerSourceId;
    private containerIdExists;
    private pagination;
    private customizationsFilter;
    constructor(item: ContextualMenuItemData, persoComponentHandlerService: PersonalizationsmarteditComponentHandlerService, persoContextService: PersonalizationsmarteditContextService, persoContextualMenuService: PersonalizationsmarteditContextualMenuService, persoMessageHandler: PersonalizationsmarteditMessageHandler, persoRestService: PersonalizationsmarteditRestService, persoUtils: PersonalizationsmarteditUtils, permissionService: IPermissionService, translateService: TranslateService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    fetchMoreActions(): Promise<void>;
    private isPersonalizationBlockedOnPage;
    private setCustomizationsFilterForNextPage;
    private fetchAllActionsForContainerId;
    private getActionsWithCustomizations;
    private setCustomizationVisible;
}
export {};
