import { GenericEditorStructure, PageTemplateType } from 'smarteditcommons';
import { PageType } from '../../dao/PageTypeService';
export interface PageBuilderModel {
    pageInfoFields?: GenericEditorStructure;
    pageType?: PageType;
    pageTemplate?: PageTemplateType;
}
export interface RestrictionsEditorFunctionBindings {
    reset?: () => void;
    cancel?: () => void;
    isDirty?: () => boolean;
}
