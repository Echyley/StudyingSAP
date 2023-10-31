import { TranslateService } from '@ngx-translate/core';
import { PersonalizationsmarteditMessageHandler, PersonalizationsmarteditCommerceCustomizationService, Customization, CustomizationVariation, CustomizationsFilter, CustomizationsList } from 'personalizationcommons';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import { IConfirmationModalService, IWaitDialogService } from 'smarteditcommons';
export declare class ManagerViewUtilsService {
    private personalizationsmarteditRestService;
    private personalizationsmarteditMessageHandler;
    private personalizationsmarteditCommerceCustomizationService;
    private waitDialogService;
    private confirmationModalService;
    private translateService;
    constructor(personalizationsmarteditRestService: PersonalizationsmarteditRestService, personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler, personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService, waitDialogService: IWaitDialogService, confirmationModalService: IConfirmationModalService, translateService: TranslateService);
    deleteCustomizationAction(customization: Customization, customizations: Customization[]): Promise<void>;
    deleteVariationAction(customization: Customization, variation: CustomizationVariation): Promise<void>;
    toggleVariationActive(customization: Customization, variation: CustomizationVariation): Promise<void>;
    customizationClickAction(customization: Customization): Promise<void>;
    getCustomizations(filter: CustomizationsFilter): Promise<CustomizationsList>;
    updateCustomizationRank(customizationCode: string, increaseValue: number): Promise<void>;
    updateVariationRank(customization: Customization, variationCode: string, increaseValue: number): Promise<void>;
    setCustomizationRank(customization: Customization, increaseValue: number, customizations: Customization[]): Promise<void>;
    setVariationRank(customization: Customization, variation: CustomizationVariation, increaseValue: number): Promise<void>;
    private getActionsForVariation;
    private markCustomizationAsDeleted;
    private updateVariationStatus;
    private updateVariationStatusAndEnablement;
}
