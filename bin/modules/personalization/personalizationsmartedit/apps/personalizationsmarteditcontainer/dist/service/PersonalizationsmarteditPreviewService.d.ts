import { IExperience, IExperienceService } from 'smarteditcommons';
export declare class PersonalizationsmarteditPreviewService {
    protected experienceService: IExperienceService;
    constructor(experienceService: IExperienceService);
    removePersonalizationDataFromPreview(): Promise<IExperience>;
    updatePreviewTicketWithVariations(variations: any): Promise<IExperience>;
}
