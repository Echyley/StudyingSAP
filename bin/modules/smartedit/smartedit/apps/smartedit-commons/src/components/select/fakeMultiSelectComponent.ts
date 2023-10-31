/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewEncapsulation, ViewChild, Type, ChangeDetectorRef } from '@angular/core';
import { cloneDeep } from 'lodash';
import { SelectComponent, SelectOnChange, SelectReset } from 'smarteditcommons';
export const mockProductsV2 = [
    {
        id: 'product13',
        label: 'Test Product 13#',
        image: '',
        price: 123
    },
    {
        id: 'product14',
        label: 'Test Product 14#',
        image: '',
        price: 789
    },
    {
        id: 'product15',
        label: 'Test Product 15#',
        image: '',
        price: 234
    },
    {
        id: 'product16',
        label: 'Test Product 16#',
        image: '',
        price: 234
    }
];
export const mockSites = [
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
    }
];
export const mockProductsV1 = [
    {
        id: 'product1',
        label: 'Test Product 1#',
        image: '',
        price: 123
    },
    {
        id: 'product2',
        label: 'Test Product 2#',
        image: '',
        price: 234
    },
    {
        id: 'product3',
        label: 'Test Product 3#',
        image: '',
        price: 567
    },
    {
        id: 'product4',
        label: 'Test Product 4#',
        image: '',
        price: 554
    },
    {
        id: 'product5',
        label: 'Test Product 5#',
        image: '',
        price: 557
    },
    {
        id: 'product6',
        label: 'Test Product 6#',
        image: '',
        price: 557
    },
    {
        id: 'product7',
        label: 'Test Product 7#',
        image: '',
        price: 557
    },
    {
        id: 'product8',
        label: 'Test Product 8#',
        image: '',
        price: 557
    },
    {
        id: 'product9',
        label: 'Test Product 9#',
        image: '',
        price: 557
    },
    {
        id: 'product10',
        label: 'Test Product 10#',
        image: '',
        price: 557
    },
    {
        id: 'product11',
        label: 'Test Product 11#',
        image: '',
        price: 557
    },
    {
        id: 'product12',
        label: 'Test Product 12#',
        image: '',
        price: 557
    }
];

@Component({
    selector: 'test-multi-select',
    template: `
        <div class="smartedit-testing-overlay">
            <se-select
                *ngIf="isDisplayed"
                id="multi-select"
                [multiSelect]="true"
                [(model)]="model"
                [(reset)]="reset"
                [keepModelOnReset]="!forceReset"
                [onChange]="onChange"
                [isReadOnly]="isReadOnly"
                [fetchStrategy]="fetchStrategy">
            </se-select>
        </div>
    `,
    encapsulation: ViewEncapsulation.None,
    styles: [
        `
            .select-container {
                width: 35rem;
                margin-left: 1rem;
            }
        `
    ]
})
export class FakeMultiSelectComponent {
    @ViewChild(SelectComponent) selectComponent: SelectComponent<any>;
    public model = ['product2', 'product3'];
    public actionableSearchItem: any;
    public isReadOnly = false;
    public isDisplayed = true;
    public onRemoveModel: string;
    public forceReset = true;
    public reset: SelectReset | any;
    public onSelectModel: string;
    public onChange: SelectOnChange;
    public onChangeCounter = 0;
    public fetchStrategy = {
        fetchAll: (): Promise<any> => Promise.resolve([...this.source])
    };
    public source = mockProductsV1;
    constructor(public _cdr: ChangeDetectorRef) {
        this.reset = (): void => {
            this.selectComponent.updateModelIfNotFoundInItems(this.source);
        };
    }
    public changeSource(): void {
        this.source = this.source === mockProductsV1 ? mockProductsV2 : mockProductsV1;
        this.reset();
    }
    public clearSource(): void {
        this.model = undefined;
        this.source = [];
    }

    public changeResultsHeaderTemplate(type: 'label' | 'component', component?: Type<any>): void {
        if (type === 'label') {
            this.selectComponent.resultsHeaderLabel = 'Results Header Label';
            this.selectComponent.resultsHeaderComponent = undefined;
        } else if (type === 'component') {
            this.selectComponent.resultsHeaderLabel = undefined;
            this.selectComponent.resultsHeaderComponent = 'Results Header Custom Component' as any;
        }
    }

    public setSearchEnabled(enabled: boolean): void {
        this.selectComponent.searchEnabled = enabled;
        this.isReadOnly = !enabled;
    }

    public changeModel(): void {
        this.model = ['product4', 'product5'];
    }

    public clearModel(): void {
        this.model = undefined;
    }

    public getFetchPageResults(
        sites: typeof mockSites | typeof mockProductsV1,
        pageSize: number,
        currentPage: number
    ): Promise<any> {
        const results = cloneDeep(sites)
            .slice(currentPage * pageSize)
            .slice(0, pageSize);
        return Promise.resolve({
            results,
            pagination: { totalCount: 20, count: null, page: null, totalPages: null }
        });
    }
}
