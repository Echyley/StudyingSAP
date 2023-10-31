/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface ManageCustomizationViewModalData {
    customizationCode: string;
    /** Variation Code of the edited variation under Customization. Null when editing only a Customization. */
    variationCode: string | null;
}
