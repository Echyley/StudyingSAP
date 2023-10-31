/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ElementRef, Renderer2, SimpleChange, SimpleChanges } from '@angular/core';
import { noop } from 'lodash';
import { Observable, Subject } from 'rxjs';

import { ListItemKeyboardControlDirective } from './list-item-keyboard-control.directive';
import { KeyboardKey, ListKeyboardControlDirective } from './list-keyboard-control.directive';

class QueryListMock {
    public changes: Observable<ListItemKeyboardControlDirective>;
    private changesSubject: Subject<ListItemKeyboardControlDirective>;
    private items: ListItemKeyboardControlDirective[];

    get length() {
        return this.items.length;
    }

    constructor(items: ListItemKeyboardControlDirective[]) {
        this.items = items || [];
        this.changesSubject = new Subject();
        this.changes = this.changesSubject.asObservable();
    }

    public notifyOnChanges() {
        this.changesSubject.next();
    }

    public clearItems() {
        this.items = [];
    }

    public simulateChanges(items?: ListItemKeyboardControlDirective[]) {
        if (items) {
            this.items = items;
        }
        this.changesSubject.next();
    }

    public toArray() {
        return [...this.items];
    }
}

let listKeyboardControlDirective: ListKeyboardControlDirective;
type Input = Partial<
    Pick<
        typeof listKeyboardControlDirective,
        'suListKeyboardControlEnabled' | 'suListKeyboardControlDisabledPredicate'
    >
>;
let mockListItems: ListItemKeyboardControlDirective[];
let queryListMock: QueryListMock;

describe('ListKeyboardControlDirective', () => {
    beforeEach(() => {
        listKeyboardControlDirective = new ListKeyboardControlDirective();
        mockListItems = createMockListItemDirectives(3);
        queryListMock = new QueryListMock(mockListItems);
    });

    describe('ngOnChanges', () => {
        describe('initial', () => {
            it('GIVEN no inputs WHEN initialize THEN it should set first item as active', () => {
                initialize(queryListMock);

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
            });
            it('GIVEN control is enabled WHEN initialize THEN it should set first item as active', () => {
                initialize(queryListMock, {
                    suListKeyboardControlEnabled: true
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
            });

            it('GIVEN control is disabled WHEN initialize THEN active item should not be set', () => {
                initialize(queryListMock, { suListKeyboardControlEnabled: false });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(null);
            });

            it('GIVEN control is enabled AND predicate is provided WHEN initialize THEN it should set non disabled item as active', () => {
                const expectedActiveItemIndex = 2;
                const predicate = (_: ListItemKeyboardControlDirective, index: number) =>
                    index < expectedActiveItemIndex;

                initialize(queryListMock, {
                    suListKeyboardControlEnabled: true,
                    suListKeyboardControlDisabledPredicate: predicate
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(
                    expectedActiveItemIndex
                );
            });
        });

        describe('suListKeyboardControlEnabled', () => {
            it('GIVEN was disabled WHEN control is enabled THEN it should set first item as active', () => {
                initialize(queryListMock, { suListKeyboardControlEnabled: false });

                simulateNgOnChanges({
                    suListKeyboardControlEnabled: new SimpleChange(false, true, false)
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
            });

            it('GIVEN was enabled WHEN control is disabled THEN it should unset active item', () => {
                initialize(queryListMock, { suListKeyboardControlEnabled: true });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);

                simulateNgOnChanges({
                    suListKeyboardControlEnabled: new SimpleChange(true, false, false)
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(null);
            });
        });

        describe('suListKeyboardControlDisabledPredicate', () => {
            const predicate = (_: ListItemKeyboardControlDirective, index: number) => index < 2;
            it('GIVEN content was enabled WHEN providing a predicate THEN it should set first non disabled item as active', () => {
                initialize(queryListMock, { suListKeyboardControlEnabled: true });

                simulateNgOnChanges({
                    suListKeyboardControlDisabledPredicate: new SimpleChange(
                        undefined,
                        predicate,
                        false
                    )
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).toBe(2);
            });

            it('GIVEN content was enabled AND predicate was provided WHEN removing a predicate THEN it should set previosuly disabled item as active', () => {
                initialize(queryListMock, {
                    suListKeyboardControlEnabled: true,
                    suListKeyboardControlDisabledPredicate: predicate
                });

                simulateNgOnChanges({
                    suListKeyboardControlDisabledPredicate: new SimpleChange(
                        predicate,
                        undefined,
                        false
                    )
                });

                expect((listKeyboardControlDirective as any).activeItemIndex).not.toBe(2);
            });
        });
    });

    describe('items change', () => {
        it('GIVEN no items THEN it should clear active item', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            const activeItemIndex = 1;
            setActiveItemByIndex(mockListItems, activeItemIndex);

            queryListMock.clearItems();
            queryListMock.simulateChanges();

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(null);
        });

        it('GIVEN new items does not include the active item THEN it should clear active item', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            const activeItemIndex = 2;
            setActiveItemByIndex(mockListItems, activeItemIndex);

            const newItems = mockListItems.slice(0, 2);
            queryListMock.simulateChanges(newItems);

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(null);
        });

        it('GIVEN items AND control is disabled THEN it should clear active item', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            simulateNgOnChanges({
                suListKeyboardControlEnabled: new SimpleChange(true, false, false)
            });

            queryListMock.simulateChanges();

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(null);
        });
    });

    describe('Arrow down is pressed', () => {
        it('GIVEN no active item THEN first item should be set as active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });
            (listKeyboardControlDirective as any).clearActiveItem();

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
        });

        it('GIVEN active item THEN next item should be set as active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(1);
        });

        it('GIVEN last item is active THEN it should remain active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));
            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));
            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            const expectedActiveItemIndex = 2;
            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(
                expectedActiveItemIndex
            );
        });

        it('GIVEN predicate THEN it should skip disabled item AND set next one as active', () => {
            const predicate = (_: ListItemKeyboardControlDirective, index: number) => index === 1;
            initialize(queryListMock, {
                suListKeyboardControlEnabled: true,
                suListKeyboardControlDisabledPredicate: predicate
            });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(2);
        });
    });

    describe('Arrow up is pressed', () => {
        it('GIVEN no active item THEN first item should be set as active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });
            (listKeyboardControlDirective as any).clearActiveItem();

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowUp'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
        });

        it('GIVEN active item THEN previous item should be set as active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));
            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowUp'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(1);
        });

        it('GIVEN first item is active THEN it should remain active', () => {
            initialize(queryListMock, { suListKeyboardControlEnabled: true });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowUp'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
        });

        it('GIVEN predicate THEN it should skip disabled item AND set previous one as active', () => {
            const predicate = (_: ListItemKeyboardControlDirective, index: number) => index === 1;
            initialize(queryListMock, {
                suListKeyboardControlEnabled: true,
                suListKeyboardControlDisabledPredicate: predicate
            });

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowDown'));

            listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('ArrowUp'));

            expect((listKeyboardControlDirective as any).activeItemIndex).toBe(0);
        });
    });

    it('WHEN enter is pressed THEN it should emit @Output event with the index of active item', () => {
        initialize(queryListMock, { suListKeyboardControlEnabled: true });

        const suListKeyboardControlEnterKeydownSpy = spyOn(
            listKeyboardControlDirective.suListKeyboardControlEnterKeydown,
            'emit'
        );
        listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('Enter'));

        expect(suListKeyboardControlEnterKeydownSpy).toHaveBeenCalledTimes(1);
    });

    it('WHEN escape is pressed THEN the @Output event should be emitted', () => {
        initialize(queryListMock, { suListKeyboardControlEnabled: true });

        const suListKeyboardControlEscKeydownSpy = spyOn(
            listKeyboardControlDirective.suListKeyboardControlEscKeydown,
            'emit'
        );
        listKeyboardControlDirective.onKeyDown(mockKeyboardEvent('Esc'));

        expect(suListKeyboardControlEscKeydownSpy).toHaveBeenCalledTimes(1);
    });
});

