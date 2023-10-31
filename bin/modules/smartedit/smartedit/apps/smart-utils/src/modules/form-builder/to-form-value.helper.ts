/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FormGroupSchema, FormListSchema, FormSchema } from './models';

/**
 * Recursively explore schema to return a form data structure
 * which is used to generated dynamic forms.
 *
 * If a schema is a field, simply return the related data
 *
 * If a schema is a group or list, explore its inner
 * schemas and return equivalent value
 *
 * @param data persisted data object
 * @param schema related schema
 */
export const toFormValue = (data: any, schema: FormSchema): any => {
    if (schema.type === 'field') {
        // Return related value
        return data;
    }

    if (schema.type === 'group') {
        return processGroupSchema(data, schema as FormGroupSchema);
    }

    return processListSchema(data, schema as FormListSchema);
};

/**
 * Process schemas of type FormGroupSchema.
 *
 * @param data persisted data object
 * @param schema related schema
 */
const processGroupSchema = (data: any, schema: FormGroupSchema): any => {
    const value: any = {};

    // Populate fields of value based on inner schemas
    Object.keys(schema.schemas).forEach((key: string) => {
        // Create inner data object with only data related to the inner schema
        const innerData = getInnerData(data, key, schema.schemas[key].type);
        value[key] = toFormValue(innerData, schema.schemas[key]);
    });

    return value;
};

/**
 * Process schemas of type FormListSchema.
 *
 * @param data persisted data object
 * @param schema related schema
 */
const processListSchema = (data: any, schema: FormListSchema): any => {
    // If schema is a list, related data must also be a list
    const listSchema = schema;
    const listValue: any[] = [];
    const listData = Array.isArray(data) ? data : [];

    // If there is only one inner schema, treat it as an array of 1
    const innerSchemas = Array.isArray(listSchema.schema) ? listSchema.schema : [listSchema.schema];

    listData.forEach((el, i) => {
        // If there is more data than schemas, use the last one.
        if (i > innerSchemas.length - 1) {
            i = innerSchemas.length - 1;
        }

        listValue.push(toFormValue(el, innerSchemas[i]));
    });
    return listValue;
};

/**
 * Return inner field of persisted data.
 *
 * If inner field is found, then it is returned
 * If the schema is a field, and the data field wasn't found, return null
 * Otherwise, the data object itself is returned.
 *
 * @param data persisted data object
 * @param key name of the value to retrieve from data
 * @param schemaType type of the related schema
 */
const getInnerData = (data: any, key: string, schemaType: 'field' | 'group' | 'list'): any => {
    // If data is null or undefined, return null
    if (data == null) {
        return null;
    }

    const value = data[key];
    if (value !== undefined) {
        return value;
    } else if (schemaType === 'field') {
        return null;
    }

    return data;
};
