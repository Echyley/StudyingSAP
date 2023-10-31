/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export class PersonalizationsmarteditCommerceCustomizationService {
    protected nonCommerceActionTypes = ['cxCmsActionData'];
    protected types: any[] = [];

    public isNonCommerceAction(action: any): any {
        return this.nonCommerceActionTypes.some((val) => val === action.type);
    }

    public isCommerceAction(action: any): boolean {
        return !this.isNonCommerceAction(action);
    }

    public isTypeEnabled(type: any, seConfigurationData: any): boolean {
        return (
            seConfigurationData !== undefined &&
            seConfigurationData !== null &&
            seConfigurationData[type.confProperty] === true
        );
    }

    public registerType(item: any): void {
        const type = item.type;
        let exists = false;

        this.types.forEach((val) => {
            if (val.type === type) {
                exists = true;
            }
        });

        if (!exists) {
            this.types.push(item);
        }
    }

    public getAvailableTypes(seConfigurationData: any): any {
        return this.types.filter((item) => this.isTypeEnabled(item, seConfigurationData));
    }

    public isCommerceCustomizationEnabled(seConfigurationData: any): boolean {
        const at = this.getAvailableTypes(seConfigurationData);
        return at.length > 0;
    }

    public getNonCommerceActionsCount(variation: any): number {
        return (variation.actions || []).filter(this.isNonCommerceAction, this).length;
    }

    public getCommerceActionsCountMap(variation: any): any {
        const result: any = {};

        (variation.actions || []).filter(this.isCommerceAction, this).forEach((action: any) => {
            const typeKey = action.type.toLowerCase();

            let count = result[typeKey];
            if (count === undefined) {
                count = 1;
            } else {
                count += 1;
            }
            result[typeKey] = count;
        });

        return result;
    }

    public getCommerceActionsCount(variation: any): number {
        return (variation.actions || []).filter(this.isCommerceAction, this).length;
    }
}
