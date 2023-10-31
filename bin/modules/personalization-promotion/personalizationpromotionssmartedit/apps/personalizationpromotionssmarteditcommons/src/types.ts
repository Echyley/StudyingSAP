/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Page } from 'smarteditcommons';

export interface PromotionsAction {
    type: string;
    promotionId: string;
}

export interface PromotionsCatalog {
    catalog: string;
    catalogVersion: string;
}

export interface PromotionsPage extends Page<Promotion> {
    promotions: Promotion[];
}

export interface Promotion {
    code: string;
    promotionGroup: string;
    status: string;
    id?: string;
    label?: string;
}
