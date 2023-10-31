import { OnInit } from '@angular/core';
import { ModalManagerService, IdWithLabel, TypedMap } from 'smarteditcommons';
export interface SelectComponentTypeModalComponentData {
    subTypes: TypedMap<string>;
}
export declare class SelectComponentTypeModalComponent implements OnInit {
    private modalManager;
    subTypes: IdWithLabel[];
    constructor(modalManager: ModalManagerService<SelectComponentTypeModalComponentData>);
    ngOnInit(): void;
    closeWithSelectedId(subTypeId: string): void;
    private mapSubTypesToIdWithLabel;
}
