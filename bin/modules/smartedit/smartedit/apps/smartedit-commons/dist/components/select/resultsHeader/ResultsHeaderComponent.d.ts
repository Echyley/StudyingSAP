import { EventEmitter, Type } from '@angular/core';
import { ActionableSearchItem } from '../actionableSearchItem';
export declare class ResultsHeaderComponent {
    search: string;
    resultsHeaderComponent: Type<any>;
    resultsHeaderLabel: string;
    displayResultsHeaderLabel: boolean;
    actionableSearchItem: ActionableSearchItem;
    actionButtonClick: EventEmitter<void>;
    onActionButtonClick(): void;
    showResultsHeaderItem(): boolean;
}
