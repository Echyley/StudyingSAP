/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { noop, cloneDeep } from 'lodash';

import {
    GenericEditorDropdownConfiguration,
    GenericEditorDropdownServiceFactory,
    GenericEditorField,
    IDropdownPopulator,
    IGenericEditorDropdownSelectedOptionEventData,
    IGenericEditorDropdownService,
    SystemEventService,
    GenericEditorUtil,
    LogService
} from 'smarteditcommons';

import { dataHelper } from 'testhelpers';
import {
    DropdownPopulatorFetchPageResponse,
    DropdownPopulatorInterface,
    DropdownPopulatorPagePayload,
    OptionsDropdownPopulator,
    UriDropdownPopulator
} from './populators';
interface MockFetchPageResponse {
    someArray: any[];
}

class MockOptionsDropdownPopulator extends OptionsDropdownPopulator {
    constructor() {
        super(null, null);
    }
}
const optionsDropdownPopulator = new MockOptionsDropdownPopulator();
const genericEditorUtil = new GenericEditorUtil();

class MockUriDropdownPopulator extends UriDropdownPopulator {
    constructor() {
        super(null, null, null);
    }
}
const uriDropdownPopulator = new MockUriDropdownPopulator();

class CustomPropertyTypeDropdownPopulator extends DropdownPopulatorInterface {}
const customPropertyTypeDropdownPopulator = new CustomPropertyTypeDropdownPopulator(null, null);

class CMSItemDropdownDropdownPopulator extends DropdownPopulatorInterface {}
const cMSItemDropdownDropdownPopulator = new CMSItemDropdownDropdownPopulator(null, null);

class CmpTypecategoryDropdownPopulator extends DropdownPopulatorInterface {}
const cmpTypecategoryDropdownPopulator = new CmpTypecategoryDropdownPopulator(null, null);

class CmpTypeDropdownPopulator extends DropdownPopulatorInterface {}
const cmpTypeDropdownPopulator = new CmpTypeDropdownPopulator(null, null);

const mockOption1 = {
    id: 'id1',
    label: 'label1 - sample'
};
const mockOption2 = {
    id: 'id2',
    label: 'label2 - sample option'
};
const mockOption3 = {
    id: 'id3',
    label: 'label3 - option'
};
const selectedData = {
    pagination: { count: 10, page: 0, totalCount: 84, totalPages: 9 },
    results: [
        {
            uid: 'admingroup',
            name: 'admingroup'
        },
        {
            uid: 'cmsworkflow',
            name: 'cmsworkflow'
        },
        {
            uid: 'cmstranslateworkflow',
            name: 'cmstranslateworkflow'
        }
    ]
};
const fetchedData = {
    pagination: { count: 10, page: 0, totalCount: 84, totalPages: 9 },
    results: [
        {
            uid: 'admingroup',
            name: 'admingroup',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmsworkflow',
            name: 'cmsworkflow',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmstranslateworkflow',
            name: 'cmstranslateworkflow',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmstgroupworkflow',
            name: 'cmstgroupworkflow',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmsworkflowreadonlygroup',
            name: 'cmsworkflowreadonlygroup',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmstranslateworkflow-de',
            name: 'cmstranslateworkflow-de',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmstranslateworkflow-jp',
            name: 'cmstranslateworkflow-jp',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmstranslateworkflow-zh',
            name: 'cmstranslateworkflow-zh',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmspublishergroup',
            name: 'cmspublishergroup',
            techniqueID: '123213213123123'
        },
        {
            uid: 'cmsreviewergroup',
            name: 'cmsreviewergroup',
            techniqueID: '123213213123123'
        }
    ]
};

const mockOptions = [cloneDeep(mockOption1), cloneDeep(mockOption2), cloneDeep(mockOption3)];

