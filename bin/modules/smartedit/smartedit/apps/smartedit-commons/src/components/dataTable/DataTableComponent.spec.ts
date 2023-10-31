/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { Component, Inject, Injector, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslateModule } from '@ngx-translate/core';
import {
    DATA_TABLE_COMPONENT_DATA,
    DataTableComponent,
    DataTableComponentData,
    DataTableConfig,
    DynamicPagedListColumnKey,
    SortStatus
} from 'smarteditcommons';
import { DataTableRendererComponent } from './DataTableRenderer';

@Component({
    selector: 'my-selector',
    template: ` <div>{{ data.item[data.column.property] }}</div> `
})
class RenderedComponent {
    constructor(@Inject(DATA_TABLE_COMPONENT_DATA) public data: DataTableComponentData) {}
}
@NgModule({
    declarations: [RenderedComponent],
    imports: [CommonModule],
    entryComponents: [RenderedComponent]
})
class MockModule {}

const config: DataTableConfig = {
    sortBy: 'item_1',
    reversed: false,
    injectedContext: { id: 'injectedId' }
};
const sortStatus: SortStatus = {
    internalSortBy: 'item_1',
    reversed: false,
    currentPage: 1
};
const items: any[] = [
    {
        col_1: 'col_1_row_1',
        col_2: 'col_2_row_1',
        col_3: 'col_3_row_1'
    },
    {
        col_1: 'col_1_row_2',
        col_2: 'col_2_row_2',
        col_3: 'col_3_row_2'
    },
    {
        col_1: 'col_1_row_3',
        col_2: 'col_2_row_3',
        col_3: 'col_3_row_3'
    }
];
const columns: DynamicPagedListColumnKey[] = [
    {
        i18n: 'col_1',
        property: 'col_1',
        sortable: true
    },
    {
        i18n: 'col_2',
        property: 'col_2',
        sortable: true,
        component: RenderedComponent
    },
    {
        i18n: 'col_3',
        property: 'col_3',
        sortable: true
    }
];

describe('DataTableComponent', () => {
    let component: DataTableComponent;
    let fixture: ComponentFixture<DataTableComponent>;
    let nativeElement: HTMLElement;
    let inject: jasmine.SpyObj<Injector>;

    async function createDataTableComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), MockModule],
            declarations: [DataTableComponent, DataTableRendererComponent],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(DataTableComponent, {
                set: {
                    templateUrl: '/base/src/components/dataTable/DataTableComponent.html',
                    styleUrls: ['/base/src/components/dataTable/DataTableComponent.scss']
                }
            })
            .overrideComponent(DataTableRendererComponent, {
                set: {
                    providers: [{ provide: Injector, useValue: inject }]
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(DataTableComponent);
        component = fixture.componentInstance;

        component.config = config;
        component.columns = columns;
        component.items = items;
        component.sortStatus = sortStatus;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    beforeEach(async () => {
        await createDataTableComponentContext();
    });

    it('Should create DataTable Component correctly', () => {
        expect(component).toBeDefined();
    });

    it('it renders correct number of headers', () => {
        expect(nativeElement.querySelectorAll('.se-paged-list__header').length).toBe(
            columns.length
        );
    });

    it('it renders correct number of rows', () => {
        expect(nativeElement.querySelectorAll('.se-paged-list-item').length).toBe(items.length);
    });

    it('renders correct number of component cells', () => {
        expect(nativeElement.querySelectorAll('my-selector').length).toBe(columns.length);
    });

    it('renders correct number of default cells', () => {
        expect(
            nativeElement.querySelector('tr:nth-child(1) > td.se-paged-list-item-col_3').textContent
        ).toBe('col_3_row_1');
        expect(
            nativeElement.querySelector('tr:nth-child(2) > td.se-paged-list-item-col_3').textContent
        ).toBe('col_3_row_2');
        expect(
            nativeElement.querySelector('tr:nth-child(3) > td.se-paged-list-item-col_3').textContent
        ).toBe('col_3_row_3');
    });
});
