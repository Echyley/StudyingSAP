/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { of } from 'rxjs';
import { AbstractForm, FormField, FormGroupSchema, FormList, FormSchema } from '../models';

import { AsyncValidatorRegistryService } from './async-validator-registry.service';
import { ComponentRegistryService } from './component-registry.service';
import { SchemaCompilerService } from './schema-compiler.service';
import { ValidatorRegistryService } from './validator-registry.service';

let schemaCompilerService: SchemaCompilerService;

let componentRegistry: jasmine.SpyObj<ComponentRegistryService>;
let validatorRegistry: jasmine.SpyObj<ValidatorRegistryService>;
let asyncValidatorRegistry: jasmine.SpyObj<AsyncValidatorRegistryService>;

let requiredValidator: jasmine.Spy;
let uniqueValidator: jasmine.Spy;

const CUSTOM_PARAM = 'custom-parameters';
const groupSchema: FormGroupSchema = {
    type: 'group',
    schemas: {
        nested: {
            type: 'group',
            component: 'custom',
            validators: {
                required: CUSTOM_PARAM
            },
            schemas: {}
        },
        name: {
            type: 'field',
            component: 'text',
            validators: {
                required: CUSTOM_PARAM,
                'dont-add': undefined
            },
            asyncValidators: {
                unique: 'https://unique.com'
            },
            inputs: {
                property: 'some-property'
            }
        }
    }
};
const differentTypesSchemas = [
    {
        type: 'field',
        component: 'text',
        inputs: {
            type: 'number'
        }
    },
    {
        type: 'field',
        component: 'text',
        inputs: {
            type: 'text'
        }
    }
] as FormSchema[];

