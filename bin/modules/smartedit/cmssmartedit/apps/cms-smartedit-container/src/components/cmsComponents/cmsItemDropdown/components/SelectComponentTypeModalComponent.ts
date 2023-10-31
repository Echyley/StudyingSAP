/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { ModalManagerService, IdWithLabel, TypedMap } from 'smarteditcommons';

export interface SelectComponentTypeModalComponentData {
    subTypes: TypedMap<string>;
}

@Component({
    selector: 'se-select-component-type-modal',
    template: `
        <se-sub-type-selector
            class="sub-type-selector"
            [subTypes]="subTypes"
            (onSubTypeSelect)="closeWithSelectedId($event)">
        </se-sub-type-selector>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectComponentTypeModalComponent implements OnInit {
    public subTypes: IdWithLabel[];

    constructor(private modalManager: ModalManagerService<SelectComponentTypeModalComponentData>) {}

    ngOnInit(): void {
        this.modalManager
            .getModalData()
            .pipe(take(1))
            .subscribe(({ subTypes }) => (this.subTypes = this.mapSubTypesToIdWithLabel(subTypes)));
    }

    public closeWithSelectedId(subTypeId: string): void {
        this.modalManager.close(subTypeId);
    }

    private mapSubTypesToIdWithLabel(subTypes: TypedMap<string>): IdWithLabel[] {
        return Object.keys(subTypes).map((key) => ({
            id: key,
            label: subTypes[key]
        }));
    }
}
