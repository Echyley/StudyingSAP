import { CustomizationVariation } from '../types';
export declare class Customize {
    enabled: boolean;
    selectedCustomization: any;
    selectedVariations: CustomizationVariation[] | CustomizationVariation | null;
    selectedComponents: string[] | null;
    constructor();
}