const mockSitesPage1 = [
    {
        id: 'site1',
        label: 'Site1'
    },
    {
        id: 'site2',
        label: 'Site2'
    },
    {
        id: 'site3',
        label: 'Site3'
    },
    {
        id: 'site4',
        label: 'Site4'
    },
    {
        id: 'site5',
        label: 'Site5'
    },
    {
        id: 'site6',
        label: 'Site6'
    },
    {
        id: 'site7',
        label: 'Site7'
    },
    {
        id: 'site8',
        label: 'Site8'
    },
    {
        id: 'site9',
        label: 'Site9'
    },
    {
        id: 'site10',
        label: 'Site10'
    }
];
const mockSitesPage2 = [
    {
        id: 'site11',
        label: 'Site11'
    },
    {
        id: 'site12',
        label: 'Site12'
    },
    {
        id: 'site13',
        label: 'Site13'
    },
    {
        id: 'site14',
        label: 'Site14'
    },
    {
        id: 'site15',
        label: 'Site15'
    },
    {
        id: 'site16',
        label: 'Site16'
    },
    {
        id: 'site17',
        label: 'Site17'
    },
    {
        id: 'site18',
        label: 'Site18'
    },
    {
        id: 'site19',
        label: 'Site19'
    },
    {
        id: 'site20',
        label: 'Site20'
    }
];
const mockSites = [...mockSitesPage1, ...mockSitesPage2];
const i18nKeySmarteditComponentTypeDropDownAName = 'type.thesmarteditComponentType.dropdownA.name';

// options
const fieldWithOptions: GenericEditorField = {
    cmsStructureType: 'EditableDropdown',
    qualifier: 'dropdownA',
    i18nKey: i18nKeySmarteditComponentTypeDropDownAName,
    options: [],
    smarteditComponentType: 'componentX'
};

// uri
const fieldWithUri: GenericEditorField = {
    cmsStructureType: 'EditableDropdown',
    qualifier: 'dropdownA',
    i18nKey: i18nKeySmarteditComponentTypeDropDownAName,
    uri: '/someuri',
    smarteditComponentType: 'componentX'
};

const fieldWithDependsOn: GenericEditorField = {
    cmsStructureType: 'EditableDropdown',
    qualifier: 'dropdownA',
    i18nKey: i18nKeySmarteditComponentTypeDropDownAName,
    uri: '/someuri',
    dependsOn: 'dropdown1,dropdown2',
    smarteditComponentType: 'componentX'
};

// no populator
const fieldWithNoneNoPopulator: GenericEditorField = {
    cmsStructureType: 'EditableDropdown',
    qualifier: 'dropdownX',
    i18nKey: i18nKeySmarteditComponentTypeDropDownAName,
    smarteditComponentType: 'componentY'
};

// uri & options
const fieldWithBoth: GenericEditorField = {
    cmsStructureType: 'EditableDropdown',
    qualifier: 'dropdownA',
    i18nKey: i18nKeySmarteditComponentTypeDropDownAName,
    uri: '/someuri',
    options: [],
    smarteditComponentType: 'componentX'
};

// propertyType
const fieldWithPropertyType: GenericEditorField = {
    cmsStructureType: 'SingleProductSelector',
    propertyType: 'customPropertyType',
    qualifier: 'dropdownA',
    i18nKey: 'type.thesmarteditComponentType.product.name',
    required: true
};

// structureType
const fieldWithStructureType: GenericEditorField = {
    cmsStructureType: 'CMSItemDropdown',
    qualifier: 'cmsItemDropdown'
};

const fieldWithSmarteditComponentTypeAndQualifier: GenericEditorField = {
    cmsStructureType: null,
    smarteditComponentType: 'CmpType',
    qualifier: 'category'
};

// smarteditComponentType
const fieldWithSmarteditComponentType: GenericEditorField = {
    cmsStructureType: null,
    smarteditComponentType: 'CmpType',
    qualifier: null
};

const conf: GenericEditorDropdownConfiguration = {
    field: null,
    model: {
        dropdown1: '1',
        dropdown2: '2',
        dropdownA: 'id1'
    },
    qualifier: 'dropdownA',
    id: new Date().valueOf().toString(10)
};

