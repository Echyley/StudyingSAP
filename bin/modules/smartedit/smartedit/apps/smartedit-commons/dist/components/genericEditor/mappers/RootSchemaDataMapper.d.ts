import { FormGroupSchema, FormSchema, Payload, TypedMap } from '@smart/utils';
import { GenericEditorField, GenericEditorFieldsMap, GenericEditorFieldValidatorMap, GenericEditorTab } from '../types';
export declare const createValidatorMap: (validators: GenericEditorFieldValidatorMap, id: string, structure: GenericEditorField, required: boolean, component: Payload) => TypedMap<boolean>;
export interface SchemaDataMapper {
    id: string;
    toValue(): any;
    toSchema(): FormSchema;
}
/**
 * A schema and data mapper for the GenericEditorRootTabsComponent.
 */
export declare class RootSchemaDataMapper {
    private mappers;
    private tabs;
    private fieldsMap;
    constructor(mappers: SchemaDataMapper[], tabs: GenericEditorTab[], fieldsMap: GenericEditorFieldsMap);
    toValue(): any;
    toSchema(): FormGroupSchema;
}
