/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditCommerceCustomizationService,
    PERSONALIZATION_MODEL_STATUS_CODES,
    Customization,
    CustomizationVariation,
    CustomizationAction,
    CustomizationsFilter,
    CustomizationsList
} from 'personalizationcommons';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import { IConfirmationModalService, IWaitDialogService } from 'smarteditcommons';

@Injectable()
export class ManagerViewUtilsService {
    constructor(
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        private personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService,
        private waitDialogService: IWaitDialogService,
        private confirmationModalService: IConfirmationModalService,
        private translateService: TranslateService
    ) {}

    public async deleteCustomizationAction(
        customization: Customization,
        customizations: Customization[]
    ): Promise<void> {
        await this.confirmationModalService.confirm({
            description: 'personalization.modal.manager.deletecustomization.content'
        });
        try {
            const responseCustomization =
                await this.personalizationsmarteditRestService.getCustomization(customization);
            await this.markCustomizationAsDeleted(
                responseCustomization,
                customization,
                customizations
            );
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.deletingcustomization')
            );
        }
    }

    public async deleteVariationAction(
        customization: Customization,
        variation: CustomizationVariation
    ): Promise<void> {
        await this.confirmationModalService.confirm({
            description: 'personalization.modal.manager.deletevariation.content'
        });
        try {
            const responseVariation = await this.personalizationsmarteditRestService.getVariation(
                customization.code,
                variation.code
            );
            responseVariation.status = PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
            await this.updateVariationStatus(responseVariation, variation, customization);
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.deletingvariation')
            );
        }
    }

    public async toggleVariationActive(
        customization: Customization,
        variation: CustomizationVariation
    ): Promise<void> {
        try {
            const responseVariation = await this.personalizationsmarteditRestService.getVariation(
                customization.code,
                variation.code
            );
            responseVariation.enabled = !responseVariation.enabled;
            responseVariation.status = responseVariation.enabled
                ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
                : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
            await this.updateVariationStatusAndEnablement(
                responseVariation,
                variation,
                customization
            );
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingvariation')
            );
        }
    }

    public async customizationClickAction(customization: Customization): Promise<void> {
        try {
            const response =
                await this.personalizationsmarteditRestService.getVariationsForCustomization(
                    customization.code,
                    customization
                );
            customization.variations.forEach((variation) => {
                variation.actions = this.getActionsForVariation(
                    variation.code,
                    response.variations
                );
                variation.numberOfComponents =
                    this.personalizationsmarteditCommerceCustomizationService.getNonCommerceActionsCount(
                        variation
                    );
                variation.commerceCustomizations =
                    this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(
                        variation
                    );
                variation.numberOfCommerceActions =
                    this.personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(
                        variation
                    );
            });
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingcustomization')
            );
        }
    }

    public getCustomizations(filter: CustomizationsFilter): Promise<CustomizationsList> {
        return this.personalizationsmarteditRestService.getCustomizations(filter);
    }

    public updateCustomizationRank(
        customizationCode: string,
        increaseValue: number
    ): Promise<void> {
        return this.personalizationsmarteditRestService
            .updateCustomizationRank(customizationCode, increaseValue)
            .catch(() => {
                this.personalizationsmarteditMessageHandler.sendError(
                    this.translateService.instant('personalization.error.updatingcustomization')
                );
            });
    }

    public async updateVariationRank(
        customization: Customization,
        variationCode: string,
        increaseValue: number
    ): Promise<void> {
        try {
            const responseVariation = await this.personalizationsmarteditRestService.getVariation(
                customization.code,
                variationCode
            );
            responseVariation.rank = responseVariation.rank + increaseValue;
            return this.personalizationsmarteditRestService
                .editVariation(customization.code, responseVariation)
                .catch(() => {
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant('personalization.error.editingvariation')
                    );
                });
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingvariation')
            );
        }
    }

    public async setCustomizationRank(
        customization: Customization,
        increaseValue: number,
        customizations: Customization[]
    ): Promise<void> {
        const nextItem = customizations[customizations.indexOf(customization) + increaseValue];
        this.waitDialogService.showWaitModal();
        try {
            await this.updateCustomizationRank(
                customization.code,
                nextItem.rank - customization.rank
            );
            customization.rank += increaseValue;
            nextItem.rank -= increaseValue;
            const index = customizations.indexOf(customization);
            const tempItem = customizations.splice(index, 1);
            customizations.splice(index + increaseValue, 0, tempItem[0]);
            this.waitDialogService.hideWaitModal();
        } catch {
            this.waitDialogService.hideWaitModal();
        }
    }

    public async setVariationRank(
        customization: Customization,
        variation: CustomizationVariation,
        increaseValue: number
    ): Promise<void> {
        const nextItem =
            customization.variations[customization.variations.indexOf(variation) + increaseValue];
        this.waitDialogService.showWaitModal();
        try {
            await this.updateVariationRank(customization, variation.code, increaseValue);
            variation.rank += increaseValue;
            nextItem.rank -= increaseValue;
            const index = customization.variations.indexOf(variation);
            const tempItem = customization.variations.splice(index, 1);
            customization.variations.splice(index + increaseValue, 0, tempItem[0]);
        } catch (error) {
            console.log('Unexpected error occurred while updating variation rank', error);
        } finally {
            this.waitDialogService.hideWaitModal();
        }
    }

    private getActionsForVariation(
        variationCode: string,
        variationsArray: CustomizationVariation[]
    ): CustomizationAction[] {
        variationsArray = variationsArray || [];
        const variation = variationsArray.filter((elem) => variationCode === elem.code)[0];
        return variation ? variation.actions : [];
    }

    private async markCustomizationAsDeleted(
        responseCustomization: Customization,
        originalCustomization: Customization,
        customizations: Customization[]
    ): Promise<void> {
        responseCustomization.status = PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
        try {
            await this.personalizationsmarteditRestService.updateCustomization(
                responseCustomization
            );
            customizations.splice(customizations.indexOf(originalCustomization), 1);
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.deletingcustomization')
            );
        }
    }

    private async updateVariationStatus(
        responseVariation: CustomizationVariation,
        variation: CustomizationVariation,
        customization: Customization
    ): Promise<void> {
        try {
            const response = await this.personalizationsmarteditRestService.editVariation(
                customization.code,
                responseVariation
            );
            variation.status = response.status;
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.deletingvariation')
            );
        }
    }

    private async updateVariationStatusAndEnablement(
        responseVariation: CustomizationVariation,
        variation: CustomizationVariation,
        customization: Customization
    ): Promise<void> {
        try {
            const response = await this.personalizationsmarteditRestService.editVariation(
                customization.code,
                responseVariation
            );
            variation.enabled = response.enabled;
            variation.status = response.status;
        } catch {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.editingvariation')
            );
        }
    }
}