const mockCustomDropdownPopulators: DropdownPopulatorInterface[] = [
    customPropertyTypeDropdownPopulator,
    cMSItemDropdownDropdownPopulator,
    cmpTypecategoryDropdownPopulator,
    cmpTypeDropdownPopulator
];
let GenericEditorDropdownService: new (
    config: GenericEditorDropdownConfiguration
) => IGenericEditorDropdownService;

let componentXDropdownPopulator: jasmine.SpyObj<IDropdownPopulator>;
let componentYDropdownADropdownPopulator: jasmine.SpyObj<IDropdownPopulator>;
let systemEventService: jasmine.SpyObj<SystemEventService>;

describe('GenericEditorDropdownServiceFactory', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });

    it('DropdownService initializes fine', () => {
        const dropdownService = new GenericEditorDropdownService({
            ...conf,
            field: fieldWithNoneNoPopulator
        });

        expect(dropdownService.field).toEqual(fieldWithNoneNoPopulator);
        expect(dropdownService.model).toEqual(conf.model);
        expect(dropdownService.qualifier).toEqual(conf.qualifier);
    });

    it(
        'GIVEN DropdownService is initialized WHEN the field object has both options and uri attributes ' +
            'THEN it throws an error',
        () => {
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithBoth
            });

            expect(() => {
                return dropdownService.init();
            }).toThrow(new Error('se.dropdown.contains.both.uri.and.options'));
        }
    );

    it(
        'GIVEN DropdownService is initialized WHEN the field object has "dependsOn" attribute ' +
            'THEN init method must register an event',
        () => {
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithDependsOn
            });

            spyOn(dropdownService as any, '_respondToChange').and.returnValue(undefined);

            dropdownService.init();

            expect(systemEventService.subscribe).toHaveBeenCalledWith(
                conf.id + 'LinkedDropdown',
                jasmine.any(Function)
            );
            const respondToChangeCallback = systemEventService.subscribe.calls.argsFor(0)[1];
            respondToChangeCallback(null);
            expect((dropdownService as any)._respondToChange).toHaveBeenCalled();
        }
    );

    it(
        'GIVEN DropdownService is initialized WHEN fetchAll is called THEN ' +
            'the respective populator is called with the correct payload',
        async () => {
            // GIVEN
            const searchKey = 'sample';
            const selection = {
                a: 'b'
            };

            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });

            const searchResult = mockOptions.filter((option) =>
                option.label.toUpperCase().includes(searchKey.toUpperCase())
            );
            uriDropdownPopulator.fetchAll = jasmine
                .createSpy()
                .and.returnValue(Promise.resolve(searchResult));
            dropdownService.init();
            dropdownService.selection = selection;

            // WHEN
            await dropdownService.fetchAll(searchKey);

            // THEN
            expect(uriDropdownPopulator.fetchAll).toHaveBeenCalledWith({
                field: fieldWithUri,
                model: conf.model,
                search: searchKey,
                selection
            });
            expect(dropdownService.items).toEqual([mockOption1, mockOption2]);
        }
    );

    it(
        'GIVEN DropdownService is initialized WHEN triggerAction is called THEN ' +
            'publishAsync method is called with correct attributes',
        async () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });

            dropdownService.init();

            // WHEN
            await dropdownService.fetchAll();
            dropdownService.triggerAction();

            // THEN
            const eventId = conf.id + 'LinkedDropdown';
            expect(systemEventService.publishAsync).toHaveBeenCalledWith(eventId, {
                qualifier: conf.qualifier,
                optionObject: mockOption1
            });
        }
    );

    it(
        'GIVEN DropdownService is initialized WHEN _respondToChange is called AND if the fields dependsOn does not match the input qualifier ' +
            'THEN then reset should not be called (nothing happens)',
        () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });
            dropdownService.reset = noop;
            spyOn(dropdownService, 'reset');
            dropdownService.init();

            // WHEN
            const changeObject = cloneDeep(
                mockOption1 as unknown as IGenericEditorDropdownSelectedOptionEventData
            );
            (dropdownService as any)._respondToChange(conf.qualifier, changeObject);

            // THEN
            expect(dropdownService.reset).not.toHaveBeenCalled();
        }
    );

    it(
        'GIVEN DropdownService is initialized WHEN _respondToChange is called AND if the fields dependsOn matches the input qualifier ' +
            'THEN then reset is called on the child component and a selection is made ready for the next refresh',
        () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithDependsOn
            });

            dropdownService.reset = noop;
            spyOn(dropdownService, 'reset');
            dropdownService.init();

            // WHEN
            const changeObject = {
                qualifier: 'dropdown1',
                optionObject: {}
            };
            (dropdownService as any)._respondToChange('SomeKey', changeObject);

            // THEN
            expect(dropdownService.reset).toHaveBeenCalled();
            expect((dropdownService as any).selection).toBe(changeObject.optionObject);
        }
    );
});

