/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IExperience, IExperienceService } from 'smarteditcommons';

@Injectable()
export class PersonalizationsmarteditPreviewService {
    constructor(protected experienceService: IExperienceService) {}

    public removePersonalizationDataFromPreview(): Promise<IExperience> {
        return this.updatePreviewTicketWithVariations([]);
    }

    public async updatePreviewTicketWithVariations(variations: any): Promise<IExperience> {
        const experience = await this.experienceService.getCurrentExperience();
        if (!experience) {
            return undefined;
        }
        if (JSON.stringify(experience.variations) === JSON.stringify(variations)) {
            return undefined;
        }
        experience.variations = variations;
        await this.experienceService.setCurrentExperience(experience);
        return this.experienceService.updateExperience();
    }
}
