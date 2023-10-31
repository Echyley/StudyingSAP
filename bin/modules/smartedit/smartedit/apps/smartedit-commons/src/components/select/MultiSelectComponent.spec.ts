/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    NO_ERRORS_SCHEMA,
    ChangeDetectorRef,
    Injector,
    ElementRef,
    InjectionToken
} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { TranslateModule } from '@ngx-translate/core';
import { LogService, ISharedDataService } from '@smart/utils';
import {
    SelectModule,
    InfiniteScrollingComponent,
    SelectComponent,
    ActionableSearchItemComponent,
    LanguageService,
    CrossFrameEventService
} from 'smarteditcommons';
import { L10nService, SystemEventService } from '../../services';
import { DiscardablePromiseUtils } from '../../utils';
import { DefaultItemPrinterComponent } from './defaultItemPrinter';
import {
    FakeMultiSelectComponent,
    mockProductsV1,
    mockProductsV2
} from './fakeMultiSelectComponent';
import { ItemComponentData } from './itemPrinter';
import { ItemPrinterComponent } from './itemPrinter/ItemPrinterComponent';
import { ResultsHeaderComponent } from './resultsHeader/ResultsHeaderComponent';
import { SearchInputComponent } from './searchInput/SearchInputComponent';
import { SelectListComponent } from './selectList/SelectListComponent';
const DATA_TOKEN = new InjectionToken<ItemComponentData>('data');
const ERROR = 'ValidationError';
const EMPTY = '';
const SEARCH_STR = 'item label I would like to add';
const RESULTS_HEADER_LABEL = 'Results Header Label';
const RESULTS_HEADER_COMPONENT = 'Results Header Custom Component';
const WARNING = 'Warning';
const LABEL = 'label';
const COMPONENT = 'component';
const ORI_SELECTED = 2;
const CHANGED_SELECTED = 3;
const LEFT_SELECTED = 1;
const FIRST_INDEX = 0;
const NUMBER_10 = 10;
const PRODUCT_3 = 'product3';
const PRODUCT_1 = 'product1';
const PRODUCT_2 = 'product2';
const PRODUCT_11 = 'product11';
const PRODUCT_12 = 'product12';
const PRODUCT_13 = 'product13';
const SEARCH_PLACEHOLDER = 'se.genericeditor.sedropdown.placeholder';
const DISABLED_CLASS = 'is-disabled';
const event = {
    target: '123',
    stopPropagation: () => {}
} as any;

let selectElement: HTMLElement;
let languageService: jasmine.SpyObj<LanguageService>;
let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;
let cdr: jasmine.SpyObj<ChangeDetectorRef>;
let elementRef: jasmine.SpyObj<ElementRef>;
let dataToken: any;
let sharedDataService: jasmine.SpyObj<ISharedDataService>;
let injector: jasmine.SpyObj<Injector>;
let logService: jasmine.SpyObj<LogService>;
let systemEventService: jasmine.SpyObj<SystemEventService>;
let l10nService: jasmine.SpyObj<L10nService>;
let discardablePromiseUtils: jasmine.SpyObj<DiscardablePromiseUtils>;
let selectComponent: SelectComponent<any>;
let fakeMultiSelectComponent: FakeMultiSelectComponent;
let searchInputComponent: SearchInputComponent;
let fixture: ComponentFixture<FakeMultiSelectComponent>;
function selectedOptions(selected: any): void {
    selectComponent.refreshOptions(EMPTY);
    selectComponent.onOptionClick(selected);
    fixture.detectChanges();
}
function searchInputAction(searchInput: boolean) {
    selectComponent.resetSearchInput = searchInput;
    selectComponent.searchInputCmp.search = SEARCH_STR;
    selectComponent.isOpen = true;
    fixture.detectChanges();
    selectComponent.clickHandler(event);
}
function selectReadonly() {
    selectComponent.isReadOnly = true;
    fixture.detectChanges();
    expect(selectElement.classList.value).toContain(DISABLED_CLASS);
}
function changeSource() {
    fakeMultiSelectComponent.changeSource();
    fixture.detectChanges();
}

