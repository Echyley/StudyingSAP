/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, forwardRef, Inject, Injector, Input, OnInit } from '@angular/core';
import { CONTEXTUAL_MENU_ITEM_DATA, IContextualMenuButton } from 'smarteditcommons';
import { PageTreeSlotMenuComponent } from './PageTreeSlotMenuComponent';

@Component({
    template: `
        <div *ngIf="item.action.component">
            <ng-container
                *ngComponentOutlet="
                    item.action.component;
                    injector: componentInjector
                "></ng-container>
        </div>
    `,
    selector: 'se-pagetree-slot-menu-item'
})
export class PageTreeSlotMenuItem implements OnInit {
    @Input() item: IContextualMenuButton;
    public componentInjector: Injector;

    constructor(
        @Inject(forwardRef(() => PageTreeSlotMenuComponent))
        public parent: PageTreeSlotMenuComponent,
        private readonly injector: Injector
    ) {}

    ngOnInit(): void {
        this.createComponentInjector();
    }

    private createComponentInjector(): void {
        this.componentInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: CONTEXTUAL_MENU_ITEM_DATA,
                    useValue: {
                        componentAttributes: this.parent.componentAttributes,
                        setRemainOpen: (): void => void 0
                    }
                }
            ]
        });
    }
}
