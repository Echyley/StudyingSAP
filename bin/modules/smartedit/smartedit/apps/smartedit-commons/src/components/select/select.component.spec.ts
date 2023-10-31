/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormGroup, FormControl, FormsModule } from '@angular/forms';
import {
    ButtonModule,
    FormModule as FundamentalFormModule,
    MenuModule,
    PopoverModule,
    SelectModule as FundamentalSelectModule
} from '@fundamental-ngx/core';
import { TranslateModule } from '@ngx-translate/core';
import { SelectModule } from '@smart/utils';
import {
    FakeSelectComponent,
    SelectOneComponent,
    SelectTwoComponent,
    SelectThreeComponent
} from './fakeselect.component';
const FIRST_INDEX = 0;
const SECOND_INDEX = 1;
const SELECT_ONE = '#select-one';
const SELECT_TWO = '#select-two';
const SELECT_THREE = '#select-three';
const SELECT_TWO_BUTTON_TEXT = '#select-two .fd-button__text';
const SELECT_ONE_OUTPUT = '#select-one-output';
const SELECT_THREE_OUTPUT = '#select-three-output';
const SELECT_MENU = '.su-select__menu';
const FIRST_LABEL = 'label_0';
const FIRST_VALUE = 'value_0';
const SECOND_LABEL = 'label_1';
const SECOND_VALUE = 'value_1';
const PLACEHOLDER = '#select-one .fd-dropdown__control';
const PLACEHOLDER_TWO = '#select-two .fd-dropdown__control';
const PLACEHOLDER_THREE = '#select-three .fd-dropdown__control';
const PLACEHOLDER_TEXT = 'My placeholder';
describe('TestFakeSelectComponent', () => {
    let selectOne: HTMLElement;
    let selectTwo: HTMLElement;
    let selectThree: HTMLElement;
    let selectMenu: HTMLElement;
    let placeholder: HTMLElement;
    let placeholderTwo: HTMLElement;
    let placehoderThree: HTMLElement;
    let nativeElement: HTMLElement;
    let selectTWoButtonText: HTMLElement;
    let selectOneOutput: HTMLElement;
    let selectThreeOutput: HTMLElement;
    let fakeSelectComponent: FakeSelectComponent;
    let selectOneComponent: SelectOneComponent;
    let selectThreeCompnent: SelectThreeComponent;
    let fixture: ComponentFixture<FakeSelectComponent>;
    async function createTestFakeSelectComponentContext() {
        await TestBed.configureTestingModule({
            imports: [
                TranslateModule.forRoot(),
                FormsModule,
                ReactiveFormsModule,
                ButtonModule,
                FundamentalFormModule,
                SelectModule,
                MenuModule,
                PopoverModule,
                FundamentalSelectModule
            ],
            declarations: [
                FakeSelectComponent,
                SelectOneComponent,
                SelectTwoComponent,
                SelectThreeComponent
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
        fixture = TestBed.createComponent(FakeSelectComponent);
        fakeSelectComponent = fixture.componentInstance;
        fixture.detectChanges();
        selectOneComponent = fakeSelectComponent.selectOneCompnent;
        selectThreeCompnent = fakeSelectComponent.selectThreeCompnent;
        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
        selectOne = nativeElement.querySelector(SELECT_ONE);
        selectTwo = nativeElement.querySelector(SELECT_TWO);
        selectThree = nativeElement.querySelector(SELECT_THREE);
        selectMenu = nativeElement.querySelector(SELECT_MENU);
        placeholder = nativeElement.querySelector(PLACEHOLDER);
        placeholderTwo = nativeElement.querySelector(PLACEHOLDER_TWO);
        placehoderThree = nativeElement.querySelector(PLACEHOLDER_THREE);
        selectTWoButtonText = nativeElement.querySelector(SELECT_TWO_BUTTON_TEXT);
        selectOneOutput = nativeElement.querySelector(SELECT_ONE_OUTPUT);
        selectThreeOutput = nativeElement.querySelector(SELECT_THREE_OUTPUT);
    }

    beforeEach(async () => {
        await createTestFakeSelectComponentContext();
    });

    it('Should create Infinite Scrolling Component correctly', () => {
        expect(createTestFakeSelectComponentContext).toBeDefined();
        expect(fakeSelectComponent).toBeDefined();
    });

    describe('- Basic', () => {
        it('GIVEN select trigger is clicked THEN menu is present', () => {
            selectOne.click();
            fixture.detectChanges();
            expect(selectMenu).toBeDefined();
        });

        it('GIVEN select trigger is clicked when dropdown is open THEN menu is absent', () => {
            selectOne.click();
            fixture.detectChanges();
            selectOne.click();
            fixture.detectChanges();
            expect(selectMenu).toBe(null);
        });

        it('Select placeholder exists', () => {
            expect(placeholder.textContent).toContain(PLACEHOLDER_TEXT);
        });

        it('GIVEN select value is selected THEN the select menu is absent', () => {
            selectOne.click();
            fixture.detectChanges();
            selectOneComponent.handleItemSelected(selectOneComponent.items[FIRST_INDEX]);
            expect(selectMenu).toBe(null);
        });

        it('GIVEN select value is selected THEN the select output displays value', () => {
            selectOne.click();
            fixture.detectChanges();
            selectOneComponent.handleItemSelected(selectOneComponent.items[FIRST_INDEX]);
            expect(selectOneOutput.innerText).toEqual(FIRST_VALUE);
        });

        it('GIVEN select value is selected THEN the placeholder value changes', () => {
            selectOne.click();
            fixture.detectChanges();
            selectOneComponent.handleItemSelected(selectOneComponent.items[FIRST_INDEX]);
            fixture.detectChanges();
            expect(placeholder.textContent).toContain(FIRST_LABEL);
        });
    });

    describe('- With Initial Value', () => {
        it('Select placeholder exists with initial value label', () => {
            expect(placeholderTwo.textContent).toContain(FIRST_LABEL);
        });

        it('GIVEN select dropdown menu item is clicked THEN placeholder is no longer set to initial value', () => {
            selectTwo.click();
            fixture.detectChanges();
            selectTWoButtonText.textContent = SECOND_LABEL;
            expect(placeholderTwo.textContent).toContain(SECOND_LABEL);
        });
    });

    describe('- Intergration with Angular Forms', () => {
        it('GIVEN select value is selected THEN the select output displays value using reactive forms', () => {
            selectThree.click();
            fixture.detectChanges();
            selectThreeCompnent.form
                .get('select')
                .setValue(selectThreeCompnent.items[SECOND_INDEX]);
            fixture.detectChanges();
            fixture.whenStable().then(() => {
                expect(selectThreeOutput.textContent).toContain(SECOND_VALUE);
            });
        });

        it('GIVEN angular form control is initialized with value THEN the select placeholder should display value', () => {
            selectThree.click();
            fixture.detectChanges();
            selectThreeCompnent.form = new FormGroup({
                select: new FormControl(selectThreeCompnent.items[SECOND_INDEX])
            });
            fixture.detectChanges();
            expect(placehoderThree.textContent).toContain(SECOND_LABEL);
        });
    });
});
