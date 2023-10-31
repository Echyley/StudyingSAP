/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { TypedMap } from 'smarteditcommons';

@Component({
    selector: 'se-actionable-alert',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `<div>
        <p>{{ description | translate : descriptionDetails }}</p>
        <div>
            <a fd-link href (click)="onHyperLinkClick($event)">{{
                hyperlinkLabel | translate : hyperlinkDetails
            }}</a>
        </div>
    </div> `
})
export class ActionableAlertComponent {
    @Input() description: string;
    @Input() descriptionDetails: TypedMap<string>;
    @Input() hyperlinkLabel: string;
    @Input() hyperlinkDetails: TypedMap<string>;
    @Output() hyperLinkClick: EventEmitter<void>;

    constructor() {
        this.hyperLinkClick = new EventEmitter();
    }

    public onHyperLinkClick(event: Event): void {
        event.preventDefault();
        this.hyperLinkClick.emit();
    }
}
