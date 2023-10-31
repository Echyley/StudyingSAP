/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FormGrouping, ILanguage } from '@smart/utils';
import lo from 'lodash';
import { IUriContext } from 'smarteditcommons/services';
import { Payload } from '../../../dtos';
import { GenericEditorField, GenericEditorFieldMessage, GenericEditorTab } from '../types';
import { GenericEditorState } from './GenericEditorState';

import { proxifyDataObject } from './InternalProperty';

const tabs: GenericEditorTab[] = [];
const string1 = 'eyJtZXNzYWdlIjoiVGhpcyBmaWVs';
const string2 = 'ZCBjYW5ub3QgY29udGFpbiBzcGVjaWF';
const string3 = 'sIGNoYXJhY3RlcnMuIiwicmVhc29uIjoi';
const string4 = 'bWlzc2luZyIsInN1YmplY3QiOiJmaWVsZDEiLCJzdWJ';
const string5 = 'qZWN0VHlwZSI6InBhcmFtZXRlciIsInR5cGUiOiJWYWxpZGF0aW9uR';
const string6 = 'XJyb3IiLCJsYW5ndWFnZSI6ImVuIiwibWFya2VyIjoiZW4ifQ==';
const finalString = string1 + string2 + string3 + string4 + string5 + string6;
const validationErrorsDefault = [
    {
        message: 'This field cannot contain special characters',
        reason: 'missing',
        subject: 'field1',
        subjectType: 'parameter',
        type: 'ValidationError'
    },
    {
        message: 'This field is required and must to be between 1 and 255 characters long.',
        reason: 'missing',
        subject: 'field2',
        subjectType: 'parameter',
        type: 'ValidationError'
    }
];
const validationErrorsEn = [
    {
        ...lo.cloneDeep(validationErrorsDefault[0]),
        message: 'This field cannot contain special characters. Language: [en]'
    },
    lo.cloneDeep(validationErrorsDefault[1])
];
const ERROR_MES_FOR_SPECIAL_CHARACTERS = 'This field cannot contain special characters.';

let form: jasmine.SpyObj<FormGrouping>;
let seValidationMessageParser: jasmine.Spy;

describe('GenericEditor - GenericEditorState sanitizedPayload will call customSanitize when defined', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('for ShortString component', () => {
        const fields: GenericEditorField[] = [
            {
                qualifier: 'specialField',
                cmsStructureType: 'ShortString',
                localized: true,
                customSanitize(value: string, sanitize: (str: string) => string): string {
                    return `${value}_sanitized`;
                }
            }
        ] as any;

        const payload: any = {
            specialField: 'some_value'
        };

        const state = new GenericEditorState(
            'id',
            form,
            payload,
            {},
            {},
            tabs,
            fields,
            [] as any,
            {} as any
        );

        const sanitizedPayload = state.sanitizedPayload();
        expect(sanitizedPayload).toEqual({
            specialField: 'some_value_sanitized'
        });
    });

    it('for LongString component', () => {
        const fields: GenericEditorField[] = [
            {
                qualifier: 'specialField',
                cmsStructureType: 'LongString',
                localized: true,
                customSanitize(value: string, sanitize: (str: string) => string) {
                    return `${value}_sanitized`;
                }
            }
        ] as any;

        const payload: any = {
            specialField: 'some_value'
        };

        const state = new GenericEditorState(
            'id',
            form,
            payload,
            {},
            {},
            tabs,
            fields,
            [] as any,
            {} as any
        );

        const sanitizedPayload = state.sanitizedPayload();
        expect(sanitizedPayload).toEqual({
            specialField: 'some_value_sanitized'
        });
    });
});

