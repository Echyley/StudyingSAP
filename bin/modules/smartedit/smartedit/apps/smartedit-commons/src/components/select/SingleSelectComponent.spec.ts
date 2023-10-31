/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectorRef,
    NO_ERRORS_SCHEMA,
    Injector,
    ElementRef,
    InjectionToken
} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { LogService, ISharedDataService } from '@smart/utils';
import {
    SelectModule,
    InfiniteScrollingComponent,
    ActionableSearchItemComponent,
    CrossFrameEventService,
    LanguageService,
    SelectComponent,
    VALIDATION_MESSAGE_TYPES
} from 'smarteditcommons';
import { L10nService, SystemEventService } from '../../services';
import { DiscardablePromiseUtils } from '../../utils';
import { DefaultItemPrinterComponent } from './defaultItemPrinter';
import { FakeSingleSelectComponent, mockLanguagesV1 } from './FakeSingleSelectComponent';
import { ItemComponentData } from './itemPrinter';
import { ItemPrinterComponent } from './itemPrinter/ItemPrinterComponent';
import { ResultsHeaderComponent } from './resultsHeader/ResultsHeaderComponent';
import { SearchInputComponent } from './searchInput/SearchInputComponent';
import { SelectListComponent } from './selectList/SelectListComponent';

