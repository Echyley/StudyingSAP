export declare type IconAlignment = 'right' | 'left';
export interface CollapsibleContainerApi {
    isExpanded: () => boolean;
}
/**
 * Describes JSON object containing the configuration to be applied on a collapsible container.
 */
export interface CollapsibleContainerConfig {
    /**
     * Defines whether collapsible is opened by default
     */
    expandedByDefault: boolean;
    /**
     * Defines alignment of the icon(left or right) to the container header.
     */
    iconAlignment: IconAlignment;
    /**
     * Defines whether header icon is visible
     */
    iconVisible: boolean;
}
