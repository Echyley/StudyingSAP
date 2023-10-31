export declare class PersonalizationsmarteditCommerceCustomizationService {
    protected nonCommerceActionTypes: string[];
    protected types: any[];
    isNonCommerceAction(action: any): any;
    isCommerceAction(action: any): boolean;
    isTypeEnabled(type: any, seConfigurationData: any): boolean;
    registerType(item: any): void;
    getAvailableTypes(seConfigurationData: any): any;
    isCommerceCustomizationEnabled(seConfigurationData: any): boolean;
    getNonCommerceActionsCount(variation: any): number;
    getCommerceActionsCountMap(variation: any): any;
    getCommerceActionsCount(variation: any): number;
}