function initialize(
    queryList: QueryListMock,
    inputs?: {
        suListKeyboardControlEnabled?: boolean;
        suListKeyboardControlDisabledPredicate?: (
            item: ListItemKeyboardControlDirective,
            index: number
        ) => boolean;
    }
): void {
    if (inputs) {
        simulateNgOnChanges({
            suListKeyboardControlEnabled: new SimpleChange(
                undefined,
                inputs.suListKeyboardControlEnabled,
                true
            ),
            suListKeyboardControlDisabledPredicate: new SimpleChange(
                undefined,
                inputs.suListKeyboardControlDisabledPredicate,
                true
            )
        });
    }
    listKeyboardControlDirective.items = queryList as any;
    listKeyboardControlDirective.ngAfterContentInit();
}

function simulateNgOnChanges(inputs: { [key in keyof Input]: SimpleChange }) {
    let input: keyof Input;
    for (input in inputs) {
        if (typeof inputs[input] !== undefined) {
            const value = inputs[input];
            listKeyboardControlDirective[input] = value.currentValue;
        }
    }
    listKeyboardControlDirective.ngOnChanges(inputs as SimpleChanges);
}

function setActiveItemByIndex(
    listItemDirectives: ListItemKeyboardControlDirective[],
    index: number
): void {
    (listKeyboardControlDirective as any).activeItem = listItemDirectives[index];
    (listKeyboardControlDirective as any).activeItemIndex = index;
}

function mockKeyboardEvent(key: keyof typeof KeyboardKey): KeyboardEvent {
    return new KeyboardEvent('keydown', {
        key: KeyboardKey[key]
    });
}

function createMockListItemDirectives(count = 1): ListItemKeyboardControlDirective[] {
    const mockArray = [];
    for (let x = 0; x < count; x++) {
        const directive = createMockListItemDirective();
        mockArray.push(directive);
    }
    return mockArray;

    function createMockListItemDirective(): ListItemKeyboardControlDirective {
        const elementRef: jasmine.SpyObj<ElementRef> = jasmine.createSpyObj('elementRef', [
            'nativeElement'
        ]);
        elementRef.nativeElement.scrollIntoView = noop;
        const renderer2: jasmine.SpyObj<Renderer2> = jasmine.createSpyObj('rendere2', [
            'addClass',
            'removeClass',
            'setAttribute'
        ]);

        return new ListItemKeyboardControlDirective(elementRef, renderer2);
    }
}
