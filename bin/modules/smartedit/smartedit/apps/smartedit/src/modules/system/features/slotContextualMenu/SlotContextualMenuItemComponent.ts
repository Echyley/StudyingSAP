/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { forwardRef, Component, HostListener, Inject, Input, Injector } from '@angular/core';

import { IContextualMenuButton, CONTEXTUAL_MENU_ITEM_DATA } from 'smarteditcommons';
import { SlotContextualMenuDecoratorComponent } from './SlotContextualMenuDecoratorComponent';

@Component({
    selector: 'slot-contextual-menu-item',
    template: `
        <div *ngIf="!item.action.component">
            <span
                *ngIf="item.iconIdle && parent.isHybrisIcon(item.displayClass)"
                id="{{ item.i18nKey | translate }}-{{ parent.smarteditComponentId }}-{{
                    parent.smarteditComponentType
                }}-hyicon"
                (click)="parent.triggerMenuItemAction(item, $event)"
                [ngClass]="{ clickable: true }">
                <img
                    [src]="isHovered ? item.iconNonIdle : item.iconIdle"
                    id="{{ item.i18nKey | translate }}-{{ parent.smarteditComponentId }}-{{
                        parent.smarteditComponentType
                    }}-hyicon-img"
                    title="{{ item.i18nKey | translate }}" />
            </span>
            <img
                [title]="item.i18nKey | translate"
                *ngIf="item.iconIdle && !parent.isHybrisIcon(item.displayClass)"
                [ngClass]="{ clickable: true }"
                (click)="parent.triggerMenuItemAction(item, $event)"
                [src]="isHovered ? item.iconNonIdle : item.iconIdle"
                [alt]="item.i18nKey"
                class="{{ item.displayClass }}"
                id="{{ item.i18nKey | translate }}-{{ parent.smarteditComponentId }}-{{
                    parent.smarteditComponentType
                }}" />
        </div>

        <div *ngIf="item.action.component">
            <ng-container
                *ngComponentOutlet="
                    item.action.component;
                    injector: componentInjector
                "></ng-container>
        </div>
    `
})
export class SlotContextualMenuItemComponent {
    @Input() item: IContextualMenuButton;

    public isHovered: boolean;
    public componentInjector: Injector;

    constructor(
        @Inject(forwardRef(() => SlotContextualMenuDecoratorComponent))
        public parent: SlotContextualMenuDecoratorComponent,
        private injector: Injector
    ) {}

    @HostListener('mouseover') onMouseOver(): void {
        this.isHovered = true;
    }
    @HostListener('mouseout') onMouseOut(): void {
        this.isHovered = false;
    }

    ngOnInit(): void {
        this.createComponentInjector();
    }

    ngOnChanges(): void {
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
                        setRemainOpen: (key: string, remainOpen: boolean): void =>
                            this.parent.setRemainOpen(key, remainOpen)
                    }
                }
            ]
        });
    }
}
