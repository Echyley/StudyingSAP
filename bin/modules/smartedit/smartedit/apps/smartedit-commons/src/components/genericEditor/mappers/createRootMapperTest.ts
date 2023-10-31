/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { createRootMapper } from './createRootMapper';

const fieldsMap = {
    default: [
        {
            cmsStructureType: 'LongString',
            qualifier: 'headline',
            localized: false,
            required: true,
            template: 'longStringTemplate.html',
            editable: true
        },
        {
            cmsStructureType: 'Boolean',
            qualifier: 'active',
            localized: false,
            required: false,
            template: 'booleanWrapperTemplate.html',
            editable: true
        },
        {
            cmsStructureType: 'Boolean',
            qualifier: 'visible',
            localized: false,
            required: false,
            template: 'booleanWrapperTemplate.html',
            editable: true
        },
        {
            cmsStructureType: 'RichText',
            qualifier: 'content',
            localized: true,
            template: 'richTextTemplate.html',
            editable: true
        },
        {
            cmsStructureType: 'RichText',
            qualifier: 'contentRequired',
            localized: true,
            required: true,
            template: 'richTextTemplate.html',
            editable: true
        }
    ]
};
const tabs = [{ id: 'default', title: 'se.genericeditor.tab.default.title' }];
const component = {
    headline: 'The Headline',
    active: true,
    content: {
        en: 'the content to edit',
        fr: 'le contenu a editer'
    },
    contentRequired: {
        en: 'the required content to edit',
        fr: 'le obligatoire contenu a editer'
    },
    external: false,
    urlLink: '/url-link'
};
const languages = [
    { nativeName: 'English', isocode: 'en', required: true },
    { nativeName: 'French', isocode: 'fr', required: false }
] as any;

describe('GenericEditor - createRootMapper', () => {
    it('should create a root mapper', () => {
        const mapper = createRootMapper(fieldsMap, component, languages, tabs);

        expect(mapper.toValue()).toEqual({
            default: {
                headline: 'The Headline',
                active: true,
                visible: undefined,
                content: component.content,
                contentRequired: component.contentRequired
            }
        });
        expect(mapper.toSchema()).toEqual({
            type: 'group',
            component: 'tabs',
            inputs: {
                tabs,
                fieldsMap
            },
            schemas: {
                default: {
                    type: 'group',
                    persist: false,
                    schemas: {
                        headline: {
                            type: 'field',
                            component: 'field',
                            validators: {
                                required: true
                            },
                            inputs: {
                                component,
                                id: 'headline',
                                qualifier: 'headline',
                                field: fieldsMap.default[0]
                            }
                        },
                        active: {
                            type: 'field',
                            component: 'field',
                            inputs: {
                                component,
                                id: 'active',
                                qualifier: 'active',
                                field: fieldsMap.default[1]
                            },
                            validators: {
                                required: undefined
                            }
                        },
                        visible: {
                            type: 'field',
                            component: 'field',
                            inputs: {
                                component,
                                id: 'visible',
                                qualifier: 'visible',
                                field: fieldsMap.default[2]
                            },
                            validators: {
                                required: undefined
                            }
                        },
                        content: {
                            type: 'group',
                            component: 'localized',
                            inputs: {
                                field: fieldsMap.default[3],
                                languages,
                                component
                            },
                            schemas: {
                                en: {
                                    type: 'field',
                                    component: 'field',
                                    validators: {
                                        required: undefined
                                    },
                                    inputs: {
                                        component: component.content,
                                        field: fieldsMap.default[3],
                                        qualifier: 'en',
                                        id: 'en'
                                    }
                                },
                                fr: {
                                    type: 'field',
                                    component: 'field',
                                    validators: {
                                        required: undefined
                                    },
                                    inputs: {
                                        component: component.content,
                                        field: fieldsMap.default[3],
                                        qualifier: 'fr',
                                        id: 'fr'
                                    }
                                }
                            }
                        },
                        contentRequired: {
                            type: 'group',
                            component: 'localized',
                            inputs: {
                                field: fieldsMap.default[4],
                                languages,
                                component
                            },
                            schemas: {
                                en: {
                                    type: 'field',
                                    component: 'field',
                                    validators: {
                                        required: true
                                    },
                                    inputs: {
                                        component: component.contentRequired,
                                        field: fieldsMap.default[4],
                                        qualifier: 'en',
                                        id: 'en'
                                    }
                                },
                                fr: {
                                    type: 'field',
                                    component: 'field',
                                    validators: {
                                        required: undefined
                                    },
                                    inputs: {
                                        component: component.contentRequired,
                                        field: fieldsMap.default[4],
                                        qualifier: 'fr',
                                        id: 'fr'
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    });
});
