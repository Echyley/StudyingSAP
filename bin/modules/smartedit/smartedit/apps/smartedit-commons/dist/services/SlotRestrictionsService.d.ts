import { ISlotRestrictionsService, COMPONENT_IN_SLOT_STATUS } from './interfaces';
export declare class SlotRestrictionsService extends ISlotRestrictionsService {
    getAllComponentTypesSupportedOnPage(): Promise<any[] | void>;
    getSlotRestrictions(slotId: string): Promise<string[] | void>;
    isSlotEditable(slotId: string): Promise<boolean>;
    isComponentAllowedInSlot(slot: any, dragInfo: any): Promise<COMPONENT_IN_SLOT_STATUS>;
}
