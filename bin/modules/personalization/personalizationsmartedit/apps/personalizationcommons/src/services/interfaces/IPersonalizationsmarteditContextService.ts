/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CombinedView, Customize, Personalization, SeData } from 'personalizationcommons/dtos';

export interface IPersonalizationsmarteditContextService {
    getPersonalization(): Personalization;
    setPersonalization(personalization: Personalization): void;
    getCustomize(): Customize;
    setCustomize(customize: Customize): void;
    getCombinedView(): CombinedView;
    setCombinedView(combinedView: CombinedView): void;
    getSeData(): SeData;
    setSeData(seData: SeData): void;
}