describe('GenericEditor - GenericEditorState - validation', () => {
    function getGenericEditorStateArgs(
        fields: GenericEditorField[]
    ): [
        string,
        FormGrouping,
        any,
        any,
        any,
        GenericEditorTab[],
        GenericEditorField[],
        ILanguage[],
        IUriContext
    ] {
        return ['id', form, {}, {}, {}, tabs, fields, [] as any, {} as any];
    }

    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('displayValidationErrors will add errors messages and localization languages to the field', () => {
        const validationErrors = lo.cloneDeep(validationErrorsDefault);
        const fields: GenericEditorField[] = [
            {
                qualifier: 'field1',
                cmsStructureType: 'something'
            },
            {
                qualifier: 'field2',
                cmsStructureType: 'something'
            }
        ];

        const state = new GenericEditorState(...getGenericEditorStateArgs(fields));

        spyOn(state as any, '_getParseValidationMessage').and.returnValue(
            seValidationMessageParser
        );

        state.displayValidationMessages(validationErrors, true);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages.length).toEqual(1);

        expect(fields[0].messages[0].message).toEqual(validationErrors[0].message);
        expect(fields[0].messages[0].marker).toEqual('field1');

        expect(fields[1].messages[0].message).toEqual(validationErrors[1].message);
        expect(fields[1].messages[0].marker).toEqual('field2');
    });

    it('displayValidationMessages will add language from validation errors for the language property if the field is localized else will add the qualifier to the language property ', () => {
        const validationErrors = lo.cloneDeep(validationErrorsEn);
        seValidationMessageParser.and.callFake((message: any) => {
            const error: any = {};
            if (message === validationErrors[0].message) {
                error.message = ERROR_MES_FOR_SPECIAL_CHARACTERS;
                error.language = 'en';
            } else {
                error.message = message;
            }
            return error;
        });

        const fields: GenericEditorField[] = [
            {
                qualifier: 'field1',
                cmsStructureType: 'someType',
                localized: true
            },
            {
                qualifier: 'field2',
                cmsStructureType: 'someType'
            }
        ];

        const state = new GenericEditorState(...getGenericEditorStateArgs(fields));

        spyOn(state as any, '_getParseValidationMessage').and.callFake(
            (m: GenericEditorFieldMessage) => seValidationMessageParser(m)
        );

        state.displayValidationMessages(validationErrors, true);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages.length).toEqual(1);

        expect(fields[0].messages[0].message).toEqual(ERROR_MES_FOR_SPECIAL_CHARACTERS);
        expect(fields[0].messages[0].marker).toEqual('en');

        expect(fields[1].messages[0].message).toEqual(validationErrors[1].message);
        expect(fields[1].messages[0].marker).toEqual('field2');
    });

    it('displayValidationMessages will not show the message if it has already been added to the list of messages', () => {
        const validationErrors = lo.cloneDeep(validationErrorsEn);
        const fields = [
            {
                qualifier: 'field1',
                localized: true,
                messages: [
                    {
                        message: 'This field cannot contain special characters. Language: [en]',
                        reason: 'missing',
                        subject: 'field1',
                        subjectType: 'parameter',
                        type: 'ValidationError',
                        uniqId: finalString
                    }
                ]
            },
            {
                qualifier: 'field2'
            }
        ] as any as GenericEditorField[];

        const state = new GenericEditorState(...getGenericEditorStateArgs(fields));

        spyOn(state as any, '_getParseValidationMessage').and.callFake(function () {
            return {
                message: ERROR_MES_FOR_SPECIAL_CHARACTERS,
                language: 'en',
                marker: 'en'
            };
        });
        state.displayValidationMessages(validationErrors, true);
        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages[0]).toEqual({
            message: ERROR_MES_FOR_SPECIAL_CHARACTERS,
            reason: 'missing',
            subject: 'field1',
            subjectType: 'parameter',
            language: 'en',
            marker: 'en',
            type: 'ValidationError',
            uniqId: finalString
        });
    });

    it('GIVEN a list of validationMessages WHEN displayValidationErrors is called with keepAllErrors as false THEN it will filter out all fields that are pristine', () => {
        const validationErrors = [
            {
                ...validationErrorsDefault[0],
                fromSubmit: false,
                isNonPristine: true
            },
            {
                ...validationErrorsDefault[1],
                fromSubmit: false,
                isNonPristine: false
            }
        ];

        const fields: GenericEditorField[] = [
            {
                qualifier: 'field1',
                cmsStructureType: 'someType'
            },
            {
                qualifier: 'field2',
                cmsStructureType: 'someType'
            }
        ];

        const state = new GenericEditorState(...getGenericEditorStateArgs(fields));

        spyOn(state as any, '_getParseValidationMessage').and.callFake(
            (m: GenericEditorFieldMessage) => seValidationMessageParser(m)
        );

        state.displayValidationMessages(validationErrors, false);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages[0].subject).toEqual('field1');
    });

    it('fieldsAreUserChecked WILL fail validation WHEN a required checkbox field is not checked', () => {
        const fields: GenericEditorField[] = [
            {
                qualifier: 'content',
                cmsStructureType: 'Paragraph',
                requiresUserCheck: {
                    content: true
                },
                isUserChecked: false
            }
        ];

        const state = new GenericEditorState(...getGenericEditorStateArgs(fields));

        const valid = state.fieldsAreUserChecked();
        expect(valid).toBeFalsy();
    });
});

