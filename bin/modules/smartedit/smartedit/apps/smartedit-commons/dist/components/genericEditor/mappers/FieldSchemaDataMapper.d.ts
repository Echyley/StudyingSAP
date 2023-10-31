import { FormSchema, Payload } from '@smart/utils';
import { GenericEditorField } from '../types';
import { SchemaDataMapper } from './RootSchemaDataMapper';
/**
 * A schema and data mapper for the GenericEditorDynamicFieldComponent.
 */
export declare class FieldSchemaDataMapper implements SchemaDataMapper {
    id: string;
    private structure;
    private required;
    private component;
    constructor(id: string, structure: GenericEditorField, required: boolean, component: Payload);
    toValue(): any;
    toSchema(): FormSchema;
}
