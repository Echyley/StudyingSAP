import { Injector } from '@angular/core';
import { IContextualMenuButton } from 'smarteditcommons';
import { SlotContextualMenuDecoratorComponent } from './SlotContextualMenuDecoratorComponent';
export declare class SlotContextualMenuItemComponent {
    parent: SlotContextualMenuDecoratorComponent;
    private injector;
    item: IContextualMenuButton;
    isHovered: boolean;
    componentInjector: Injector;
    constructor(parent: SlotContextualMenuDecoratorComponent, injector: Injector);
    onMouseOver(): void;
    onMouseOut(): void;
    ngOnInit(): void;
    ngOnChanges(): void;
    private createComponentInjector;
}
