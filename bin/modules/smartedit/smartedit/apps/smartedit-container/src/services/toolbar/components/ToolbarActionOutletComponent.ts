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

import { ToolbarItemInternal, ToolbarItemType, ToolbarSection } from 'smarteditcommons';
import { ToolbarComponent } from './ToolbarComponent';

/** @internal  */

@Component({
    selector: 'se-toolbar-action-outlet',
    template: `
        <div
            class="se-template-toolbar__action-template"
            [ngClass]="{
                'se-toolbar-action': isSectionRight,
                'se-toolbar-action--active': isSectionRight && isPermitionGranted
            }">
            <ng-container *ngIf="item.component && item.type === type.TEMPLATE">
                <ng-container
                    *ngComponentOutlet="item.component; injector: actionInjector"></ng-container>
            </ng-container>

            <!--ACTION and HYBRID_ACTION types-->

            <div *ngIf="item.type !== type.TEMPLATE">
                <se-toolbar-action [item]="item"></se-toolbar-action>
            </div>
        </div>
    `
})
export class ToolbarActionOutletComponent implements OnInit, OnChanges {
    @Input() item: ToolbarItemInternal;

    public actionInjector: Injector;
    public type = ToolbarItemType;

    constructor(@Inject(forwardRef(() => ToolbarComponent)) private toolbar: ToolbarComponent) {}

    ngOnInit(): void {
        this.setup();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.item) {
            this.setup();
        }
    }

    public get isSectionRight(): boolean {
        return this.item.section === ToolbarSection.right;
    }

    public get isPermitionGranted(): boolean {
        return this.item.isPermissionGranted;
    }

    private setup(): void {
        this.actionInjector = this.toolbar.createInjector(this.item);
    }
}
