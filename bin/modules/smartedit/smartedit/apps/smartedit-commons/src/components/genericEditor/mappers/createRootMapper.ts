/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from '@smart/utils';
import { toPairs } from 'lodash';

import { ILanguage } from '../../../services';

import { proxifyDataObject } from '../models';
import { GenericEditorField, GenericEditorFieldsMap, GenericEditorTab } from '../types';
import { FieldSchemaDataMapper } from './FieldSchemaDataMapper';
import { LocalizedSchemaDataMapper } from './LocalizedSchemaDataMapper';
import { RootSchemaDataMapper } from './RootSchemaDataMapper';
import { TabSchemaDataMapper } from './TabSchemaDataMapper';

const createFieldMapper = (
    qualifier: string,
    field: GenericEditorField,
    required: boolean,
    component: Payload
): FieldSchemaDataMapper => new FieldSchemaDataMapper(qualifier, field, required, component);

const createLocalizedMapper = (
    field: GenericEditorField,
    languages: ILanguage[],
    component: Payload
): LocalizedSchemaDataMapper => {
    if (component[field.qualifier] === undefined) {
        component[field.qualifier] = {};
    }

    component[field.qualifier] = proxifyDataObject(component[field.qualifier]);
    const localMappers = languages.map(({ isocode, required }) =>
        createFieldMapper(
            isocode,
            field,
            field.required && required,
            component[field.qualifier] as Payload
        )
    );

    return new LocalizedSchemaDataMapper(
        field.qualifier,
        localMappers,
        field,
        languages,
        component
    );
};

const createTabMapper = (
    id: string,
    fields: GenericEditorField[],
    languages: ILanguage[],
    component: Payload
): TabSchemaDataMapper => {
    const fieldMappers = fields.map((field: GenericEditorField) => {
        if (field.localized) {
            return createLocalizedMapper(field, languages, component);
        }
        return createFieldMapper(field.qualifier, field, field.required, component);
    });

    return new TabSchemaDataMapper(id, fieldMappers);
};

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
export const createRootMapper = (
    fieldsMap: GenericEditorFieldsMap,
    component: Payload,
    languages: ILanguage[],
    tabs: GenericEditorTab[]
): RootSchemaDataMapper => {
    const rootMappers = toPairs(fieldsMap).map(([id, fields]: [string, GenericEditorField[]]) =>
        createTabMapper(id, fields, languages, component)
    );

    return new RootSchemaDataMapper(rootMappers, tabs, fieldsMap);
};
