/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, Injector, OnInit } from '@angular/core';
import {
    CONTEXTUAL_MENU_ITEM_DATA,
    IContextualMenuButton,
    POPUP_OVERLAY_DATA
} from 'smarteditcommons';
import { ParentMenu } from './ParentMenu';

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
    selector: 'se-contextual-menu-item-overlay'
})
export class PageTreeMenuItemOverlayComponent implements OnInit {
    public componentInjector: Injector;

    constructor(
        @Inject(POPUP_OVERLAY_DATA) private readonly data: { item: IContextualMenuButton },
        private readonly parent: ParentMenu,
        private readonly injector: Injector
    ) {}

    ngOnInit(): void {
        this.createComponentInjector();
    }

    public get item(): IContextualMenuButton {
        return this.data.item;
    }

    private createComponentInjector(): void {
        this.componentInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: CONTEXTUAL_MENU_ITEM_DATA,
                    useValue: {
                        componentAttributes: this.parent.componentAttributes,
                        setRemainOpen: (key: string, remainOpen: boolean): void =>
                            this.parent.setRemainOpen(key, remainOpen)
                    }
                }
            ]
        });
    }
}
