/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output
} from '@angular/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

/**
 * Represents controls that allows the user to search for a specific page version.
 */
@Component({
    selector: 'se-versions-search',
    templateUrl: './VersionsSearchComponent.html',
    styleUrls: ['./VersionsSearchComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VersionsSearchComponent implements OnInit, OnDestroy {
    @Input() versionsFoundCount: number;
    @Input() showSearchControls: boolean;
    @Output() searchTermChange = new EventEmitter<string>();

    public showResetButton = false;
    public searchTerm: string;

    private searchTerm$: Observable<string>;
    private searchTermSubject: Subject<string>;
    private searchTermSubscription: Subscription;

    ngOnInit(): void {
        this.initSearchInputFilter();
    }

    ngOnDestroy(): void {
        this.searchTermSubscription.unsubscribe();
    }

    public onChange(value: string): void {
        this.searchTermSubject.next(value);
    }

    public resetSearchBox(): void {
        this.setSearchTermAndEmit('');
    }

    private initSearchInputFilter(): void {
        this.searchTermSubject = new Subject<string>();
        this.searchTerm$ = this.searchTermSubject
            .asObservable()
            .pipe(debounceTime(500), distinctUntilChanged());

        this.searchTermSubscription = this.searchTerm$.subscribe((value: string) =>
            this.setSearchTermAndEmit(value)
        );
    }

    private setSearchTermAndEmit(value: string): void {
        this.searchTerm = value;
        this.showResetButton = this.searchTerm !== '';
        this.searchTermChange.emit(this.searchTerm);
    }
}