const DATA_TOKEN = new InjectionToken<ItemComponentData>('data');
const EMPTY = '';
const DISABLED_CLASS = 'is-disabled';
const ID_DE = 'de';
const ID_EN = 'en';
const event = {
    target: 'target',
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
let selectComponent: SelectComponent<any>;
let fakeSingleSelectComponent: FakeSingleSelectComponent;
let discardablePromiseUtils: jasmine.SpyObj<DiscardablePromiseUtils>;
let fixture: ComponentFixture<FakeSingleSelectComponent>;
let callNumberBefore: number;

function selectedOptions(selected: any): void {
    selectComponent.onOptionClick(selected);
    fixture.detectChanges();
}

function removeSelectedOption(removed: any): void {
    selectComponent.removeSelectedOption(event, removed);
    fixture.detectChanges();
}

async function changeSource() {
    fakeSingleSelectComponent.changeSource();
    selectComponent.refreshOptions(EMPTY);
    fakeSingleSelectComponent.reset();
    fixture.detectChanges();
    await fixture.whenStable();
}
//  display condition of the search input in production code
function getSearchInputStatus(searchEnabled: boolean, isOpen: boolean) {
    return searchEnabled && isOpen;
}

async function createTestFakeSingleSelectComponentContext() {
    crossFrameEventService = jasmine.createSpyObj(['subscribe']);
    languageService = jasmine.createSpyObj(['getResolveLocaleIsoCode']);
    l10nService = jasmine.createSpyObj(['languageSwitch$']);
    l10nService.languageSwitch$ = jasmine.createSpyObj(['subscribe', 'pipe']);
    await TestBed.configureTestingModule({
        imports: [TranslateModule.forRoot(), SelectModule],
        declarations: [FakeSingleSelectComponent],
        providers: [
            { provide: LanguageService, useValue: languageService },
            { provide: SystemEventService, useValue: systemEventService },
            { provide: CrossFrameEventService, useValue: crossFrameEventService },
            { provide: ChangeDetectorRef, useValue: cdr }
        ],
        schemas: [NO_ERRORS_SCHEMA]
    })
        .overrideComponent(InfiniteScrollingComponent, {
            set: {
                templateUrl:
                    '/base/src/components/infiniteScrolling/InfiniteScrollingComponent.html',
                styleUrls: [''],
                providers: [{ provide: DiscardablePromiseUtils, useValue: discardablePromiseUtils }]
            }
        })
        .overrideComponent(SelectComponent, {
            set: {
                templateUrl: '/base/src/components/select/SelectComponent.html',
                styleUrls: ['/base/src/components/select/SelectComponent.scss'],
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
                templateUrl: '/base/src/components/select/itemPrinter/ItemPrinterComponent.html',
                styleUrls: [''],
                providers: [{ provide: Injector, useValue: injector }]
            }
        })
        .overrideComponent(ResultsHeaderComponent, {
            set: {
                templateUrl:
                    '/base/src/components/select/resultsHeader/ResultsHeaderComponent.html',
                styleUrls: ['/base/src/components/select/resultsHeader/ResultsHeaderComponent.scss']
            }
        })
        .overrideComponent(SearchInputComponent, {
            set: {
                templateUrl: '/base/src/components/select/searchInput/SearchInputComponent.html',
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

    fixture = TestBed.createComponent(FakeSingleSelectComponent);
    fakeSingleSelectComponent = fixture.componentInstance;
    fixture.detectChanges();
    selectComponent = fakeSingleSelectComponent.selectComponent;
    fixture.detectChanges();
    selectElement = fixture.debugElement.nativeElement.querySelector('#single-select');
}

describe('TestFakeSingleSelectComponent', () => {
    beforeEach(async () => {
        await createTestFakeSingleSelectComponentContext();
        selectComponent.refreshOptions(EMPTY);
        fixture.detectChanges();
        await fixture.whenStable();
    });

    afterEach(() => {
        TestBed.resetTestingModule();
    });

    it('Should create Multi Select Component correctly', () => {
        expect(fakeSingleSelectComponent).toBeDefined();
        expect(selectComponent).toBeDefined();
    });

    describe('Single Selector -', () => {
        it('GIVEN a validation state THEN the selector should display ERROR, WARNING and NO STATE', () => {
            selectComponent.setValidationState(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
            fixture.detectChanges();
            expect(selectElement.querySelector('.has-error')).toBeTruthy();

            selectComponent.setValidationState(VALIDATION_MESSAGE_TYPES.WARNING);
            fixture.detectChanges();
            expect(selectElement.querySelector('.has-warning')).toBeTruthy();

            selectComponent.resetValidationState();
            fixture.detectChanges();
            expect(selectElement.querySelector('.has-error')).toBeNull();
            expect(selectElement.querySelector('.has-warning')).toBeNull();
        });

        it('GIVEN a user selects the German option THEN the displayed value of the option should be "de"', () => {
            selectedOptions(mockLanguagesV1[1]);
            expect(selectComponent.selected.id).toEqual(ID_DE);
        });

        it(
            'GIVEN a selected option that does not exist in another set of options and "Force Reset" is checked ' +
                'WHEN switching to another set of options THEN the selected model should reset',
            async () => {
                selectedOptions(mockLanguagesV1[1]);
                expect(selectComponent.model).toEqual(ID_DE);
                await changeSource();
                expect(selectComponent.model).toBeUndefined();
            }
        );

        it(
            'GIVEN the initial options and "Force Reset" is unchecked WHEN selecting a language that does not exist in another set ' +
                'THEN the selected model should not reset',
            async () => {
                fakeSingleSelectComponent.forceReset = false;
                fixture.detectChanges();
                selectedOptions(mockLanguagesV1[1]);
                expect(selectComponent.model).toEqual(ID_DE);
                await changeSource();
                expect(selectComponent.model).toEqual(ID_DE);
            }
        );

        it(
            'GIVEN a selected option that does exist in another set of options and "Force Reset" is checked ' +
                'WHEN switching to another set of options THEN the selected model should reset',
            async () => {
                selectedOptions(mockLanguagesV1[0]);
                expect(selectComponent.model).toEqual(ID_EN);
                await changeSource();
                expect(selectComponent.model).toEqual(ID_EN);
            }
        );

        it('GIVEN a custom item component THEN options and selected option should be displayed with that component', () => {
            selectedOptions(mockLanguagesV1[0]);
            expect(selectComponent.model).toEqual(ID_EN);
            expect(selectComponent.items.find((item) => item.id === ID_EN)).toBeDefined();
        });

        it(
            'GIVEN initial settings WHEN selecting an option THEN that option should be selected AND ' +
                ' displayed in dropdown list at the same time',
            () => {
                expect(selectComponent.selected.id).toEqual(ID_DE);
                selectComponent.onSingleSelectIsOpenChange(true);
                expect(selectComponent.items.find((item) => item.id === ID_DE)).toBeDefined();
            }
        );
    });

    describe('Placeholder -', () => {
        it('GIVEN settings for controls WHEN removing a selected option THEN the placeholder should be displayed in place of selected item', () => {
            expect(selectComponent.selected.id).toEqual(ID_DE);
            removeSelectedOption(mockLanguagesV1[1]);
            expect(!selectComponent.selected).toEqual(true);
        });
        it('GIVEN an option is not selected WHEN opening a dropdown THEN search field should not have a placeholder', () => {
            expect(selectComponent.selected.id).toEqual(ID_DE);
            removeSelectedOption(mockLanguagesV1[1]);
            expect(selectComponent.selected).toBeUndefined();
            expect(selectComponent.showPlaceholder()).toEqual(false);
        });
        it('GIVEN an option is selected WHEN opening a dropdown THEN search field should have a placeholder', () => {
            expect(selectComponent.showPlaceholder()).toEqual(true);
        });
    });

    describe('Search Input -', () => {
        it('GIVEN "searchEnabled" is false WHEN opening a dropdown THEN the search input should not be displayed', () => {
            expect(getSearchInputStatus(false, true)).toBeFalse();
        });
        it('GIVEN "searchEnabled" is true WHEN opening a dropdown THEN the search input should be displayed', () => {
            expect(getSearchInputStatus(true, true)).toBeTrue();
        });
    });

    describe('isReadOnly', () => {
        it('GIVEN "isReadOnly" is true THEN component should be disabled', () => {
            selectComponent.isReadOnly = true;
            fixture.detectChanges();
            expect(selectElement.classList.value).toContain(DISABLED_CLASS);
        });
    });

    describe('callbacks', () => {
        beforeEach(() => {
            selectComponent.onRemove = (): void => {
                fakeSingleSelectComponent.onRemoveCounter++;
            };
            selectComponent.onSelect = (): void => {
                fakeSingleSelectComponent.onSelectCounter++;
            };
        });
        describe('onChange', () => {
            beforeEach(() => {
                selectComponent.onChange = (): void => {
                    fakeSingleSelectComponent.onChangeCounter++;
                };
                callNumberBefore = fakeSingleSelectComponent.onChangeCounter;
            });

            it('GIVEN "onChange" WHEN removing a selected option THEN "onChange" function should be called', () => {
                removeSelectedOption(mockLanguagesV1[1]);
                expect(fakeSingleSelectComponent.onChangeCounter - callNumberBefore).toEqual(1);
            });

            it('GIVEN "onChange" WHEN selecting an option which is not selected THEN "onChange" function should be called', () => {
                selectedOptions(mockLanguagesV1[0]);
                expect(fakeSingleSelectComponent.onChangeCounter - callNumberBefore).toEqual(1);
            });

            it('GIVEN "onChange" WHEN selecting an option which is selected THEN "onChange" function should not be called', () => {
                selectedOptions(mockLanguagesV1[1]);
                expect(fakeSingleSelectComponent.onChangeCounter - callNumberBefore).toEqual(0);
            });

            it('GIVEN "onChange" WHEN Parent Component changes the model THEN "onChange" function should be called', async () => {
                fakeSingleSelectComponent.changeModel();
                fixture.detectChanges();
                await fixture.whenStable();
                expect(fakeSingleSelectComponent.onChangeCounter - callNumberBefore).toEqual(1);
            });
        });

        it('GIVEN "onRemove" WHEN removing a selected option THEN the "onRemove" function should be called', () => {
            callNumberBefore = fakeSingleSelectComponent.onRemoveCounter;
            removeSelectedOption(mockLanguagesV1[1]);
            expect(fakeSingleSelectComponent.onRemoveCounter - callNumberBefore).toEqual(1);
        });

        it('GIVEN "onSelect" WHEN selecting an option THEN the "onSelect" function should be called', () => {
            callNumberBefore = fakeSingleSelectComponent.onSelectCounter;
            selectedOptions(mockLanguagesV1[0]);
            expect(fakeSingleSelectComponent.onSelectCounter).toEqual(1);
        });
    });
});