describe('FormBuilder - SchemaCompilerService', () => {
    beforeEach(() => {
        createCommonSpyObjects();
    });

    it('should compile a group schema recursively with proper inputs', () => {
        const group = schemaCompilerService.compileGroup(null, groupSchema);

        expect(componentRegistry.get.calls.allArgs()).toEqual([['custom'], ['text']]);

        expect(requiredValidator).toHaveBeenCalledWith(CUSTOM_PARAM);
        expect(validatorRegistry.get.calls.argsFor(0)).toEqual(['required']);
        expect(validatorRegistry.get.calls.argsFor(1)).toEqual(['required']);

        expect(uniqueValidator).toHaveBeenCalledWith('https://unique.com');
        expect(asyncValidatorRegistry.get.calls.allArgs()).toEqual([['unique']]);

        expect(group.value).toEqual({
            nested: {},
            name: null
        });

        const formElement = group.get('name') as AbstractForm;
        expect(formElement.getInput('property')).toBe('some-property');
    });

    it('should compile a group schema recursively with initial value', () => {
        const group = schemaCompilerService.compileGroup(
            {
                name: 'Bob'
            },
            groupSchema
        );

        const formElement = group.get('name') as AbstractForm;
        expect(formElement.value).toBe('Bob');
    });

    it('should pick up inline components from the options instead of the components in the registry', () => {
        const group = schemaCompilerService.compileGroup(
            {
                name: 'Bob'
            },
            {
                type: 'group',
                schemas: {
                    name: {
                        type: 'field',
                        component: 'custom'
                    }
                }
            },
            {
                components: {
                    /**
                     * The real type is actually an Angular component class.
                     */
                    custom: 'custom-inline-component' as any
                }
            }
        );

        const formElement = group.get('name') as AbstractForm;
        expect(formElement.component).toBe('custom-inline-component' as any);
        expect(componentRegistry.get).toHaveBeenCalledTimes(0);
    });

    it('should pick up inline validators from the options instead of the validators in the registries', () => {
        const inlineValidator = jasmine.createSpy('custom_inline');
        const inlineValidatorAsync = jasmine.createSpy('custom_inline_async');

        const config = {
            validators: {
                custom_inline: inlineValidator.and.returnValue(() => null)
            },
            asyncValidators: {
                custom_inline_async: inlineValidatorAsync.and.returnValue(() =>
                    Promise.resolve(null)
                )
            }
        };

        const group = schemaCompilerService.compileGroup(
            {
                name: 'Bob'
            },
            {
                type: 'group',
                validators: {
                    required: 'custom_inline_param'
                },
                asyncValidators: {
                    unique: 'custom_inline_async_param'
                },
                schemas: {
                    name: {
                        type: 'field',
                        component: 'custom',
                        validators: {
                            custom_inline: 'custom_inline_param'
                        },
                        asyncValidators: {
                            custom_inline_async: 'custom_inline_async_param'
                        }
                    }
                }
            },
            config
        );

        const formElement = group.get('name') as FormField;
        expect(formElement.validatorParams.validators.custom_inline).toBe('custom_inline_param');
        expect(formElement.validatorParams.asyncValidators.custom_inline_async).toBe(
            'custom_inline_async_param'
        );
        expect(group.validatorParams.get('required')).toBe('custom_inline_param');
        expect(group.validatorParams.getAsync('unique')).toBe('custom_inline_async_param');
        expect(inlineValidator).toHaveBeenCalledWith('custom_inline_param');
        expect(inlineValidatorAsync).toHaveBeenCalledWith('custom_inline_async_param');

        expect(validatorRegistry.get.calls.argsFor(0)[0]).toBe('required');
        expect(asyncValidatorRegistry.get.calls.argsFor(0)[0]).toBe('unique');
    });

    it('should compile a list schema with an initial value', () => {
        const list = schemaCompilerService.compileList([1, 2, 3], {
            type: 'list',
            component: 'list',
            validators: {
                required: CUSTOM_PARAM
            },
            schema: {
                type: 'field',
                component: 'text'
            }
        });

        expect(list.size()).toBe(3);
        expect(list.controls.length).toBe(3);
        expect(list.getPersistedValue()).toEqual([1, 2, 3]);

        expect(list.controls[0].component).toBe('text-type' as any);
        expect(list.controls[0].persist).toBeTruthy();
        expect(list.component).toBe('custom-list' as any);
        expect(list.validatorParams.get('required')).toBe(CUSTOM_PARAM);
        expect(list.persist).toBeTruthy();
    });

    it('should compile a group with a nested list', () => {
        const group = schemaCompilerService.compileGroup(
            {
                list: [1, 2, 3]
            },
            {
                type: 'group',
                schemas: {
                    list: {
                        type: 'list',
                        component: 'list',
                        schema: {
                            type: 'field',
                            component: 'text'
                        }
                    }
                }
            }
        );

        const list = group.get('list') as FormList;
        expect(list.controls.length).toBe(3); // maybe
        expect(group.getPersistedValue()).toEqual({
            list: [1, 2, 3]
        });
    });

    it('should throw an error if no schema was provided in the list form list forms', () => {
        expect(() => {
            schemaCompilerService.compileList([1, 2, 3], {
                type: 'list',
                component: 'list',
                schema: []
            });
        }).toThrowError(
            'SchemaCompilerService - One or more schemas must be provided to compile a form list.'
        );
    });

    it('should map values in a list to different types of schemas', () => {
        const list = schemaCompilerService.compileList([1, 'string'], {
            type: 'list',
            component: 'list',
            schema: differentTypesSchemas
        });

        expect(list.controls[0].getInput('type')).toBe('number');
        expect(list.controls[0].value).toBe(1);
        expect(list.controls[1].getInput('type')).toBe('text');
        expect(list.controls[1].value).toBe('string');
    });

    it('should map values in a list to different types of schemas with more values then schemas', () => {
        const list = schemaCompilerService.compileList([1, 'string', 'string', 'string'], {
            type: 'list',
            component: 'list',
            schema: differentTypesSchemas
        });

        const [first, ...others] = list.controls;

        expect(first.getInput('type')).toBe('number');
        expect(first.value).toBe(1);

        others.forEach((form) => {
            expect(form.getInput('type')).toBe('text');
            expect(form.value).toBe('string');
        });
    });
});

function createCommonSpyObjects() {
    componentRegistry = jasmine.createSpyObj('componentRegistry', ['get', 'set']);
    validatorRegistry = jasmine.createSpyObj('validatorRegistry', ['get', 'set']);
    asyncValidatorRegistry = jasmine.createSpyObj('asyncValidatorRegistry', ['get', 'set']);

    schemaCompilerService = new SchemaCompilerService(
        componentRegistry,
        validatorRegistry,
        asyncValidatorRegistry
    );

    componentRegistry.get.and.callFake((name: string): any => {
        switch (name) {
            case 'custom':
                return 'custom-type';
            case 'text':
                return 'text-type';
            case 'list':
                return 'custom-list';
            default:
                return '';
        }
    });

    requiredValidator = jasmine.createSpy('requiredValidator');
    validatorRegistry.get.and.callFake((name: string): any => {
        switch (name) {
            case 'required':
                return (params: string) => () => requiredValidator(params);
            case 'dont-add':
                return () => () => 'dont-add-validator';
            default:
                return () => () => '';
        }
    });

    uniqueValidator = jasmine.createSpy('uniqueValidator');
    asyncValidatorRegistry.get.and.callFake((name: string): any => {
        if (name === 'unique') {
            return (params: string) => () => of(uniqueValidator(params));
        }
        return () => () => '';
    });
}