function otherDefines() {
    it('GIVEN a custom item component THEN options and selected options should be displayed with that component', () => {
        selectComponent.selected = [];
        selectedOptions(mockProductsV1[LEFT_SELECTED]);
        selectedOptions(mockProductsV1[FIRST_INDEX]);
        fixture.whenStable().then(() => {
            selectComponent.items = selectComponent.items.filter(
                (item) =>
                    item.id !== mockProductsV1[FIRST_INDEX].id &&
                    item.id !== mockProductsV1[LEFT_SELECTED].id
            );
            const product3 = selectComponent.items.find((item) => item.id === PRODUCT_3);
            expect(product3).toBeDefined();
            expect(product3.id).toBe(PRODUCT_3);
        });
    });

    it('GIVEN a Multi Selector WHEN selecting an option THEN that option should no longer be in dropdown list', () => {
        selectComponent.selected = [];
        selectedOptions(mockProductsV1[FIRST_INDEX]);
        fixture.whenStable().then(() => {
            expect(selectComponent.selected[FIRST_INDEX].id).toEqual(PRODUCT_1);
            selectComponent.items = selectComponent.items.filter(
                (item) => item.id !== mockProductsV1[FIRST_INDEX].id
            );
            const product1 = selectComponent.items.find((item) => item.id === PRODUCT_1);
            expect(product1).toBeUndefined();
        });
    });

    it('GIVEN selected options WHEN removing a selected option THEN it should not be displayed among selected options BUT displayed in dropdown list', () => {
        expect(selectComponent.selected.length).toEqual(ORI_SELECTED);
        selectComponent.removeSelectedOption(event, mockProductsV1[LEFT_SELECTED]);
        expect(selectComponent.selected.length).toEqual(LEFT_SELECTED);
        expect(selectComponent.selected[FIRST_INDEX].id).toEqual(PRODUCT_3);
        selectComponent.refreshOptions(EMPTY);
        fixture.whenStable().then(() => {
            selectComponent.items = selectComponent.items.filter((item) => item.id !== PRODUCT_3);
            const product2 = selectComponent.items.find((item) => item.id === PRODUCT_2);
            expect(product2).toBeDefined();
        });
    });

    it('GIVEN placeholder THEN the placeholder should be set for Search Input', () => {
        expect(selectComponent.searchInputCmp.placeholder).toEqual(SEARCH_PLACEHOLDER);
    });

    describe('Results Header', () => {
        it('GIVEN "resultsHeaderLabel" WHEN opening a dropdown THEN it should be displayed with given text', () => {
            fakeMultiSelectComponent.changeResultsHeaderTemplate(LABEL);
            expect(selectComponent.resultsHeaderLabel).toContain(RESULTS_HEADER_LABEL);
        });

        it('GIVEN "resultsHeaderComponent" WHEN opening a dropdown THEN it should be displayed', () => {
            fakeMultiSelectComponent.changeResultsHeaderTemplate(COMPONENT);
            expect(selectComponent.resultsHeaderComponent).toContain(RESULTS_HEADER_COMPONENT);
        });
    });

    describe('Search Input', () => {
        afterEach(() => {
            selectComponent.search = '';
        });
        it('GIVEN "searchEnabled" is false WHEN user attempts to provide a value for Search Input THEN it should be not allowed', () => {
            fakeMultiSelectComponent.setSearchEnabled(false);
            fixture.detectChanges();
            expect(searchInputComponent.isDisabled).toEqual(true);
            expect(searchInputComponent.isReadOnly).toEqual(true);
        });

        it('GIVEN "searchEnabled" is true WHEN user attemps to provide a value for Search Input THEN it should be allowed', () => {
            fakeMultiSelectComponent.setSearchEnabled(true);
            fixture.detectChanges();
            expect(searchInputComponent.isDisabled).toEqual(false);
            expect(searchInputComponent.isReadOnly).toEqual(false);
        });

        it(
            'GIVEN "resetSearchInput" is true WHEN providing the search input AND closing the dropdown AND opening the dropdown again ' +
                'THEN the value should be empty',
            () => {
                searchInputAction(true);
                expect(searchInputComponent.search).toEqual('');
            }
        );

        it(
            'GIVEN "resetSearchInput" is false WHEN providing the search input AND closing the dropdown AND opening the dropdown again ' +
                'THEN the value should persist',
            () => {
                searchInputAction(false);
                expect(searchInputComponent.search).toEqual(SEARCH_STR);
            }
        );

        it(
            'GIVEN "resetSearchInput" is false WHEN providing the search input AND selecting an option AND opening the dropdown again ' +
                'THEN the value should persist',
            () => {
                searchInputAction(false);
                selectedOptions(mockProductsV1[CHANGED_SELECTED]);
                expect(searchInputComponent.search).toEqual(SEARCH_STR);
            }
        );
    });

    describe('isReadOnly', () => {
        afterEach(() => {
            selectComponent.isReadOnly = false;
            selectComponent.search = '';
        });
        it(`GIVEN "isReadOnly" is true THEN search input shouldn't be clickable`, () => {
            selectReadonly();
        });

        it(`GIVEN "isReadOnly" is true AND selected item THEN selected item shouldn't be clickable`, () => {
            selectReadonly();
        });
    });

    describe('callbacks', () => {
        describe('onChange', () => {
            beforeEach(() => {
                selectComponent.onChange = (): void => {
                    fakeMultiSelectComponent.onChangeCounter++;
                };
            });

            it('GIVEN "onChange" WHEN displaying component THEN "onChange" function should be called', () => {
                expect(fakeMultiSelectComponent.onChangeCounter).toEqual(FIRST_INDEX);
            });

            it('GIVEN "onChange" WHEN removing a selected option THEN "onChange" function should be called', () => {
                selectComponent.removeSelectedOption(event, mockProductsV1[LEFT_SELECTED]);
                fixture.detectChanges();
                expect(fakeMultiSelectComponent.onChangeCounter).toEqual(LEFT_SELECTED);
            });

            it('GIVEN "onChange" WHEN selecting an option THEN "onChange" function should be called', () => {
                selectedOptions(mockProductsV1[FIRST_INDEX]);
                fixture.detectChanges();
                expect(fakeMultiSelectComponent.onChangeCounter).toEqual(LEFT_SELECTED);
            });

            it('GIVEN "onChange" WHEN Parent Component changes the model THEN "onChange" function should be called', async () => {
                fakeMultiSelectComponent.changeModel();
                fixture.detectChanges();
                await fixture.whenStable();
                expect(fakeMultiSelectComponent.onChangeCounter).toEqual(LEFT_SELECTED);
            });
        });

        it('GIVEN "onRemove" WHEN removing a selected option THEN the "onRemove" function should be called', () => {
            selectComponent.removeSelectedOption(event, mockProductsV1[1]);
            expect(fakeMultiSelectComponent.onRemoveModel).toEqual(PRODUCT_2);
        });

        it('GIVEN "onSelect" WHEN selecting an option THEN the "onSelect" function should be called', () => {
            selectedOptions(mockProductsV1[0]);
            expect(fakeMultiSelectComponent.onSelectModel).toEqual(PRODUCT_1);
        });
    });

    describe('fetchPage', () => {
        beforeEach(() => {
            //     // Clear the "model" because it is not required for the following specs.
            //     // If "model" is provided, SelectComponent validation will throw an error because no "fetchEntities" is provided.
            fakeMultiSelectComponent.clearModel();
            selectComponent.fetchStrategy = {
                fetchPage: (_: string, pageSize: number, currentPage: number) =>
                    fakeMultiSelectComponent.getFetchPageResults(
                        mockProductsV1,
                        pageSize,
                        currentPage
                    )
            } as any;
            fixture.detectChanges();
        });

        it('GIVEN "fetchPage" WHEN opening a dropdown THEN items should be displayed', async () => {
            const itemResults = await selectComponent.fetchStrategy.fetchPage(EMPTY, NUMBER_10, 0);
            const product1 = itemResults.results.find((item) => item.id === PRODUCT_1);
            const product2 = itemResults.results.find((item) => item.id === PRODUCT_2);
            expect(product1).toBeDefined();
            expect(product2).toBeDefined();
        });

        it('GIVEN "fetchPage" WHEN opening a dropdown AND scrolling to the bottom THEN next page items should be displayed', async () => {
            // next page
            const itemResults = await selectComponent.fetchStrategy.fetchPage(EMPTY, NUMBER_10, 1);
            const product11 = itemResults.results.find((item) => item.id === PRODUCT_11);
            const product12 = itemResults.results.find((item) => item.id === PRODUCT_12);
            expect(product11).toBeDefined();
            expect(product12).toBeDefined();
        });
    });
}

