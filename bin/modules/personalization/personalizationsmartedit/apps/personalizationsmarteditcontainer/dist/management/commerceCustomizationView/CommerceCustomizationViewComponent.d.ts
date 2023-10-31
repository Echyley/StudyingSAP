import { OnInit, Type, Injector } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SeData, Customization, CustomizationVariation, PersonalizationsmarteditUtils, PersonalizationsmarteditMessageHandler, PersonalizationsmarteditCommerceCustomizationService, PersonlizationSearchExtensionComponent, PersonlizationPromotionExtensionComponent } from 'personalizationcommons';
import { Action, BaseAction } from 'personalizationsmarteditcontainer/interfaces';
import { SystemEventService, LogService, ModalManagerService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from '../../service/PersonalizationsmarteditRestService';
import { ActionsDataFactory } from './ActionsDataFactory';
export interface CommerceCustomizationViewData {
    customization: Customization;
    variation: CustomizationVariation;
}
interface SearchProfileComponent {
    component: Type<any>;
    injector: Injector;
}
interface PromotionComponent {
    component: Type<any>;
    injector: Injector;
}
export declare class CommerceCustomizationViewComponent implements OnInit {
    private injector;
    private translateService;
    private actionsDataFactory;
    private personalizationsmarteditRestService;
    private personalizationsmarteditMessageHandler;
    private systemEventService;
    private personalizationsmarteditCommerceCustomizationService;
    private personalizationsmarteditContextService;
    private personalizationsmarteditUtils;
    private modalManager;
    private logService;
    promotionCode: string;
    searchProfilesCode: string;
    customization: Customization;
    variation: CustomizationVariation;
    actions: Action[];
    availableTypes: any[];
    select: any;
    customizationStatusText: string;
    variationStatusText: string;
    customizationStatus: string;
    variationStatus: string;
    removedActions: Action[];
    customizationTypeFetchStrategy: {
        fetchAll: () => Promise<any[]>;
    };
    searchProfileComponent: SearchProfileComponent | undefined;
    promotionComponent: PromotionComponent | undefined;
    constructor(injector: Injector, personlizationSearchExtensionComponent: PersonlizationSearchExtensionComponent, personlizationPromotionExtensionComponent: PersonlizationPromotionExtensionComponent, translateService: TranslateService, actionsDataFactory: ActionsDataFactory, personalizationsmarteditRestService: PersonalizationsmarteditRestService, personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler, systemEventService: SystemEventService, personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService, personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditUtils: PersonalizationsmarteditUtils, modalManager: ModalManagerService, logService: LogService);
    ngOnInit(): void;
    onActionTypeChange(): void;
    getSeData(): SeData;
    isItemInSelectedActions: (action: BaseAction, comparer: (p1: any, p2: any) => boolean) => Action;
    removeSelectedAction(actionWrapper: any): void;
    addAction: (action: BaseAction, comparer: (p1: any, p2: any) => boolean) => void;
    displayAction(actionWrapper: any): string;
    getActionsToDisplay(): Action[];
    isDirty(): boolean;
    private getSelectedTypeCode;
    private getCustomization;
    private getVariation;
    private initPersonlizationSearchExtensionComponent;
    private initPersonlizationPromotionExtensionComponent;
    private populateActions;
    private getType;
    private sendRefreshEvent;
    private onCancel;
    private onSave;
    private getActionTypesForActions;
    private createActions;
    private deleteActions;
    private updateActions;
}
export {};
