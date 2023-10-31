import { Payload } from '@smart/utils';
import { ILanguage } from '../../../services';
import { GenericEditorFieldsMap, GenericEditorTab } from '../types';
import { RootSchemaDataMapper } from './RootSchemaDataMapper';
/**
 * @internal
 * The createRootMapper is an entry factory to creating the RootSchemaDataMapper and
 * the subsequent the nested mappers for tabs, localized fields, and dynamic fields components.
 * The returning instance is of type RootSchemaDataMapper which contains
 * two methods for building the data and schema object that will be passed
 * to the form builder's schema compiler service to build the FormGrouping.
 *
 * @param {GenericEditorFieldsMap} fieldsMap
 * @param {Payload} component
 * @param {ILanguage[]} languages
 * @param {GenericEditorTab[]} tabs
 * @return {RootSchemaDataMapper} A mapper for building data and schema for it to be
 * consumed by the SchemaCompilerService in the FormBuilder module.
 */
export declare const createRootMapper: (fieldsMap: GenericEditorFieldsMap, component: Payload, languages: ILanguage[], tabs: GenericEditorTab[]) => RootSchemaDataMapper;
