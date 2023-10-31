import { Type, ChangeDetectorRef } from '@angular/core';
import { SelectComponent, SelectOnChange, SelectReset } from 'smarteditcommons';
export declare const mockProductsV2: {
    id: string;
    label: string;
    image: string;
    price: number;
}[];
export declare const mockSites: {
    id: string;
    label: string;
}[];
export declare const mockProductsV1: {
    id: string;
    label: string;
    image: string;
    price: number;
}[];
export declare class FakeMultiSelectComponent {
    _cdr: ChangeDetectorRef;
    selectComponent: SelectComponent<any>;
    model: string[];
    actionableSearchItem: any;
    isReadOnly: boolean;
    isDisplayed: boolean;
    onRemoveModel: string;
    forceReset: boolean;
    reset: SelectReset | any;
    onSelectModel: string;
    onChange: SelectOnChange;
    onChangeCounter: number;
    fetchStrategy: {
        fetchAll: () => Promise<any>;
    };
    source: {
        id: string;
        label: string;
        image: string;
        price: number;
    }[];
    constructor(_cdr: ChangeDetectorRef);
    changeSource(): void;
    clearSource(): void;
    changeResultsHeaderTemplate(type: 'label' | 'component', component?: Type<any>): void;
    setSearchEnabled(enabled: boolean): void;
    changeModel(): void;
    clearModel(): void;
    getFetchPageResults(sites: typeof mockSites | typeof mockProductsV1, pageSize: number, currentPage: number): Promise<any>;
}
