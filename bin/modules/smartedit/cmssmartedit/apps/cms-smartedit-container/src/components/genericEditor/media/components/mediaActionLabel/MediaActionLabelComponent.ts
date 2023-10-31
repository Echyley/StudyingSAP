/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

enum MediaActionLabelState {
    disabled = 'disabled'
}

@Component({
    selector: 'se-media-action-label',
    templateUrl: './MediaActionLabelComponent.html',
    styleUrls: ['./MediaActionLabelComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaActionLabelComponent {
    @Input() i18nKey: string;
    @Input() state?: MediaActionLabelState;
}
