/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { AsyncValidatorMap, ComponentTypeMap, ValidatorMap } from '../services';

import { AbstractForm } from './abstract-form';

export interface FormBuilderConfig {
    types?: ComponentTypeMap;
    validators?: ValidatorMap;
    asyncValidators?: AsyncValidatorMap;
}

export interface AbstractFormSchemas {
    [prop: string]: FormSchema;
}

export interface AbstractFormSchema {
    type: 'field' | 'group' | 'list';
    /**
     * Inputs to be set onto the the dynamic form components.
     */
    inputs?: { [key: string]: any };
    /**
     * Used to determine if current form property is persistable when
     * getPersistedValue is called on the form element. If property does not persist
     * than it will not be part of the generated object model.
     */
    persist?: boolean;
}

/**
 * Dynamic descriptor for a form list.
 */
export interface FormListSchema extends AbstractFormSchema {
    /**
     * The component.
     */
    component: string;
    /**
     * If only a single schema is provided, itt will be used for all values.
     * If an array of schemas are provided then each value at
     * their index will get the schema provided at the index in this
     * list. If the array is shorter than the array of values then
     * the last schema will be used for the rest of the values.
     */
    schema: FormSchema | FormSchema[];
    /**
     * A map of validators registered in the ValidatorRegistry for
     * validating the field. The index represents the name of the validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    validators?: {
        [index: string]: any;
    };
    /**
     * A map of async validators registered in the AsyncValidatorRegistry for
     * validating the field. The index represents the name of the async validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    asyncValidators?: {
        [index: string]: any;
    };
}

/**
 * Dynamic descriptor for a group field.
 */
export interface FormGroupSchema extends AbstractFormSchema {
    /**
     * The component id registered in the ComponentRegistry for
     * rendering the group component. If none is specified
     * the default group component is used to list the forms.
     */
    component?: string;
    /**
     * A map of AbstractFormSchema.
     */
    schemas: AbstractFormSchemas;
    /**
     * A map of validators registered in the ValidatorRegistry for
     * validating the field. The index represents the name of the validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    validators?: {
        [index: string]: any;
    };
    /**
     * A map of async validators registered in the AsyncValidatorRegistry for
     * validating the field. The index represents the name of the async validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    asyncValidators?: {
        [index: string]: any;
    };
}

/**
 * Dynamic descriptor for a form field.
 */
export interface FormFieldSchema extends AbstractFormSchema {
    /**
     * The component id registered in the ComponentRegistry for
     * rendering the form field.
     */
    component: string;
    /**
     * Sets disabled in the constructor of the FormControl.
     */
    disabled?: boolean;
    /**
     * A map of validators registered in the ValidatorRegistry for
     * validating the field. The index represents the name of the validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    validators?: {
        [index: string]: any;
    };
    /**
     * A map of async validators registered in the AsyncValidatorRegistry for
     * validating the field. The index represents the name of the async validator, and the value
     * represents parameters to be passed to the validator. If the value is
     * set to undefined, the validator wont be added to the FormControl.
     */
    asyncValidators?: {
        [index: string]: any;
    };
}

export type FormSchema = FormGroupSchema | FormFieldSchema | FormListSchema;

/**
 * Map of forms on the FormGroup's 'forms' property.
 */
export interface AbstractForms {
    [index: string]: AbstractForm;
}

/**
 * Implemented by a component for listening to changes when an input is changed dynamically
 * through the AbstractForm setInput method.
 * @example
 * <pre>
 *     @Component({ ... })
 *     export class DynamicFormComponent implements DynamicInputChange {
 *         onDynamicInputChange() {
 *             ...
 *         }
 *     }
 * <pre>
 */
export interface DynamicInputChange {
    onDynamicInputChange(): void;
}
