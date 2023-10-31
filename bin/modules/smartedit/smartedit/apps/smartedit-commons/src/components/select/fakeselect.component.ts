/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// eslint-disable-next-line max-classes-per-file
import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ISelectItem } from 'smarteditcommons';

@Component({
    selector: 'select-one',
    template: `
        <su-select
            id="select-one"
            [items]="items"
            [placeholder]="defaultPlaceHolder"
            (onItemSelected)="handleItemSelected($event)">
        </su-select>
        <div id="select-one-output" [innerText]="selectedValue"></div>
    `
})
export class SelectOneComponent {
    public defaultPlaceHolder = 'My placeholder';
    public items: ISelectItem<string>[] = [
        { id: 0, label: 'label_0', value: 'value_0' },
        { id: 1, label: 'label_1', value: 'value_1' }
    ];
    public selectedValue: string;

    constructor(private cdr: ChangeDetectorRef) {}

    public handleItemSelected(item: ISelectItem<string>): void {
        this.selectedValue = item.value;
        this.defaultPlaceHolder = item.label;
        this.cdr.detectChanges();
    }
}

@Component({
    selector: 'select-two',
    template: `
        <form>
            <su-select
                id="select-two"
                [items]="items"
                [initialValue]="items[0]"
                [placeholder]="'My placeholder'">
            </su-select>
        </form>
    `
})
export class SelectTwoComponent {
    public items: ISelectItem<string>[] = [
        { id: 0, label: 'label_0', value: 'value_0' },
        { id: 1, label: 'label_1', value: 'value_1' }
    ];
}

@Component({
    selector: 'select-three',
    template: `
        <form [formGroup]="form">
            <su-select
                id="select-three"
                [items]="items"
                formControlName="select"
                [placeholder]="'My placeholder'">
            </su-select>
        </form>

        <div id="select-three-output" [innerText]="selectedValue"></div>
    `
})
export class SelectThreeComponent {
    public items: ISelectItem<string>[] = [
        { id: 0, label: 'label_0', value: 'value_0' },
        { id: 1, label: 'label_1', value: 'value_1' }
    ];

    public form: FormGroup = new FormGroup({
        select: new FormControl(this.items[0])
    });

    public selectedValue: string;
    private valueChangesSub: Subscription;
    constructor(private cdr: ChangeDetectorRef) {}
    ngOnInit(): void {
        this.valueChangesSub = this.form
            .get('select')
            .valueChanges.subscribe((item: ISelectItem<string>) => {
                this.selectedValue = item.value;
                this.cdr.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this.valueChangesSub?.unsubscribe();
    }
}
@Component({
    selector: 'test-fake-select-component',
    template: `
        <div class="smartedit-testing-overlay">
            <select-one></select-one>
            <select-two></select-two>
            <select-three></select-three>
        </div>
    `
})
export class FakeSelectComponent {
    @ViewChild(SelectOneComponent) selectOneCompnent: SelectOneComponent;
    @ViewChild(SelectTwoComponent) selectTwoCompnent: SelectTwoComponent;
    @ViewChild(SelectThreeComponent) selectThreeCompnent: SelectThreeComponent;
}