describe('GenericEditor - GenericEditorState', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('method isDirty will sanitize before checking if pristine and component HTML are equal', () => {
        let pristine: Payload = {
            a: {
                en: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>'
            },
            b: '1',
            c: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>'
        };

        let component = {
            a: {
                en: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>'
            },
            b: '1',
            c: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>'
        };

        let fields: GenericEditorField[] = [
            {
                cmsStructureType: 'RichText',
                qualifier: 'a',
                localized: true
            },
            {
                qualifier: 'b',
                cmsStructureType: 'someType'
            },
            {
                qualifier: 'c',
                cmsStructureType: 'RichText',
                localized: false
            }
        ] as any;

        let state = new GenericEditorState(
            'id',
            form,
            component,
            {},
            pristine,
            tabs,
            fields,
            [] as any,
            {} as any
        );

        expect(state.isDirty()).toEqual(false);

        pristine = {
            a: {
                en: '<h2>test1</h2> <p>test2</p>'
            }
        };

        component = {
            a: {
                en: '<h2>TEST2</h2> \n<p>test1</p>'
            }
        } as any;

        fields = [
            {
                cmsStructureType: 'RichText',
                qualifier: 'a',
                localized: true
            }
        ] as any;

        state = new GenericEditorState(
            'id',
            form,
            component,
            {},
            pristine,
            tabs,
            fields,
            [] as any,
            {} as any
        );

        expect(state.isDirty()).toEqual(true);
    });

    it('isDirty will return true even for properties that are not fields', () => {
        const pristine = {
            a: '123 ',
            b: '0'
        };

        const component = {
            a: '123',
            b: ''
        };

        const state = new GenericEditorState(
            'id',
            form,
            component,
            {},
            pristine,
            tabs,
            [],
            [] as any,
            {} as any
        );

        expect(state.isDirty()).toEqual(true);
    });

    it('should patch the component', () => {
        const fields: GenericEditorField[] = [
            {
                qualifier: 'property'
            },
            {
                qualifier: 'content',
                localized: true
            }
        ] as any;

        const component = {
            content: {
                en: 'content_en'
            },
            property: 'property_data'
        };

        const state = new GenericEditorState(
            'id',
            form,
            component,
            proxifyDataObject(component),
            {},
            tabs,
            fields,
            [] as any,
            {} as any
        );

        state.patchComponent({
            content: {
                en: 'content_new_value'
            },
            property: 'property_new_value'
        });

        expect(state.component).toEqual({
            content: {
                en: 'content_new_value'
            },
            property: 'property_new_value'
        });
    });
});

function buildCommonSpyObjects() {
    seValidationMessageParser = jasmine
        .createSpy('seValidationMessageParser')
        .and.callFake(function (message: any) {
            return {
                message
            };
        });

    form = jasmine.createSpyObj('form', ['control']);
}
