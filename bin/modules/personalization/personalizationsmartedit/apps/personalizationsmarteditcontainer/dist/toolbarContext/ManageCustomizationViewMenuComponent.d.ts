import { PersonalizationsmarteditContextUtils } from 'personalizationcommons';
import { ManagerViewService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewService';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditPreviewService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditPreviewService';
import { ToolbarItemInternal } from 'smarteditcommons';
import { ManageCustomizationViewManager } from '../management/manageCustomizationView';
export declare class ManageCustomizationViewMenuComponent {
    private personalizationsmarteditContextService;
    private personalizationsmarteditContextUtils;
    private personalizationsmarteditPreviewService;
    private manageCustomizationViewManager;
    private personalizationsmarteditManagerView;
    private toolbarItem;
    constructor(personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils, personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService, manageCustomizationViewManager: ManageCustomizationViewManager, personalizationsmarteditManagerView: ManagerViewService, toolbarItem: ToolbarItemInternal);
    createCustomizationClick(): void;
    managerViewClick(): void;
}
