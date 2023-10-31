/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './PaginationComponent.scss';
import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * The SmartEdit component that provides pagination by providing a visual pagination bar and buttons/numbers to navigate between pages.
 */
@Component({
    selector: 'se-pagination',
    templateUrl: './PaginationComponent.html'
})
export class PaginationComponent {
    @Input() totalItems: number;
    @Input() displayTotalItems = false;
    @Input() itemsPerPage: number;
    @Input() currentPage: number;

    /**
     * Event emitted when either a button or page number is clicked. Passes back the current page number.
     *
     * The invoker can bind this to a custom function to act and fetch results based on new page number.
     */
    @Output() onChange: EventEmitter<number> = new EventEmitter();

    public onPageChanged(page: number): void {
        this.currentPage = page;
        this.onChange.emit(page);
    }
}
