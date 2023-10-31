import { Injector, OnInit } from '@angular/core';
import { IContextualMenuButton } from 'smarteditcommons';
import { PageTreeSlotMenuComponent } from './PageTreeSlotMenuComponent';
export declare class PageTreeSlotMenuItem implements OnInit {
    parent: PageTreeSlotMenuComponent;
    private readonly injector;
    item: IContextualMenuButton;
    componentInjector: Injector;
    constructor(parent: PageTreeSlotMenuComponent, injector: Injector);
    ngOnInit(): void;
    private createComponentInjector;
}
