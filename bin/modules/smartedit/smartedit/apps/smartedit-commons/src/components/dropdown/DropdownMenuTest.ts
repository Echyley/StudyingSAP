/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    EventEmitter,
    Input,
    NO_ERRORS_SCHEMA,
    Output,
    SimpleChange
} from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { TranslateModule } from '@ngx-translate/core';
import { YJQUERY_TOKEN } from '@smart/utils';
import { take, toArray } from 'rxjs/operators';
import { DropdownMenuComponent } from './dropdownMenu/DropdownMenuComponent';
import { DropdownMenuItemComponent } from './dropdownMenu/DropdownMenuItemComponent';
import { IDropdownMenuItem } from './dropdownMenu/IDropdownMenuItem';
import { mockDropdownItems } from './mockDropdownItems';

@Component({
    selector: 'se-dropdown-menu-item',
    template: ''
})
class FakeDropdownMenuItemComponent implements Partial<DropdownMenuItemComponent> {
    @Input() dropdownItem: IDropdownMenuItem;
    @Input() selectedItem: any;
    @Output() selectedItemChange = new EventEmitter<any>();
}

describe('DropdownMenuTest: with fake child component)', () => {
    let fixture: ComponentFixture<DropdownMenuComponent>;
    let component: DropdownMenuComponent;
    let itemComponent: FakeDropdownMenuItemComponent;
    beforeEach(async () => {
        const yjQuery = jasmine.createSpy('yJQuery');
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [DropdownMenuComponent, FakeDropdownMenuItemComponent],
            schemas: [NO_ERRORS_SCHEMA],
            providers: [{ provide: YJQUERY_TOKEN, useValue: yjQuery }]
        })
            .overrideComponent(DropdownMenuComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/dropdown/dropdownMenu/DropdownMenuComponent.html',
                    styleUrls: [
                        '/base/src/components/dropdown/dropdownMenu/DropdownMenuComponent.scss'
                    ]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(DropdownMenuComponent);
        component = fixture.componentInstance;
        component.dropdownItems = mockDropdownItems.component;
        component.selectedItem = { id: 'whatever Id' };
        component.ngOnChanges({
            dropdownItems: new SimpleChange(undefined, mockDropdownItems.component, false)
        });
        fixture.detectChanges();

        const counterEl = fixture.debugElement.query(By.directive(FakeDropdownMenuItemComponent));
        itemComponent = counterEl.componentInstance;
    });

    it('renders DropdownMenuComponent with fake DropdownMenuItemComponent', () => {
        expect(component).toBeTruthy();
        expect(itemComponent).toBeTruthy();
        expect(itemComponent.dropdownItem.component.name).toBe('MockDropdownMenuItemComponent');
        expect(itemComponent.selectedItem).toEqual({ id: 'whatever Id' });
    });

    describe('ngOnChanges', () => {
        it('throws an error if callback and component are provided at the same time', () => {
            const errorMessage =
                'DropdownMenuComponent.validateDropdownItem - Dropdown Item must contain "callback" or "component"';

            const dropdownItems = [
                { ...mockDropdownItems.callback[0], ...mockDropdownItems.component[0] }
            ];
            component.dropdownItems = dropdownItems;
            expect(() =>
                component.ngOnChanges({
                    dropdownItems: new SimpleChange(undefined, dropdownItems, false)
                })
            ).toThrowError(errorMessage);
        });

        it('GIVEN callback WHEN ngOnChanges is called THEN default component will be set', () => {
            // Given
            component.dropdownItems = mockDropdownItems.callback;

            // When
            component.ngOnChanges({
                dropdownItems: new SimpleChange(undefined, mockDropdownItems.callback, false)
            });

            // Assert
            expect(component.clonedDropdownItems[0].component.name).toBe(
                'DropdownMenuItemDefaultComponent'
            );
        });

        it('GIVEN component WHEN ngOnChanges is called THEN component property should not be affected', () => {
            // GIVEN
            component.dropdownItems = mockDropdownItems.component;

            // WHEN
            component.ngOnChanges({
                dropdownItems: new SimpleChange(undefined, mockDropdownItems.component, false)
            });

            // THEN
            expect(component.clonedDropdownItems[0].component.name).toBe(
                'MockDropdownMenuItemComponent'
            );
        });
    });

    describe('clickHandler', () => {
        it('toggleMenuElement is toggled, popover is open, toggleMenuElement is toggled, popover is closed', fakeAsync((): void => {
            let results: boolean[] = [];
            component.isOpenChange.pipe(take(2), toArray()).subscribe((_results) => {
                results = _results;
            });
            component.toggleMenuElement.nativeElement.click();
            component.toggleMenuElement.nativeElement.click();
            tick();
            expect(results).toEqual([true, false]);
        }));

        it('toggleMenuElement is toggled, popover is open, other element is toggled, popover is closed', fakeAsync((): void => {
            let results: boolean[] = [];
            component.isOpenChange.pipe(take(2), toArray()).subscribe((_results) => {
                results = _results;
            });
            component.toggleMenuElement.nativeElement.click();
            document.dispatchEvent(new MouseEvent('click', { clientX: 0, clientY: 0 }));
            tick();
            expect(results).toEqual([true, false]);
        }));
    });
});
