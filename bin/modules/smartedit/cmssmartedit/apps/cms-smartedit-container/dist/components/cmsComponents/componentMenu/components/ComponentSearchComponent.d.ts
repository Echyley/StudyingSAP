import { OnInit, OnDestroy, EventEmitter } from '@angular/core';
import { SystemEventService } from 'smarteditcommons';
export declare class ComponentSearchComponent implements OnInit, OnDestroy {
    private systemEventService;
    placeholderI18nKey: string;
    onChange: EventEmitter<string>;
    searchQuery: string;
    showResetButton: boolean;
    private unRegResetComponentMenuEvent;
    private searchQuerySubject$;
    private searchQuerySubscription;
    constructor(systemEventService: SystemEventService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    onSearchQueryChange(newValue: string): void;
    resetSearch(event?: Event): void;
}
