/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    Output,
    Type
} from '@angular/core';

import { ActionableSearchItem } from '../actionableSearchItem';

/** @internal */
@Component({
    selector: 'se-select-results-header',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select-results-header]': 'true'
    },
    templateUrl: './ResultsHeaderComponent.html',
    styleUrls: ['./ResultsHeaderComponent.scss']
})
export class ResultsHeaderComponent {
    @Input() search: string;
    @Input() resultsHeaderComponent: Type<any>;
    @Input() resultsHeaderLabel: string;
    @Input() displayResultsHeaderLabel: boolean;
    @Input() actionableSearchItem: ActionableSearchItem;

    @Output() actionButtonClick = new EventEmitter<void>();

    public onActionButtonClick(): void {
        this.actionButtonClick.emit();
    }

    /**
     * Whether the `ResultsHeaderItemComponent` is displayed.
     *
     * Custom component displayed above Results Header Label.
     */
    public showResultsHeaderItem(): boolean {
        return typeof this.resultsHeaderComponent !== 'undefined';
    }
}
