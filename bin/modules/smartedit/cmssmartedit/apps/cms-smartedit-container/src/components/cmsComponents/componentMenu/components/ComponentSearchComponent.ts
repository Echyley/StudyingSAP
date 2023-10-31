/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    OnDestroy,
    Input,
    Output,
    EventEmitter,
    ChangeDetectionStrategy
} from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { SystemEventService } from 'smarteditcommons';
import { RESET_COMPONENT_MENU_EVENT } from '../constants';

@Component({
    selector: 'se-component-search',
    templateUrl: './ComponentSearchComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentSearchComponent implements OnInit, OnDestroy {
    @Input() placeholderI18nKey: string;
    @Output() onChange: EventEmitter<string>;

    public searchQuery: string;
    public showResetButton: boolean;

    private unRegResetComponentMenuEvent: () => void;
    private searchQuerySubject$: Subject<string>;
    private searchQuerySubscription: Subscription;

    constructor(private systemEventService: SystemEventService) {
        this.onChange = new EventEmitter();

        this.searchQuery = '';
        this.showResetButton = false;
        this.searchQuerySubject$ = new Subject();
        this.searchQuerySubscription = this.searchQuerySubject$
            .pipe(debounceTime(500), distinctUntilChanged())
            .subscribe((newValue) => {
                this.searchQuery = newValue;
                this.onChange.emit(this.searchQuery);
            });
    }

    ngOnInit(): void {
        this.resetSearch();

        this.unRegResetComponentMenuEvent = this.systemEventService.subscribe(
            RESET_COMPONENT_MENU_EVENT,
            () => this.resetSearch()
        );
    }

    ngOnDestroy(): void {
        this.unRegResetComponentMenuEvent();
        this.searchQuerySubscription.unsubscribe();
    }

    public onSearchQueryChange(newValue: string): void {
        this.searchQuerySubject$.next(newValue);
        this.showResetButton = !!newValue.length;
    }

    public resetSearch(event?: Event): void {
        if (event) {
            event.stopPropagation();
        }

        this.searchQuery = '';
        this.showResetButton = false;
        this.onChange.emit(this.searchQuery);
    }
}
