import { ChangeDetectorRef } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ISelectItem } from 'smarteditcommons';
export declare class SelectOneComponent {
    private cdr;
    defaultPlaceHolder: string;
    items: ISelectItem<string>[];
    selectedValue: string;
    constructor(cdr: ChangeDetectorRef);
    handleItemSelected(item: ISelectItem<string>): void;
}
export declare class SelectTwoComponent {
    items: ISelectItem<string>[];
}
export declare class SelectThreeComponent {
    private cdr;
    items: ISelectItem<string>[];
    form: FormGroup;
    selectedValue: string;
    private valueChangesSub;
    constructor(cdr: ChangeDetectorRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
}
export declare class FakeSelectComponent {
    selectOneCompnent: SelectOneComponent;
    selectTwoCompnent: SelectTwoComponent;
    selectThreeCompnent: SelectThreeComponent;
}
