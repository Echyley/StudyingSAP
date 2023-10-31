import { EventEmitter, OnChanges } from '@angular/core';
export declare class ScrollZoneBodyComponent implements OnChanges {
    additionalClasses: string[];
    isTransparent: boolean;
    scrollZoneBodyMouseenter: EventEmitter<void>;
    scrollZoneBodyMouseleave: EventEmitter<void>;
    cssClasses: string[];
    constructor();
    ngOnChanges(): void;
    onMouseenter(): void;
    onMouseleave(): void;
    private getCssClasses;
}
