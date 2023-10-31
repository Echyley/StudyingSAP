import { Customization, CustomizationVariation } from 'personalizationcommons';
import { IModalService } from 'smarteditcommons';
export declare class PersonalizationsmarteditCommerceCustomizationView {
    private modalService;
    constructor(modalService: IModalService);
    openCommerceCustomizationAction: (customization: Customization, variation: CustomizationVariation) => void;
}
