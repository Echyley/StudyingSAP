/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CustomizationVariation } from '../types';
export class Customize {
    public enabled: boolean;
    public selectedCustomization: any;
    public selectedVariations: CustomizationVariation[] | CustomizationVariation | null;
    public selectedComponents: string[] | null;

    constructor() {
        this.enabled = false;
        this.selectedCustomization = null;
        this.selectedVariations = null;
        this.selectedComponents = null;
    }
}
