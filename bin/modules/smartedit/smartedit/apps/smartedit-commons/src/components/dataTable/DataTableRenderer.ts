/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    Injector,
    Input,
    OnChanges,
    SimpleChanges
} from '@angular/core';
import {
    DynamicPagedListColumnKey,
    DynamicPagedListDropdownItem
} from '../dynamicPagedList/interfaces';
import { DATA_TABLE_COMPONENT_DATA } from './DataTableComponent';

@Component({
    selector: 'se-data-table-renderer',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `
        <div *ngIf="column.component; else cellText">
            <ng-container
                *ngComponentOutlet="column.component; injector: componentInjector"></ng-container>
        </div>
        <ng-template #cellText
            ><span>{{ item[column.property] }}</span></ng-template
        >
    `
})
export class DataTableRendererComponent implements OnChanges {
    @Input() column: DynamicPagedListColumnKey;
    @Input() item: DynamicPagedListDropdownItem;

    public componentInjector: Injector;

    constructor(private readonly injector: Injector) {}

    ngOnChanges(changes: SimpleChanges): void {
        const columnChanges = changes.column;
        if (columnChanges?.currentValue.component) {
            this.createInjector();
        }
    }

    private createInjector(): void {
        const { column, item } = this;

        this.componentInjector = Injector.create({
            providers: [
                {
                    provide: DATA_TABLE_COMPONENT_DATA,
                    useValue: { column, item }
                }
            ],
            parent: this.injector
        });
    }
}
