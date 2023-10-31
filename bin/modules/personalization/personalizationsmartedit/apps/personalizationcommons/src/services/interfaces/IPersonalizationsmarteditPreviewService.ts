/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IExperience } from 'smarteditcommons';

export interface IPersonalizationsmarteditPreviewService {
    removePersonalizationDataFromPreview(): Promise<IExperience>;
    updatePreviewTicketWithVariations(variations: any): Promise<IExperience>;
}
