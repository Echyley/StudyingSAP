/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { cloneDeep } from 'lodash';
import {
    PersonalizationsmarteditMessageHandler,
    PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN,
    PersonlizationPromotionExtensionInjector
} from 'personalizationcommons';
import {
    PromotionsSmarteditRestService,
    PromotionsCatalog,
    PromotionsPage,
    Promotion,
    PromotionsAction
} from 'personalizationpromotionssmarteditcommons';
import { IExperienceService } from 'smarteditcommons';
import { PROMOTION_ACTION_TYPE } from '../utils/constants';

@Component({
    selector: 'promotions-smartedit-promotion',
    templateUrl: './PromotionsSmarteditPromotionComponent.html'
})
export class PromotionsSmarteditPromotionComponent implements OnInit {
    public selectedPromotion: Promotion = null;
    public allPromotions: Promotion[] = [];
    public availablePromotions: Promotion[] = [];
    public getSelectedTypeCode: () => string;
    public fetchStrategy = {
        fetchAll: (): Promise<any[]> => {
            this.availablePromotions = [];
            this.allPromotions.forEach((promotion) => {
                const action = this.buildAction(promotion);
                if (this.isActionInSelectDisabled(action, this.comparer) === undefined) {
                    this.availablePromotions.push(cloneDeep(promotion));
                }
            });
            return Promise.resolve(this.availablePromotions);
        }
    };

    private addAction: (action: PromotionsAction, comparer: (p1, p2) => boolean) => void;
    private isActionInSelectDisabled: (
        action: PromotionsAction,
        comparer: (p1, p2) => boolean
    ) => any;

    constructor(
        @Inject(PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN)
        private personlizationPromotionExtensionInjector: PersonlizationPromotionExtensionInjector,
        private translateService: TranslateService,
        protected experienceService: IExperienceService,
        protected promotionssmarteditRestService: PromotionsSmarteditRestService,
        protected personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler
    ) {
        this.addAction = personlizationPromotionExtensionInjector.addAction;
        this.isActionInSelectDisabled =
            personlizationPromotionExtensionInjector.isActionInSelectDisabled;
        this.getSelectedTypeCode = personlizationPromotionExtensionInjector.getSelectedTypeCode;
    }

    ngOnInit(): void {
        this.getAvailablePromotions();
    }

    public async getAvailablePromotions(): Promise<void> {
        try {
            const response = await this.getPromotions();
            this.allPromotions = response.promotions;
            this.allPromotions.forEach((item) => {
                item.id = item.code;
                item.label = item.code + '(' + item.promotionGroup + ')';
            });
        } catch (e) {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingpromotions')
            );
        }
    }

    public getPromotionCode(): string {
        return PROMOTION_ACTION_TYPE;
    }

    public async getPromotions(): Promise<PromotionsPage> {
        const catalogs = await this.getCatalogs();
        const promotion = await this.promotionssmarteditRestService.getPromotions(catalogs);
        return Promise.resolve(promotion);
    }

    public async getCatalogs(): Promise<PromotionsCatalog[]> {
        const catalogs: PromotionsCatalog[] = [];
        const experience = await this.experienceService.getCurrentExperience();
        catalogs.push({
            catalog: experience.catalogDescriptor.catalogId,
            catalogVersion: experience.catalogDescriptor.catalogVersion
        });
        experience.productCatalogVersions.forEach((item: any) => {
            catalogs.push({
                catalog: item.catalog,
                catalogVersion: item.catalogVersion
            });
        });
        return Promise.resolve(catalogs);
    }

    public buildAction(promotion: Promotion): PromotionsAction {
        return {
            type: 'cxPromotionActionData',
            promotionId: promotion.code
        };
    }

    public comparer = (a1: PromotionsAction, a2: PromotionsAction): boolean =>
        a1.type === a2.type && a1.promotionId === a2.promotionId;

    public promotionSelectedEvent = (promotion: Promotion): void => {
        const action = this.buildAction(promotion);
        this.addAction(action, this.comparer);
    };
}
