/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Directive, ElementRef, OnInit, Renderer2 } from '@angular/core';

/**
 * Directive for marking list item for 'ListKeyboardControlDirective' to allow for navigating with keyboard.
 */
@Directive({
    selector: '[suListItemKeyboardControl]'
})
export class ListItemKeyboardControlDirective implements OnInit {
    /** @internal */
    private readonly activeClassName = 'is-active';

    constructor(private hostElement: ElementRef, private renderer: Renderer2) {}

    /** @internal */
    ngOnInit(): void {
        this.setTabIndex();
    }

    public getElement(): HTMLElement {
        return this.hostElement.nativeElement;
    }

    public setActive(): void {
        const elm = this.getElement();
        this.renderer.addClass(elm, this.activeClassName);

        elm.scrollIntoView({ block: 'nearest' });
    }

    public setInactive(): void {
        this.renderer.removeClass(this.getElement(), this.activeClassName);
    }

    /** @internal */
    private setTabIndex(): void {
        this.renderer.setAttribute(this.getElement(), 'tabindex', '-1');
    }
}
