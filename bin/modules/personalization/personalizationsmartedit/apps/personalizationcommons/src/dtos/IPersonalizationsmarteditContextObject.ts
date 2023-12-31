/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CombinedView } from './CombinedView';
import { Customize } from './Customize';
import { Personalization } from './Personalization';
import { SeData } from './SeData';

export interface IPersonalizationsmarteditContextObject {
    personalization: Personalization;
    customize: Customize;
    combinedView: CombinedView;
    seData: SeData;
}
