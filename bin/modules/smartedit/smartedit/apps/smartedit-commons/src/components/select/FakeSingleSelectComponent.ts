/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ViewEncapsulation, ViewChild, ChangeDetectorRef } from '@angular/core';
import { SelectComponent } from 'smarteditcommons';
export const mockLanguagesV1 = [
    {
        id: 'en',
        label: 'English'
    },
    {
        id: 'de',
        label: 'German'
    },
    {
        id: 'ru',
        label: 'Russian'
    },
    {
        id: 'pl',
        label: 'Polish'
    }
];
export const mockLanguagesV2 = [
    {
        id: 'fr',
        label: 'French'
    },
    {
        id: 'en',
        label: 'English'
    },
    {
        id: 'es',
        label: 'Spanish'
    },
    {
        id: 'it',
        label: 'Italian'
    }
];

@Component({
    selector: 'test-single-select',
    template: `
        <div class="smartedit-testing-overlay">
            <se-select
                *ngIf="isDisplayed"
                id="single-select"
                [(model)]="model"
                [controls]="controls"
                [keepModelOnReset]="!forceReset"
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
export class FakeSingleSelectComponent {
    @ViewChild(SelectComponent) selectComponent: SelectComponent<any>;
    public model = 'de';
    public fetchStrategy = {
        fetchAll: (): Promise<any> => Promise.resolve([...this.source])
    };
    public forceReset = true;
    public controls = true;

    public onChangeCounter = 0;
    public onRemoveCounter = 0;
    public onSelectCounter = 0;

    public isDisplayed = true;

    public source = mockLanguagesV1;

    constructor(public _cdr: ChangeDetectorRef) {}

    public changeModel(): void {
        this.model = this.source.find((item) => item.id !== this.model).id;
    }

    public changeSource(): void {
        this.source = this.source === mockLanguagesV1 ? mockLanguagesV2 : mockLanguagesV1;
    }

    public reset(): void {
        this.selectComponent.updateModelIfNotFoundInItems(this.source);
    }
}