describe('GenericEditorDropdownServiceFactory - other checks', () => {
    beforeEach(() => {
        buildCommonSpyObjects();
    });
    describe('GIVEN DropdownService is initialized with a field object THEN it should set respective Populator', () => {
        it(
            'GIVEN a field object that has the "options" attribute ' +
                'THEN the OptionsDropdownPopulator populator will be set',
            () => {
                // GIVEN
                const dropdownService = new GenericEditorDropdownService({
                    field: fieldWithOptions,
                    qualifier: undefined,
                    model: undefined,
                    id: undefined
                });

                // WHEN
                dropdownService.init();

                // THEN
                expect(dropdownService.populator).toEqual(optionsDropdownPopulator);
                expect(dropdownService.isPaged).toBe(false);
            }
        );

        it(
            'GIVEN a field object that has the "uri" attribute ' +
                'THEN the UriDropdownPopulator populator will be set',
            () => {
                // GIVEN
                const dropdownService = new GenericEditorDropdownService({
                    field: fieldWithUri,
                    qualifier: undefined,
                    model: undefined,
                    id: undefined
                });

                // WHEN
                dropdownService.init();

                // THEN
                expect(dropdownService.populator).toEqual(uriDropdownPopulator);
            }
        );

        describe(
            'GIVEN a field object that has a "propertyType" attribute ' +
                'THEN the respective populator will be set',
            () => {
                it('Angular', () => {
                    // GIVEN
                    const dropdownService = new GenericEditorDropdownService({
                        field: fieldWithPropertyType,
                        qualifier: undefined,
                        model: undefined,
                        id: undefined
                    });

                    // WHEN
                    dropdownService.init();

                    // THEN
                    expect(dropdownService.populator).toEqual(customPropertyTypeDropdownPopulator);
                });
            }
        );

        describe(
            'GIVEN a field object that has "cmsStructureType" attribute ' +
                'THEN the respective populator will be set',
            () => {
                it('Angular', () => {
                    // GIVEN
                    const dropdownService = new GenericEditorDropdownService({
                        field: fieldWithStructureType,
                        qualifier: undefined,
                        model: undefined,
                        id: undefined
                    });

                    // WHEN
                    dropdownService.init();

                    // THEN
                    expect(dropdownService.populator).toEqual(cMSItemDropdownDropdownPopulator);
                });
            }
        );

        describe(
            'GIVEN a field object that has "smarteditComponentType" and "qualifier" attribute ' +
                'THEN the respective populator will be set',
            () => {
                it('Angular', () => {
                    // GIVEN
                    const dropdownService = new GenericEditorDropdownService({
                        field: fieldWithSmarteditComponentTypeAndQualifier,
                        qualifier: undefined,
                        model: undefined,
                        id: undefined
                    });

                    // WHEN
                    dropdownService.init();

                    // THEN
                    expect(dropdownService.populator).toEqual(cmpTypecategoryDropdownPopulator);
                });
            }
        );

        describe(
            'GIVEN a field object that has "smarteditComponentType" and has no "qualifier" attribute ' +
                'THEN the respective populator will be set',
            () => {
                it('Angular', () => {
                    // GIVEN
                    const dropdownService = new GenericEditorDropdownService({
                        field: fieldWithSmarteditComponentType,
                        qualifier: undefined,
                        model: undefined,
                        id: undefined
                    });

                    // WHEN
                    dropdownService.init();

                    // THEN
                    expect(dropdownService.populator).toEqual(cmpTypeDropdownPopulator);
                });
            }
        );
    });

    describe('fetchPage', () => {
        it('GIVEN DropdownService is initialized WHEN fetchPage is called THEN it retrieves and returns the result with the right format', async () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });
            dropdownService.init();

            // WHEN
            const page1 = await dropdownService.fetchPage('', 10, 0);

            // THEN
            const firstItem = (page1 as DropdownPopulatorFetchPageResponse).results[0];
            expect(firstItem.id).toEqual('site1');
        });

        it('GIVEN DropdownService is initialized WHEN fetchPage is called THEN it should add the result items to the items array', async () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });
            dropdownService.init();

            // WHEN
            const pageSize = 10;
            const page1 = (await dropdownService.fetchPage(
                '',
                pageSize,
                0
            )) as DropdownPopulatorFetchPageResponse;
            const firstItem = page1.results[0];

            // THEN
            expect(firstItem.id).toEqual('site1');
            expect(page1.results.length).toEqual(10);

            // WHEN
            const page2 = (await dropdownService.fetchPage(
                '',
                pageSize,
                1
            )) as DropdownPopulatorFetchPageResponse;
            const page2FirstItem = page2.results[0];

            // THEN
            expect(page2FirstItem.id).toEqual('site11');
            expect(page2.results.length).toEqual(10);

            expect((dropdownService as any).items.length).toEqual(20);
        });

        it('WHEN fetchPage retrieved data, the selected items should remove from fetched results', () => {
            // GIVEN
            const dropdownService = new GenericEditorDropdownService({
                ...conf,
                field: fieldWithUri
            });
            dropdownService.isMultiDropdown = true;
            dropdownService.init();
            const isIncludeSpy = spyOn<any>(dropdownService, 'isInclude');
            dropdownService.limitToNonSelectedItems(fetchedData, selectedData);
            expect(isIncludeSpy).toHaveBeenCalled();
        });
    });
});

