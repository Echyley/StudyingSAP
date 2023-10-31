export interface ManageCustomizationViewModalData {
    customizationCode: string;
    /** Variation Code of the edited variation under Customization. Null when editing only a Customization. */
    variationCode: string | null;
}
