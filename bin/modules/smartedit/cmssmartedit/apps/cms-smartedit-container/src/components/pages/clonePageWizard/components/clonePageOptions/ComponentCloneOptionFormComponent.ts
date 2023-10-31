/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
    selector: 'se-component-clone-option-form',
    templateUrl: './ComponentCloneOptionFormComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentCloneOptionFormComponent implements OnInit {
    @Output() onSelectionChange: EventEmitter<string>;

    public componentInSlotOption: string;
    public readonly CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION = {
        REFERENCE_EXISTING: 'reference',
        CLONE: 'clone'
    };

    constructor() {
        this.onSelectionChange = new EventEmitter();
    }

    ngOnInit(): void {
        this.componentInSlotOption =
            this.CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.REFERENCE_EXISTING;
        this.onSelectionChange.emit(this.componentInSlotOption);
    }

    updateComponentInSlotOption(option: string): void {
        this.onSelectionChange.emit(option);
    }
}
