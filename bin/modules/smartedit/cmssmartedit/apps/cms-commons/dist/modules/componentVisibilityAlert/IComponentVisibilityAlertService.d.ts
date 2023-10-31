import { SlotComponent } from './types';
/**
 * Displays an alert informing the user possibility to edit Component Settings when the component is hidden or restricted.
 * Provides an instant feedback for the user so he can still change some settings instead of searching the Storefront for
 * the slot to which the component belongs.
 *
 * When the user clicks on the link, the Alert will be closed and Component Settings will be reopened.
 * When no action is performed, the alert will disappear after a few seconds.
 */
export declare abstract class IComponentVisibilityAlertService {
    /**
     * Method checks on a component visibility and triggers the display of a
     * contextual alert when the component is either hidden or restricted.
     */
    checkAndAlertOnComponentVisibility(component: SlotComponent): Promise<void>;
}
