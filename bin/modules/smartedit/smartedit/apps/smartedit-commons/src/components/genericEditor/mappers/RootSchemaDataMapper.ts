/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbstractFormSchemas, FormGroupSchema, FormSchema, Payload, TypedMap } from '@smart/utils';
import { GenericEditorRootTabsComponent } from '../components/rootTabs/GenericEditorRootTabsComponent';
import {
    GenericEditorField,
    GenericEditorFieldsMap,
    GenericEditorFieldValidatorMap,
    GenericEditorTab
} from '../types';

export const createValidatorMap = (
    validators: GenericEditorFieldValidatorMap,
    id: string,
    structure: GenericEditorField,
    required: boolean,
    component: Payload
): TypedMap<boolean> =>
    Object.keys(validators || {}).reduce(
        (acc: TypedMap<boolean>, item: string) => ({
            ...acc,
            [item]: validators[item](id, structure, required, component)
        }),
        {}
    );
export interface SchemaDataMapper {
    id: string;
    toValue(): any;
    toSchema(): FormSchema;
}

/**
 * A schema and data mapper for the GenericEditorRootTabsComponent.
 */
export class RootSchemaDataMapper {
    constructor(
        private mappers: SchemaDataMapper[],
        private tabs: GenericEditorTab[],
        private fieldsMap: GenericEditorFieldsMap
    ) {}

    toValue(): any {
        return this.mappers.reduce((acc, mapper) => {
            acc[mapper.id] = mapper.toValue();
            return acc;
        }, {} as any);
    }

    toSchema(): FormGroupSchema {
        return {
            type: 'group',
            component: 'tabs',
            schemas: this.mappers.reduce((acc, mapper) => {
                acc[mapper.id] = mapper.toSchema();
                return acc;
            }, {} as AbstractFormSchemas),
            inputs: {
                tabs: this.tabs,
                fieldsMap: this.fieldsMap
            } as Partial<GenericEditorRootTabsComponent>
        };
    }
}