function buildCommonSpyObjects() {
    spyOn(uriDropdownPopulator, 'fetchAll').and.returnValue(Promise.resolve(mockOptions));
    spyOn(uriDropdownPopulator, 'fetchPage').and.callFake(((
        payload: DropdownPopulatorPagePayload
    ) => {
        const { currentPage, pageSize } = payload;

        const results = dataHelper.getFetchPageResults(mockSites, currentPage, pageSize);
        const response: MockFetchPageResponse = {
            someArray: results
        };
        return Promise.resolve(response);
    }) as any);

    componentYDropdownADropdownPopulator = jasmine.createSpyObj(
        'componentYdropdownADropdownPopulator',
        ['fetchAll']
    );
    componentYDropdownADropdownPopulator.fetchAll.and.returnValue(mockOptions as any);

    componentXDropdownPopulator = jasmine.createSpyObj('componentXDropdownPopulator', ['fetchAll']);

    componentXDropdownPopulator.fetchAll.and.returnValue(mockOptions as any);

    systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe', 'publishAsync']);

    GenericEditorDropdownService = GenericEditorDropdownServiceFactory(
        genericEditorUtil,
        new LogService(),
        'LinkedDropdown',
        'ClickDropdown',
        'DropdownPopulator',
        systemEventService,
        optionsDropdownPopulator,
        uriDropdownPopulator,
        mockCustomDropdownPopulators
    );
}
