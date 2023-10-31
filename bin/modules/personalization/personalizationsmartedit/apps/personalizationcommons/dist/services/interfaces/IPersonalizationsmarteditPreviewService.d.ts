import { IExperience } from 'smarteditcommons';
export interface IPersonalizationsmarteditPreviewService {
    removePersonalizationDataFromPreview(): Promise<IExperience>;
    updatePreviewTicketWithVariations(variations: any): Promise<IExperience>;
}
