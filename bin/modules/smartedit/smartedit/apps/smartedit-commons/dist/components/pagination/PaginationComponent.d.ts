import './PaginationComponent.scss';
import { EventEmitter } from '@angular/core';
export declare class PaginationComponent {
    totalItems: number;
    displayTotalItems: boolean;
    itemsPerPage: number;
    currentPage: number;
    onChange: EventEmitter<number>;
    onPageChanged(page: number): void;
}
