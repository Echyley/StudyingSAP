import { EventEmitter } from '@angular/core';
import { TypedMap } from 'smarteditcommons';
export declare class ActionableAlertComponent {
    description: string;
    descriptionDetails: TypedMap<string>;
    hyperlinkLabel: string;
    hyperlinkDetails: TypedMap<string>;
    hyperLinkClick: EventEmitter<void>;
    constructor();
    onHyperLinkClick(event: Event): void;
}
