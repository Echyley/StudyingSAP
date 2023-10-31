import { FormGroupSchema } from '@smart/utils';
import { SchemaDataMapper } from './RootSchemaDataMapper';
/**
 * A schema and data mapper for the GenericEditorTabComponent.
 */
export declare class TabSchemaDataMapper implements SchemaDataMapper {
    id: string;
    private mappers;
    constructor(id: string, mappers: SchemaDataMapper[]);
    toValue(): any;
    toSchema(): FormGroupSchema;
}
