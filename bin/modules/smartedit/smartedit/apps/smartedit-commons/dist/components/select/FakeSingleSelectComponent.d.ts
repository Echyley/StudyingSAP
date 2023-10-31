import { ChangeDetectorRef } from '@angular/core';
import { SelectComponent } from 'smarteditcommons';
export declare const mockLanguagesV1: {
    id: string;
    label: string;
}[];
export declare const mockLanguagesV2: {
    id: string;
    label: string;
}[];
export declare class FakeSingleSelectComponent {
    _cdr: ChangeDetectorRef;
    selectComponent: SelectComponent<any>;
    model: string;
    fetchStrategy: {
        fetchAll: () => Promise<any>;
    };
    forceReset: boolean;
    controls: boolean;
    onChangeCounter: number;
    onRemoveCounter: number;
    onSelectCounter: number;
    isDisplayed: boolean;
    source: {
        id: string;
        label: string;
    }[];
    constructor(_cdr: ChangeDetectorRef);
    changeModel(): void;
    changeSource(): void;
    reset(): void;
}
