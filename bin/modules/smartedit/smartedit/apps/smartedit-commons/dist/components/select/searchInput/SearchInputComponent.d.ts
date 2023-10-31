import { ElementRef, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { ISharedDataService } from '@smart/utils';
export declare class SearchInputComponent implements OnInit, OnDestroy {
    private elementRef;
    private sharedDataService;
    isDisabled: boolean;
    isReadOnly: boolean;
    isTypeAheadEnabled: boolean;
    placeholder: string;
    search: string;
    searchKeyup: EventEmitter<{
        event: Event;
        value: string;
    }>;
    searchChange: EventEmitter<string>;
    searchInput: ElementRef;
    private keyUpEvent;
    private searchTerm$;
    private searchTermSubject;
    private searchTermSubscription;
    private configurations;
    constructor(elementRef: ElementRef, sharedDataService: ISharedDataService);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    get nativeElement(): any;
    get inputElement(): any;
    focus(): void;
    onChange(value: string): void;
    onKeyup(event: KeyboardEvent): void;
    private initSearchInputFilter;
}
