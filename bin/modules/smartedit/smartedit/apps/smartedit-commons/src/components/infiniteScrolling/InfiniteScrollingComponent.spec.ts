/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA, ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DiscardablePromiseUtils } from '../../utils';
import { InfiniteScrollingComponent, TechnicalUniqueIdAware } from './InfiniteScrollingComponent';
import { InfiniteScrollingFakeChildComponent, items } from './InfiniteScrollingFakeChildComponent';
import { TestInfiniteScrollingFakeComponent } from './TestInfiniteScrollingFakeComponent';

const containerClass = 'container-class';
const holderClass = 'holder-class';
const childCountOrIndex = 10;
const maxChildCountOrIndex = 20;
const firstIndex = 0;
const maskAbsentIndexOrCount = 1;
const lastIndex = 19;

describe('TestInfiniteScrollingFakeComponent', () => {
    let nativeElement: HTMLElement;
    let defaultScrollingContainer: any;
    let defaultScrollingHolder: any;
    let defaultScrollingChild: any;
    let infiniteScrollingComponent: InfiniteScrollingComponent<TechnicalUniqueIdAware>;
    let testInfiniteScrollingFakeComponent: TestInfiniteScrollingFakeComponent;
    let cdr: jasmine.SpyObj<ChangeDetectorRef>;
    let discardablePromiseUtils: jasmine.SpyObj<DiscardablePromiseUtils>;
    let fixture: ComponentFixture<TestInfiniteScrollingFakeComponent>;
    async function createTestInfiniteScrollingComponentContext() {
        discardablePromiseUtils = jasmine.createSpyObj(['apply', 'clear']);
        discardablePromiseUtils.apply.and.returnValue(null);
        discardablePromiseUtils.clear.and.returnValue(null);
        await TestBed.configureTestingModule({
            declarations: [
                TestInfiniteScrollingFakeComponent,
                InfiniteScrollingComponent,
                InfiniteScrollingFakeChildComponent
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(InfiniteScrollingComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/infiniteScrolling/InfiniteScrollingComponent.html',
                    styleUrls: [''],
                    providers: [
                        { provide: ChangeDetectorRef, useValue: cdr },
                        { provide: DiscardablePromiseUtils, useValue: discardablePromiseUtils }
                    ]
                }
            })
            .compileComponents();
        fixture = TestBed.createComponent(TestInfiniteScrollingFakeComponent);
        testInfiniteScrollingFakeComponent = fixture.componentInstance;
        fixture.detectChanges();
        infiniteScrollingComponent =
            testInfiniteScrollingFakeComponent.infiniteScrollingComponentRef;
        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
        defaultScrollingContainer = nativeElement.querySelector(
            '#default-container .se-infinite-scrolling__container'
        );
        defaultScrollingHolder = nativeElement.querySelector(
            '#default-container .se-infinite-scrolling__holder'
        );
        defaultScrollingChild = nativeElement.querySelector(
            '#default-container .se-infinite-scrolling__holder .items'
        );
    }

    beforeEach(async () => {
        await createTestInfiniteScrollingComponentContext();
    });

    it('Should create Infinite Scrolling Component correctly', () => {
        expect(createTestInfiniteScrollingComponentContext).toBeDefined();
        expect(TestInfiniteScrollingFakeComponent).toBeDefined();
        expect(infiniteScrollingComponent).toBeDefined();
    });

    describe('Initial Setup', () => {
        it('Given a correct class of Infinite Scrolling Container', () => {
            expect(defaultScrollingContainer.className).toContain(containerClass);
        });

        it('Given a correct class of Infinite Scrolling Holder', () => {
            expect(defaultScrollingHolder.className).toContain(holderClass);
        });

        it('Given the default Infinite Scrolling, which should have correct children count', () => {
            expect(defaultScrollingChild.children.length).toEqual(childCountOrIndex);
        });

        it('Given the default Infinite Scrolling, which should have one of initial items', () => {
            expect(defaultScrollingChild.children[firstIndex].checkVisibility()).toBeTrue();
        });

        it('Given the default Infinite Scrolling, which does not have item out of its initial scope', () => {
            expect(
                nativeElement.querySelector(`#default-container #item${maxChildCountOrIndex}`)
            ).toBe(null);
        });

        it('Given the Immediate Infinite Scroll, which should fetch next page when consumer applied condition for displaying items so that the container height was not populated by the items from the first page', () => {
            expect(
                nativeElement.querySelector(`#immediate-check-container #item${childCountOrIndex}`)
            ).toBeDefined();
            expect(
                nativeElement.querySelector(`#immediate-check-container #item${lastIndex}`)
            ).toBeDefined();
        });
    });

    describe('After scroll - ', () => {
        beforeEach(() => {
            const scrollToBottom: HTMLButtonElement =
                nativeElement.querySelector('#scrollToBottom');
            spyOn(scrollToBottom, 'click');
            scrollToBottom.click();
            testInfiniteScrollingFakeComponent.setItems(items);
            fixture.detectChanges();
        });

        it('Given the default Infinite Scrolling Component that has correct children count', () => {
            expect(defaultScrollingChild.children.length).toEqual(maxChildCountOrIndex);
        });

        it('Given the default Infinite Scrolling Component that has item', () => {
            expect(
                nativeElement.querySelector(`#default-container #item${lastIndex}`)
            ).toBeDefined();
        });
    });

    describe('Mask - ', () => {
        beforeEach(async () => {
            const inputMask = nativeElement.querySelector('#mask');
            (inputMask as any).value = childCountOrIndex;
            const filterItems = await testInfiniteScrollingFakeComponent.fetchPage(
                (inputMask as any).value,
                1,
                0
            );
            testInfiniteScrollingFakeComponent.setItems(filterItems);
            fixture.detectChanges();
        });

        it('Given the default Infinite Scrolling Component that has correct children count', () => {
            expect(defaultScrollingChild.children.length).toEqual(maskAbsentIndexOrCount);
        });

        it('Given the default Infinite Scrolling Component that has item', () => {
            expect(
                nativeElement.querySelector(`#default-container #item${childCountOrIndex}`)
            ).toBeDefined();
        });

        it('Given the default Infinite Scrolling Component that does not have item', () => {
            expect(
                nativeElement.querySelector(`#default-container #item${maskAbsentIndexOrCount}`)
            ).toBe(null);
        });
    });
});
