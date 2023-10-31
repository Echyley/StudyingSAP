import { EventEmitter, OnDestroy, OnInit } from '@angular/core';
export declare class VersionsSearchComponent implements OnInit, OnDestroy {
    versionsFoundCount: number;
    showSearchControls: boolean;
    searchTermChange: EventEmitter<string>;
    showResetButton: boolean;
    searchTerm: string;
    private searchTerm$;
    private searchTermSubject;
    private searchTermSubscription;
    ngOnInit(): void;
    ngOnDestroy(): void;
    onChange(value: string): void;
    resetSearchBox(): void;
    private initSearchInputFilter;
    private setSearchTermAndEmit;
}
