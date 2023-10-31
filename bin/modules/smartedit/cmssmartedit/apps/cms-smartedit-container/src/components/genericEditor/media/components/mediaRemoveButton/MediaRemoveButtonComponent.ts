/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'se-media-remove-button',
    templateUrl: './MediaRemoveButtonComponent.html',
    styleUrls: ['./MediaRemoveButtonComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaRemoveButtonComponent {
    @Input() isDisabled: boolean;
    @Output() clickHandler = new EventEmitter<Event>();

    public onClick($event: Event): void {
        $event.stopPropagation();
        this.clickHandler.emit($event);
    }
}
