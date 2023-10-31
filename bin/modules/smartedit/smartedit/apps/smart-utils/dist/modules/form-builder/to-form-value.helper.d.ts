import { FormSchema } from './models';
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
export declare const toFormValue: (data: any, schema: FormSchema) => any;
