export interface SlotInfo {
    /** The Uid of the slot where to drop the component. */
    targetSlotId: string;
    /** The UUid of the slot where to drop the component. */
    targetSlotUUId?: string;
}
/** Represents the properties of the component required to create a clone. */
export interface CloneComponentInfo extends SlotInfo {
    dragInfo: DragInfo;
    /** The position in the slot where to add the new component. */
    position: number;
}
/** Represents the component to be added to a slot. */
export interface DragInfo {
    componentId: string;
    componentUuid: string;
    componentType: string;
}
export declare abstract class IComponentEditingFacade {
    addNewComponentToSlot(slotInfo: SlotInfo, catalogVersionUuid: string, componentType: string, position: number): Promise<void>;
    addExistingComponentToSlot(targetSlotId: string, dragInfo: DragInfo, position: number): Promise<void>;
    cloneExistingComponentToSlot(componentInfo: CloneComponentInfo): Promise<void>;
    moveComponent(sourceSlotId: string, targetSlotId: string, componentId: string, position: number): Promise<void>;
}
