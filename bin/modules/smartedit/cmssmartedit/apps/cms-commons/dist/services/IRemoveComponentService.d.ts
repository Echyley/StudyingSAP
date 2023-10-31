import { ICMSComponent, RemoveComponentInfo } from '../dtos';
/**
 * Service interface specifying the contract used to remove a component from a slot.
 * This class serves as an interface and should be extended, not instantiated.
 */
export declare abstract class IRemoveComponentService {
    /**
     * Removes the component specified by the given ID from the component specified by the given ID.
     *
     * @param slotId The ID of the slot from which to remove the component.
     * @param componentId The ID of the component to remove from the slot.
     */
    removeComponent(configuration: RemoveComponentInfo): Promise<ICMSComponent>;
}
