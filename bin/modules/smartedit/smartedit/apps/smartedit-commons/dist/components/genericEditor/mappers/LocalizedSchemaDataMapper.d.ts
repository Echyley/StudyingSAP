import { FormSchema, Payload } from '@smart/utils';
import { ILanguage } from '../../../services';
import { GenericEditorField } from '../types';
import { SchemaDataMapper } from './RootSchemaDataMapper';
/**
 * A schema and data mapper for the LocalizedElementComponent.
 */
export declare class LocalizedSchemaDataMapper implements SchemaDataMapper {
    id: string;
    private mappers;
    private structure;
    private languages;
    private component;
    constructor(id: string, mappers: SchemaDataMapper[], structure: GenericEditorField, languages: ILanguage[], component: Payload);
    toValue(): any;
    toSchema(): FormSchema;
}
