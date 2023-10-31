/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Directive, ElementRef, Inject, OnDestroy, OnInit } from '@angular/core';

@Directive({
    selector: '[persoPreventParentScroll]'
})
export class PersonalizationPreventParentScrollDirective implements OnInit, OnDestroy {
    constructor(@Inject(ElementRef) private element: ElementRef) {}

    mouseWheelEventHandler = (event: any): void => this.onMouseWheel(event);

    ngOnInit(): void {
        const element: Element = this.element.nativeElement;
        element.addEventListener('mousewheel', this.mouseWheelEventHandler);
        element.addEventListener('DOMMouseScroll', this.mouseWheelEventHandler);
    }

    ngOnDestroy(): void {
        const element: Element = this.element.nativeElement;
        element.removeEventListener('mousewheel', this.mouseWheelEventHandler);
        element.removeEventListener('DOMMouseScroll', this.mouseWheelEventHandler);
    }

    private onMouseWheel(event: any): void {
        const element: any = this.element.nativeElement;
        const originalEventCondition =
            event.originalEvent &&
            (event.originalEvent.wheelDeltaY || event.originalEvent.wheelDelta);
        const IEEventCondition = -(event.deltaY || event.delta) || 0;
        element.parentElement.parentElement.parentElement.scrollTop -=
            event.wheelDeltaY || originalEventCondition || event.wheelDelta || IEEventCondition;
        event.stopPropagation();
        event.preventDefault();
        event.returnValue = false;
    }
}
