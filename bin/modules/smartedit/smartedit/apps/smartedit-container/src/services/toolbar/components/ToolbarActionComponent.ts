/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    forwardRef,
    Component,
    Inject,
    Injector,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges
} from '@angular/core';

import {
    Placement,
    ToolbarDropDownPosition,
    ToolbarItemInternal,
    ToolbarItemType,
    ToolbarSection
} from 'smarteditcommons';
import { ToolbarComponent } from './ToolbarComponent';

/** @internal  */
@Component({
    selector: 'se-toolbar-action',
    templateUrl: './ToolbarActionComponent.html'
})
export class ToolbarActionComponent implements OnInit, OnChanges {
    @Input() item: ToolbarItemInternal;

    public actionInjector: Injector;
    public type = ToolbarItemType;

    constructor(@Inject(forwardRef(() => ToolbarComponent)) public toolbar: ToolbarComponent) {}

    ngOnInit(): void {
        this.setup();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.item) {
            this.setup();
        }
    }

    public get isCompact(): boolean {
        return this.item.actionButtonFormat === 'compact';
    }

    public get placement(): Placement {
        if (
            this.item.section === ToolbarSection.left ||
            this.item.section === ToolbarSection.middle
        ) {
            return 'bottom-start';
        } else if (this.item.section === ToolbarSection.right) {
            return 'bottom-end';
        }

        switch (this.item.dropdownPosition) {
            case ToolbarDropDownPosition.left:
                return 'bottom-start';
            case ToolbarDropDownPosition.right:
                return 'bottom-end';
            default:
                return 'bottom';
        }
    }

    private setup(): void {
        this.actionInjector = this.toolbar.createInjector(this.item);
    }
}
