/** Represents configuration of a component to be displayed by ComponentVisibilityAlert. */
export interface SlotComponent {
    /** CatalogVersion uuid of the cmsItem. */
    catalogVersion: string;
    /**
     * Uuid of the cmsItem.
     */
    itemId: string;
    /**
     * Type of the cmsItem
     */
    itemType: string;
    /** Stating whether a restriction is applied to the cmsItem. */
    restricted: boolean;
    /**
     * Id of the slot where the cmsItem was added or modified.
     */
    slotId: string;
    /**
     * Stating whether the cmsItem is rendered.
     */
    visible: boolean;
}
