import { CMSItemStructureField } from 'cmscommons';
export interface ErrorMapping {
    subject: string;
    errorCode: string;
}
export interface ActionableErrorMapping extends ErrorMapping {
    structure: CMSItemStructureField;
}
export interface NonActionableErrorMapping extends ErrorMapping {
    messageKey: string;
}
export declare const ACTIONABLE_ERRORS: ActionableErrorMapping[];
export declare const NON_ACTIONABLE_ERRORS: NonActionableErrorMapping[];
