import { IRestrictionType } from '../../../dao/RestrictionTypesRestService';
import { RestrictionCMSItem } from '../types';
interface BaseRestrictionConfig {
    getRestrictionTypesFn?: () => Promise<IRestrictionType[]>;
    getSupportedRestrictionTypesFn?: () => Promise<string[]>;
    mode: string;
}
export interface EditingRestrictionConfig extends BaseRestrictionConfig {
    restriction: RestrictionCMSItem;
}
export interface SelectingRestrictionConfig extends BaseRestrictionConfig {
    existingRestrictions: RestrictionCMSItem[];
}
export declare class RestrictionPickerConfigService {
    MODE_EDITING: string;
    MODE_SELECT: string;
    getConfigForEditing(existingRestriction: RestrictionCMSItem, getSupportedRestrictionTypesFn: () => Promise<string[]>): EditingRestrictionConfig;
    getConfigForSelecting(existingRestrictions: RestrictionCMSItem[], getRestrictionTypesFn: () => Promise<IRestrictionType[]>, getSupportedRestrictionTypesFn: () => Promise<string[]>): SelectingRestrictionConfig;
    isEditingMode(config: EditingRestrictionConfig | SelectingRestrictionConfig): boolean;
    isSelectMode(config: EditingRestrictionConfig | SelectingRestrictionConfig): boolean;
    isValidConfig(config: SelectingRestrictionConfig | EditingRestrictionConfig): boolean;
}
export {};
