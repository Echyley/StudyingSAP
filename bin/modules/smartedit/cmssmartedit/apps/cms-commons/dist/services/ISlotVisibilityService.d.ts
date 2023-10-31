import { ICMSComponent } from 'cmscommons';
import { TypedMap } from 'smarteditcommons';
export declare abstract class ISlotVisibilityService {
    /**
     * Returns the list of hidden components for a given slotId
     */
    getHiddenComponents(slotId: string): Promise<ICMSComponent[]>;
    /**
     * Reloads and cache's the pagesContentSlotsComponents for the current page in context.
     * this method can be called when ever a component is added or modified to the slot
     * so that the pagesContentSlotsComponents is re-evaluated.
     *
     * @returns A promise that resolves to the contentSlot - Components [] map for the page in context.
     */
    reloadSlotsInfo(): Promise<TypedMap<ICMSComponent[]>>;
}
