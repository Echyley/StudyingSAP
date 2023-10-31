import { ICMSPage } from 'smarteditcommons';
export interface RestrictionsDTO {
    onlyOneRestrictionMustApply: boolean;
    restrictionUuids: string[];
    alwaysEnableSubmit: boolean;
}
export interface WizardCallbacks {
    isDirtyPageInfo?: () => boolean;
    isValidPageInfo?: () => boolean;
    resetPageInfo?: () => void;
    savePageInfo?: () => Promise<ICMSPage>;
}
