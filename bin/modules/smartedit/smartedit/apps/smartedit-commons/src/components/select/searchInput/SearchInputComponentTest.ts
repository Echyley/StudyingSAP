/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ElementRef } from '@angular/core';
import { ISharedDataService } from '@smart/utils';
import { SearchInputComponent } from './SearchInputComponent';

describe('SearchInputComponent Test', () => {
    let searchInputComponent: SearchInputComponent;
    let searchTermChangeEmitSpy: jasmine.Spy;
    const sharedDataService: jasmine.SpyObj<ISharedDataService> =
        jasmine.createSpyObj<ISharedDataService>('sharedDataService', ['get']);

    class MockElementRef implements ElementRef {
        nativeElement = {
            focus() {
                // This is intentional
            }
        };
    }

    describe('onChange with configuration for typeAhead', () => {
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();
            sharedDataService.get.and.returnValue(
                Promise.resolve({
                    typeAheadMiniSearchTermLength: 2,
                    typeAheadDebounce: 100
                })
            );
            searchInputComponent = new SearchInputComponent(
                new MockElementRef(),
                sharedDataService
            );
            searchTermChangeEmitSpy = spyOn(searchInputComponent.searchChange, 'emit');
            await searchInputComponent.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it(
            'should emit search value after onChange trigger when time is over than configuration.typeAheadDebounce when' +
                ' TypeAhead is Enabled',
            () => {
                searchInputComponent.isTypeAheadEnabled = true;
                searchInputComponent.onChange('expect');
                clock.tick(101);
                expect(searchTermChangeEmitSpy).toHaveBeenCalled();
            }
        );

        it(
            'should not call the emit when the search term length is not over than configuration.typeAheadMiniSearchTermLength' +
                ' when TypeAhead is Enabled',
            () => {
                searchInputComponent.isTypeAheadEnabled = true;
                searchInputComponent.onChange('e');
                clock.tick(101);
                expect(searchTermChangeEmitSpy).not.toHaveBeenCalled();
            }
        );

        it('should not call the emit when time is not over than configuration.typeAheadDebounce when TypeAhead is Enabled', () => {
            searchInputComponent.isTypeAheadEnabled = true;
            searchInputComponent.onChange('expect');
            clock.tick(30);
            expect(searchTermChangeEmitSpy).not.toHaveBeenCalled();
        });

        it(
            'should emit search value after onChange trigger when time is not over than configuration.typeAheadDebounce when' +
                ' TypeAhead is disabled',
            () => {
                searchInputComponent.isTypeAheadEnabled = false;
                searchInputComponent.onChange('ex');
                expect(searchTermChangeEmitSpy).toHaveBeenCalled();
            }
        );

        it(
            'should call the emit when the search term length is not over than configuration.typeAheadDebounce when' +
                ' TypeAhead is disabled',
            () => {
                searchInputComponent.isTypeAheadEnabled = false;
                searchInputComponent.onChange('ex');
                clock.tick(1);
                expect(searchTermChangeEmitSpy).toHaveBeenCalled();
            }
        );
    });

    describe('onChange without configuration for typeAhead', () => {
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();
            sharedDataService.get.and.returnValue(Promise.resolve({}));
            searchInputComponent = new SearchInputComponent(
                new MockElementRef(),
                sharedDataService
            );
            searchTermChangeEmitSpy = spyOn(searchInputComponent.searchChange, 'emit');
            await searchInputComponent.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it('should emit search value after onChange trigger with 500 debounce when TypeAhead is Enabled', () => {
            searchInputComponent.isTypeAheadEnabled = true;
            searchInputComponent.onChange('e');
            clock.tick(501);
            expect(searchTermChangeEmitSpy).toHaveBeenCalled();
        });

        it('should not call the emit when time is not over than default debounce time 500 when TypeAhead is Enabled', () => {
            searchInputComponent.isTypeAheadEnabled = true;
            searchInputComponent.onChange('w');
            clock.tick(300);
            expect(searchTermChangeEmitSpy).not.toHaveBeenCalled();
        });

        it('should emit search value after onChange trigger when TypeAhead is Disabled', () => {
            searchInputComponent.isTypeAheadEnabled = false;
            searchInputComponent.onChange('e');
            expect(searchTermChangeEmitSpy).toHaveBeenCalled();
        });
    });
});
