import { OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PersonalizationsmarteditMessageHandler, PersonlizationPromotionExtensionInjector } from 'personalizationcommons';
import { PromotionsSmarteditRestService, PromotionsCatalog, PromotionsPage, Promotion, PromotionsAction } from 'personalizationpromotionssmarteditcommons';
import { IExperienceService } from 'smarteditcommons';
export declare class PromotionsSmarteditPromotionComponent implements OnInit {
    private personlizationPromotionExtensionInjector;
    private translateService;
    protected experienceService: IExperienceService;
    protected promotionssmarteditRestService: PromotionsSmarteditRestService;
    protected personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler;
    selectedPromotion: Promotion;
    allPromotions: Promotion[];
    availablePromotions: Promotion[];
    getSelectedTypeCode: () => string;
    fetchStrategy: {
        fetchAll: () => Promise<any[]>;
    };
    private addAction;
    private isActionInSelectDisabled;
    constructor(personlizationPromotionExtensionInjector: PersonlizationPromotionExtensionInjector, translateService: TranslateService, experienceService: IExperienceService, promotionssmarteditRestService: PromotionsSmarteditRestService, personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler);
    ngOnInit(): void;
    getAvailablePromotions(): Promise<void>;
    getPromotionCode(): string;
    getPromotions(): Promise<PromotionsPage>;
    getCatalogs(): Promise<PromotionsCatalog[]>;
    buildAction(promotion: Promotion): PromotionsAction;
    comparer: (a1: PromotionsAction, a2: PromotionsAction) => boolean;
    promotionSelectedEvent: (promotion: Promotion) => void;
}
