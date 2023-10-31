import { EventEmitter } from '@angular/core';
import { IdWithLabel } from 'smarteditcommons';
export declare class SubTypeSelectorComponent {
    subTypes: string[];
    onSubTypeSelect: EventEmitter<string>;
    select({ id }: IdWithLabel): void;
}
