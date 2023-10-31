/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';

import { LanguageService } from '../../../../../../services';
import { DropdownPopulatorPayload } from '../types';
import { OptionsDropdownPopulator } from './OptionsDropdownPopulator';

describe('OptionsDropdownPopulator', () => {
    let optionsDropdownPopulator: OptionsDropdownPopulator;

    const languageService = jasmine.createSpyObj<LanguageService>('languageService', [
        'getResolveLocale'
    ]);
    const translateService = jasmine.createSpyObj<TranslateService>('translateService', [
        'instant'
    ]);
    translateService.instant.and.callFake((key: string) => key);

    let payload: DropdownPopulatorPayload;

    beforeEach(() => {
        languageService.getResolveLocale.and.returnValue(Promise.resolve(null));

        payload = {
            field: {
                cmsStructureType: 'EditableDropdown',
                qualifier: 'dropdownA',
                i18nKey: 'theKey',
                idAttribute: 'uid',
                labelAttributes: ['label1', 'label2'],
                options: [
                    {
                        id: '1',
                        label: 'OptionA1'
                    },
                    {
                        id: '2',
                        label: 'OptionA2'
                    },
                    {
                        id: '3',
                        label: 'OptionA3'
                    },
                    {
                        id: '4',
                        label1: 'OptionA4'
                    },
                    {
                        id: '5',
                        label2: 'OptionA5'
                    },
                    {
                        id: '6',
                        label: 'OptionA6 - the label',
                        label2: 'OptionA6 - not the label'
                    },
                    {
                        uid: '7',
                        label: 'OptionA7'
                    }
                ]
            },
            model: {
                dropdownA: '1'
            },
            selection: null,
            search: null
        };

        optionsDropdownPopulator = new OptionsDropdownPopulator(languageService, translateService);
    });

    it(
        'GIVEN options populator is called WHEN I call fetchAll method ' +
            'THEN should return a promise containing list of options in the field object of the payload',
        async () => {
            const data = await optionsDropdownPopulator.fetchAll(payload);

            expect(data).toEqual([
                {
                    id: '1',
                    label: 'OptionA1'
                },
                {
                    id: '2',
                    label: 'OptionA2'
                },
                {
                    id: '3',
                    label: 'OptionA3'
                },
                {
                    id: '4',
                    label: 'OptionA4',
                    label1: 'OptionA4'
                },
                {
                    id: '5',
                    label: 'OptionA5',
                    label2: 'OptionA5'
                },
                {
                    id: '6',
                    label: 'OptionA6 - the label',
                    label2: 'OptionA6 - not the label'
                },
                {
                    id: '7',
                    uid: '7',
                    label: 'OptionA7'
                }
            ]);
        }
    );

    it(
        'GIVEN options populator is called WHEN I call fetchAll method with a search attribute ' +
            'THEN should return a promise containing list of filtered options based on the search string',
        async () => {
            payload.search = 'A2';
            const data = await optionsDropdownPopulator.fetchAll(payload);

            expect(data).toEqual([
                {
                    id: '2',
                    label: 'OptionA2'
                }
            ]);
        }
    );

    it(
        'GIVEN options populator is called WHEN I call fetchAll method with a search attribute ' +
            'THEN should return a promise containing list of filtered options based on the search string and language',
        async () => {
            const localizedPayload: DropdownPopulatorPayload = {
                field: {
                    cmsStructureType: 'EditableDropdown',
                    qualifier: 'dropdownA',
                    i18nKey: 'theKey',
                    idAttribute: 'uid',
                    labelAttributes: ['label1', 'label2'],
                    options: [
                        {
                            id: '1',
                            label: {
                                en: 'OptionA1-en',
                                fr: 'OptionA1-fr'
                            }
                        },
                        {
                            id: '2',
                            label: {
                                en: 'OptionA2-en',
                                fr: 'OptionA2-fr'
                            }
                        },
                        {
                            id: '3',
                            label: {
                                en: 'OptionA24-en',
                                fr: 'OptionA43-fr'
                            }
                        },
                        {
                            id: '4',
                            label: {
                                en: 'OptionA34-en',
                                fr: 'OptionA34-fr'
                            }
                        },
                        {
                            id: '5',
                            label: {
                                en: 'OptionA54-en',
                                fr: 'OptionA54-fr'
                            }
                        }
                    ]
                },
                model: {
                    dropdownA: '1'
                },
                selection: null,
                search: '4-en'
            };

            const data = await optionsDropdownPopulator.fetchAll(localizedPayload);

            expect(data).toEqual([
                {
                    id: '3',
                    label: {
                        en: 'OptionA24-en',
                        fr: 'OptionA43-fr'
                    }
                },
                {
                    id: '4',
                    label: {
                        en: 'OptionA34-en',
                        fr: 'OptionA34-fr'
                    }
                },
                {
                    id: '5',
                    label: {
                        en: 'OptionA54-en',
                        fr: 'OptionA54-fr'
                    }
                }
            ]);
        }
    );
});
