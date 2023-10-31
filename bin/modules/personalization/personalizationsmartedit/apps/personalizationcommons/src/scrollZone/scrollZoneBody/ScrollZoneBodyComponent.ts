/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    Input,
    EventEmitter,
    Output,
    OnChanges,
    ChangeDetectionStrategy
} from '@angular/core';

@Component({
    selector: 'personalizationsmartedit-scroll-zone-body',
    templateUrl: './ScrollZoneBodyComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ScrollZoneBodyComponent implements OnChanges {
    @Input() additionalClasses: string[];
    @Input() isTransparent: boolean;

    @Output() scrollZoneBodyMouseenter: EventEmitter<void>;
    @Output() scrollZoneBodyMouseleave: EventEmitter<void>;

    public cssClasses: string[];

    constructor() {
        this.additionalClasses = [];
        this.isTransparent = false;

        this.scrollZoneBodyMouseenter = new EventEmitter();
        this.scrollZoneBodyMouseleave = new EventEmitter();

        this.cssClasses = [];
    }

    ngOnChanges(): void {
        this.cssClasses = this.getCssClasses(this.isTransparent, this.additionalClasses || []);
    }

    public onMouseenter(): void {
        this.scrollZoneBodyMouseenter.emit();
    }

    public onMouseleave(): void {
        this.scrollZoneBodyMouseleave.emit();
    }

    private getCssClasses(isTransparent: boolean, additionalClasses: string[]): string[] {
        const transparencyClass = isTransparent
            ? 'perso__scrollzone--transparent'
            : 'perso__scrollzone--normal';
        return [transparencyClass, ...additionalClasses];
    }
}