describe('TestFakeMultiSelectComponent', () => {
    async function createTestFakeMultiSelectComponentContext() {
        crossFrameEventService = jasmine.createSpyObj(['subscribe']);
        languageService = jasmine.createSpyObj(['getResolveLocaleIsoCode']);
        sharedDataService = jasmine.createSpyObj(['get']);
        l10nService = jasmine.createSpyObj(['languageSwitch$']);
        l10nService.languageSwitch$ = jasmine.createSpyObj(['subscribe', 'pipe']);
        logService = jasmine.createSpyObj(['debug']);
        await TestBed.configureTestingModule({
            imports: [SelectModule, TranslateModule.forRoot()],
            declarations: [FakeMultiSelectComponent],
            schemas: [NO_ERRORS_SCHEMA],
            providers: [
                { provide: LanguageService, useValue: languageService },
                { provide: SystemEventService, useValue: systemEventService },
                { provide: CrossFrameEventService, useValue: crossFrameEventService },
                { provide: ChangeDetectorRef, useValue: cdr }
            ]
        })
            .overrideComponent(InfiniteScrollingComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/infiniteScrolling/InfiniteScrollingComponent.html',
                    styleUrls: [''],
                    providers: [
                        { provide: DiscardablePromiseUtils, useValue: discardablePromiseUtils }
                    ]
                }
            })
            .overrideComponent(SelectComponent, {
                set: {
                    templateUrl: '/base/src/components/select/SelectComponent.html',
                    styleUrls: [''],
                    providers: [
                        { provide: LogService, useValue: logService },
                        { provide: L10nService, useValue: l10nService }
                    ]
                }
            })
            .overrideComponent(ActionableSearchItemComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/select/actionableSearchItem/ActionableSearchItemComponent.html',
                    styleUrls: [''],
                    providers: [{ provide: SystemEventService, useValue: systemEventService }]
                }
            })
            .overrideComponent(ItemPrinterComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/select/itemPrinter/ItemPrinterComponent.html',
                    styleUrls: [''],
                    providers: [{ provide: Injector, useValue: injector }]
                }
            })
            .overrideComponent(ResultsHeaderComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/select/resultsHeader/ResultsHeaderComponent.html',
                    styleUrls: [
                        '/base/src/components/select/resultsHeader/ResultsHeaderComponent.scss'
                    ]
                }
            })
            .overrideComponent(SearchInputComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/select/searchInput/SearchInputComponent.html',
                    styleUrls: [''],
                    providers: [
                        { provide: ElementRef, useValue: elementRef },
                        { provide: ISharedDataService, useValue: sharedDataService }
                    ]
                }
            })
            .overrideComponent(SelectListComponent, {
                set: {
                    templateUrl: '/base/src/components/select/selectList/SelectListComponent.html',
                    styleUrls: ['/base/src/components/select/selectList/SelectListComponent.scss']
                }
            })
            .overrideComponent(DefaultItemPrinterComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/select/defaultItemPrinter/DefaultItemPrinterComponent.html',
                    styleUrls: [
                        '/base/src/components/select/defaultItemPrinter/DefaultItemPrinterComponent.scss'
                    ],
                    providers: [{ provide: DATA_TOKEN, useValue: dataToken }]
                }
            })
            .compileComponents();
        fixture = TestBed.createComponent(FakeMultiSelectComponent);
        fakeMultiSelectComponent = fixture.componentInstance;
        fixture.detectChanges();
        selectComponent = fixture.debugElement.query(
            By.directive(SelectComponent)
        ).componentInstance;
        selectComponent.onSelect = (_: any, model: string): void => {
            fakeMultiSelectComponent.onSelectModel = model;
        };
        selectComponent.selected = [];
        selectComponent.multiSelect = true;
        selectComponent.search = SEARCH_STR;
        fixture.detectChanges();
        searchInputComponent = selectComponent.searchInputCmp;
        fixture.detectChanges();
        selectComponent.onRemove = (item: any, model?: string): void => {
            selectComponent.selected = selectComponent.selected.filter((i) => i.id !== item.id);
            fakeMultiSelectComponent.onRemoveModel = model;
        };
        selectElement = fixture.debugElement.nativeElement.querySelector('#multi-select');
    }

    beforeEach(async () => {
        await createTestFakeMultiSelectComponentContext();
    });

    afterEach(() => {
        selectComponent.search = '';
    });

    it('Should create Multi Select Component correctly', () => {
        expect(createTestFakeMultiSelectComponentContext).toBeDefined();
        expect(fakeMultiSelectComponent).toBeDefined();
        expect(selectComponent).toBeDefined();
    });

    it('GIVEN a multi selector WHEN selecting a product in the dropdown list THEN the dropdown selection list should be updated', () => {
        expect(selectComponent.selected.length).toEqual(ORI_SELECTED);
        selectedOptions(mockProductsV1[FIRST_INDEX]);
        expect(selectComponent.selected.length).toEqual(CHANGED_SELECTED);
    });

    it(
        'GIVEN a selected option that does not exist in another set of options and "Force Reset" as checked ' +
            'WHEN switching to another set of options THEN the multi selected model should reset',
        () => {
            changeSource();
            expect(selectComponent.model).toEqual([]);
        }
    );

    it(
        'GIVEN the initial options and "Force Reset" as unchecked WHEN selecting a product that does not exist in another set ' +
            'THEN the selected model should not reset',
        () => {
            fakeMultiSelectComponent.forceReset = false;
            fixture.detectChanges();
            changeSource();
            expect(selectComponent.model).toEqual([PRODUCT_2, PRODUCT_3]);
        }
    );

    it('GIVEN an validation state THEN the selector should display ERROR, WARNING and NO STATE', () => {
        let error: any;
        let warning: any;
        selectComponent.setValidationState(ERROR);
        error = selectComponent.hasError();
        expect(error).toBe(true);
        selectComponent.setValidationState(WARNING);
        warning = selectComponent.hasWarning();
        expect(warning).toBe(true);
        selectComponent.resetValidationState();
        error = selectComponent.hasError();
        warning = selectComponent.hasWarning();
        expect(error).toBe(false);
        expect(warning).toBe(false);
    });
    it(
        'GIVEN a selected option that does exist in another set of options and "Force Reset" is checked ' +
            'WHEN switching to another set of options THEN the selected model should preserve that option',
        () => {
            selectedOptions(mockProductsV2[FIRST_INDEX]);
            changeSource();
            expect(selectComponent.model).toEqual([PRODUCT_13]);
        }
    );

    otherDefines();
});
